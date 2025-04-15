package com.anymind.model.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
public class PayInput {

    @NotNull(message = "Customer ID is required")
    @NotBlank(message = "Customer ID is required")
    private String customerId;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;

    @NotNull(message = "paymentMethod is required")
    @NotBlank(message = "paymentMethod is required")
    private String paymentMethod;

    private Map<String, String> additionalItems;

}
