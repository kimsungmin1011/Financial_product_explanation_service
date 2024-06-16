package com.example.swallow.controller;

import com.example.swallow.service.FinancialTermService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/financial-term")
public class FinancialTermController {

    @Autowired
    private FinancialTermService financialTermService;

    @GetMapping("/search")
    public ResponseEntity<String> getFinancialTermMeaning(@RequestParam String term) {
        try {
            if (term == null || term.isEmpty()) {
                return ResponseEntity.badRequest().body("The 'term' parameter is required.");
            }
            String result = financialTermService.getFinancialTermMeaning(term);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}
