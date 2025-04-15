package com.anymind.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
public class PayInput {

    private String customerId;
    private BigDecimal price;
    private String paymentMethod;
    private Map<String, String> additionalItems;

}
