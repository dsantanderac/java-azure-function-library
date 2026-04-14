package com.function.repository;

import com.function.config.OracleConnectionManager;
import com.function.model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioRepository {

    public Usuario save(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO USUARIO_BIBLIOTECA (nombre, apellido, correo, telefono, fecha_registro, estado) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = OracleConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, new String[]{"id_usuario"})) {
            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getApellido());
            ps.setString(3, usuario.getCorreo());
            ps.setString(4, usuario.getTelefono());
            ps.setTimestamp(5, Timestamp.valueOf(usuario.getFechaRegistro() != null ? usuario.getFechaRegistro() : java.time.LocalDateTime.now()));
            ps.setString(6, usuario.getEstado() != null ? usuario.getEstado() : "ACTIVO");
            
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    usuario.setIdUsuario(rs.getLong(1));
                }
            }
        }
        return usuario;
    }

    public Usuario findById(Long id) throws SQLException {
        String sql = "SELECT * FROM USUARIO_BIBLIOTECA WHERE id_usuario = ?";
        try (Connection conn = OracleConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUsuario(rs);
                }
            }
        }
        return null;
    }

    public List<Usuario> findAll() throws SQLException {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM USUARIO_BIBLIOTECA";
        try (Connection conn = OracleConnectionManager.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                usuarios.add(mapResultSetToUsuario(rs));
            }
        }
        return usuarios;
    }

    public void update(Usuario usuario) throws SQLException {
        String sql = "UPDATE USUARIO_BIBLIOTECA SET nombre = ?, apellido = ?, correo = ?, telefono = ?, estado = ? WHERE id_usuario = ?";
        try (Connection conn = OracleConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getApellido());
            ps.setString(3, usuario.getCorreo());
            ps.setString(4, usuario.getTelefono());
            ps.setString(5, usuario.getEstado());
            ps.setLong(6, usuario.getIdUsuario());
            ps.executeUpdate();
        }
    }

    public void delete(Long id) throws SQLException {
        String sql = "DELETE FROM USUARIO_BIBLIOTECA WHERE id_usuario = ?";
        try (Connection conn = OracleConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    private Usuario mapResultSetToUsuario(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setIdUsuario(rs.getLong("id_usuario"));
        u.setNombre(rs.getString("nombre"));
        u.setApellido(rs.getString("apellido"));
        u.setCorreo(rs.getString("correo"));
        u.setTelefono(rs.getString("telefono"));
        u.setFechaRegistro(rs.getTimestamp("fecha_registro").toLocalDateTime());
        u.setEstado(rs.getString("estado"));
        return u;
    }
}
