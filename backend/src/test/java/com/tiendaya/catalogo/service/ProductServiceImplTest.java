package com.tiendaya.catalogo.service;

import com.tiendaya.catalogo.dto.ProductRequestDTO;
import com.tiendaya.catalogo.dto.ProductResponseDTO;
import com.tiendaya.catalogo.exception.ResourceNotFoundException;
import com.tiendaya.catalogo.model.Product;
import com.tiendaya.catalogo.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product sampleProduct;

    @BeforeEach
    void setUp() {
        sampleProduct = Product.builder()
                .id(1L)
                .name("Mouse Inalámbrico")
                .description("Mouse óptico con receptor USB")
                .category("Electrónica")
                .price(new BigDecimal("45900.00"))
                .stock(2)
                .active(true)
                .build();
    }

    @Test
    void getProductById_cuandoExiste_devuelveElProductoConLowStockCalculado() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));

        ProductResponseDTO result = productService.getProductById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Mouse Inalámbrico");
        assertThat(result.isLowStock()).isTrue();
    }

    @Test
    void getProductById_cuandoNoExiste_lanzaResourceNotFoundException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProductById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void createProduct_conActiveNulo_seCreaComoActivoPorDefecto() {
        ProductRequestDTO request = new ProductRequestDTO();
        request.setName("Producto Nuevo");
        request.setDescription("Descripción");
        request.setCategory("Hogar");
        request.setPrice(new BigDecimal("10000.00"));
        request.setStock(20);
        request.setActive(null);

        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product p = invocation.getArgument(0);
            p.setId(10L);
            return p;
        });

        ProductResponseDTO result = productService.createProduct(request);

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.isActive()).isTrue();
        assertThat(result.isLowStock()).isFalse();
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void deleteProduct_cuandoNoExiste_lanzaResourceNotFoundException_yNoLlamaDelete() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.deleteProduct(99L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(productRepository, never()).delete(any(Product.class));
    }

    @Test
    void updateActiveStatus_cambiaElFlagYPersiste() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProductResponseDTO result = productService.updateActiveStatus(1L, false);

        assertThat(result.isActive()).isFalse();
        verify(productRepository).save(sampleProduct);
    }

    @Test
    void getAllProducts_delegaEnElRepositorioConLosFiltrosNormalizados() {
        when(productRepository.search(any(), any(), any())).thenReturn(List.of(sampleProduct));

        List<ProductResponseDTO> result = productService.getAllProducts("  Electrónica  ", "", "price", "desc");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Mouse Inalámbrico");
        verify(productRepository, times(1)).search(any(), any(), any());
    }
}
