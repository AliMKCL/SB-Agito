package com.staj.stock.repository;

import com.staj.stock.entity.StockSale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockSaleRepository extends JpaRepository<StockSale, String> {
}
