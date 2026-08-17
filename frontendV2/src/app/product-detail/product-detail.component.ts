import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Product } from '../product.model';
import { ProductService } from '../product.service';

@Component({
  selector: 'app-product-detail',
  templateUrl: './product-detail.component.html',
  styleUrls: ['./product-detail.component.css']
})
export class ProductDetailComponent implements OnInit {
  product: Product | null = null;
  loading = true;
  error = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private productService: ProductService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (!id) {
      this.error = 'ID de producto inválido';
      this.loading = false;
      return;
    }

    this.productService.getProduct(id).subscribe({
      next: data => {
        this.product = data;
        this.loading = false;
      },
      error: () => {
        this.error = 'No se encontró el producto';
        this.loading = false;
        this.showError(this.error);
      }
    });
  }

  back(): void {
    this.router.navigate(['/products']);
  }

  edit(): void {
    if (this.product?.id) {
      this.router.navigate(['/products', this.product.id, 'edit']);
    }
  }

  delete(): void {
    if (!this.product?.id) {
      return;
    }

    const confirmed = window.confirm(`¿Eliminar el producto "${this.product.name}"? Esta acción no se puede deshacer.`);
    if (!confirmed) {
      return;
    }

    this.productService.deleteProduct(this.product.id).subscribe({
      next: () => {
        this.showMessage('Producto eliminado correctamente.');
        this.router.navigate(['/products']);
      },
      error: () => {
        this.error = 'No se pudo eliminar el producto.';
        this.showError(this.error);
      }
    });
  }

  private showMessage(message: string): void {
    this.snackBar.open(message, 'Cerrar', { duration: 3000 });
  }

  private showError(message: string): void {
    this.snackBar.open(message, 'Cerrar', { duration: 5000, panelClass: ['error-snackbar'] });
  }
}