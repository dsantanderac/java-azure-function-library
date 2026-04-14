package com.function;

import com.function.model.Usuario;
import com.function.service.LibraryService;
import com.function.util.JsonUtil;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;

import java.util.Optional;

public class UsuarioFunction {

    private final LibraryService libraryService = new LibraryService();

    @FunctionName("usuarios")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        String method = request.getHttpMethod().toString();
        String idStr = request.getQueryParameters().get("id");

        try {
            switch (method) {
                case "GET":
                    if (idStr != null) {
                        Usuario u = libraryService.getUsuario(Long.parseLong(idStr));
                        return u != null ? request.createResponseBuilder(HttpStatus.OK).body(JsonUtil.toJson(u)).header("Content-Type", "application/json").build()
                                         : request.createResponseBuilder(HttpStatus.NOT_FOUND).body("Usuario no encontrado").build();
                    } else {
                        return request.createResponseBuilder(HttpStatus.OK).body(JsonUtil.toJson(libraryService.getAllUsuarios())).header("Content-Type", "application/json").build();
                    }
                case "POST":
                    String bodyPost = request.getBody().orElse("");
                    Usuario newUser = JsonUtil.fromJson(bodyPost, Usuario.class);
                    if (newUser == null) return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Invalid JSON").build();
                    return request.createResponseBuilder(HttpStatus.CREATED).body(JsonUtil.toJson(libraryService.createUsuario(newUser))).header("Content-Type", "application/json").build();
                case "PUT":
                    String bodyPut = request.getBody().orElse("");
                    Usuario upUser = JsonUtil.fromJson(bodyPut, Usuario.class);
                    if (upUser == null || upUser.getIdUsuario() == null) return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("ID required").build();
                    libraryService.updateUsuario(upUser);
                    return request.createResponseBuilder(HttpStatus.OK).body("Usuario actualizado").build();
                case "DELETE":
                    if (idStr == null) return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("ID required").build();
                    libraryService.deleteUsuario(Long.parseLong(idStr));
                    return request.createResponseBuilder(HttpStatus.OK).body("Usuario eliminado").build();
                default:
                    return request.createResponseBuilder(HttpStatus.METHOD_NOT_ALLOWED).build();
            }
        } catch (Exception e) {
            context.getLogger().severe(e.getMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage()).build();
        }
    }
}
