import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
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

  constructor(private productService: ProductService, private router: Router) {}

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
      }
    });
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
      next: () => this.loadProducts(),
      error: () => {
        this.error = 'No se pudo eliminar el producto.';
      }
    });
  }
}
