import { Component, OnInit } from '@angular/core';
import { ProductService } from './product.service';
import { Product } from './product.model';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  products: Product[] = [];
  loading = true;
  error = '';

  constructor(private productService: ProductService) {}

  ngOnInit(): void {
    this.loadProducts();
  }

  loadProducts(): void {
    this.loading = true;
    this.productService.getProducts().subscribe({
      next: data => {
        this.products = data;
        this.loading = false;
      },
      error: err => {
        this.error = 'No se pudieron cargar los productos';
        console.error(err);
        this.loading = false;
      }
    });
  }

  onProductCreated(): void {
    this.loadProducts();
  }
}
