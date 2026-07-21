package com.staj.stock.repository;

import com.staj.stock.entity.StockEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StockEntryRepository extends JpaRepository<StockEntry, Integer> {
    List<StockEntry> findByCode(String code);
}
