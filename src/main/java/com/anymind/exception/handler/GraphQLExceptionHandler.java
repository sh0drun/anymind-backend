package com.anymind.exception.handler;

import com.anymind.exception.PaymentValidationException;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.stereotype.Component;

@Component
public class GraphQLExceptionHandler extends DataFetcherExceptionResolverAdapter {

    @Override
    protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
        if (ex instanceof PaymentValidationException) {
            return GraphqlErrorBuilder.newError()
                    .message(ex.getMessage())
                    .build();
        }
        return null;
    }
}
