package com.antony.biblioteca.controller;

import com.antony.biblioteca.model.Libro;
import com.antony.biblioteca.service.LibroService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/libros")
public class LibroController {
    private final LibroService libroService;

    public LibroController(LibroService libroService) {
        this.libroService = libroService;
    }

    @PostMapping
    public ResponseEntity<?> registrar(@RequestBody Libro libro) {
        if (!datosValidos(libro)) {
            return respuestaError("Datos inválidos", HttpStatus.BAD_REQUEST);
        }
        if (libroService.existeIsbn(libro.getIsbn(), null)) {
            return respuestaError("Ya existe un libro con ese ISBN", HttpStatus.CONFLICT);
        }
        Libro creado = libroService.registrar(libro);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "mensaje", "Libro registrado correctamente",
                "codigo", 201,
                "datos", creado));
    }

    @GetMapping
    public ResponseEntity<?> consultarTodos() {
        List<Libro> libros = libroService.consultarTodos();
        return ResponseEntity.ok(Map.of(
                "mensaje", "Libros obtenidos correctamente",
                "total", libros.size(),
                "datos", libros));
    }

    @GetMapping("/titulo/{titulo}")
    public ResponseEntity<?> consultarPorTitulo(@PathVariable String titulo) {
        return libroService.consultarPorTitulo(titulo)
                .<ResponseEntity<?>>map(libro -> ResponseEntity.ok(Map.of(
                        "mensaje", "Libro encontrado",
                        "codigo", 200,
                        "datos", libro)))
                .orElseGet(() -> respuestaError("Libro no encontrado", HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody Libro libro) {
        if (!datosValidos(libro)) {
            return respuestaError("Datos inválidos", HttpStatus.BAD_REQUEST);
        }
        if (libroService.existeIsbn(libro.getIsbn(), id)) {
            return respuestaError("Ya existe un libro con ese ISBN", HttpStatus.CONFLICT);
        }
        return libroService.actualizar(id, libro)
                .<ResponseEntity<?>>map(actualizado -> ResponseEntity.ok(Map.of(
                        "mensaje", "Libro actualizado correctamente",
                        "codigo", 200,
                        "datos", actualizado)))
                .orElseGet(() -> respuestaError("Libro no encontrado", HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        return libroService.eliminar(id)
                ? ResponseEntity.noContent().build()
                : respuestaError("Libro no encontrado", HttpStatus.NOT_FOUND);
    }

    private boolean datosValidos(Libro libro) {
        return libro.getTitulo() != null && !libro.getTitulo().isBlank()
                && libro.getAutor() != null && !libro.getAutor().isBlank()
                && libro.getIsbn() != null && !libro.getIsbn().isBlank()
                && libro.getAnioPublicacion() != null && libro.getAnioPublicacion() > 0
                && libro.getEstado() != null && !libro.getEstado().isBlank();
    }

    private ResponseEntity<Map<String, Object>> respuestaError(String mensaje, HttpStatus estado) {
        return ResponseEntity.status(estado).body(Map.of(
                "mensaje", mensaje,
                "codigo", estado.value()));
    }
}
