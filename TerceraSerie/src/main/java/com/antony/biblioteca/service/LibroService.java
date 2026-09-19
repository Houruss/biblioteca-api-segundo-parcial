package com.antony.biblioteca.service;

import com.antony.biblioteca.model.Libro;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class LibroService {
    private final List<Libro> libros = new ArrayList<>();
    private final AtomicLong secuencia = new AtomicLong(0);

    public synchronized Libro registrar(Libro libro) {
        libro.setId(secuencia.incrementAndGet());
        libros.add(libro);
        return libro;
    }

    public synchronized List<Libro> consultarTodos() {
        return new ArrayList<>(libros);
    }

    public synchronized Optional<Libro> consultarPorTitulo(String titulo) {
        return libros.stream()
                .filter(libro -> libro.getTitulo() != null
                        && libro.getTitulo().toLowerCase().contains(titulo.toLowerCase()))
                .findFirst();
    }

    public synchronized boolean existeIsbn(String isbn, Long idIgnorado) {
        return libros.stream().anyMatch(libro ->
                libro.getIsbn() != null
                        && libro.getIsbn().equalsIgnoreCase(isbn)
                        && (idIgnorado == null || !libro.getId().equals(idIgnorado)));
    }

    public synchronized Optional<Libro> actualizar(Long id, Libro datos) {
        return libros.stream()
                .filter(libro -> libro.getId().equals(id))
                .findFirst()
                .map(libro -> {
                    libro.setTitulo(datos.getTitulo());
                    libro.setAutor(datos.getAutor());
                    libro.setIsbn(datos.getIsbn());
                    libro.setAnioPublicacion(datos.getAnioPublicacion());
                    libro.setEstado(datos.getEstado());
                    return libro;
                });
    }

    public synchronized boolean eliminar(Long id) {
        return libros.removeIf(libro -> libro.getId().equals(id));
    }
}
