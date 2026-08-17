import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ProductService } from '../product.service';
import { Product } from '../product.model';

@Component({
  selector: 'app-product-form',
  templateUrl: './product-form.component.html',
  styleUrls: ['./product-form.component.css']
})
export class ProductFormComponent implements OnInit {
  @Output() created = new EventEmitter<void>();

  productId: number | null = null;

  form = this.fb.group({
    name: ['', Validators.required],
    description: ['', Validators.required],
    category: ['', Validators.required],
    price: [0, [Validators.required, Validators.min(0.01)]],
    stock: [0, [Validators.required, Validators.min(0)]],
    active: [true]
  });

  saving = false;
  message = '';

  constructor(
    private fb: FormBuilder,
    private productService: ProductService,
    private route: ActivatedRoute,
    private router: Router,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.productId = Number(idParam);
      this.loadProduct(this.productId);
    }
  }

  get isEditMode(): boolean {
    return this.productId !== null;
  }

  goBack(): void {
    this.router.navigate(['/products']);
  }

  private loadProduct(id: number): void {
    this.productService.getProduct(id).subscribe({
      next: product => {
        this.form.patchValue(product);
      },
      error: () => {
        this.message = 'No se pudo cargar el producto para edición.';
        this.showError(this.message);
      }
    });
  }

  submit(): void {
    if (this.form.invalid) {
      this.message = 'Completa todos los campos correctamente.';
      this.showError(this.message);
      return;
    }

    this.saving = true;
    this.message = '';

    const product = this.form.value as unknown as Product;

    if (this.isEditMode && this.productId) {
      this.productService.updateProduct(this.productId, product).subscribe({
        next: () => {
          this.saving = false;
          this.message = 'Producto actualizado correctamente.';
          this.showMessage(this.message);
          this.router.navigate(['/products', this.productId]);
        },
        error: err => {
          this.saving = false;
          this.message = 'No se pudo actualizar el producto.';
          this.showError(this.message);
          console.error(err);
        }
      });
    } else {
      this.productService.createProduct(product).subscribe({
        next: () => {
          this.saving = false;
          this.message = 'Producto creado correctamente.';
          this.showMessage(this.message);
          this.form.reset({ active: true, price: 0, stock: 0 });
          this.created.emit();
        },
        error: err => {
          this.saving = false;
          this.message = 'No se pudo crear el producto.';
          this.showError(this.message);
          console.error(err);
        }
      });
    }
  }

  private showMessage(message: string): void {
    this.snackBar.open(message, 'Cerrar', { duration: 3000 });
  }

  private showError(message: string): void {
    this.snackBar.open(message, 'Cerrar', { duration: 5000, panelClass: ['error-snackbar'] });
  }
}