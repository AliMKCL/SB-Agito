package com.agito.staj.repository;

import com.agito.staj.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    Optional<Product> findByName(String name);

    Optional<Product> findByCode(String code);

    //@Query
    // Criteria query:
    // producrteporisotyCustom ve İmplementation
    // Custom --> Queryler içn
    // Implementation--> ımpllar için
    // Filter board gibi, category, name... ON yaptığında seçtiğin filterlar aktif oluyor v

    // Stock girişi (takibi) için ayrı bir yer
}
