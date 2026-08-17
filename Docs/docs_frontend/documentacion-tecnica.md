# Documentación Técnica del Frontend — TiendaYa Catálogo

> Documento técnico generado a partir del análisis del código fuente del frontend Angular.
> Objetivo: reforzar conceptos y fundamentos de Angular evidenciados en este proyecto.

---

## Tabla de contenidos

1. [Visión general](#1-visión-general)
2. [Stack tecnológico](#2-stack-tecnológico)
3. [Estructura de directorios y archivos](#3-estructura-de-directorios-y-archivos)
4. [Patrones de programación identificados](#4-patrones-de-programación-identificados)
5. [Prácticas de programación](#5-prácticas-de-programación)
6. [Conceptos de Angular evidenciados](#6-conceptos-de-angular-evidenciados)
7. [Flujo de datos y arquitectura](#7-flujo-de-datos-y-arquitectura)
8. [Configuración, build y testing](#8-configuración-build-y-testing)
9. [Glosario de conceptos clave](#9-glosario-de-conceptos-clave)

---

## 1. Visión general

El frontend de **TiendaYa Catálogo** es una **Single Page Application (SPA)** desarrollada con **Angular 17** que gestiona un catálogo de productos. Permite listar, crear, editar, ver detalle y eliminar productos (operaciones CRUD), consumiendo una API REST provista por un backend Spring Boot.

La aplicación sigue el enfoque **tradicional basado en NgModules** (no utiliza *standalone components*), lo que la hace un excelente caso de estudio para reforzar los fundamentos clásicos de Angular.

### Características funcionales

- Listado de productos en tabla con filtro por categoría.
- Creación y edición de productos mediante formularios reactivos con validación.
- Vista de detalle de un producto.
- Eliminación con confirmación.
- Indicadores visuales de estado (cargando, error, bajo stock).

### Librerías de UI

> **Importante:** Este frontend **no utiliza ninguna librería de componentes UI** como Angular Material, Bootstrap, ng-bootstrap, PrimeNG, Tailwind u otra similar.

Toda la interfaz se construye con **CSS puro escrito a mano**. No hay dependencias de estilos ni componentes de terceros en el `package.json`. Esto implica:

- **Ventajas:** Bundle más ligero, sin dependencias externas de presentación, control total sobre el diseño.
- **Consideraciones:** Todos los estilos (botones, tablas, inputs, tarjetas, colores, responsive) deben implementarse manualmente.

Los estilos se organizan en:

- `frontend/src/styles.css` — estilos globales (`body`, `.app-container`, `.card`).
- `frontend/src/app/*.component.css` — estilos encapsulados por componente.

---

## 2. Stack tecnológico

| Tecnología        | Versión     | Propósito                                      |
|-------------------|-------------|------------------------------------------------|
| Angular           | ~17.3.12    | Framework principal para construir la SPA      |
| TypeScript        | 5.4.5       | Lenguaje tipado que compila a JavaScript       |
| RxJS              | ^7.8.1      | Programación reactiva (Observables)            |
| Zone.js           | ~0.14.0     | Detección de cambios de Angular                |
| Karma + Jasmine   | ~5.1 / ^5.1 | Framework de testing unitario                  |
| Angular CLI       | ~17.3.12    | Herramienta de build, serve y scaffolding       |

> **Nota:** No se incluyen librerías de UI (Material, Bootstrap, etc.). La presentación se implementa con CSS estándar.

**Archivos relevantes del stack:**

- `frontend/package.json` — declara todas las dependencias y scripts (`start`, `build`, `test`).
- `frontend/tsconfig.json` — configuración base de TypeScript (`target: es2020`, `experimentalDecorators`, `moduleResolution: bundler`).
- `frontend/tsconfig.app.json` — extiende la base para la build de la aplicación.
- `frontend/tsconfig.spec.json` — extiende la base para los tests (tipos `jasmine` y `node`).

---

## 3. Estructura de directorios y archivos

```
frontend/
├── angular.json                  # Configuración del Angular CLI (build, serve, test)
├── karma.conf.js                 # Configuración del runner de tests Karma
├── package.json                  # Dependencias y scripts npm
├── proxy.conf.json               # Proxy de desarrollo (/api -> backend)
├── tsconfig.json                 # Configuración base de TypeScript
├── tsconfig.app.json             # Configuración TS para la app
├── tsconfig.spec.json            # Configuración TS para tests
└── src/
    ├── index.html                # HTML raíz (contiene <app-root>)
    ├── main.ts                   # Punto de entrada (bootstrap de AppModule)
    ├── polyfills.ts              # Polyfills (zone.js)
    ├── styles.css                # Estilos globales
    ├── test.ts                   # Setup del entorno de testing
    ├── environments/
    │   ├── environment.ts        # Variables de entorno (desarrollo)
    │   └── environment.prod.ts   # Variables de entorno (producción)
    └── app/
        ├── app.module.ts         # Módulo raíz (NgModules)
        ├── app-routing.module.ts # Configuración de rutas
        ├── app.component.ts      # Componente raíz
        ├── app.component.html    # Template del componente raíz
        ├── app.component.css     # Estilos del componente raíz
        ├── product.model.ts      # Modelo de dominio (interfaz Product)
        ├── product.service.ts    # Servicio de acceso a API (CRUD)
        ├── product-list/         # Componente: listado de productos
        ├── product-detail/       # Componente: detalle de producto
        └── product-form/         # Componente: crear/editar producto
```

Cada componente sigue la **convención de archivos separados** (TypeScript, HTML y CSS independientes), lo que favorece la separación de responsabilidades y el mantenimiento.

---

## 4. Patrones de programación identificados

A continuación se describen los patrones de arquitectura y diseño evidenciados en el frontend, indicando los archivos que pertenecen a cada patrón.

### 4.1 Patrón Module (NgModule)

Angular organiza la aplicación en **módulos** (`NgModule`), que actúan como contenedores de componentes, directivas, pipes y servicios, declarando qué puede usarse dentro de su contexto.

**Archivos:**

- `frontend/src/app/app.module.ts` — módulo raíz que:
  - **Declara** los componentes: `AppComponent`, `ProductFormComponent`, `ProductListComponent`, `ProductDetailComponent`.
  - **Importa** módulos necesarios: `BrowserModule`, `HttpClientModule`, `FormsModule`, `ReactiveFormsModule`, `AppRoutingModule`.
  - **Bootstrap** el componente raíz: `AppComponent`.
- `frontend/src/app/app-routing.module.ts` — módulo de routing que importa y exporta `RouterModule`.

**Concepto clave:** El `AppModule` es el punto de ensamblaje. Sin `declarations`, los componentes no se reconocen en el módulo; sin `imports`, no se dispone de directivas como `*ngIf`, `*ngFor`, `[(ngModel)]` o `[formGroup]`.

---

### 4.2 Patrón Component (Component-Based Architecture)

Angular se basa en una arquitectura orientada a **componentes**: cada componente encapsula su lógica (TS), su vista (HTML) y sus estilos (CSS).

**Archivos por componente:**

| Componente              | TypeScript                              | HTML                                      | CSS                                      |
|------------------------|-----------------------------------------|-------------------------------------------|------------------------------------------|
| Raíz                   | `app/app.component.ts`                  | `app/app.component.html`                  | `app/app.component.css`                  |
| Listado de productos   | `app/product-list/product-list.component.ts` | `app/product-list/product-list.component.html` | `app/product-list/product-list.component.css` |
| Detalle de producto    | `app/product-detail/product-detail.component.ts` | `app/product-detail/product-detail.component.html` | `app/product-detail/product-detail.component.css` |
| Formulario de producto | `app/product-form/product-form.component.ts` | `app/product-form/product-form.component.html` | `app/product-form/product-form.component.css` |

**Metadatos del decorador `@Component`:**

- `selector`: etiqueta HTML personalizada (ej. `app-product-list`).
- `templateUrl`: ruta al archivo HTML.
- `styleUrls`: rutas a archivos CSS (estilos encapsulados por defecto).

**Concepto clave:** El *view encapsulation* de Angular aplica por defecto emulación de Shadow DOM, aislando los estilos de cada componente.

---

### 4.3 Patrón Service e Inyección de Dependencias (DI)

Los **servicios** encapsulan lógica de negocio y acceso a datos, y se inyectan en los componentes mediante el sistema de **Inyección de Dependencias** de Angular.

**Archivos:**

- `frontend/src/app/product.service.ts` — servicio principal:
  - Decorado con `@Injectable({ providedIn: 'root' })`, lo que lo hace:
    - **Tree-shakeable**: si no se usa, se elimina del bundle.
    - **Singleton**: una única instancia compartida en toda la app.
  - Inyecta `HttpClient` por constructor.
  - Expone métodos CRUD que devuelven `Observable<Product[]>`, `Observable<Product>` y `Observable<void>`.

**Consumidores (inyección por constructor):**

- `frontend/src/app/app.component.ts` — `constructor(private productService: ProductService)`
- `frontend/src/app/product-list/product-list.component.ts` — inyecta `ProductService` y `Router`.
- `frontend/src/app/product-detail/product-detail.component.ts` — inyecta `ActivatedRoute`, `Router` y `ProductService`.
- `frontend/src/app/product-form/product-form.component.ts` — inyecta `FormBuilder`, `ProductService`, `ActivatedRoute` y `Router`.

**Concepto clave:** La DI de Angular resuelve automáticamente las dependencias declaradas en el constructor, facilitando el desacoplamiento y la testabilidad (se pueden proveer *mocks* en tests).

---

### 4.4 Patrón Repository / Data Access Layer

El servicio `ProductService` actúa como un **repositorio** que abstrae los detalles de acceso a la API HTTP, de modo que los componentes no conocen URLs ni métodos HTTP.

**Archivo:** `frontend/src/app/product.service.ts`

Métodos expuestos:

| Método           | HTTP     | Ruta                  | Retorno              |
|------------------|----------|-----------------------|----------------------|
| `getProducts`    | `GET`    | `/api/products`       | `Observable<Product[]>` |
| `getProduct`     | `GET`    | `/api/products/:id`   | `Observable<Product>`   |
| `createProduct`  | `POST`   | `/api/products`       | `Observable<Product>`   |
| `updateProduct`  | `PUT`    | `/api/products/:id`   | `Observable<Product>`   |
| `deleteProduct`  | `DELETE` | `/api/products/:id`   | `Observable<void>`      |

**Concepto clave:** El uso de `HttpParams` en `getProducts(category?)` permite construir query strings de forma tipada y segura. El cast genérico `this.http.get<Product[]>` garantiza tipado en tiempo de compilación.

---

### 4.5 Patrón Observable / Programación Reactiva (RxJS)

Toda la comunicación asíncrona (HTTP) se modela con **Observables** de RxJS. Los componentes se **suscriben** a estos Observables para reaccionar a los datos y errores.

**Archivos:**

- `frontend/src/app/product.service.ts` — retorna Observables desde `HttpClient`.
- `frontend/src/app/app.component.ts` — `this.productService.getProducts().subscribe({ next, error })`.
- `frontend/src/app/product-list/product-list.component.ts` — suscripción en `loadProducts()` y `deleteProduct()`.
- `frontend/src/app/product-detail/product-detail.component.ts` — suscripción en `ngOnInit()` y `delete()`.
- `frontend/src/app/product-form/product-form.component.ts` — suscripción en `loadProduct()` y `submit()`.

**Patrón de suscripción usado:**

```typescript
this.productService.getProducts().subscribe({
  next: data => { /* éxito */ },
  error: err => { /* fallo */ }
});
```

**Concepto clave:** A diferencia de las Promesas, los Observables son *lazy* (no se ejecutan hasta suscribirse) y pueden emitir múltiples valores a lo largo del tiempo. En este proyecto se usa el patrón mínimo (un valor por petición HTTP), pero la arquitectura es extensible a streams continuos.

---

### 4.6 Patrón Model / Domain Model

El modelo de dominio se define como una **interfaz TypeScript**, estableciendo un contrato tipado para los datos del producto.

**Archivo:** `frontend/src/app/product.model.ts`

```typescript
export interface Product {
  id?: number;
  name: string;
  description: string;
  category: string;
  price: number;
  stock: number;
  active: boolean;
  lowStock?: boolean;
}
```

**Concepto clave:** Las propiedades opcionales (`id?`, `lowStock?`) reflejan que un producto nuevo no tiene `id` antes de crearse, y que `lowStock` es calculado por el backend. El tipado estático permite detectar errores en tiempo de compilación y mejora el autocompletado en el IDE.

---

### 4.7 Patrón Router / Navegación

La navegación entre vistas se gestiona con el **Router** de Angular, que mapea URLs a componentes.

**Archivos:**

- `frontend/src/app/app-routing.module.ts` — define la tabla de rutas:

| Ruta                    | Componente            | Descripción                     |
|-------------------------|------------------------|---------------------------------|
| `''`                    | (redirect)             | Redirige a `/products`          |
| `products`              | `ProductListComponent`  | Listado                         |
| `products/new`          | `ProductFormComponent`  | Crear producto                   |
| `products/:id`          | `ProductDetailComponent`| Ver detalle                     |
| `products/:id/edit`     | `ProductFormComponent`  | Editar producto                 |
| `**`                    | (redirect)             | Wildcard → `/products`          |

- `frontend/src/app/app.component.html` — contiene `<router-outlet>`, el marcador donde el Router renderiza el componente activo.
- Navegación programática en:
  - `product-list.component.ts` — `this.router.navigate(['/products/new'])`, etc.
  - `product-detail.component.ts` — `this.router.navigate(['/products'])`, etc.
  - `product-form.component.ts` — `this.router.navigate(['/products', this.productId])`.

**Lectura de parámetros de ruta:**

- `product-detail.component.ts` y `product-form.component.ts` usan `this.route.snapshot.paramMap.get('id')` para obtener el `id` de la URL.

**Concepto clave:** `RouterModule.forRoot(routes)` registra las rutas a nivel de aplicación. El `router-outlet` es el ancla donde se inserta la vista de la ruta activa. La ruta `**` es una *wildcard* que captura URLs no definidas, mejorando la experiencia de usuario.

---

### 4.8 Patrón Reactive Forms

El formulario de creación/edición usa **Reactive Forms**, donde el estado del formulario se modela como un árbol de objetos (`FormGroup`, `FormControl`) en el componente, no en el template.

**Archivo:** `frontend/src/app/product-form/product-form.component.ts`

```typescript
form = this.fb.group({
  name: ['', Validators.required],
  description: ['', Validators.required],
  category: ['', Validators.required],
  price: [0, [Validators.required, Validators.min(0.01)]],
  stock: [0, [Validators.required, Validators.min(0)]],
  active: [true]
});
```

**Template asociado:** `frontend/src/app/product-form/product-form.component.html`

- `[formGroup]="form"` — vincula el formulario del componente al template.
- `formControlName="name"` — vincula cada control.
- `(ngSubmit)="submit()"` — captura el envío.
- Validación visual: `*ngIf="form.get('name')?.invalid && form.get('name')?.touched"`.
- Deshabilitación condicional: `[disabled]="saving"`.

**Concepto clave:** A diferencia de *Template-Driven Forms*, los Reactive Forms son **sincrónicos**, **testables sin DOM**, y escalan mejor para formularios complejos. `FormBuilder` reduce el boilerplate frente a la creación manual de `FormGroup`/`FormControl`.

---

### 4.9 Patrón Template-Driven Forms (uso puntual)

El filtro de categoría en el listado usa **Template-Driven Forms** con `[(ngModel)]`.

**Archivo:** `frontend/src/app/product-list/product-list.component.html`

```html
<input [(ngModel)]="categoryFilter" placeholder="Electrónica, Hogar..." />
```

**Concepto clave:** `[(ngModel)]` es *banana in a box* — sintaxis de two-way data binding. Requiere `FormsModule`. Es adecuado para formularios simples como este filtro, donde no se necesita validación compleja.

---

### 4.10 Patrón EventEmitter / Comunicación Componente-Hijo → Padre

El formulario emite un evento personalizado cuando se crea un producto, permitiendo que el componente padre reaccione.

**Archivo:** `frontend/src/app/product-form/product-form.component.ts`

```typescript
@Output() created = new EventEmitter<void>();
// ...
this.created.emit();
```

**Concepto clave:** `@Output()` declara una propiedad de salida que el template padre puede escuchar con paréntesis: `(created)="onProductCreated()"`. Es el mecanismo estándar de Angular para que un hijo notifique al padre.

---

### 4.11 Patrón Environment Configuration

La aplicación distingue configuración de desarrollo y producción mediante **archivos de entorno**.

**Archivos:**

- `frontend/src/environments/environment.ts` — `{ production: false }`
- `frontend/src/environments/environment.prod.ts` — `{ production: true }`
- `frontend/src/main.ts` — `if (environment.production) { enableProdMode(); }`
- `frontend/angular.json` — `fileReplacements` reemplaza `environment.ts` por `environment.prod.ts` en la build de producción.

**Concepto clave:** `enableProdMode()` desactiva los chequeos de desarrollo (ej. detección de cambios doble en Angular), mejorando el rendimiento en producción. El patrón de *file replacement* permite tener configuraciones distintas sin cambiar el código fuente.

---

### 4.12 Patrón Proxy / API Gateway (desarrollo)

Durante el desarrollo, un **proxy** redirige las peticiones `/api` al backend, evitando problemas de CORS y permitiendo servir frontend y backend en puertos distintos.

**Archivo:** `frontend/proxy.conf.json`

```json
{
  "/api": {
    "target": "http://localhost:8080",
    "secure": false,
    "changeOrigin": true,
    "logLevel": "debug"
  }
}
```

**Uso:** El script `start` en `package.json` lo activa: `ng serve --proxy-config proxy.conf.json --host 0.0.0.0`.

**Concepto clave:** El proxy de desarrollo del Angular CLI intercepta peticiones que coinciden con el path `/api` y las reenvía al `target`. En producción, normalmente se sirve el frontend y backend tras el mismo dominio (o se configura CORS en el backend).

---

### 4.13 Patrón Lifecycle Hook (OnInit)

Los componentes implementan el hook `ngOnInit` para ejecutar lógica de inicialización tras la creación del componente y asignación de inputs.

**Archivos:**

- `frontend/src/app/app.component.ts` — `implements OnInit`, llama a `loadProducts()`.
- `frontend/src/app/product-list/product-list.component.ts` — `implements OnInit`, llama a `loadProducts()`.
- `frontend/src/app/product-detail/product-detail.component.ts` — `implements OnInit`, lee el `id` y carga el producto.
- `frontend/src/app/product-form/product-form.component.ts` — `implements OnInit`, detecta modo edición y carga el producto.

**Concepto clave:** El constructor solo debe usarse para inyección de dependencias; la lógica de inicialización (que puede acceder a inputs y propiedades del componente) debe ir en `ngOnInit`, que se ejecuta después de que Angular haya configurado las propiedades `@Input`.

---

## 5. Prácticas de programación

### 5.1 Separación de responsabilidades (Separation of Concerns)

- **Modelo** (`product.model.ts`): solo define la estructura de datos.
- **Servicio** (`product.service.ts`): solo maneja acceso a API.
- **Componentes**: solo manejan estado de la vista y orquestan llamadas al servicio.
- **Templates**: solo presentan datos y capturan eventos.
- **Estilos**: solo presentación, encapsulados por componente.

### 5.2 Tipado estático

Todo el código está en TypeScript con tipos explícitos en firmas de métodos y propiedades públicas. El cast `this.form.value as unknown as Product` en `product-form.component.ts` es una práctica deliberada para convertir el valor tipado del formulario al modelo de dominio.

### 5.3 Manejo de estado de UI

Cada componente gestiona tres banderas de estado:

- `loading: boolean` — indica carga en curso.
- `error: string` — mensaje de error a mostrar.
- (variable de datos) — `products`, `product`, `form`.

Esto permite mostrar *feedback* visual al usuario (mensajes de "Cargando..." y errores).

### 5.4 Encapsulamiento

- `ProductService.apiUrl` es `private readonly`, evitando modificaciones externas.
- `ProductFormComponent.loadProduct` es `private`, ocultando detalles internos.
- Los getters como `isEditMode` exponen estado derivado de solo lectura.

### 5.5 Navegación programática

En lugar de enlaces `<a href>`, se usa `router.navigate([...])` para transiciones controladas desde la lógica del componente, permitiendo lógica condicional antes de navegar.

### 5.6 Confirmación de acciones destructivas

La eliminación de productos usa `window.confirm()` antes de llamar al servicio, previniendo borrados accidentales.

### 5.7 Convenciones de nomenclatura

- **Componentes**: `PascalCase` para clases (`ProductListComponent`), `kebab-case` para archivos (`product-list.component.ts`).
- **Selectores**: `app-` como prefijo (configurado en `angular.json` → `prefix: "app"`).
- **Servicios**: `ProductService` con sufijo `Service`.
- **Modelos**: `Product` sin sufijo, archivo `product.model.ts`.

### 5.8 Estilos globales vs. encapsulados (CSS puro, sin librerías de UI)

> **Importante:** No se utiliza Angular Material, Bootstrap, Tailwind ni ninguna otra librería de componentes UI.

- `frontend/src/styles.css` — estilos globales (`body`, `.app-container`, `.card`).
- `*.component.css` — estilos encapsulados por componente.

Todos los estilos (botones, tablas, inputs, tarjetas, colores) se escriben manualmente con CSS estándar. Esto mantiene el bundle ligero pero requiere implementar manualmente cualquier componente visual.

---

## 6. Conceptos de Angular evidenciados

### 6.1 Bootstrap de la aplicación

**Archivo:** `frontend/src/main.ts`

```typescript
platformBrowserDynamic()
  .bootstrapModule(AppModule)
  .catch(err => console.error(err));
```

`platformBrowserDynamic()` crea la plataforma de Angular en el navegador y arranca el `AppModule`, que es el módulo raíz. El `catch` previene errores silenciosos.

### 6.2 Decoradores

| Decorador      | Uso                                              | Archivo ejemplo                     |
|----------------|--------------------------------------------------|-------------------------------------|
| `@NgModule`    | Declara un módulo                                | `app.module.ts`                     |
| `@Component`   | Declara un componente                            | `product-list.component.ts`          |
| `@Injectable`  | Declara un servicio inyectable                   | `product.service.ts`                |
| `@Output`      | Declara un evento de salida (hijo → padre)       | `product-form.component.ts`          |

### 6.3 Data Binding

| Tipo               | Sintaxis              | Ejemplo en el proyecto                          |
|--------------------|-----------------------|-------------------------------------------------|
| Interpolación      | `{{ expr }}`          | `{{ product.name }}`                            |
| Property binding   | `[prop]="expr"`       | `[formGroup]="form"`, `[disabled]="saving"`     |
| Event binding      | `(event)="method()"`  | `(click)="goToNew()"`, `(ngSubmit)="submit()"`   |
| Two-way binding    | `[(ngModel)]="prop"`  | `[(ngModel)]="categoryFilter"`                  |

### 6.4 Directivas estructurales

| Directiva | Propósito                          | Archivo ejemplo                          |
|-----------|------------------------------------|------------------------------------------|
| `*ngIf`   | Renderizado condicional            | `product-list.component.html`            |
| `*ngFor`  | Iteración sobre colecciones        | `product-list.component.html` (`*ngFor="let product of products"`) |

**Concepto clave:** El asterisco `*` es azúcar sintáctico para `<ng-template>`. Angular transforma `*ngIf="cond"` en un `<ng-template [ngIf]="cond">`.

### 6.5 Pipes

**Uso:** `{{ product.price | currency:'USD':'symbol':'1.0-0' }}` en `product-list.component.html` y `product-detail.component.html`.

El pipe `currency` formatea números como moneda. Los argumentos controlan código de moneda, símbolo y formato de decimales (`1.0-0` = mínimo 1 dígio, máximo 0 decimales).

### 6.6 Ciclo de vida (Lifecycle Hooks)

| Hook        | Momento de ejecución                          | Componente que lo usa            |
|-------------|-----------------------------------------------|-----------------------------------|
| `ngOnInit`  | Tras crear el componente y setear `@Input`    | `AppComponent`, `ProductListComponent`, `ProductDetailComponent`, `ProductFormComponent` |

### 6.7 HttpClient y tipado genérico

```typescript
this.http.get<Product[]>(this.apiUrl, { params });
```

El genérico `<Product[]>` hace que TypeScript infiera el tipo del cuerpo de la respuesta, permitiendo autocompletado y verificación de tipos.

### 6.8 ActivatedRoute y snapshots

```typescript
const id = Number(this.route.snapshot.paramMap.get('id'));
```

`ActivatedRoute` da acceso a los parámetros de la ruta. `snapshot` toma el valor inicial (suficiente aquí porque el componente se recrea al cambiar de producto). Para parámetros que cambian sin recargar el componente, se usaría `paramMap` como Observable.

### 6.9 View encapsulation

Angular aplica por defecto *Emulated* encapsulation: añade atributos únicos a los elementos del componente para que los estilos CSS no se filtren a otros componentes. Por eso cada `*.component.css` solo afecta a su componente.

---

## 7. Flujo de datos y arquitectura

### 7.1 Diagrama de capas

```
┌──────────────────────────────────────────────────────┐
│                    Template (HTML)                    │
│  Interpolación, binding, *ngIf, *ngFor, pipes, forms  │
└────────────────────────┬─────────────────────────────┘
                         │ eventos / binding
┌────────────────────────▼─────────────────────────────┐
│                  Componente (TS)                      │
│  Estado (loading, error, data), métodos, lifecycle   │
└────────────────────────┬─────────────────────────────┘
                         │ llama a métodos
┌────────────────────────▼─────────────────────────────┐
│                   Servicio (TS)                       │
│  ProductService: CRUD, retorna Observables            │
└────────────────────────┬─────────────────────────────┘
                         │ HTTP
┌────────────────────────▼─────────────────────────────┐
│              HttpClient (Angular)                     │
│  Proxy de desarrollo (/api -> localhost:8080)         │
└────────────────────────┬─────────────────────────────┘
                         │
                   Backend Spring Boot
```

### 7.2 Flujo de ejemplo: listar productos

1. El usuario navega a `/products`.
2. El Router activa `ProductListComponent` y lo renderiza en `<router-outlet>`.
3. `ngOnInit` llama a `loadProducts()`.
4. `loadProducts()` invoca `productService.getProducts(categoryFilter)`.
5. `ProductService` hace `GET /api/products` vía `HttpClient`.
6. El proxy redirige a `http://localhost:8080/api/products`.
7. La respuesta llega como `Observable<Product[]>`.
8. El componente se suscribe: en `next` asigna `products` y `loading=false`; en `error` asigna `error`.
9. El template reacciona a los cambios de `products`, `loading` y `error` mediante data binding.

### 7.3 Flujo de ejemplo: crear producto

1. El usuario va a `/products/new` → se activa `ProductFormComponent`.
2. `ngOnInit` no encuentra `id` en la ruta → `isEditMode = false`.
3. El usuario rellena el `FormGroup`; las validaciones de `Validators` se evalúan en tiempo real.
4. Al enviar (`submit()`), si `form.invalid` se muestra un mensaje.
5. Si es válido, se llama a `productService.createProduct(product)`.
6. En `next`, se resetea el formulario y se emite `created` (`@Output`).
7. El componente padre (`AppComponent`) escucha `(created)` y recarga la lista.

---

## 8. Configuración, build y testing

### 8.1 Scripts disponibles (`package.json`)

| Script   | Comando                                          | Descripción                          |
|----------|--------------------------------------------------|--------------------------------------|
| `start`  | `ng serve --proxy-config proxy.conf.json --host 0.0.0.0` | Servidor de desarrollo con proxy    |
| `build`  | `ng build --configuration production`            | Build de producción                  |
| `test`   | `ng test`                                        | Tests unitarios con Karma/Jasmine    |

### 8.2 Build de producción (`angular.json`)

Optimizaciones activadas en `configurations.production`:

- `optimization: true` — minificación y tree-shaking.
- `outputHashing: "all"` — hashes en nombres de archivo para *cache busting*.
- `sourceMap: false` — no genera source maps (menor tamaño).
- `aot: true` — *Ahead of Time* compilation (compila templates en build, no en runtime).
- `buildOptimizer: true` — optimizaciones adicionales de código.
- `vendorChunk: false` — no separa vendor en chunk propio.
- `fileReplacements` — reemplaza `environment.ts` por `environment.prod.ts`.
- `budgets` — alerta si el bundle inicial supera 2MB (warning) o 5MB (error).

### 8.3 Testing

**Archivos:**

- `frontend/karma.conf.js` — configura Karma con Jasmine, ChromeHeadless, coverage.
- `frontend/src/test.ts` — inicializa el entorno de testing de Angular (`getTestBed().initTestEnvironment(...)`).
- `frontend/tsconfig.spec.json` — incluye archivos `*.spec.ts`.

**Concepto clave:** `getTestBed` configura el compilador de testing (`BrowserDynamicTestingModule`) para que los componentes puedan instanciarse en los tests sin un navegador real, usando `ChromeHeadless` para ejecución en CI.

---

## 9. Glosario de conceptos clave

| Término                | Definición breve                                                              |
|------------------------|-------------------------------------------------------------------------------|
| **NgModule**           | Contenedor que agrupa componentes, directivas y pipes.                       |
| **Componente**         | Clase con `@Component` que controla una porción de la UI.                    |
| **Servicio**           | Clase con `@Injectable` que provee lógica reutilizable vía DI.               |
| **DI**                 | Inyección de Dependencias: Angular provee instancias automáticamente.       |
| **Observable**         | Stream de valores asíncronos (RxJS); se consume con `subscribe`.             |
| **Router**             | Sistema que mapea URLs a componentes y gestiona la navegación.               |
| **router-outlet**      | Directiva que marca dónde renderizar el componente de la ruta activa.        |
| **Reactive Forms**     | Formularios cuyo estado se gestiona en el componente con `FormGroup`.        |
| **Template-Driven**    | Formularios cuyo estado se gestiona en el template con `ngModel`.            |
| **Lifecycle Hook**     | Métodos que Angular invoca en momentos clave del ciclo de vida.              |
| **AOT**                | *Ahead of Time*: compila templates en build, mejorando rendimiento.         |
| **Tree-shaking**       | Eliminación de código no usado del bundle final.                             |
| **Pipe**               | Transforma datos en el template (ej. `currency`, `date`).                    |
| **Directiva estructural** | Modifica el DOM (`*ngIf`, `*ngFor`).                                       |
| **Encapsulation**      | Aislamiento de estilos CSS por componente.                                    |
| **Proxy**              | Redirección de peticiones en desarrollo para evitar CORS.                    |

---

## Resumen de patrones y archivos asociados

| Patrón                          | Archivos principales                                                                                       |
|---------------------------------|-----------------------------------------------------------------------------------------------------------|
| Module (NgModule)               | `app.module.ts`, `app-routing.module.ts`                                                                  |
| Component                      | `app.component.*`, `product-list/*`, `product-detail/*`, `product-form/*`                                |
| Service / DI                   | `product.service.ts` (proveedor), componentes (consumidores)                                              |
| Repository / Data Access       | `product.service.ts`                                                                                      |
| Observable / Reactive (RxJS)   | `product.service.ts`, `app.component.ts`, `product-list.component.ts`, `product-detail.component.ts`, `product-form.component.ts` |
| Model / Domain Model           | `product.model.ts`                                                                                        |
| Router / Navigation            | `app-routing.module.ts`, `app.component.html` (`router-outlet`), componentes con `Router`/`ActivatedRoute` |
| Reactive Forms                 | `product-form.component.ts`, `product-form.component.html`                                                |
| Template-Driven Forms          | `product-list.component.html` (`[(ngModel)]`)                                                             |
| EventEmitter (Output)          | `product-form.component.ts` (`@Output() created`)                                                        |
| Environment Configuration      | `environment.ts`, `environment.prod.ts`, `main.ts`, `angular.json`                                        |
| Proxy (dev)                    | `proxy.conf.json`, `package.json` (script `start`)                                                        |
| Lifecycle Hook (OnInit)        | `app.component.ts`, `product-list.component.ts`, `product-detail.component.ts`, `product-form.component.ts` |

---

*Documento generado a partir del análisis estático del código fuente del frontend en `/workspaces/tiendaya-catalogo/frontend`.*