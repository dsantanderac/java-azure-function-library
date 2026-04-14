package com.function;

import com.function.model.Libro;
import com.function.service.LibraryService;
import com.function.util.JsonUtil;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;

import java.util.Optional;

public class LibroFunction {

    private final LibraryService libraryService = new LibraryService();

    @FunctionName("libros")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        String method = request.getHttpMethod().toString();
        String idStr = request.getQueryParameters().get("id");

        try {
            switch (method) {
                case "GET":
                    if (idStr != null) {
                        Libro l = libraryService.getLibro(Long.parseLong(idStr));
                        return l != null ? request.createResponseBuilder(HttpStatus.OK).body(JsonUtil.toJson(l)).header("Content-Type", "application/json").build()
                                         : request.createResponseBuilder(HttpStatus.NOT_FOUND).body("Libro no encontrado").build();
                    } else {
                        return request.createResponseBuilder(HttpStatus.OK).body(JsonUtil.toJson(libraryService.getAllLibros())).header("Content-Type", "application/json").build();
                    }
                case "POST":
                    String bodyPost = request.getBody().orElse("");
                    Libro newL = JsonUtil.fromJson(bodyPost, Libro.class);
                    if (newL == null) return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Invalid JSON").build();
                    return request.createResponseBuilder(HttpStatus.CREATED).body(JsonUtil.toJson(libraryService.createLibro(newL))).header("Content-Type", "application/json").build();
                case "PUT":
                    String bodyPut = request.getBody().orElse("");
                    Libro upL = JsonUtil.fromJson(bodyPut, Libro.class);
                    if (upL == null || upL.getIdLibro() == null) return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("ID required").build();
                    libraryService.updateLibro(upL);
                    return request.createResponseBuilder(HttpStatus.OK).body("Libro actualizado").build();
                case "DELETE":
                    if (idStr == null) return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("ID required").build();
                    libraryService.deleteLibro(Long.parseLong(idStr));
                    return request.createResponseBuilder(HttpStatus.OK).body("Libro eliminado").build();
                default:
                    return request.createResponseBuilder(HttpStatus.METHOD_NOT_ALLOWED).build();
            }
        } catch (Exception e) {
            context.getLogger().severe(e.getMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage()).build();
        }
    }
}
