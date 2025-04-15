package com.anymind.service.strategy;

import com.anymind.model.entity.PaymentMethod;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class RandomPriceModifierStrategy implements PriceModifierStrategy {

    @Override
    public BigDecimal applyModifier(BigDecimal price, PaymentMethod paymentMethod) {
        float min = paymentMethod.getMinModifier();
        float max = paymentMethod.getMaxModifier();
        float randomModifier = min + (float) (Math.random() * (max - min));
        BigDecimal finalPrice = price.multiply(BigDecimal.valueOf(randomModifier));
        return finalPrice.setScale(2, RoundingMode.HALF_UP);
    }
}
