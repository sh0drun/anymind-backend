package com.anymind.controller;

import com.anymind.model.dto.PayInput;
import com.anymind.model.dto.PayResult;
import com.anymind.service.PaymentService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @MutationMapping
    public PayResult pay(@Argument PayInput input) {
        return paymentService.pay(input);
    }
}