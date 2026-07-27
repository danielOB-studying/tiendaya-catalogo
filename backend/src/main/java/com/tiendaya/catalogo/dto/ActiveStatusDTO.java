package com.tiendaya.catalogo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO usado por el endpoint PATCH /api/products/{id}/active
 * para activar o desactivar un producto sin editarlo completo.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActiveStatusDTO {

    @NotNull(message = "El campo 'active' es obligatorio")
    private Boolean active;
}
