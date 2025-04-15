package com.anymind.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SalesByHour {

    private String datetime;
    private BigDecimal sales;
    private int points;

}
