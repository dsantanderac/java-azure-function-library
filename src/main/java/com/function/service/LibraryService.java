package com.function.service;

import com.function.model.Libro;
import com.function.model.Prestamo;
import com.function.model.Usuario;
import com.function.repository.LibroRepository;
import com.function.repository.PrestamoRepository;
import com.function.repository.UsuarioRepository;

import java.sql.SQLException;
import java.util.List;

public class LibraryService {
    private final UsuarioRepository usuarioRepository = new UsuarioRepository();
    private final LibroRepository libroRepository = new LibroRepository();
    private final PrestamoRepository prestamoRepository = new PrestamoRepository();

    // Usuarios
    public Usuario createUsuario(Usuario u) throws SQLException { return usuarioRepository.save(u); }
    public Usuario getUsuario(Long id) throws SQLException { return usuarioRepository.findById(id); }
    public List<Usuario> getAllUsuarios() throws SQLException { return usuarioRepository.findAll(); }
    public void updateUsuario(Usuario u) throws SQLException { usuarioRepository.update(u); }
    public void deleteUsuario(Long id) throws SQLException { usuarioRepository.delete(id); }

    // Libros
    public Libro createLibro(Libro l) throws SQLException { return libroRepository.save(l); }
    public Libro getLibro(Long id) throws SQLException { return libroRepository.findById(id); }
    public List<Libro> getAllLibros() throws SQLException { return libroRepository.findAll(); }
    public void updateLibro(Libro l) throws SQLException { libroRepository.update(l); }
    public void deleteLibro(Long id) throws SQLException { libroRepository.delete(id); }

    // Prestamos
    public Prestamo createPrestamo(Prestamo p) throws SQLException { return prestamoRepository.save(p); }
    public List<Prestamo> getAllPrestamos() throws SQLException { return prestamoRepository.findAll(); }
    public void updatePrestamo(Prestamo p) throws SQLException { prestamoRepository.update(p); }
    public void deletePrestamo(Long id) throws SQLException { prestamoRepository.delete(id); }
}
