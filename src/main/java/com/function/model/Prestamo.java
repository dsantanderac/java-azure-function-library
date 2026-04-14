package com.function.model;

import java.time.LocalDateTime;

public class Prestamo {
    private Long idPrestamo;
    private Long idUsuario;
    private Long idLibro;
    private LocalDateTime fechaPrestamo;
    private LocalDateTime fechaDevolucion;
    private String estado;

    public Prestamo() {}

    public Long getIdPrestamo() { return idPrestamo; }
    public void setIdPrestamo(Long idPrestamo) { this.idPrestamo = idPrestamo; }
    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }
    public Long getIdLibro() { return idLibro; }
    public void setIdLibro(Long idLibro) { this.idLibro = idLibro; }
    public LocalDateTime getFechaPrestamo() { return fechaPrestamo; }
    public void setFechaPrestamo(LocalDateTime fechaPrestamo) { this.fechaPrestamo = fechaPrestamo; }
    public LocalDateTime getFechaDevolucion() { return fechaDevolucion; }
    public void setFechaDevolucion(LocalDateTime fechaDevolucion) { this.fechaDevolucion = fechaDevolucion; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
