package com.anymind.service;

import com.anymind.exception.PaymentValidationException;
import com.anymind.model.dto.PayInput;
import com.anymind.model.dto.PayResult;
import com.anymind.model.entity.Payment;
import com.anymind.model.entity.PaymentMethod;
import com.anymind.repository.PaymentMethodRepository;
import com.anymind.repository.PaymentRepository;
import com.anymind.service.strategy.PriceModifierStrategy;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private PaymentMethodRepository paymentMethodRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PriceModifierStrategy priceModifierStrategy;

    @Mock
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void pay_shouldSucceed_whenValidInput() throws Exception {
        PaymentMethod visaMethod = new PaymentMethod();
        visaMethod.setPaymentMethod("VISA");
        visaMethod.setMinModifier(0.9f);
        visaMethod.setMaxModifier(1.1f);
        visaMethod.setPointRate(0.02f);
        visaMethod.setAdditionalRequiredFields("[\"last4\"]");

        when(paymentMethodRepository.findByPaymentMethod("VISA"))
                .thenReturn(Optional.of(visaMethod));

        when(priceModifierStrategy.applyModifier(any(), eq(visaMethod)))
                .thenReturn(BigDecimal.valueOf(97.98));

        Map<String, String> additionalItems = new HashMap<>();
        additionalItems.put("last4", "1234");

        PayInput input = new PayInput();
        input.setCustomerId("customer123");
        input.setPrice(BigDecimal.valueOf(100));
        input.setPaymentMethod("VISA");
        input.setAdditionalItems(additionalItems);

        when(objectMapper.writeValueAsString(any()))
                .thenReturn("{\"last4\":\"1234\"}");

        PaymentMethod visa = new PaymentMethod();
        visa.setPaymentMethod("VISA");
        visa.setMinModifier(0.95f);
        visa.setMaxModifier(1.0f);
        visa.setPointRate(0.03f);
        visa.setAdditionalRequiredFields("[\"last4\"]");

        Payment savedPayment = new Payment();
        savedPayment.setId(UUID.randomUUID());
        savedPayment.setFinalPrice(BigDecimal.valueOf(97.98));
        savedPayment.setPoints(2);
        savedPayment.setPaymentMethod(visa);
        savedPayment.setCustomerId("customer123");

        when(paymentRepository.save(any())).thenReturn(savedPayment);

        PayResult result = paymentService.pay(input);

        assertNotNull(result.getPaymentId());
        assertEquals(BigDecimal.valueOf(97.98).setScale(2), result.getFinalPrice());
        assertEquals(2, result.getPoints());
    }

    @Test
    void pay_shouldFail_whenInvalidPaymentMethod() {
        PayInput input = new PayInput();
        input.setCustomerId("x");
        input.setPrice(BigDecimal.valueOf(100));
        input.setPaymentMethod("BITCOIN");

        when(paymentMethodRepository.findByPaymentMethod("BITCOIN"))
                .thenReturn(Optional.empty());

        PaymentValidationException ex = assertThrows(PaymentValidationException.class,
                () -> paymentService.pay(input));
        assertEquals("Invalid payment method: BITCOIN", ex.getMessage());
    }

    @Test
    void pay_shouldFail_whenMissingRequiredField() {
        PaymentMethod visa = new PaymentMethod();
        visa.setPaymentMethod("VISA");
        visa.setAdditionalRequiredFields("[\"last4\"]");

        when(paymentMethodRepository.findByPaymentMethod("VISA"))
                .thenReturn(Optional.of(visa));

        PayInput input = new PayInput();
        input.setCustomerId("x");
        input.setPrice(BigDecimal.valueOf(100));
        input.setPaymentMethod("VISA");
        input.setAdditionalItems(Map.of());

        PaymentValidationException ex = assertThrows(PaymentValidationException.class,
                () -> paymentService.pay(input));
        assertEquals("Missing required field: last4", ex.getMessage());
    }

    @Test
    void pay_shouldFail_whenNegativePrice() {
        PayInput input = new PayInput();
        input.setCustomerId("x");
        input.setPrice(BigDecimal.valueOf(-5));
        input.setPaymentMethod("VISA");

        PaymentValidationException ex = assertThrows(PaymentValidationException.class,
                () -> paymentService.pay(input));
        assertEquals("Price must be greater than zero", ex.getMessage());
    }

    @Test
    void pay_shouldFail_whenCustomerIdBlank() {
        PayInput input = new PayInput();
        input.setCustomerId("  ");
        input.setPrice(BigDecimal.valueOf(100));
        input.setPaymentMethod("VISA");

        PaymentValidationException ex = assertThrows(PaymentValidationException.class,
                () -> paymentService.pay(input));
        assertEquals("Customer ID must not be blank", ex.getMessage());
    }
}
