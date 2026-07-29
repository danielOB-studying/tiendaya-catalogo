# Estrategia de Manejo de Logs — Backend `tiendaya-catalogo`

> Documento de buenas prácticas DevOps para el logging del backend (Spring Boot + Java 17).
> Objetivo: que cualquier persona del equipo (dev, QA, DevOps) entienda **qué**, **por qué** y **cómo** aplicar la estrategia.

---

## 1. Estado actual

| Aspecto | Situación actual |
|--------|------------------|
| Framework de logging | Logback (incluido por Spring Boot), salida a consola en texto plano |
| Uso de logs en código | **No hay** (`@Slf4j`, `Logger`, `System.out` no se usan) |
| Configuración de logs | No existe en `application.properties` |
| Formato | Texto plano, no estructurado |
| Trazabilidad | No implementada |
| Secretos | Credenciales de Oracle en texto plano en `application.properties` ⚠️ |

**Conclusión:** el backend funciona, pero no es observable. En producción sería muy difícil diagnosticar incidentes.

---

## 2. Objetivos de la estrategia

1. **Observabilidad**: convertir los logs en datos buscables y accionables.
2. **Trazabilidad**: seguir una petición de extremo a extremo (`traceId`).
3. **Seguridad**: nunca exponer datos sensibles.
4. **Operabilidad**: integrarse con plataformas de monitoreo (Loki, ELK, Datadog).
5. **Simplicidad**: usar lo que Spring Boot ya trae, sin sobre-ingeniería.

---

## 3. Stack recomendado

| Componente | Herramienta | ¿Ya está? |
|-----------|-------------|-----------|
| Fachada de logging | SLF4J | ✅ (incluido por Spring Boot) |
| Implementación | Logback | ✅ (incluido por Spring Boot) |
| Inyección de logger | Lombok `@Slf4j` | ✅ (Lombok ya está en el `pom.xml`) |
| Formato JSON | `logstash-logback-encoder` | ❌ **Agregar** |
| Trazabilidad | Micrometer Tracing (Spring Boot 3) | ❌ **Agregar** |
| Recolección | Fluent Bit / Promtail / Filebeat | Externo a la app |

---

## 4. Niveles de log y cuándo usarlos

| Nivel | Cuándo usarlo | Ejemplo |
|-------|--------------|---------|
| `ERROR` | Fallos que impiden una operación (con stack trace). | No se pudo conectar a la base de datos |
| `WARN`  | Situaciones anormales pero recuperables. | Parámetro de orden inválido → se usa "name" por defecto |
| `INFO`  | Eventos de negocio relevantes (ciclo de vida). | Producto creado con id=10 |
| `DEBUG` | Detalle de depuración, solo en desarrollo. | Filtros normalizados: category=Electrónica, name=null |
| `TRACE` | Muy detallado, casi nunca en producción. | Contenido completo de un DTO |

> **Regla de oro:** en producción se usan `INFO`, `WARN` y `ERROR`. `DEBUG` y `TRACE` se activan solo para diagnosticar.

---

## 5. Configuración paso a paso

### Paso 1 — Agregar la dependencia JSON

En `backend/pom.xml`, dentro de `<dependencies>`:

```xml
<!-- Logging estructurado en JSON para producción -->
<dependency>
    <groupId>net.logstash.logback</groupId>
    <artifactId>logstash-logback-encoder</artifactId>
    <version>7.4</version>
</dependency>
```

### Paso 2 — Crear `logback-spring.xml`

Crear el archivo `backend/src/main/resources/logback-spring.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>

    <!-- ===== Perfil DEV: consola legible ===== -->
    <springProfile name="dev,default">
        <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
            <encoder>
                <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
            </encoder>
        </appender>
        <root level="INFO">
            <appender-ref ref="CONSOLE"/>
        </root>
    </springProfile>

    <!-- ===== Perfil PROD: JSON a stdout para recolectores ===== -->
    <springProfile name="prod">
        <appender name="JSON" class="ch.qos.logback.core.ConsoleAppender">
            <encoder class="net.logstash.logback.encoder.LogstashEncoder">
                <includeMdcKeyName>traceId</includeMdcKeyName>
                <includeMdcKeyName>spanId</includeMdcKeyName>
                <customFields>{"service":"catalogo","env":"prod"}</customFields>
            </encoder>
        </appender>
        <root level="WARN">
            <appender-ref ref="JSON"/>
        </root>
    </springProfile>

</configuration>
```

