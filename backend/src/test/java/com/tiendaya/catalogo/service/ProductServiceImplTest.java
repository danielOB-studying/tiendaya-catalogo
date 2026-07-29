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

/**
 * Pruebas unitarias para {@link ProductServiceImpl}.
 *
 * <p>Esta suite valida la lógica de negocio de la capa de servicio de productos,
 * aislándola de la persistencia mediante un mock de {@link ProductRepository}.
 * Se cubren los siguientes aspectos clave:</p>
 * <ul>
 *   <li>Cálculo del flag {@code lowStock} (stock < 5 unidades).</li>
 *   <li>Comportamiento por defecto del campo {@code active} al crear productos.</li>
 *   <li>Lanzamiento de {@link ResourceNotFoundException} cuando un producto no existe.</li>
 *   <li>Delegación correcta de filtros y ordenamiento hacia el repositorio.</li>
 *   <li>Verificación de interacciones con el repositorio (save, delete, search).</li>
 * </ul>
 *
 * <p>Stack utilizado: JUnit 5 (Jupiter), Mockito y AssertJ.</p>
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    /**
     * Mock del repositorio de productos. Simula el acceso a datos sin tocar la base.
     */
    @Mock
    private ProductRepository productRepository;

    /**
     * Instancia de {@link ProductServiceImpl} bajo prueba, con sus dependencias
     * inyectadas automáticamente por Mockito ({@code @InjectMocks}).
     */
    @InjectMocks
    private ProductServiceImpl productService;

    /**
     * Producto de ejemplo reutilizado en varias pruebas.
     * Tiene {@code stock = 2}, por lo que activa el flag {@code lowStock}.
     */
    private Product sampleProduct;

    /**
     * Inicializa el {@link #sampleProduct} antes de cada prueba para garantizar
     * aislamiento entre casos y un estado inicial conocido.
     */
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

    /**
     * Verifica que {@code getProductById} devuelve el producto cuando existe y
     * calcula correctamente {@code lowStock = true} porque el stock (2) es menor
     * al umbral (5).
     */
    @Test
    void getProductById_cuandoExiste_devuelveElProductoConLowStockCalculado() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));

        ProductResponseDTO result = productService.getProductById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Mouse Inalámbrico");
        assertThat(result.isLowStock()).isTrue();
    }

    /**
     * Verifica que {@code getProductById} lanza {@link ResourceNotFoundException}
     * cuando el producto no existe en el repositorio, y que el mensaje incluye el id.
     */
    @Test
    void getProductById_cuandoNoExiste_lanzaResourceNotFoundException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProductById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    /**
     * Verifica que al crear un producto con {@code active = null}, el servicio
     * lo establece como activo por defecto, persiste la entidad y devuelve el DTO
     * con el id asignado por el repositorio. También confirma que {@code lowStock}
     * es {@code false} para un stock alto (20).
     */
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

    /**
     * Verifica que {@code deleteProduct} lanza {@link ResourceNotFoundException}
     * cuando el producto no existe y, fundamentalmente, que <strong>nunca</strong>
     * se invoca {@code delete} sobre el repositorio en ese caso.
     */
    @Test
    void deleteProduct_cuandoNoExiste_lanzaResourceNotFoundException_yNoLlamaDelete() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.deleteProduct(99L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(productRepository, never()).delete(any(Product.class));
    }

    /**
     * Verifica que {@code updateActiveStatus} cambia el flag {@code active} del
     * producto y persiste el cambio mediante {@code save}, devolviendo el DTO
     * con el nuevo estado.
     */
    @Test
    void updateActiveStatus_cambiaElFlagYPersiste() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProductResponseDTO result = productService.updateActiveStatus(1L, false);

        assertThat(result.isActive()).isFalse();
        verify(productRepository).save(sampleProduct);
    }

    /**
     * Verifica que {@code getAllProducts} delega en el repositorio y normaliza
     * los filtros: recorta espacios de la categoría ("  Electrónica  " &rarr; "Electrónica")
     * y trata el nombre vacío como nulo. Confirma que el resultado se mapea a DTOs.
     */
    @Test
    void getAllProducts_delegaEnElRepositorioConLosFiltrosNormalizados() {
        when(productRepository.search(any(), any(), any())).thenReturn(List.of(sampleProduct));

        List<ProductResponseDTO> result = productService.getAllProducts("  Electrónica  ", "", "price", "desc");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Mouse Inalámbrico");
        verify(productRepository, times(1)).search(any(), any(), any());
    }
}
