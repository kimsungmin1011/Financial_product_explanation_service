package com.example.swallow.controller;

import com.example.swallow.configuration.RateLimitConfig;
import com.example.swallow.model.Product;
import com.example.swallow.repository.ProductRepository;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RateLimitConfig rateLimitConfig; // RateLimitConfig 클래스의 인스턴스를 주입받습니다.

    private ResponseEntity<String> rateLimitExceeded() {
        // 요청 한도를 초과했을 때 반환할 HTTP 상태와 메시지를 설정합니다.
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body("Rate limit exceeded. Please try again later.");
    }

    private boolean isLimitExceeded(HttpServletRequest request) {
        // 각 요청에 대해 요청 한도가 초과되었는지 확인합니다.
        Bucket bucket = rateLimitConfig.resolveBucket(request);
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        return !probe.isConsumed();
    }

    @GetMapping
    public ResponseEntity<?> getAllProducts(HttpServletRequest request) {
        // 요청 한도에 도달하면 메시지 반환
        if (isLimitExceeded(request)) {
            return rateLimitExceeded();
        }
        List<Product> products = productRepository.findAll();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id, HttpServletRequest request) {
        if (isLimitExceeded(request)) {
            return rateLimitExceeded();
        }
        return productRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchProducts(
            HttpServletRequest request,
            @RequestParam(required = false) String fndNm,
            @RequestParam(required = false) String ctg,
            @RequestParam(required = false) String fndTp,
            @RequestParam(required = false) String basDt,
            @RequestParam(required = false) Integer riskRating) {

        if (isLimitExceeded(request)) {
            return rateLimitExceeded();
        }

        List<Product> products;
        if (fndNm != null) {
            products = productRepository.findByFndNmContaining(fndNm);
        } else if (ctg != null) {
            products = productRepository.findByCtg(ctg);
        } else if (fndTp != null) {
            products = productRepository.findByFndTp(fndTp);
        } else if (basDt != null) {
            products = productRepository.findByBasDt(basDt);
        } else if (riskRating != null) {
            products = productRepository.findByRiskRating(riskRating);
        } else {
            products = productRepository.findAll();
        }
        return ResponseEntity.ok(products);
    }

    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody Product product, HttpServletRequest request) {
        if (isLimitExceeded(request)) {
            return rateLimitExceeded();
        }
        Product createdProduct = productRepository.save(product);
        return ResponseEntity.ok(createdProduct);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody Product productDetails, HttpServletRequest request) {
        if (isLimitExceeded(request)) {
            return rateLimitExceeded();
        }

        return productRepository.findById(id).map(product -> {
            product.setBasDt(productDetails.getBasDt());
            product.setSrtnCd(productDetails.getSrtnCd());
            product.setFndNm(productDetails.getFndNm());
            product.setCtg(productDetails.getCtg());
            product.setSetpDt(productDetails.getSetpDt());
            product.setFndTp(productDetails.getFndTp());
            product.setPrdClsfCd(productDetails.getPrdClsfCd());
            product.setAsoStdCd(productDetails.getAsoStdCd());
            product.setRiskRating(productDetails.getRiskRating());
            Product updatedProduct = productRepository.save(product);
            return ResponseEntity.ok(updatedProduct);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id, HttpServletRequest request) {
        if (isLimitExceeded(request)) {
            return rateLimitExceeded();
        }
        productRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
