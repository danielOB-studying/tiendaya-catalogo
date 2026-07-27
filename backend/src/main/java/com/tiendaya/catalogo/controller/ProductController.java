package com.tiendaya.catalogo.controller;

import com.tiendaya.catalogo.dto.ActiveStatusDTO;
import com.tiendaya.catalogo.dto.ProductRequestDTO;
import com.tiendaya.catalogo.dto.ProductResponseDTO;
import com.tiendaya.catalogo.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

/**
 * API REST del módulo de catálogo de productos.
 * Base path: /api/products
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * Lista productos. Soporta filtros y orden opcionales vía query params:
     * GET /api/products
     * GET /api/products?category=Electrónica
     * GET /api/products?name=audifonos
     * GET /api/products?sortBy=price&sortDir=desc
     */
    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String name,
            @RequestParam(required = false, defaultValue = "name") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortDir) {
        return ResponseEntity.ok(productService.getAllProducts(category, name, sortBy, sortDir));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(@Valid @RequestBody ProductRequestDTO request) {
        ProductResponseDTO created = productService.createProduct(request);
        return ResponseEntity.created(URI.create("/api/products/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(@PathVariable Long id,
                                                              @Valid @RequestBody ProductRequestDTO request) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Extra: activar/desactivar un producto sin editarlo completo.
     * PATCH /api/products/{id}/active   body: { "active": true }
     */
    @PatchMapping("/{id}/active")
    public ResponseEntity<ProductResponseDTO> updateActiveStatus(@PathVariable Long id,
                                                                   @Valid @RequestBody ActiveStatusDTO body) {
        return ResponseEntity.ok(productService.updateActiveStatus(id, body.getActive()));
    }
}
