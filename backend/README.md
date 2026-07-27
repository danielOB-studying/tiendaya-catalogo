# TiendaYa Catálogo

Aplicación backend en Spring Boot para gestionar productos del catálogo.

## Estructura del proyecto

- src/main/java/com/tiendaya/catalogo/controller: controladores REST
- src/main/java/com/tiendaya/catalogo/service: lógica de negocio
- src/main/java/com/tiendaya/catalogo/repository: acceso a datos
- src/main/java/com/tiendaya/catalogo/model: entidades JPA
- src/main/java/com/tiendaya/catalogo/dto: objetos de transferencia
- src/main/java/com/tiendaya/catalogo/exception: excepciones y manejo centralizado
- src/main/java/com/tiendaya/catalogo/config: configuración Spring
- src/main/resources: configuración y recursos de la app
- src/test/java: pruebas automatizadas
- docs/legacy: archivos viejos o de soporte

## Punto de entrada

La clase principal es [src/main/java/com/tiendaya/catalogo/CatalogoApplication.java](src/main/java/com/tiendaya/catalogo/CatalogoApplication.java).

## Ejecución

```bash
mvn spring-boot:run
```
