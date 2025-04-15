package com.anymind.service;

import com.anymind.exception.InvalidDateRangeException;
import com.anymind.model.dto.SalesByHour;
import com.anymind.model.dto.SalesQueryInput;
import com.anymind.model.entity.Payment;
import com.anymind.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SalesServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private SalesService salesService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getSalesBreakdown_shouldReturnHourlySummary_whenValidInput() {
        SalesQueryInput input = new SalesQueryInput();
        input.setStartDateTime("2025-04-15T01:00:00");
        input.setEndDateTime("2025-04-15T02:00:00");

        Payment payment1 = new Payment();
        payment1.setDatetime(Timestamp.valueOf("2025-04-15 01:15:00"));
        payment1.setFinalPrice(BigDecimal.valueOf(100));
        payment1.setPoints(10);

        Payment payment2 = new Payment();
        payment2.setDatetime(Timestamp.valueOf("2025-04-15 01:45:00"));
        payment2.setFinalPrice(BigDecimal.valueOf(50));
        payment2.setPoints(5);

        when(paymentRepository.findAllByDatetimeBetween(any(), any())).thenReturn(List.of(payment1, payment2));

        List<SalesByHour> result = salesService.getSalesBreakdown(input);

        assertEquals(1, result.size());
        assertEquals("2025-04-15T01:00:00", result.get(0).getDatetime());
        assertEquals(BigDecimal.valueOf(150), result.get(0).getSales());
        assertEquals(15, result.get(0).getPoints());
    }

    @Test
    void getSalesBreakdown_shouldThrowException_whenStartAfterEnd() {
        SalesQueryInput input = new SalesQueryInput();
        input.setStartDateTime("2025-04-15T03:00:00");
        input.setEndDateTime("2025-04-15T01:00:00");

        InvalidDateRangeException ex = assertThrows(InvalidDateRangeException.class, () -> {
            salesService.getSalesBreakdown(input);
        });

        assertTrue(ex.getMessage().contains("Start datetime must be before"));
    }

    @Test
    void getSalesBreakdown_shouldReturnEmptyList_whenNoPaymentsFound() {
        SalesQueryInput input = new SalesQueryInput();
        input.setStartDateTime("2025-04-15T01:00:00");
        input.setEndDateTime("2025-04-15T02:00:00");

        when(paymentRepository.findAllByDatetimeBetween(any(), any())).thenReturn(List.of());

        List<SalesByHour> result = salesService.getSalesBreakdown(input);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getSalesBreakdown_shouldThrowException_whenDateFormatIsInvalid() {
        SalesQueryInput input = new SalesQueryInput();
        input.setStartDateTime("invalid-date");
        input.setEndDateTime("2025-04-15T02:00:00");

        assertThrows(InvalidDateRangeException.class, () -> salesService.getSalesBreakdown(input));
    }
}
