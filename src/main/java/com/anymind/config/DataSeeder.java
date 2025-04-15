package com.anymind.config;

import com.anymind.model.entity.Payment;
import com.anymind.model.entity.PaymentMethod;
import com.anymind.repository.PaymentMethodRepository;
import com.anymind.repository.PaymentRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class DataSeeder {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final PaymentMethodRepository paymentMethodRepository;
    private final PaymentRepository paymentRepository;

    public DataSeeder(PaymentMethodRepository paymentMethodRepository,
                      PaymentRepository paymentRepository) {
        this.paymentMethodRepository = paymentMethodRepository;
        this.paymentRepository = paymentRepository;
    }

    @PostConstruct
    public void seed() {
        seedPaymentMethods();
        seedPayments();
    }

    private void seedPaymentMethods() {
        if (paymentMethodRepository.count() == 0) {

            PaymentMethod cash = new PaymentMethod();
            cash.setPaymentMethod("CASH");
            cash.setMinModifier(0.9f);
            cash.setMaxModifier(1.0f);
            cash.setPointRate(0.05f);
            cash.setAdditionalRequiredFields("[]");

            PaymentMethod cashOnDelivery = new PaymentMethod();
            cashOnDelivery.setPaymentMethod("CASH_ON_DELIVERY");
            cashOnDelivery.setMinModifier(1.0f);
            cashOnDelivery.setMaxModifier(1.02f);
            cashOnDelivery.setPointRate(0.05f);
            cashOnDelivery.setAdditionalRequiredFields("[\"courier\"]");

            PaymentMethod visa = new PaymentMethod();
            visa.setPaymentMethod("VISA");
            visa.setMinModifier(0.95f);
            visa.setMaxModifier(1.0f);
            visa.setPointRate(0.03f);
            visa.setAdditionalRequiredFields("[\"last4\"]");

            PaymentMethod mastercard = new PaymentMethod();
            mastercard.setPaymentMethod("MASTERCARD");
            mastercard.setMinModifier(0.95f);
            mastercard.setMaxModifier(1.0f);
            mastercard.setPointRate(0.03f);
            mastercard.setAdditionalRequiredFields("[\"last4\"]");

            PaymentMethod amex = new PaymentMethod();
            amex.setPaymentMethod("AMEX");
            amex.setMinModifier(0.98f);
            amex.setMaxModifier(1.01f);
            amex.setPointRate(0.02f);
            amex.setAdditionalRequiredFields("[\"last4\"]");

            PaymentMethod jcb = new PaymentMethod();
            jcb.setPaymentMethod("JCB");
            jcb.setMinModifier(0.95f);
            jcb.setMaxModifier(1.0f);
            jcb.setPointRate(0.05f);
            jcb.setAdditionalRequiredFields("[\"last4\"]");

            PaymentMethod linePay = new PaymentMethod();
            linePay.setPaymentMethod("LINE_PAY");
            linePay.setMinModifier(1.0f);
            linePay.setMaxModifier(1.0f);
            linePay.setPointRate(0.01f);
            linePay.setAdditionalRequiredFields("[]");

            PaymentMethod paypay = new PaymentMethod();
            paypay.setPaymentMethod("PAYPAY");
            paypay.setMinModifier(1.0f);
            paypay.setMaxModifier(1.0f);
            paypay.setPointRate(0.01f);
            paypay.setAdditionalRequiredFields("[]");

            PaymentMethod points = new PaymentMethod();
            points.setPaymentMethod("POINTS");
            points.setMinModifier(1.0f);
            points.setMaxModifier(1.0f);
            points.setPointRate(0.0f);
            points.setAdditionalRequiredFields("[]");

            PaymentMethod grabPay = new PaymentMethod();
            grabPay.setPaymentMethod("GRAB_PAY");
            grabPay.setMinModifier(1.0f);
            grabPay.setMaxModifier(1.0f);
            grabPay.setPointRate(0.01f);
            grabPay.setAdditionalRequiredFields("[]");

            PaymentMethod bankTransfer = new PaymentMethod();
            bankTransfer.setPaymentMethod("BANK_TRANSFER");
            bankTransfer.setMinModifier(1.0f);
            bankTransfer.setMaxModifier(1.0f);
            bankTransfer.setPointRate(0.0f);
            bankTransfer.setAdditionalRequiredFields("[\"bank\", \"accountNumber\"]");

            PaymentMethod cheque = new PaymentMethod();
            cheque.setPaymentMethod("CHEQUE");
            cheque.setMinModifier(0.9f);
            cheque.setMaxModifier(1.0f);
            cheque.setPointRate(0.0f);
            cheque.setAdditionalRequiredFields("[\"bank\", \"chequeNumber\"]");

            paymentMethodRepository.saveAll(List.of(
                    cash, cashOnDelivery, visa, mastercard, amex, jcb,
                    linePay, paypay, points, grabPay, bankTransfer, cheque
            ));

            log.info("Payment methods seeded successfully");
        } else {
            log.info("Payment methods already exist. No seeding needed");
        }
    }

    private void seedPayments() {
        if (paymentRepository.count() == 0) {
            List<Payment> seedPayments = new ArrayList<>();

            LocalDateTime baseTime = LocalDateTime.of(2025, 4, 15, 0, 0);

            for (int i = 0; i < 12; i++) {
                Payment payment = new Payment();
                payment.setCustomerId("demo-user-" + (11-i));

                payment.setPrice(BigDecimal.valueOf(100 + i * 100));
                payment.setFinalPrice(BigDecimal.valueOf(97 + i * 100));
                payment.setPoints(1 + i * 5);

                payment.setDatetime(Timestamp.valueOf(baseTime.plusHours(i)));
                payment.setPaymentMethod("VISA");
                seedPayments.add(payment);
            }

            String[] paymentMethods = {"VISA", "MASTERCARD", "AMEX", "PAYPAY"};
            Random random = new Random();

            for (int i = 0; i < 20; i++) {
                Payment extraPayment = new Payment();

                int hour = random.nextInt(12);
                int minutes = random.nextInt(60);
                int customerIndex = random.nextInt(20);

                extraPayment.setCustomerId("customer-" + customerIndex);

                int priceBase = 50 + random.nextInt(450);
                extraPayment.setPrice(BigDecimal.valueOf(priceBase));

                extraPayment.setFinalPrice(BigDecimal.valueOf(priceBase * 0.95));

                extraPayment.setPoints(priceBase / 20);

                extraPayment.setPaymentMethod(paymentMethods[random.nextInt(paymentMethods.length)]);

                extraPayment.setDatetime(Timestamp.valueOf(baseTime.plusHours(hour).plusMinutes(minutes)));

                seedPayments.add(extraPayment);
            }

            for (int i = 0; i < 5; i++) {
                Payment nextDayPayment = new Payment();
                nextDayPayment.setCustomerId("next-day-user-" + i);
                nextDayPayment.setPrice(BigDecimal.valueOf(200 + i * 50));
                nextDayPayment.setFinalPrice(BigDecimal.valueOf(190 + i * 50));
                nextDayPayment.setPoints(10 + i * 2);
                nextDayPayment.setDatetime(Timestamp.valueOf(baseTime.plusDays(1).plusHours(i)));
                nextDayPayment.setPaymentMethod("VISA");
                seedPayments.add(nextDayPayment);
            }

            paymentRepository.saveAll(seedPayments);
            log.info("Payment data seeded successfully: {} records created", seedPayments.size());
        } else {
            log.info("Payments already exist. No seeding needed");
        }
    }
}
