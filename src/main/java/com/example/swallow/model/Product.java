package com.example.swallow.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "basDt", nullable = false)
    private String basDt;

    @Column(name = "srtnCd", nullable = false)
    private String srtnCd;

    @Column(name = "fndNm", nullable = false)
    private String fndNm;

    @Column(name = "ctg", nullable = false)
    private String ctg;

    @Column(name = "setpDt", nullable = false)
    private String setpDt;

    @Column(name = "fndTp", nullable = false)
    private String fndTp;

    @Column(name = "prdClsfCd", nullable = false)
    private String prdClsfCd;

    @Column(name = "asoStdCd", nullable = false)
    private String asoStdCd;

    @Column(name = "riskRating", nullable = false)
    private int riskRating;
}