> **Por qué dos perfiles:** en desarrollo quieres leer la consola fácilmente; en producción necesitas JSON para que Loki/ELK lo indexe.

### Paso 3 — Niveles por perfil en `application.properties`

Agregar al final del archivo actual:

```properties
# ===== Logging =====
# Nivel general y de la app por perfil
logging.level.root=WARN
logging.level.com.tiendaya.catalogo=INFO

# En desarrollo, activar DEBUG:
# logging.level.com.tiendaya.catalogo=DEBUG

# SQL: apagado en producción para ruido
logging.level.org.hibernate.SQL=OFF
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=OFF
```

Crear además `application-prod.properties`:

```properties
# Producción
logging.level.root=WARN
logging.level.com.tiendaya.catalogo=INFO
logging.level.org.hibernate.SQL=OFF
```

### Paso 4 — Usar `@Slf4j` en el código

Ejemplo aplicado a `ProductServiceImpl.java`:

```java
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    @Override
    public ProductResponseDTO getProductById(Long id) {
        log.debug("Buscando producto id={}", id);
        Product product = findEntityOrThrow(id);
        log.info("Producto encontrado id={} name={}", id, product.getName());
        return toResponseDTO(product);
    }

    @Override
    public ProductResponseDTO createProduct(ProductRequestDTO request) {
        log.info("Creando producto name={} category={}", request.getName(), request.getCategory());
        // ... lógica existente ...
        ProductResponseDTO result = toResponseDTO(productRepository.save(product));
        log.info("Producto creado id={}", result.getId());
        return result;
    }

    @Override
    public void deleteProduct(Long id) {
        log.warn("Eliminando producto id={}", id);
        Product product = findEntityOrThrow(id);
        productRepository.delete(product);
        log.info("Producto eliminado id={}", id);
    }
}
```

> **Buenas prácticas al loguear:**
> - Usa **placeholders** `{}` en vez de concatenación (`+`). Es más eficiente.
> - No loguees dentro de bucles tight (puede generar mucho ruido).
> - Loguea el **contexto** (ids, nombres), no el objeto completo.

### Paso 5 — Log de excepciones en `GlobalExceptionHandler`

```java
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleNotFound(ResourceNotFoundException ex, HttpServletRequest req) {
        log.warn("Recurso no encontrado: {} | path={}", ex.getMessage(), req.getRequestURI());
        // ... respuesta existente ...
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGeneric(Exception ex, HttpServletRequest req) {
        log.error("Error inesperado | path={}", req.getRequestURI(), ex); // ex = stack trace
        // ... respuesta existente ...
    }
}
```

> **Importante:** pasa el objeto `Exception` como último argumento para que Logback imprima el stack trace. **Nunca** loguees el cuerpo de la petición si puede contener datos sensibles.

---

## 6. Trazabilidad distribuida (`traceId`)

En microservicios o APIs, cada petición debe tener un identificador único que viaje por todos los logs.

### Dependencia (Spring Boot 3)

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-tracing-bridge-brave</artifactId>
</dependency>
```

### Propiedades

```properties
# Trazabilidad
management.tracing.sampling.probability=1.0
logging.pattern.level=%5p [%X{traceId:-},%X{spanId:-}]
```

Resultado: cada línea incluye `[traceId,spanId]`, y en JSON esos campos se indexan automáticamente.

---

## 7. Seguridad: qué NO loguear

| ❌ Nunca | ✅ En su lugar |
|----------|--------------|
| Contraseñas, tokens, API keys | "Autenticación exitosa para usuario=X" |
| Datos personales (PII) | Solo identificadores no sensibles |
| Cuerpo completo de peticiones | Solo metadatos (path, método, status) |
| Números de tarjeta, correos | Enmascarar: `****1234` |

### Externalizar credenciales de la base de datos

**Hoy** (inseguro):
```properties
spring.datasource.password=TaskFlow123
```

**Recomendado** (variables de entorno):
```properties
spring.datasource.password=${DB_PASSWORD}
```

Y en el entorno (Docker, Kubernetes, CI/CD):
```bash
export DB_PASSWORD=TaskFlow123
```

Mejor aún: usar un gestor de secretos (Vault, AWS Secrets Manager, Kubernetes Secrets).

---

## 8. Arquitectura de recolección (12-factor app)

```
┌─────────────┐     stdout      ┌──────────────┐     ┌──────────────┐
│  App Spring │ ──────────────▶ │  Fluent Bit  │ ──▶ │  Loki / ELK  │
│  (contenedor)│   (JSON logs)  │  / Promtail  │     │  / Datadog   │
└─────────────┘                 └──────────────┘     └──────────────┘
                                        │
                                        ▼
                                 ┌──────────────┐
                                 │   Grafana    │
                                 │  (alertas)   │
                                 └──────────────┘
