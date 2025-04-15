package com.anymind.resolver;

import com.anymind.model.dto.SalesByHour;
import com.anymind.model.dto.SalesQueryInput;
import com.anymind.service.SalesService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class SalesQueryResolver {

    private final SalesService salesService;

    public SalesQueryResolver(SalesService salesService) {
        this.salesService = salesService;
    }

    @QueryMapping
    public List<SalesByHour> sales(@Argument @Valid @NotNull(message = "input is required") SalesQueryInput input) {
        return salesService.getSalesBreakdown(input);
    }

}
