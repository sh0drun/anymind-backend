package com.anymind.service;

import com.anymind.exception.PaymentProcessingException;
import com.anymind.exception.PaymentValidationException;
import com.anymind.model.dto.PayInput;
import com.anymind.model.dto.PayResult;
import com.anymind.model.entity.Payment;
import com.anymind.model.entity.PaymentMethod;
import com.anymind.repository.PaymentMethodRepository;
import com.anymind.repository.PaymentRepository;
import com.anymind.service.strategy.PriceModifierStrategy;
import com.anymind.util.ValidationUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Service
public class PaymentService {

    private final PaymentMethodRepository paymentMethodRepository;
    private final PaymentRepository paymentRepository;
    private final PriceModifierStrategy priceModifierStrategy;
    private final ObjectMapper objectMapper;
    private final Validator validator;

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    public PaymentService(PaymentMethodRepository paymentMethodRepository,
                          PaymentRepository paymentRepository,
                          PriceModifierStrategy priceModifierStrategy,
                          Validator validator) {
        this.paymentMethodRepository = paymentMethodRepository;
        this.paymentRepository = paymentRepository;
        this.priceModifierStrategy = priceModifierStrategy;
        this.objectMapper = new ObjectMapper();
        this.validator = validator;
    }

    @Transactional
    public PayResult pay(PayInput input) {
        ValidationUtil.validateAndThrow(input, validator, PaymentValidationException::new);

        log.info("Processing payment for customer: {}", input.getCustomerId());

        if (input.getPrice() == null || input.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new PaymentValidationException("Price must be greater than zero");
        }

        if (input.getCustomerId() == null || input.getCustomerId().isBlank()) {
            throw new PaymentValidationException("Customer ID must not be blank");
        }

        PaymentMethod paymentMethod = paymentMethodRepository.findByPaymentMethod(input.getPaymentMethod())
                .orElseThrow(() -> new PaymentValidationException("Invalid payment method: " + input.getPaymentMethod()));

        BigDecimal finalPrice = priceModifierStrategy.applyModifier(input.getPrice(), paymentMethod);

        try {
            List<String> requiredFields = objectMapper.readValue(paymentMethod.getAdditionalRequiredFields(), new TypeReference<List<String>>() {});
            if (requiredFields != null && !requiredFields.isEmpty()) {
                Map<String, String> additionalItems = input.getAdditionalItems();

                for (String field : requiredFields) {
                    if (additionalItems == null || !additionalItems.containsKey(field)) {
                        throw new PaymentValidationException("Missing required field: " + field);
                    }
                }
            }
        } catch (JsonProcessingException e) {
            throw new PaymentProcessingException("Failed to parse required fields JSON", e);
        }

        float pointRate = paymentMethod.getPointRate();
        BigDecimal pointsDecimal = finalPrice.multiply(BigDecimal.valueOf(pointRate));
        int points = pointsDecimal.setScale(0, RoundingMode.DOWN).intValue();

        Payment payment = new Payment();
        payment.setCustomerId(input.getCustomerId());
        payment.setPrice(input.getPrice());
        payment.setFinalPrice(finalPrice);
        payment.setPoints(points);
        payment.setPaymentMethod(paymentMethod);
        payment.setDatetime(new Timestamp(System.currentTimeMillis()));

        if (input.getAdditionalItems() != null && !input.getAdditionalItems().isEmpty()) {
            try {
                payment.setAdditionalItem(objectMapper.writeValueAsString(input.getAdditionalItems()));
            } catch (JsonProcessingException e) {
                throw new PaymentProcessingException("Failed to serialize additional items", e);
            }
        }

        payment = paymentRepository.save(payment);

        PayResult result = new PayResult();
        result.setPaymentId(payment.getId());
        result.setFinalPrice(payment.getFinalPrice());
        result.setPoints(payment.getPoints());

        log.info("Payment created successfully: paymentId={}", payment.getId());

        return result;
    }
}