```

**Principios clave:**
- La app **solo** escribe a `stdout`/`stderr`.
- Un **agente externo** (Fluent Bit, Promtail, Filebeat) recolecta y envía.
- **No** rotar archivos dentro del contenedor; lo hace el recolector/orquestador.
- Los logs se consultan y correlacionan en una plataforma central (Grafana, Kibana).

---

## 9. MDC: contexto por petición

El **MDC** (Mapped Diagnostic Context) permite añadir datos que aparecen automáticamente en cada log de esa petición (usuario, endpoint, `requestId`).

```java
import org.slf4j.MDC;

@Component
public class LoggingFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        MDC.put("requestId", UUID.randomUUID().toString());
        MDC.put("path", req.getRequestURI());
        try {
            chain.doFilter(req, res);
        } finally {
            MDC.clear(); // limpiar siempre
        }
    }
}
```

Con Micrometer Tracing, el `traceId` ya se propaga vía MDC automáticamente.

---

## 10. Rotación y retención

| Escenario | Estrategia |
|-----------|-----------|
| Contenedor (Docker/K8s) | Solo stdout; el recolector gestiona retención |
| On-premise sin recolector | `RollingFileAppender`: 10 MB/archivo, máx. 7 archivos, retención 30 días |
| Cloud | stdout → servicio gestionado (CloudWatch, Stackdriver) |

Ejemplo de `RollingFileAppender` (solo si no hay recolector):

```xml
<appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
    <file>logs/catalogo.log</file>
    <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
        <fileNamePattern>logs/catalogo-%d{yyyy-MM-dd}.%i.log.gz</fileNamePattern>
        <maxFileSize>10MB</maxFileSize>
        <maxHistory>30</maxHistory>
        <totalSizeCap>1GB</totalSizeCap>
    </rollingPolicy>
    <encoder class="net.logstash.logback.encoder.LogstashEncoder"/>
</appender>
```

---

## 11. Alertas sugeridas

| Métrica | Umbral de alerta |
|---------|-----------------|
| Tasa de `ERROR` | > 5/min durante 5 min |
| `ResourceNotFoundException` frecuente | > 20/min (posible bug de cliente) |
| Latencia HTTP alta | p95 > 2 s |
| Caída de conexión a Oracle | Cualquier `ERROR` de HikariCP |

Estas alertas se configuran en Grafana/Datadog a partir de los logs estructurados.

---

## 12. Checklist de implementación

- [ ] Agregar `logstash-logback-encoder` al `pom.xml`
- [ ] Crear `logback-spring.xml` con perfiles `dev` y `prod`
- [ ] Configurar niveles en `application.properties` y `application-prod.properties`
- [ ] Agregar `@Slf4j` en `ProductServiceImpl`, `ProductController`, `GlobalExceptionHandler`
- [ ] Loguear eventos de negocio (crear/actualizar/borrar) a nivel `INFO`
- [ ] Loguear excepciones con stack trace a nivel `ERROR`
- [ ] Externalizar credenciales con variables de entorno
- [ ] (Opcional) Agregar Micrometer Tracing para `traceId`
- [ ] (Opcional) Agregar filtro MDC con `requestId`
- [ ] Configurar recolector externo (Fluent Bit/Promtail)
- [ ] Definir alertas en la plataforma de monitoreo

---

## 13. Resumen en una frase

> **Logback + `@Slf4j` + JSON en producción + `traceId` + recolección externa + cero secretos en logs.**

Esta combinación convierte los logs en una herramienta de diagnóstico potente, segura y lista para entornos DevOps modernos.