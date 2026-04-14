package com.function.repository;

import com.function.config.OracleConnectionManager;
import com.function.model.Libro;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LibroRepository {

    public Libro save(Libro libro) throws SQLException {
        String sql = "INSERT INTO LIBRO (titulo, autor, isbn, anio_publicacion, categoria, stock, disponible) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = OracleConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, new String[]{"id_libro"})) {
            ps.setString(1, libro.getTitulo());
            ps.setString(2, libro.getAutor());
            ps.setString(3, libro.getIsbn());
            ps.setInt(4, libro.getAnioPublicacion());
            ps.setString(5, libro.getCategoria());
            ps.setInt(6, libro.getStock());
            ps.setBoolean(7, libro.getDisponible() != null ? libro.getDisponible() : true);
            
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    libro.setIdLibro(rs.getLong(1));
                }
            }
        }
        return libro;
    }

    public Libro findById(Long id) throws SQLException {
        String sql = "SELECT * FROM LIBRO WHERE id_libro = ?";
        try (Connection conn = OracleConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToLibro(rs);
                }
            }
        }
        return null;
    }

    public List<Libro> findAll() throws SQLException {
        List<Libro> libros = new ArrayList<>();
        String sql = "SELECT * FROM LIBRO";
        try (Connection conn = OracleConnectionManager.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                libros.add(mapResultSetToLibro(rs));
            }
        }
        return libros;
    }

    public void update(Libro libro) throws SQLException {
        String sql = "UPDATE LIBRO SET titulo = ?, autor = ?, isbn = ?, anio_publicacion = ?, categoria = ?, stock = ?, disponible = ? WHERE id_libro = ?";
        try (Connection conn = OracleConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, libro.getTitulo());
            ps.setString(2, libro.getAutor());
            ps.setString(3, libro.getIsbn());
            ps.setInt(4, libro.getAnioPublicacion());
            ps.setString(5, libro.getCategoria());
            ps.setInt(6, libro.getStock());
            ps.setBoolean(7, libro.getDisponible());
            ps.setLong(8, libro.getIdLibro());
            ps.executeUpdate();
        }
    }

    public void delete(Long id) throws SQLException {
        String sql = "DELETE FROM LIBRO WHERE id_libro = ?";
        try (Connection conn = OracleConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    private Libro mapResultSetToLibro(ResultSet rs) throws SQLException {
        Libro l = new Libro();
        l.setIdLibro(rs.getLong("id_libro"));
        l.setTitulo(rs.getString("titulo"));
        l.setAutor(rs.getString("autor"));
        l.setIsbn(rs.getString("isbn"));
        l.setAnioPublicacion(rs.getInt("anio_publicacion"));
        l.setCategoria(rs.getString("categoria"));
        l.setStock(rs.getInt("stock"));
        l.setDisponible(rs.getBoolean("disponible"));
        return l;
    }
}
