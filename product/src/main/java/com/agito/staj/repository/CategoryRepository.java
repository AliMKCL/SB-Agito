package com.agito.staj.repository;

import com.agito.staj.entity.Category;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    @EntityGraph(attributePaths = {"translations", "parent"})
    @Query("SELECT DISTINCT c FROM Category c JOIN c.translations t WHERE LOWER(t.name) = LOWER(:name)")
    Optional<Category> findByName(@Param("name") String name);

    @EntityGraph(attributePaths = {"translations", "parent"})
    @Query("SELECT DISTINCT c FROM Category c JOIN c.translations t WHERE LOWER(t.name) = LOWER(:name) AND t.languageCode = :lang")
    Optional<Category> findByNameAndLanguage(@Param("name") String name, @Param("lang") String lang);

    @Override
    @EntityGraph(attributePaths = {"translations", "parent"})
    Optional<Category> findById(Integer id);

    @Override
    @EntityGraph(attributePaths = {"translations", "parent"})
    List<Category> findAll();
}
