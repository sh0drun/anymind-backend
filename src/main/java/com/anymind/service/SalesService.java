package com.anymind.service;

import com.anymind.exception.InvalidDateRangeException;
import com.anymind.model.dto.SalesByHour;
import com.anymind.model.dto.SalesQueryInput;
import com.anymind.model.entity.Payment;
import com.anymind.repository.PaymentRepository;
import com.anymind.util.ValidationUtil;
import jakarta.validation.Validator;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SalesService {

    private final PaymentRepository paymentRepository;

    private final Validator validator;

    public SalesService(PaymentRepository paymentRepository,
                        Validator validator) {
        this.paymentRepository = paymentRepository;
        this.validator = validator;
    }

    public List<SalesByHour> getSalesBreakdown(SalesQueryInput input) {
        ValidationUtil.validateAndThrow(input, validator, InvalidDateRangeException::new);

        LocalDateTime start;
        LocalDateTime end;

        try {
            start = input.getParsedStartDateTime();
            end = input.getParsedEndDateTime();
        } catch (Exception e) {
            throw new InvalidDateRangeException("Invalid datetime format !");
        }

        if (start.isAfter(end)) {
            throw new InvalidDateRangeException("Start datetime must be before or equal to end datetime !");
        }

        List<Payment> payments = paymentRepository.findAllByDatetimeBetween(Timestamp.valueOf(start), Timestamp.valueOf(end));

        Map<LocalDateTime, List<Payment>> paymentsByHour = payments.stream()
                .collect(Collectors.groupingBy(payment -> truncateToHour(payment.getDatetime().toLocalDateTime())));

        return paymentsByHour.entrySet().stream()
                .map(this::createSalesSummary)
                .sorted(Comparator.comparing(SalesByHour::getDatetime))
                .collect(Collectors.toList());
    }

    private LocalDateTime truncateToHour(LocalDateTime dateTime) {
        return dateTime.withMinute(0).withSecond(0).withNano(0);
    }

    private SalesByHour createSalesSummary(Map.Entry<LocalDateTime, List<Payment>> hourlyPayments) {
        LocalDateTime hour = hourlyPayments.getKey();
        List<Payment> payments = hourlyPayments.getValue();

        BigDecimal totalSales = payments.stream().map(Payment::getFinalPrice).reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalPoints = payments.stream().mapToInt(Payment::getPoints).sum();

        SalesByHour result = new SalesByHour();
        result.setDatetime(hour.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        result.setSales(totalSales);
        result.setPoints(totalPoints);

        return result;
    }
}