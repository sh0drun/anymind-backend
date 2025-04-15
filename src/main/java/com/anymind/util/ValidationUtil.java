package com.anymind.util;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import java.util.Set;
import java.util.stream.Collectors;

public class ValidationUtil {

    private ValidationUtil() {}

    public static <T> void validateAndThrow(T input, Validator validator, RuntimeExceptionSupplier exceptionSupplier) {
        Set<ConstraintViolation<T>> violations = validator.validate(input);
        if (!violations.isEmpty()) {
            String message = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining("; "));

            throw exceptionSupplier.supply(message);
        }
    }

    @FunctionalInterface
    public interface RuntimeExceptionSupplier {
        RuntimeException supply(String message);
    }
}
