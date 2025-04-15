package com.anymind.service.strategy;

import com.anymind.model.entity.PaymentMethod;
import java.math.BigDecimal;

public interface PriceModifierStrategy {
    BigDecimal applyModifier(BigDecimal price, PaymentMethod paymentMethod);
}