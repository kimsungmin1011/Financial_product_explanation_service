package com.example.swallow.repository;

import com.example.swallow.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByFndNmContaining(String fndNm);
    List<Product> findByCtg(String ctg);
    List<Product> findByFndTp(String fndTp);
    List<Product> findByBasDt(String basDt);
    List<Product> findByRiskRating(Integer riskRating);
}
