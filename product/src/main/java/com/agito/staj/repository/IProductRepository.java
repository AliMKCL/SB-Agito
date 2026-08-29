package com.agito.staj.repository;

import com.agito.staj.entity.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IProductRepository extends JpaRepository<Product, Integer> {

    @EntityGraph(attributePaths = {"translations", "category"})
    @Query("SELECT DISTINCT p FROM Product p JOIN p.translations t WHERE LOWER(t.name) = LOWER(:name)")
    Optional<Product> findByName(@Param("name") String name);

    @EntityGraph(attributePaths = {"translations", "category"})
    @Query("SELECT DISTINCT p FROM Product p JOIN p.translations t WHERE LOWER(t.name) = LOWER(:name) AND t.languageCode = :lang")
    Optional<Product> findByNameAndLanguage(@Param("name") String name, @Param("lang") String lang);

    @EntityGraph(attributePaths = {"translations", "category"})
    Optional<Product> findByCode(String code);

    @Override
    @EntityGraph(attributePaths = {"translations", "category"})
    List<Product> findAll();
}
