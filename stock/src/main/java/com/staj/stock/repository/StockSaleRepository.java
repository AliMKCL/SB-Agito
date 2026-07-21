package com.staj.stock.repository;

import com.staj.stock.entity.StockSale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StockSaleRepository extends JpaRepository<StockSale, Integer> {
    List<StockSale> findByCode(String code);
}
