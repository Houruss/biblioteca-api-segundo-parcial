package com.antony.biblioteca.controller;

import com.antony.biblioteca.model.Libro;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class LibroControllerTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper objectMapper;

    @Test
    void flujoCompleto() throws Exception {
        Libro libro = new Libro(null, "Cien años de soledad", "Gabriel García Márquez",
                "9780307474728", 1967, "Disponible");

        mvc.perform(post("/api/libros").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(libro)))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.datos.id").value(1));
        mvc.perform(get("/api/libros")).andExpect(status().isOk());
        mvc.perform(get("/api/libros/titulo/Cien")).andExpect(status().isOk());
        mvc.perform(put("/api/libros/1").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(libro)))
                .andExpect(status().isOk());
        mvc.perform(delete("/api/libros/1")).andExpect(status().isNoContent());
    }
}
