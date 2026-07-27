package com.tiendaya.catalogo.service;

import com.tiendaya.catalogo.dto.ProductRequestDTO;
import com.tiendaya.catalogo.dto.ProductResponseDTO;

import java.util.List;

public interface ProductService {

    List<ProductResponseDTO> getAllProducts(String category, String name, String sortBy, String sortDir);

    ProductResponseDTO getProductById(Long id);

    ProductResponseDTO createProduct(ProductRequestDTO request);

    ProductResponseDTO updateProduct(Long id, ProductRequestDTO request);

    void deleteProduct(Long id);

    ProductResponseDTO updateActiveStatus(Long id, boolean active);
}
