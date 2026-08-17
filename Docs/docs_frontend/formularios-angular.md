# Formularios Angular en el Frontend: Reactive Forms vs Template-driven

## Resumen

Los formularios Angular del frontend son de tipo **Reactivo (Reactive Forms)**.

El formulario de producto (`ProductFormComponent`) utiliza el enfoque **Reactive Forms** de Angular. A continuación, se detallan las evidencias encontradas en los archivos del proyecto.

---

## Evidencias por archivo

### 1. `frontend/src/app/product-form/product-form.component.ts`

- Importa `FormBuilder` y `Validators` desde `@angular/forms` (API de Reactive Forms).
- El formulario se construye con `this.fb.group({...})`, creando un `FormGroup` tipado.
- Usa validadores síncronos como `Validators.required`, `Validators.min(0.01)`, `Validators.min(0)`.
- Manipula el formulario mediante API reactiva: `this.form.patchValue(product)`, `this.form.invalid`, `this.form.value`, `this.form.reset({...})`.

```typescript
import { FormBuilder, Validators } from '@angular/forms';

// ...

form = this.fb.group({
  name: ['', Validators.required],
  description: ['', Validators.required],
  category: ['', Validators.required],
  price: [0, [Validators.required, Validators.min(0.01)]],
  stock: [0, [Validators.required, Validators.min(0)]],
  active: [true]
});

constructor(
  private fb: FormBuilder,
  // ...
) {}
```

### 2. `frontend/src/app/product-form/product-form.component.html`

- Usa la directiva `[formGroup]="form"` para enlazar el formulario del componente.
- Cada campo usa `formControlName="..."` (directiva reactiva), no `[(ngModel)]` (que sería Template-driven).
- Las validaciones se consultan con `form.get('name')?.invalid && form.get('name')?.touched`.

```html
<form [formGroup]="form" (ngSubmit)="submit()">
  <input formControlName="name" />
  <div class="error" *ngIf="form.get('name')?.invalid && form.get('name')?.touched">
    El nombre es obligatorio.
  </div>
  <!-- ... -->
</form>
```

### 3. `frontend/src/app/app.module.ts`

- Importa **ambos** módulos: `FormsModule` y `ReactiveFormsModule`.
- Sin embargo, `FormsModule` (necesario para Template-driven con `ngModel`) **no se utiliza** en el formulario de producto; está presente pero el formulario emplea exclusivamente `ReactiveFormsModule`.

```typescript
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

// ...

imports: [BrowserModule, HttpClientModule, FormsModule, ReactiveFormsModule, AppRoutingModule]
```

---

## Tabla resumen

| Aspecto | Enfoque usado |
|---|---|
| Tipo de formulario | **Reactive Forms** |
| Construcción | `FormBuilder.group()` |
| Enlace en plantilla | `[formGroup]` + `formControlName` |
| Validación | `Validators` en TS, consultada con `form.get()` |
| Template-driven (`ngModel`) | No usado en el formulario |

---

## Conclusión

El formulario de producto implementa el patrón **Reactive Forms** de Angular. Este enfoque es una buena elección para este caso, ya que ofrece:

- **Tipado más fuerte** y estructura explícita del modelo de formulario.
- **Validación centralizada** en el componente TypeScript.
- **Facilidad para pruebas unitarias**, al poder instanciar el `FormGroup` sin necesidad del DOM.
- **Manejo de estado predecible** mediante observables y métodos como `patchValue` y `reset`.

> **Nota:** Aunque `FormsModule` está importado en `AppModule`, no se usa en el formulario de producto. Podría eliminarse si no se emplea en ningún otro componente del proyecto.