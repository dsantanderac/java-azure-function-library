package com.function.graphql;

import com.function.service.LibraryService;
import com.function.model.Usuario;
import com.function.model.Prestamo;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.*;
import graphql.schema.DataFetcher;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class GraphQLProvider {

    private final LibraryService libraryService = new LibraryService();
    private GraphQL graphQL;

    public GraphQLProvider() {
        init();
    }

    private void init() {
        try {
            InputStream is = getClass().getResourceAsStream("/schema.graphqls");
            String schemaString;
            try (Scanner s = new Scanner(is, StandardCharsets.UTF_8)) {
                schemaString = s.useDelimiter("\\A").next();
            }

            SchemaParser schemaParser = new SchemaParser();
            TypeDefinitionRegistry typeRegistry = schemaParser.parse(schemaString);

            RuntimeWiring wiring = buildWiring();
            SchemaGenerator schemaGenerator = new SchemaGenerator();
            GraphQLSchema graphQLSchema = schemaGenerator.makeExecutableSchema(typeRegistry, wiring);

            this.graphQL = GraphQL.newGraphQL(graphQLSchema).build();
        } catch (Exception e) {
            throw new RuntimeException("Error initializing GraphQL", e);
        }
    }

    private RuntimeWiring buildWiring() {
        return RuntimeWiring.newRuntimeWiring()
                .type("Query", typeWiring -> typeWiring
                        .dataFetcher("usuario", (DataFetcher<Usuario>) env -> {
                            String id = env.getArgument("id");
                            return libraryService.getUsuario(Long.parseLong(id));
                        })
                        .dataFetcher("usuarios", (DataFetcher<?>) env -> libraryService.getAllUsuarios())
                        .dataFetcher("prestamos", (DataFetcher<?>) env -> libraryService.getAllPrestamos()))
                .type("Mutation", typeWiring -> typeWiring
                        .dataFetcher("crearUsuario", (DataFetcher<Usuario>) env -> {
                            Usuario u = new Usuario();
                            u.setNombre(env.getArgument("nombre"));
                            u.setApellido(env.getArgument("apellido"));
                            u.setCorreo(env.getArgument("correo"));
                            u.setTelefono(env.getArgument("telefono"));
                            return libraryService.createUsuario(u);
                        })
                        .dataFetcher("crearPrestamo", (DataFetcher<Prestamo>) env -> {
                            Prestamo p = new Prestamo();
                            p.setIdUsuario(Long.parseLong(env.getArgument("idUsuario")));
                            p.setIdLibro(Long.parseLong(env.getArgument("idLibro")));
                            return libraryService.createPrestamo(p);
                        }))
                .build();
    }

    public GraphQL getGraphQL() {
        return graphQL;
    }
}
