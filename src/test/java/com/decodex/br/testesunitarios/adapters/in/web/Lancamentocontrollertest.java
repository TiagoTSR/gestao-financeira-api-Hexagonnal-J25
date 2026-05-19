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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.decodex.br.adapters.in.web.LancamentoController;
import com.decodex.br.domain.model.Categoria;
import com.decodex.br.domain.model.Endereco;
import com.decodex.br.domain.model.Lancamento;
import com.decodex.br.domain.model.Pessoa;
import com.decodex.br.domain.model.TipoLancamento;
import com.decodex.br.domain.port.in.CategoriaUseCase;
import com.decodex.br.domain.port.in.LancamentoUseCase;
import com.decodex.br.domain.port.in.PessoaUseCase;

@WebMvcTest(LancamentoController.class)
class LancamentoControllerTest {

 @Autowired
 private MockMvc mockMvc;

 @MockitoBean
 private LancamentoUseCase lancamentoUseCase;

 @MockitoBean
 private CategoriaUseCase categoriaUseCase;

 @MockitoBean
 private PessoaUseCase pessoaUseCase;

 // ── helpers ──────────────────────────────────────────────────────────────

 private Categoria categoriaFake() {
     return new Categoria(1L, "Lazer");
 }

 private Endereco enderecoFake() {
     return new Endereco("Rua A", "123", null, "Centro", "38400-000", "Uberlândia", "MG");
 }

 private Pessoa pessoaFake() {
     return new Pessoa(1L, "João Silva", enderecoFake(), true);
 }

 private Lancamento lancamentoFake() {
     return new Lancamento(
         1L,
         "Salário",
         LocalDate.of(2024, 6, 10),
         null,
         new BigDecimal("6500.00"),
         "Observação",
         TipoLancamento.RECEITA,
         categoriaFake(),
         pessoaFake()
     );
 }

 // -------------------------------------------------------------------------
 // GET /lancamentos
 // -------------------------------------------------------------------------

 @Test
 @DisplayName("GET /lancamentos → 200 com lista de lançamentos")
 void findAll_deveRetornarListaComStatus200() throws Exception {
     when(lancamentoUseCase.findAll()).thenReturn(List.of(lancamentoFake()));

     mockMvc.perform(get("/lancamentos"))
         .andExpect(status().isOk())
         .andExpect(jsonPath("$.length()").value(1))
         .andExpect(jsonPath("$[0].id").value(1))
         .andExpect(jsonPath("$[0].descricao").value("Salário"))
         .andExpect(jsonPath("$[0].tipo").value("RECEITA"));
 }

 @Test
 @DisplayName("GET /lancamentos → 200 com lista vazia")
 void findAll_deveRetornarListaVaziaComStatus200() throws Exception {
     when(lancamentoUseCase.findAll()).thenReturn(List.of());

     mockMvc.perform(get("/lancamentos"))
         .andExpect(status().isOk())
         .andExpect(jsonPath("$.length()").value(0));
 }

 // -------------------------------------------------------------------------
 // GET /lancamentos/{id}
 // -------------------------------------------------------------------------

 @Test
 @DisplayName("GET /lancamentos/{id} → 200 quando lançamento existe")
 void findById_deveRetornarLancamentoComStatus200() throws Exception {
     when(lancamentoUseCase.findById(1L)).thenReturn(lancamentoFake());

     mockMvc.perform(get("/lancamentos/1"))
         .andExpect(status().isOk())
         .andExpect(jsonPath("$.id").value(1))
         .andExpect(jsonPath("$.descricao").value("Salário"))
         .andExpect(jsonPath("$.valor").value(6500.00));
 }

 @Test
 @DisplayName("GET /lancamentos/{id} → 404 quando não existe")
 void findById_deveRetornar404QuandoNaoEncontrado() throws Exception {
     when(lancamentoUseCase.findById(99L))
         .thenThrow(new jakarta.persistence.EntityNotFoundException("Lançamento não encontrado"));

     mockMvc.perform(get("/lancamentos/99"))
         .andExpect(status().isNotFound());
 }

 // -------------------------------------------------------------------------
 // POST /lancamentos
 // -------------------------------------------------------------------------

