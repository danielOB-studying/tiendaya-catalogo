package com.tiendaya.catalogo.service;

import com.tiendaya.catalogo.dto.ProductRequestDTO;
import com.tiendaya.catalogo.dto.ProductResponseDTO;
import com.tiendaya.catalogo.exception.ResourceNotFoundException;
import com.tiendaya.catalogo.model.Product;
import com.tiendaya.catalogo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    /** Umbral de stock bajo, según los requisitos funcionales (< 5 unidades). */
    private static final int LOW_STOCK_THRESHOLD = 5;

    private static final Set<String> SORTABLE_FIELDS = Set.of("name", "category", "price", "stock", "id");

    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> getAllProducts(String category, String name, String sortBy, String sortDir) {
        String normalizedCategory = StringUtils.hasText(category) ? category.trim() : null;
        String normalizedName = StringUtils.hasText(name) ? name.trim() : null;

        String field = SORTABLE_FIELDS.contains(sortBy) ? sortBy : "name";
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, field);

        return productRepository.search(normalizedCategory, normalizedName, sort)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponseDTO getProductById(Long id) {
        return toResponseDTO(findEntityOrThrow(id));
    }

    @Override
    public ProductResponseDTO createProduct(ProductRequestDTO request) {
        Product product = Product.builder()
                .name(request.getName().trim())
                .description(request.getDescription() != null ? request.getDescription().trim() : null)
                .category(request.getCategory().trim())
                .price(request.getPrice())
                .stock(request.getStock())
                .active(request.getActive() == null || request.getActive())
                .build();

        return toResponseDTO(productRepository.save(product));
    }

    @Override
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO request) {
        Product product = findEntityOrThrow(id);

        product.setName(request.getName().trim());
        product.setDescription(request.getDescription() != null ? request.getDescription().trim() : null);
        product.setCategory(request.getCategory().trim());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        if (request.getActive() != null) {
            product.setActive(request.getActive());
        }

        return toResponseDTO(productRepository.save(product));
    }

    @Override
    public void deleteProduct(Long id) {
        Product product = findEntityOrThrow(id);
        productRepository.delete(product);
    }

    @Override
    public ProductResponseDTO updateActiveStatus(Long id, boolean active) {
        Product product = findEntityOrThrow(id);
        product.setActive(active);
        return toResponseDTO(productRepository.save(product));
    }

    // ---------------------------------------------------------------
    // Helpers privados
    // ---------------------------------------------------------------

    private Product findEntityOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el producto con id " + id));
    }

    private ProductResponseDTO toResponseDTO(Product product) {
        return ProductResponseDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .category(product.getCategory())
                .price(product.getPrice())
                .stock(product.getStock())
                .active(product.isActive())
                .lowStock(product.getStock() != null && product.getStock() < LOW_STOCK_THRESHOLD)
                .build();
    }
}
