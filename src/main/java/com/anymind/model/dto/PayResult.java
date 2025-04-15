package com.anymind.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class PayResult {

    private UUID paymentId;
    private BigDecimal finalPrice;
    private Integer points;

}
