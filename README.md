# Sistema de Biblioteca - Azure Functions (Java)

Este proyecto implementa un sistema simple de gestión de biblioteca utilizando Azure Functions, conectado a una base de datos Oracle mediante Oracle Wallet.

## Estructura del Proyecto

- `com.function`: Funciones de Azure (Entry points para REST y GraphQL).
- `com.function.model`: Entidades de datos (Usuario, Libro, Prestamo).
- `com.function.repository`: Capa de acceso a datos (JDBC nativo).
- `com.function.service`: Capa de lógica de negocio.
- `com.function.config`: Configuración de conexión a base de datos.
- `com.function.util`: Utilidades (JSON, etc).
- `com.function.graphql`: Proveedor y configuración de GraphQL.
- `src/main/resources/schema.graphqls`: Definición del esquema GraphQL.

## Configuración

1.  **Base de Datos**: Ejecuta el archivo `schema.sql` en tu instancia de Oracle Cloud.
2.  **Wallet**: Descarga tu Oracle Wallet y colócalo en una carpeta accesible.
3.  **Variables de Entorno**: Configura `local.settings.json` con tus credenciales:
    - `ORACLE_DB_URL`: URL JDBC (ej. `jdbc:oracle:thin:@dbname_high?TNS_ADMIN=/ruta/al/wallet`)
    - `ORACLE_DB_USER`: Usuario (ej. `ADMIN`)
    - `ORACLE_DB_PASSWORD`: Contraseña.
    - `TNS_ADMIN`: Ruta absoluta a la carpeta descomprimida del Wallet.

## Ejecución Local

```bash
mvn clean package
mvn azure-functions:run
```

## Pruebas

### API REST (Usuarios)
- **Listar**: `GET http://localhost:7071/api/usuarios`
- **Obtener por ID**: `GET http://localhost:7071/api/usuarios?id=1`
- **Crear**: `POST http://localhost:7071/api/usuarios` con body JSON:
  ```json
  { "nombre": "Ana", "apellido": "Gomez", "correo": "ana@example.com" }
  ```

### GraphQL
- **Endpoint**: `POST http://localhost:7071/api/graphql`
- **Body Query**:
  ```json
  {
    "query": "query { usuarios { nombre correo } }"
  }
  ```
- **Body Mutation**:
  ```json
  {
    "query": "mutation { crearUsuario(nombre: \"Luis\", apellido: \"Soto\", correo: \"luis@example.com\") { idUsuario nombre } }"
  }
  ```
