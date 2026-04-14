package com.function;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.function.graphql.GraphQLProvider;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;
import graphql.ExecutionInput;
import graphql.ExecutionResult;

import java.util.Map;
import java.util.Optional;

public class GraphQLFunction {

    private static final GraphQLProvider provider = new GraphQLProvider();
    private final ObjectMapper mapper = new ObjectMapper();

    @FunctionName("graphql")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        try {
            String body = request.getBody().orElse("{}");
            Map<String, Object> bodyMap = mapper.readValue(body, new TypeReference<Map<String, Object>>() {});
            
            String query = (String) bodyMap.get("query");
            @SuppressWarnings("unchecked")
            Map<String, Object> variables = (Map<String, Object>) bodyMap.get("variables");

            ExecutionInput executionInput = ExecutionInput.newExecutionInput()
                    .query(query)
                    .variables(variables != null ? variables : Map.of())
                    .build();

            ExecutionResult executionResult = provider.getGraphQL().execute(executionInput);
            Map<String, Object> resultMap = executionResult.toSpecification();

            return request.createResponseBuilder(HttpStatus.OK)
                    .body(mapper.writeValueAsString(resultMap))
                    .header("Content-Type", "application/json")
                    .build();

        } catch (Exception e) {
            context.getLogger().severe(e.getMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage())
                    .build();
        }
    }
}
