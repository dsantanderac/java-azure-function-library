package com.function;

import com.function.model.Prestamo;
import com.function.service.LibraryService;
import com.function.util.JsonUtil;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;

import java.util.Optional;

public class PrestamoFunction {

    private final LibraryService libraryService = new LibraryService();

    @FunctionName("prestamos")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        String method = request.getHttpMethod().toString();
        String idStr = request.getQueryParameters().get("id");

        try {
            switch (method) {
                case "GET":
                    return request.createResponseBuilder(HttpStatus.OK).body(JsonUtil.toJson(libraryService.getAllPrestamos())).header("Content-Type", "application/json").build();
                case "POST":
                    String bodyPost = request.getBody().orElse("");
                    Prestamo newP = JsonUtil.fromJson(bodyPost, Prestamo.class);
                    if (newP == null) return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Invalid JSON").build();
                    return request.createResponseBuilder(HttpStatus.CREATED).body(JsonUtil.toJson(libraryService.createPrestamo(newP))).header("Content-Type", "application/json").build();
                case "PUT":
                    String bodyPut = request.getBody().orElse("");
                    Prestamo upP = JsonUtil.fromJson(bodyPut, Prestamo.class);
                    if (upP == null || upP.getIdPrestamo() == null) return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("ID required").build();
                    libraryService.updatePrestamo(upP);
                    return request.createResponseBuilder(HttpStatus.OK).body("Prestamo actualizado").build();
                case "DELETE":
                    if (idStr == null) return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("ID required").build();
                    libraryService.deletePrestamo(Long.parseLong(idStr));
                    return request.createResponseBuilder(HttpStatus.OK).body("Prestamo eliminado").build();
                default:
                    return request.createResponseBuilder(HttpStatus.METHOD_NOT_ALLOWED).build();
            }
        } catch (Exception e) {
            context.getLogger().severe(e.getMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage()).build();
        }
    }
}
