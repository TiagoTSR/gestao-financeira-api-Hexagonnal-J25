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

import com.decodex.br.adapters.in.web.PessoaController;
import com.decodex.br.domain.exeption.ResourceNotFoundException;
import com.decodex.br.domain.model.Endereco;
import com.decodex.br.domain.model.Pessoa;
import com.decodex.br.domain.port.in.PessoaUseCase;

@WebMvcTest(PessoaController.class)
class PessoaControllerTest {

 @Autowired
 private MockMvc mockMvc;

 @MockitoBean
 private PessoaUseCase pessoaUseCase;

 // ── helpers ────────────────────────────────────────────────────────────────

 private Endereco enderecoFake() {
     return new Endereco("Rua do Abacaxi", "10", null, "Brasil", "38400-121", "Uberlândia", "MG");
 }

 private Pessoa pessoaFake() {
     return new Pessoa(1L, "João Silva", enderecoFake(), true);
 }

 // -------------------------------------------------------------------------
 // GET /pessoas
 // -------------------------------------------------------------------------

 @Test
 @DisplayName("GET /pessoas → 200 com lista de pessoas")
 void findAll_deveRetornarListaComStatus200() throws Exception {
     Endereco enderecoMaria = new Endereco("Rua do Sabiá", "110", "Apto 101", "Colina", "11400-121", "Ribeirão Preto", "SP");
     List<Pessoa> pessoas = List.of(
         pessoaFake(),
         new Pessoa(2L, "Maria Rita", enderecoMaria, true)
     );
     when(pessoaUseCase.findAll()).thenReturn(pessoas);

     mockMvc.perform(get("/pessoas"))
         .andExpect(status().isOk())
         .andExpect(jsonPath("$.length()").value(2))
         .andExpect(jsonPath("$[0].id").value(1))
         .andExpect(jsonPath("$[0].nome").value("João Silva"))
         .andExpect(jsonPath("$[1].nome").value("Maria Rita"));
 }

 @Test
 @DisplayName("GET /pessoas → 200 com lista vazia")
 void findAll_deveRetornarListaVaziaComStatus200() throws Exception {
     when(pessoaUseCase.findAll()).thenReturn(List.of());

     mockMvc.perform(get("/pessoas"))
         .andExpect(status().isOk())
         .andExpect(jsonPath("$.length()").value(0));
 }

 // -------------------------------------------------------------------------
 // GET /pessoas/{id}
 // -------------------------------------------------------------------------

 @Test
 @DisplayName("GET /pessoas/{id} → 200 quando pessoa existe")
 void findById_deveRetornarPessoaComStatus200() throws Exception {
     when(pessoaUseCase.findById(1L)).thenReturn(pessoaFake());

     mockMvc.perform(get("/pessoas/1"))
         .andExpect(status().isOk())
         .andExpect(jsonPath("$.id").value(1))
         .andExpect(jsonPath("$.nome").value("João Silva"))
         .andExpect(jsonPath("$.cidade").value("Uberlândia"))
         .andExpect(jsonPath("$.ativo").value(true));
 }

 @Test
 @DisplayName("GET /pessoas/{id} → 404 quando pessoa não existe")
 void findById_deveRetornar404QuandoNaoEncontrada() throws Exception {
     when(pessoaUseCase.findById(99L))
         .thenThrow(new jakarta.persistence.EntityNotFoundException("Pessoa não encontrada"));

     mockMvc.perform(get("/pessoas/99"))
         .andExpect(status().isNotFound());
 }

 // -------------------------------------------------------------------------
 // POST /pessoas
 // -------------------------------------------------------------------------

 @Test
 @DisplayName("POST /pessoas → 201 ao criar pessoa válida")
 void create_deveCriarPessoaERetornar201() throws Exception {
     when(pessoaUseCase.create(any(Pessoa.class))).thenReturn(pessoaFake());

     String body = """
         {
           "nome": "João Silva",
           "logradouro": "Rua do Abacaxi",
           "numero": "10",
           "bairro": "Brasil",
           "cep": "38.400-121",
           "cidade": "Uberlândia",
           "estado": "MG",
           "ativo": true
         }
         """;

     mockMvc.perform(post("/pessoas")
             .contentType(MediaType.APPLICATION_JSON)
             .content(body))
         .andExpect(status().isCreated())
         .andExpect(header().string("Location", org.hamcrest.Matchers.containsString("/pessoas/1")))
         .andExpect(jsonPath("$.id").value(1))
         .andExpect(jsonPath("$.nome").value("João Silva"));
 }

