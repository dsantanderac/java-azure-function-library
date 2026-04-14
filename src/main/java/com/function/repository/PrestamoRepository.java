package com.function.repository;

import com.function.config.OracleConnectionManager;
import com.function.model.Prestamo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrestamoRepository {

    public Prestamo save(Prestamo prestamo) throws SQLException {
        String sql = "INSERT INTO PRESTAMO (id_usuario, id_libro, fecha_prestamo, fecha_devolucion, estado) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = OracleConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, new String[]{"id_prestamo"})) {
            ps.setLong(1, prestamo.getIdUsuario());
            ps.setLong(2, prestamo.getIdLibro());
            ps.setTimestamp(3, Timestamp.valueOf(prestamo.getFechaPrestamo() != null ? prestamo.getFechaPrestamo() : java.time.LocalDateTime.now()));
            ps.setTimestamp(4, prestamo.getFechaDevolucion() != null ? Timestamp.valueOf(prestamo.getFechaDevolucion()) : null);
            ps.setString(5, prestamo.getEstado() != null ? prestamo.getEstado() : "ACTIVO");
            
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    prestamo.setIdPrestamo(rs.getLong(1));
                }
            }
        }
        return prestamo;
    }

    public List<Prestamo> findAll() throws SQLException {
        List<Prestamo> prestamos = new ArrayList<>();
        String sql = "SELECT * FROM PRESTAMO";
        try (Connection conn = OracleConnectionManager.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                prestamos.add(mapResultSetToPrestamo(rs));
            }
        }
        return prestamos;
    }

    public void update(Prestamo prestamo) throws SQLException {
        String sql = "UPDATE PRESTAMO SET fecha_devolucion = ?, estado = ? WHERE id_prestamo = ?";
        try (Connection conn = OracleConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, prestamo.getFechaDevolucion() != null ? Timestamp.valueOf(prestamo.getFechaDevolucion()) : null);
            ps.setString(2, prestamo.getEstado());
            ps.setLong(3, prestamo.getIdPrestamo());
            ps.executeUpdate();
        }
    }

    public void delete(Long id) throws SQLException {
        String sql = "DELETE FROM PRESTAMO WHERE id_prestamo = ?";
        try (Connection conn = OracleConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    private Prestamo mapResultSetToPrestamo(ResultSet rs) throws SQLException {
        Prestamo p = new Prestamo();
        p.setIdPrestamo(rs.getLong("id_prestamo"));
        p.setIdUsuario(rs.getLong("id_usuario"));
        p.setIdLibro(rs.getLong("id_libro"));
        p.setFechaPrestamo(rs.getTimestamp("fecha_prestamo").toLocalDateTime());
        Timestamp ts = rs.getTimestamp("fecha_devolucion");
        if (ts != null) p.setFechaDevolucion(ts.toLocalDateTime());
        p.setEstado(rs.getString("estado"));
        return p;
    }
}
