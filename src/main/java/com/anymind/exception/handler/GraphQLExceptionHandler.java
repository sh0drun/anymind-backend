package com.anymind.exception.handler;

import com.anymind.exception.InvalidDateRangeException;
import com.anymind.exception.PaymentProcessingException;
import com.anymind.exception.PaymentValidationException;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.UUID;

@Component
public class GraphQLExceptionHandler extends DataFetcherExceptionResolverAdapter {

    private static final Logger log = LoggerFactory.getLogger(GraphQLExceptionHandler.class);

    @Override
    @NonNull
    protected GraphQLError resolveToSingleError(@NonNull Throwable ex, @NonNull DataFetchingEnvironment env) {
        // Handle validation exceptions
        if (ex instanceof ConstraintViolationException constraintViolationException) {
            return handleConstraintViolationException(constraintViolationException);
        }

        if (ex instanceof MethodArgumentNotValidException methodArgumentNotValidException) {
            return handleMethodArgumentNotValidException(methodArgumentNotValidException);
        }

        if (ex instanceof PaymentValidationException ||
                ex instanceof PaymentProcessingException ||
                ex instanceof InvalidDateRangeException) {
            return buildBadRequestError(ex.getMessage());
        }

        UUID errorId = UUID.randomUUID();
        log.error("Unhandled exception during GraphQL execution [{}]", errorId, ex);

        return GraphqlErrorBuilder.newError()
                .message("An unexpected error occurred. Please contact support with code: " + errorId)
                .errorType(ErrorType.INTERNAL_ERROR)
                .build();
    }

    private GraphQLError handleConstraintViolationException(ConstraintViolationException ex) {
        String message = ex.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .findFirst()
                .orElse("Validation error");

        return GraphqlErrorBuilder.newError()
                .message(message)
                .errorType(ErrorType.BAD_REQUEST)
                .build();
    }

    private GraphQLError handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Validation error");

        return GraphqlErrorBuilder.newError()
                .message(message)
                .errorType(ErrorType.BAD_REQUEST)
                .build();
    }

    private GraphQLError buildBadRequestError(String message) {
        return GraphqlErrorBuilder.newError()
                .message(message)
                .errorType(ErrorType.BAD_REQUEST)
                .build();
    }
}