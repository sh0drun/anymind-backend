package com.anymind.resolver;

import com.anymind.model.dto.PayInput;
import com.anymind.model.dto.PayResult;
import com.anymind.service.PaymentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
public class PaymentMutationResolver {

    private final PaymentService paymentService;

    public PaymentMutationResolver(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @MutationMapping
    public PayResult pay(@Argument @Valid @NotNull(message = "input is required") PayInput input) {
        return paymentService.pay(input);
    }
}