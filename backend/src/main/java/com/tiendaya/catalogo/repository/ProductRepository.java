package com.tiendaya.catalogo.repository;

import com.tiendaya.catalogo.model.Product;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Busca productos combinando filtro opcional por categoría y por nombre.
     */
    @Query("SELECT p FROM Product p " +
           "WHERE (:category IS NULL OR LOWER(p.category) = LOWER(:category)) " +
           "AND (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))")
    List<Product> search(@Param("category") String category,
                          @Param("name") String name,
                          Sort sort);
}