 @Test
 @DisplayName("POST /pessoas → 400 quando nome está em branco")
 void create_deveRetornar400QuandoNomeEmBranco() throws Exception {
     String body = """
         {
           "nome": "",
           "ativo": true
         }
         """;

     mockMvc.perform(post("/pessoas")
             .contentType(MediaType.APPLICATION_JSON)
             .content(body))
         .andExpect(status().isBadRequest());
 }

 @Test
 @DisplayName("POST /pessoas → 400 quando body está ausente")
 void create_deveRetornar400QuandoBodyAusente() throws Exception {
     mockMvc.perform(post("/pessoas")
             .contentType(MediaType.APPLICATION_JSON))
         .andExpect(status().isBadRequest());
 }

 // -------------------------------------------------------------------------
 // PUT /pessoas/{id}
 // -------------------------------------------------------------------------

 @Test
 @DisplayName("PUT /pessoas/{id} → 200 ao atualizar pessoa existente")
 void update_deveAtualizarPessoaERetornar200() throws Exception {
     Endereco enderecoAtualizado = new Endereco("Av Brasil", "200", null, "Centro", "38400-000", "Belo Horizonte", "MG");
     Pessoa atualizada = new Pessoa(1L, "João Silva Atualizado", enderecoAtualizado, true);
     when(pessoaUseCase.update(eq(1L), any(Pessoa.class))).thenReturn(atualizada);

     String body = """
         {
           "nome": "João Silva Atualizado",
           "logradouro": "Av Brasil",
           "numero": "200",
           "bairro": "Centro",
           "cep": "38.400-000",
           "cidade": "Belo Horizonte",
           "estado": "MG",
           "ativo": true
         }
         """;

     mockMvc.perform(put("/pessoas/1")
             .contentType(MediaType.APPLICATION_JSON)
             .content(body))
         .andExpect(status().isOk())
         .andExpect(jsonPath("$.nome").value("João Silva Atualizado"))
         .andExpect(jsonPath("$.cidade").value("Belo Horizonte"));
 }

 @Test
 @DisplayName("PUT /pessoas/{id} → 404 quando pessoa não existe")
 void update_deveRetornar404QuandoNaoEncontrada() throws Exception {
     // Corpo completo e válido
     String body = """
         {
           "nome": "Qualquer",
           "logradouro": "Rua X",
           "numero": "123",
           "bairro": "Centro",
           "cep": "12345-678",
           "cidade": "São Paulo",
           "estado": "SP",
           "ativo": true
         }
         """;

     when(pessoaUseCase.update(eq(99L), any(Pessoa.class)))
         .thenThrow(new ResourceNotFoundException("Pessoa não encontrada"));

     mockMvc.perform(put("/pessoas/99")
             .contentType(MediaType.APPLICATION_JSON)
             .content(body))
         .andExpect(status().isNotFound());
 }

 @Test
 @DisplayName("PUT /pessoas/{id} → 400 quando nome está em branco")
 void update_deveRetornar400QuandoNomeEmBranco() throws Exception {
     String body = """
         {
           "nome": "",
           "ativo": true
         }
         """;

     mockMvc.perform(put("/pessoas/1")
             .contentType(MediaType.APPLICATION_JSON)
             .content(body))
         .andExpect(status().isBadRequest());
 }

 // -------------------------------------------------------------------------
 // DELETE /pessoas/{id}
 // -------------------------------------------------------------------------

 @Test
 @DisplayName("DELETE /pessoas/{id} → 204 ao deletar pessoa existente")
 void delete_deveRetornar204AoDeletar() throws Exception {
     doNothing().when(pessoaUseCase).delete(1L);

     mockMvc.perform(delete("/pessoas/1"))
         .andExpect(status().isNoContent());
 }

 @Test
 @DisplayName("DELETE /pessoas/{id} → 404 quando pessoa não existe")
 void delete_deveRetornar404QuandoNaoEncontrada() throws Exception {
     doThrow(new jakarta.persistence.EntityNotFoundException("Pessoa não encontrada"))
         .when(pessoaUseCase).delete(99L);

     mockMvc.perform(delete("/pessoas/99"))
         .andExpect(status().isNotFound());
 }
}