 @Test
 @DisplayName("POST /lancamentos → 201 ao criar lançamento válido")
 void create_deveCriarLancamentoERetornar201() throws Exception {
     when(categoriaUseCase.findById(1L)).thenReturn(categoriaFake());
     when(pessoaUseCase.findById(1L)).thenReturn(pessoaFake());
     when(lancamentoUseCase.create(any(Lancamento.class))).thenReturn(lancamentoFake());

     String body = """
         {
           "descricao": "Salário",
           "dataVencimento": "2024-06-10",
           "valor": 6500.00,
           "tipo": "RECEITA",
           "categoriaId": 1,
           "pessoaId": 1
         }
         """;

     mockMvc.perform(post("/lancamentos")
             .contentType(MediaType.APPLICATION_JSON)
             .content(body))
         .andExpect(status().isCreated())
         .andExpect(header().string("Location", org.hamcrest.Matchers.containsString("/lancamentos/1")))
         .andExpect(jsonPath("$.id").value(1))
         .andExpect(jsonPath("$.descricao").value("Salário"));
 }

 @Test
 @DisplayName("POST /lancamentos → 400 quando campos obrigatórios ausentes")
 void create_deveRetornar400QuandoCamposObrigatoriosAusentes() throws Exception {
     String body = """
         {
           "descricao": "Salário"
         }
         """;

     mockMvc.perform(post("/lancamentos")
             .contentType(MediaType.APPLICATION_JSON)
             .content(body))
         .andExpect(status().isBadRequest());
 }

 @Test
 @DisplayName("POST /lancamentos → 400 quando body está ausente")
 void create_deveRetornar400QuandoBodyAusente() throws Exception {
     mockMvc.perform(post("/lancamentos")
             .contentType(MediaType.APPLICATION_JSON))
         .andExpect(status().isBadRequest());
 }

 // -------------------------------------------------------------------------
 // PUT /lancamentos/{id}
 // -------------------------------------------------------------------------

 @Test
 @DisplayName("PUT /lancamentos/{id} → 200 ao atualizar lançamento existente")
 void update_deveAtualizarLancamentoERetornar200() throws Exception {
     Lancamento atualizado = new Lancamento(
         1L, "Salário atualizado",
         LocalDate.of(2024, 7, 10), null,
         new BigDecimal("7000.00"), null,
         TipoLancamento.RECEITA, categoriaFake(), pessoaFake()
     );

     when(categoriaUseCase.findById(1L)).thenReturn(categoriaFake());
     when(pessoaUseCase.findById(1L)).thenReturn(pessoaFake());
     when(lancamentoUseCase.update(eq(1L), any(Lancamento.class))).thenReturn(atualizado);

     String body = """
         {
           "descricao": "Salário atualizado",
           "dataVencimento": "2024-07-10",
           "valor": 7000.00,
           "tipo": "RECEITA",
           "categoriaId": 1,
           "pessoaId": 1
         }
         """;

     mockMvc.perform(put("/lancamentos/1")
             .contentType(MediaType.APPLICATION_JSON)
             .content(body))
         .andExpect(status().isOk())
         .andExpect(jsonPath("$.descricao").value("Salário atualizado"))
         .andExpect(jsonPath("$.valor").value(7000.00));
 }

 @Test
 @DisplayName("PUT /lancamentos/{id} → 404 quando lançamento não existe")
 void update_deveRetornar404QuandoNaoEncontrado() throws Exception {
     when(categoriaUseCase.findById(1L)).thenReturn(categoriaFake());
     when(pessoaUseCase.findById(1L)).thenReturn(pessoaFake());
     when(lancamentoUseCase.update(eq(99L), any(Lancamento.class)))
         .thenThrow(new jakarta.persistence.EntityNotFoundException("Lançamento não encontrado"));

     String body = """
         {
           "descricao": "Qualquer",
           "dataVencimento": "2024-07-10",
           "valor": 100.00,
           "tipo": "DESPESA",
           "categoriaId": 1,
           "pessoaId": 1
         }
         """;

     mockMvc.perform(put("/lancamentos/99")
             .contentType(MediaType.APPLICATION_JSON)
             .content(body))
         .andExpect(status().isNotFound());
 }

 // -------------------------------------------------------------------------
 // DELETE /lancamentos/{id}
 // -------------------------------------------------------------------------

 @Test
 @DisplayName("DELETE /lancamentos/{id} → 204 ao deletar lançamento existente")
 void delete_deveRetornar204AoDeletar() throws Exception {
     doNothing().when(lancamentoUseCase).delete(1L);

     mockMvc.perform(delete("/lancamentos/1"))
         .andExpect(status().isNoContent());
 }

 @Test
 @DisplayName("DELETE /lancamentos/{id} → 404 quando lançamento não existe")
 void delete_deveRetornar404QuandoNaoEncontrado() throws Exception {
     doThrow(new jakarta.persistence.EntityNotFoundException("Lançamento não encontrado"))
         .when(lancamentoUseCase).delete(99L);

     mockMvc.perform(delete("/lancamentos/99"))
         .andExpect(status().isNotFound());
 }
}