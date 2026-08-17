import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Product } from '../product.model';
import { ProductService } from '../product.service';

@Component({
  selector: 'app-product-list',
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.css']
})
export class ProductListComponent implements OnInit {
  products: Product[] = [];
  loading = true;
  error = '';
  categoryFilter = '';

  displayedColumns: string[] = ['name', 'category', 'price', 'stock', 'lowStock', 'active', 'actions'];

  constructor(
    private productService: ProductService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadProducts();
  }

  loadProducts(): void {
    this.loading = true;
    this.error = '';
    this.productService.getProducts(this.categoryFilter).subscribe({
      next: data => {
        this.products = data;
        this.loading = false;
      },
      error: () => {
        this.error = 'No se pudieron cargar los productos';
        this.loading = false;
        this.showError(this.error);
      }
    });
  }

  applyFilter(): void {
    this.loadProducts();
  }

  clearFilter(): void {
    this.categoryFilter = '';
    this.loadProducts();
  }

  goToNew(): void {
    this.router.navigate(['/products/new']);
  }

  goToDetail(product: Product): void {
    this.router.navigate(['/products', product.id]);
  }

  goToEdit(product: Product): void {
    this.router.navigate(['/products', product.id, 'edit']);
  }

  deleteProduct(product: Product): void {
    const confirmed = window.confirm(`¿Eliminar el producto "${product.name}"? Esta acción no se puede deshacer.`);
    if (!confirmed || !product.id) {
      return;
    }

    this.productService.deleteProduct(product.id).subscribe({
      next: () => {
        this.showMessage('Producto eliminado correctamente.');
        this.loadProducts();
      },
      error: () => {
        const msg = 'No se pudo eliminar el producto.';
        this.error = msg;
        this.showError(msg);
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