package com.decodex.br.testesunitarios.adapters.in.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.decodex.br.adapters.in.web.CategoriaController;
import com.decodex.br.domain.model.Categoria;
import com.decodex.br.domain.port.in.CategoriaUseCase;

@WebMvcTest(CategoriaController.class)
class CategoriaControllerTest {

 @Autowired
 private MockMvc mockMvc;

 @MockitoBean
 private CategoriaUseCase categoriaUseCase;

 // -------------------------------------------------------------------------
 // GET /categorias
 // -------------------------------------------------------------------------

 @Test
 @DisplayName("GET /categorias → 200 com lista de categorias")
 void findAll_deveRetornarListaDeCategoriasComStatus200() throws Exception {
     List<Categoria> categorias = List.of(
         new Categoria(1L, "Lazer"),
         new Categoria(2L, "Alimentação")
     );
     when(categoriaUseCase.findAll()).thenReturn(categorias);

     mockMvc.perform(get("/categorias"))
         .andExpect(status().isOk())
         .andExpect(jsonPath("$.length()").value(2))
         .andExpect(jsonPath("$[0].id").value(1))
         .andExpect(jsonPath("$[0].nome").value("Lazer"))
         .andExpect(jsonPath("$[1].id").value(2))
         .andExpect(jsonPath("$[1].nome").value("Alimentação"));
 }

 @Test
 @DisplayName("GET /categorias → 200 com lista vazia")
 void findAll_deveRetornarListaVaziaComStatus200() throws Exception {
     when(categoriaUseCase.findAll()).thenReturn(List.of());

     mockMvc.perform(get("/categorias"))
         .andExpect(status().isOk())
         .andExpect(jsonPath("$.length()").value(0));
 }

 // -------------------------------------------------------------------------
 // GET /categorias/{id}
 // -------------------------------------------------------------------------

 @Test
 @DisplayName("GET /categorias/{id} → 200 quando categoria existe")
 void findById_deveRetornarCategoriaComStatus200() throws Exception {
     Categoria categoria = new Categoria(1L, "Lazer");
     when(categoriaUseCase.findById(1L)).thenReturn(categoria);

     mockMvc.perform(get("/categorias/1"))
         .andExpect(status().isOk())
         .andExpect(jsonPath("$.id").value(1))
         .andExpect(jsonPath("$.nome").value("Lazer"));
 }

 @Test
 @DisplayName("GET /categorias/{id} → 404 quando categoria não existe")
 void findById_deveRetornar404QuandoNaoEncontrada() throws Exception {
     when(categoriaUseCase.findById(99L))
         .thenThrow(new jakarta.persistence.EntityNotFoundException("Categoria não encontrada"));

     mockMvc.perform(get("/categorias/99"))
         .andExpect(status().isNotFound());
 }

 // -------------------------------------------------------------------------
 // POST /categorias
 // -------------------------------------------------------------------------

 @Test
 @DisplayName("POST /categorias → 201 com Location header ao criar categoria")
 void create_deveCriarCategoriaERetornar201() throws Exception {
     Categoria criada = new Categoria(1L, "Lazer");
     when(categoriaUseCase.create(any(Categoria.class))).thenReturn(criada);

     String body = """
         { "nome": "Lazer" }
         """;

     mockMvc.perform(post("/categorias")
             .contentType(MediaType.APPLICATION_JSON)
             .content(body))
         .andExpect(status().isCreated())
         .andExpect(header().string("Location", org.hamcrest.Matchers.containsString("/categorias/1")))
         .andExpect(jsonPath("$.id").value(1))
         .andExpect(jsonPath("$.nome").value("Lazer"));
 }

 @Test
 @DisplayName("POST /categorias → 400 quando nome está em branco")
 void create_deveRetornar400QuandoNomeEmBranco() throws Exception {
     String body = """
         { "nome": "" }
         """;

     mockMvc.perform(post("/categorias")
             .contentType(MediaType.APPLICATION_JSON)
             .content(body))
         .andExpect(status().isBadRequest());
 }

 @Test
 @DisplayName("POST /categorias → 400 quando body está ausente")
 void create_deveRetornar400QuandoBodyAusente() throws Exception {
     mockMvc.perform(post("/categorias")
             .contentType(MediaType.APPLICATION_JSON))
         .andExpect(status().isBadRequest());
 }

 // -------------------------------------------------------------------------
 // PUT /categorias/{id}
 // -------------------------------------------------------------------------

 @Test
 @DisplayName("PUT /categorias/{id} → 200 ao atualizar categoria existente")
 void update_deveAtualizarCategoriaERetornar200() throws Exception {
     Categoria atualizada = new Categoria(1L, "Supermercado");
     when(categoriaUseCase.update(eq(1L), any(Categoria.class))).thenReturn(atualizada);

     String body = """
         { "nome": "Supermercado" }
         """;

     mockMvc.perform(put("/categorias/1")
             .contentType(MediaType.APPLICATION_JSON)
             .content(body))
         .andExpect(status().isOk())
         .andExpect(jsonPath("$.id").value(1))
         .andExpect(jsonPath("$.nome").value("Supermercado"));
 }

 @Test
 @DisplayName("PUT /categorias/{id} → 404 quando categoria não existe")
 void update_deveRetornar404QuandoNaoEncontrada() throws Exception {
     when(categoriaUseCase.update(eq(99L), any(Categoria.class)))
         .thenThrow(new jakarta.persistence.EntityNotFoundException("Categoria não encontrada"));

     String body = """
         { "nome": "Qualquer" }
         """;

     mockMvc.perform(put("/categorias/99")
             .contentType(MediaType.APPLICATION_JSON)
             .content(body))
         .andExpect(status().isNotFound());
 }

 @Test
 @DisplayName("PUT /categorias/{id} → 400 quando nome está em branco")
 void update_deveRetornar400QuandoNomeEmBranco() throws Exception {
     String body = """
         { "nome": "" }
         """;

     mockMvc.perform(put("/categorias/1")
             .contentType(MediaType.APPLICATION_JSON)
             .content(body))
         .andExpect(status().isBadRequest());
 }

 // -------------------------------------------------------------------------
 // DELETE /categorias/{id}
 // -------------------------------------------------------------------------

 @Test
 @DisplayName("DELETE /categorias/{id} → 204 ao deletar categoria existente")
 void delete_deveRetornar204AoDeletar() throws Exception {
     doNothing().when(categoriaUseCase).delete(1L);

     mockMvc.perform(delete("/categorias/1"))
         .andExpect(status().isNoContent());
 }

 @Test
 @DisplayName("DELETE /categorias/{id} → 404 quando categoria não existe")
 void delete_deveRetornar404QuandoNaoEncontrada() throws Exception {
     doThrow(new jakarta.persistence.EntityNotFoundException("Categoria não encontrada"))
         .when(categoriaUseCase).delete(99L);

     mockMvc.perform(delete("/categorias/99"))
         .andExpect(status().isNotFound());
 }
}