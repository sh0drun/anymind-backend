package com.anymind.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String customerId;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private BigDecimal finalPrice;

    @Column(nullable = false)
    private Integer points;

    @Column(nullable = false)
    private String paymentMethod;

    @Column(nullable = false)
    private Timestamp datetime;

    private String additionalItem;

}