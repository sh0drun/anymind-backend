package com.anymind.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "payment_methods")
public class PaymentMethod {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private String paymentMethod;

    @Column(nullable = false)
    private Float minModifier;

    @Column(nullable = false)
    private Float maxModifier;

    @Column(nullable = false)
    private Float pointRate;

    private String additionalRequiredFields;
}

