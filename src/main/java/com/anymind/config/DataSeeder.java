package com.anymind.config;

import com.anymind.model.PaymentMethod;
import com.anymind.repository.PaymentMethodRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataSeeder {

    private final PaymentMethodRepository paymentMethodRepository;

    public DataSeeder(PaymentMethodRepository paymentMethodRepository) {
        this.paymentMethodRepository = paymentMethodRepository;
    }

    @PostConstruct
    public void seedPaymentMethods() {
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

            System.out.println("✅ Payment methods seeded successfully.");
        } else {
            System.out.println("✅ Payment methods already exist. No seeding needed.");
        }
    }
}
