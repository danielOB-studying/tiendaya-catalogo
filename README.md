# TiendaYa Catálogo

Este repositorio está organizado en las siguientes áreas:

- `backend/`: aplicación Spring Boot que expone la API REST.
- `frontend/`: frontend Angular con CSS puro escrito a mano.
- `frontendV2/`: frontend Angular con Angular Material (versión V2 con Material UI).

## Estructura del repositorio

- `backend/`
  - `pom.xml` - configuración de Maven y dependencias.
  - `src/main/java/com/tiendaya/catalogo/...` - código fuente Java del backend.
  - `src/main/resources/application.properties` - configuración de Spring Boot y la base de datos H2.
  - `src/test/java/...` - pruebas unitarias del backend.
- `frontend/`
  - Frontend Angular con estilos CSS puros escritos a mano.
  - Incluye listado, formulario y detalle de productos.
- `frontendV2/`
  - Frontend V2 migrada a Angular Material.
  - Misma funcionalidad pero con componentes de Material UI (tablas, formularios, tarjetas, snackbars, etc.).
  - Corre en el puerto `4201` para poder ejecutarse simultáneamente con `frontend/`.

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

## Frontend con CSS puro (`frontend/`)

Frontend Angular 17 con estilos CSS escritos manualmente.

### Ejecutar el frontend CSS

Desde la raíz del repositorio:

```bash
cd frontend
npm install
npm start
```

La aplicación quedará disponible en `http://localhost:4200`.

### Compilar el frontend CSS

```bash
cd frontend
npm install
npm run build
```

### Archivos principales del frontend CSS

- `frontend/package.json`
- `frontend/angular.json`
- `frontend/src/app/app.component.html`
- `frontend/src/app/product-form/product-form.component.html`
- `frontend/src/app/app.module.ts`
- `frontend/src/app/product.service.ts`
- `frontend/proxy.conf.json`

> La configuración de proxy en `frontend/proxy.conf.json` reenvía `/api` a `http://localhost:8080`.

## Frontend V2 con Angular Material (`frontendV2/`)

Copia del frontend migrada a Angular Material. Usa componentes de Material UI en lugar de CSS manual:

- **Listado de productos**: `mat-table`, `mat-form-field`, `mat-button`, `mat-icon-button`, `mat-chip`, `mat-progress-spinner`, `mat-snack-bar`.
- **Formulario de producto**: `mat-form-field`, `mat-input`, `mat-checkbox`, `mat-error`, `mat-progress-spinner`, `mat-card`.
- **Detalle de producto**: `mat-card`, `mat-list`, `mat-chip`, `mat-button`.
- **Navegación**: `mat-toolbar`, `mat-icon`.

### Ejecutar el frontend V2

Desde la raíz del repositorio:

```bash
cd frontendV2
npm install
npm start
```

La aplicación quedará disponible en `http://localhost:4201`.

### Compilar el frontend V2

```bash
cd frontendV2
npm install
npm run build
```

### Archivos principales del frontend V2

- `frontendV2/package.json`
- `frontendV2/angular.json`
- `frontendV2/src/app/app.module.ts`
- `frontendV2/src/app/product-list/product-list.component.html`
- `frontendV2/src/app/product-form/product-form.component.html`
- `frontendV2/src/app/product-detail/product-detail.component.html`
- `frontendV2/src/app/product.service.ts`
- `frontendV2/proxy.conf.json`

> La configuración de proxy en `frontendV2/proxy.conf.json` reenvía `/api` a `http://localhost:8080`.

## Ejecutar todo junto

1. Inicia el backend:

```bash
cd backend
mvn spring-boot:run
```

2. En otra terminal, inicia el frontend con CSS puro:

```bash
cd frontend
npm install
npm start
```

3. En otra terminal, inicia el frontend V2:

```bash
cd frontendV2
npm install
npm start
```

4. Abre las UI:
   - Frontend CSS puro: `http://localhost:4200`
   - Frontend V2 (Material): `http://localhost:4201`

Ambos frontends usan la API en `http://localhost:8080`.

## Organización propuesta

- `backend/` contiene el servicio Spring Boot independiente.
- `frontend/` contiene la UI con CSS puro.
- `frontendV2/` contiene la UI con Angular Material.

Esto permite al cliente comparar ambas opciones de interfaz y escoger la que prefiera.