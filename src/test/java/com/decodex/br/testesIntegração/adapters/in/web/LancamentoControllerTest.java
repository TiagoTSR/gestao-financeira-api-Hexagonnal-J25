package com.decodex.br.testesIntegração.adapters.in.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
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
import com.decodex.br.application.dto.lancamento.LancamentoCreateDTO;
import com.decodex.br.application.dto.lancamento.LancamentoUpdateDTO;
import com.decodex.br.domain.model.Categoria;
import com.decodex.br.domain.model.Endereco;
import com.decodex.br.domain.model.Lancamento;
import com.decodex.br.domain.model.Pessoa;
import com.decodex.br.domain.model.TipoLancamento;
import com.decodex.br.domain.port.in.CategoriaUseCase;
import com.decodex.br.domain.port.in.LancamentoUseCase;
import com.decodex.br.domain.port.in.PessoaUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@WebMvcTest(controllers = LancamentoController.class)
@DisplayName("Web - LancamentoController")
class LancamentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Configurado manualmente para evitar o erro de UnsatisfiedDependencyException
    private ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @MockitoBean
    private LancamentoUseCase lancamentoUseCase;

    @MockitoBean
    private CategoriaUseCase categoriaUseCase;

    @MockitoBean
    private PessoaUseCase pessoaUseCase;

    // ── HELPERS PARA RESOLVER A REGRA DE NEGÓCIO DE NULIDADE ────────────────

    private Endereco enderecoFake() {
        return new Endereco("Rua das Flores", "10", null, "Centro", "01000-000", "São Paulo", "SP");
    }

    private Pessoa pessoaFake(Long id, String nome) {
        // Agora sempre passamos um Endereco válido para não estourar a IllegalArgumentException
        return new Pessoa(id, nome, enderecoFake(), true);
    }

    private Categoria categoriaFake(Long id, String nome) {
        return new Categoria(id, nome);
    }

    // -------------------------------------------------------------------------
    // POST /lancamentos
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve retornar 201 Created ao criar lançamento válido")
    void create_DeveRetornar201() throws Exception {
        LancamentoCreateDTO requestDTO = new LancamentoCreateDTO(
            "Salário", LocalDate.of(2025, 6, 10), null, new BigDecimal("6500.00"),
            "Referente a maio", TipoLancamento.RECEITA, 1L, 1L
        );

        Categoria categoria = categoriaFake(1L, "Alimentação");
        Pessoa pessoa = pessoaFake(1L, "João Silva");
        
        Lancamento lancamentoSalvo = new Lancamento(
            1L, "Salário", LocalDate.of(2025, 6, 10), null, new BigDecimal("6500.00"),
            "Referente a maio", TipoLancamento.RECEITA, categoria, pessoa
        );

        when(categoriaUseCase.findById(1L)).thenReturn(categoria);
        when(pessoaUseCase.findById(1L)).thenReturn(pessoa);
        when(lancamentoUseCase.create(any(Lancamento.class))).thenReturn(lancamentoSalvo);

        mockMvc.perform(post("/lancamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", org.hamcrest.Matchers.endsWith("/lancamentos/1")))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.descricao").value("Salário"))
            .andExpect(jsonPath("$.dataVencimento").value("2025-06-10"))
            .andExpect(jsonPath("$.tipo").value("RECEITA"))
            .andExpect(jsonPath("$.categoria.nome").value("Alimentação"))
            .andExpect(jsonPath("$.pessoa.nome").value("João Silva"));
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request ao enviar lançamento sem descricao")
    void create_DeveRetornar400_QuandoDadosInvalidos() throws Exception {
        LancamentoCreateDTO requestDTO = new LancamentoCreateDTO(
            null, LocalDate.of(2025, 6, 10), null, new BigDecimal("6500.00"),
            null, TipoLancamento.RECEITA, 1L, 1L
        );

        mockMvc.perform(post("/lancamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.descricao").exists());
    }

    // -------------------------------------------------------------------------
    // GET /lancamentos/{id}
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve retornar 200 OK ao buscar lançamento existente")
    void findById_DeveRetornar200() throws Exception {
        Lancamento lancamento = new Lancamento(
            1L, "Cinema", LocalDate.of(2025, 6, 15), null, new BigDecimal("50.00"),
            null, TipoLancamento.DESPESA, categoriaFake(1L, "Lazer"), pessoaFake(1L, "Maria")
        );

        when(lancamentoUseCase.findById(1L)).thenReturn(lancamento);

        mockMvc.perform(get("/lancamentos/{id}", 1L))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.descricao").value("Cinema"))
            .andExpect(jsonPath("$.valor").value(50.00));
    }

    // -------------------------------------------------------------------------
    // GET /lancamentos
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve retornar 200 OK e listar os lançamentos")
    void findAll_DeveRetornar200() throws Exception {
        Lancamento lancamento = new Lancamento(
            1L, "Salário", LocalDate.of(2025, 6, 10), null, new BigDecimal("6500.00"),
            null, TipoLancamento.RECEITA, categoriaFake(1L, "Renda"), pessoaFake(1L, "João")
        );

        when(lancamentoUseCase.findAll()).thenReturn(List.of(lancamento));

        mockMvc.perform(get("/lancamentos"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].descricao").value("Salário"));
    }

    // -------------------------------------------------------------------------
    // PUT /lancamentos/{id}
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve retornar 200 OK ao atualizar lançamento")
    void update_DeveRetornar200() throws Exception {
        LancamentoUpdateDTO requestDTO = new LancamentoUpdateDTO(
            "Aluguel", LocalDate.of(2025, 6, 5), LocalDate.of(2025, 6, 5), new BigDecimal("1200.00"),
            null, TipoLancamento.DESPESA, 2L, 2L
        );

        Categoria categoria = categoriaFake(2L, "Moradia");
        Pessoa pessoa = pessoaFake(2L, "Proprietário");
        
        Lancamento lancamentoAtualizado = new Lancamento(
            1L, "Aluguel", LocalDate.of(2025, 6, 5), LocalDate.of(2025, 6, 5), new BigDecimal("1200.00"),
            null, TipoLancamento.DESPESA, categoria, pessoa
        );

        when(categoriaUseCase.findById(2L)).thenReturn(categoria);
        when(pessoaUseCase.findById(2L)).thenReturn(pessoa);
        when(lancamentoUseCase.update(eq(1L), any(Lancamento.class))).thenReturn(lancamentoAtualizado);

        mockMvc.perform(put("/lancamentos/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.descricao").value("Aluguel"))
            .andExpect(jsonPath("$.dataPagamento").value("2025-06-05"));
    }

    // -------------------------------------------------------------------------
    // DELETE /lancamentos/{id}
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve retornar 204 No Content ao deletar lançamento")
    void delete_DeveRetornar204() throws Exception {
        doNothing().when(lancamentoUseCase).delete(1L);

        mockMvc.perform(delete("/lancamentos/{id}", 1L))
            .andExpect(status().isNoContent());
    }
}