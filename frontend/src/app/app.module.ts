import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { AppComponent } from './app.component';
import { ProductFormComponent } from './product-form/product-form.component';
import { ProductListComponent } from './product-list/product-list.component';
import { ProductDetailComponent } from './product-detail/product-detail.component';
import { AppRoutingModule } from './app-routing.module';

@NgModule({
  declarations: [AppComponent, ProductFormComponent, ProductListComponent, ProductDetailComponent],
  imports: [BrowserModule, HttpClientModule, FormsModule, ReactiveFormsModule, AppRoutingModule],
  bootstrap: [AppComponent]
})
export class AppModule {}
