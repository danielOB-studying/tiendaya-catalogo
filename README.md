# TiendaYa Catálogo

Este repositorio está organizado en dos áreas principales:

- `backend/`: aplicación Spring Boot que expone la API REST.
- `frontend/`: recursos de interfaz de usuario legacy.

## Estructura del repositorio

- `backend/`
  - `pom.xml` - configuración de Maven y dependencias.
  - `src/main/java/com/tiendaya/catalogo/...` - código fuente Java del backend.
  - `src/main/resources/application.properties` - configuración de Spring Boot y la base de datos H2.
  - `src/test/java/...` - pruebas unitarias del backend.
- `frontend/`
  - Recursos de vista y estilos del frontend.
  - No contiene un proyecto Angular completo con `package.json` ni `angular.json`.

## Backend

El backend es una API REST construida con Spring Boot.

### Punto de entrada

- `backend/src/main/java/com/tiendaya/catalogo/CatalogoApplication.java`

### Ejecutar el backend

Desde la raíz del repositorio:

```bash
cd backend
mvn spring-boot:run
```

### Compilar el backend

```bash
cd backend
mvn -DskipTests package
```

### Pruebas unitarias

```bash
cd backend
mvn test
```

### URL base

- `http://localhost:8080`

### Endpoints principales

- `GET /api/products`
- `GET /api/products/{id}`
- `POST /api/products`
- `PUT /api/products/{id}`
- `PATCH /api/products/{id}/active`
- `DELETE /api/products/{id}`

## Frontend

El frontend es ahora un proyecto Angular completo ubicado en `frontend/`.

### Ejecutar el frontend

Desde la raíz del repositorio:

```bash
cd frontend
npm install --legacy-peer-deps
npm start
```

La aplicación quedará disponible en `http://localhost:4200`.

### Compilar el frontend

```bash
cd frontend
npm install --legacy-peer-deps
npm run build
```

### Archivos principales del frontend

- `frontend/package.json`
- `frontend/angular.json`
- `frontend/src/app/app.component.html`
- `frontend/src/app/product-form/product-form.component.html`
- `frontend/src/app/app.module.ts`
- `frontend/src/app/product.service.ts`
- `frontend/proxy.conf.json`

> La configuración de proxy en `frontend/proxy.conf.json` reenvía `/api` a `http://localhost:8080`.

## Ejecutar todo junto

1. Inicia el backend:

```bash
cd backend
mvn spring-boot:run
```

2. En otra terminal, inicia el frontend:

```bash
cd frontend
npm install --legacy-peer-deps
npm start
```

3. Abre la UI en `http://localhost:4200`, que usará la API en `http://localhost:8080`.

## Organización propuesta

- `backend/` contiene el servicio Spring Boot independiente.
- `frontend/` contiene la UI legacy.

Esto hace que el repositorio sea más claro y facilita desarrollar, compilar y desplegar cada parte por separado.
