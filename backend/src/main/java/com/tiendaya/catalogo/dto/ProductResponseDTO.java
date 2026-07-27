package com.tiendaya.catalogo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO usado para exponer un producto en las respuestas de la API.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponseDTO {

    private Long id;
    private String name;
    private String description;
    private String category;
    private BigDecimal price;
    private Integer stock;
    private boolean active;

    /**
     * Calculado en el servidor: true si el stock es menor a 5 unidades.
     */
    private boolean lowStock;
}
