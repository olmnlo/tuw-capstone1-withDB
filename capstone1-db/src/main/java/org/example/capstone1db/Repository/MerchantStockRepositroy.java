package org.example.capstone1db.Repository;

import org.example.capstone1db.Model.MerchantStock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MerchantStockRepositroy extends JpaRepository<MerchantStock, Integer> {
}
