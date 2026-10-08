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
import java.util.UUID;

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
import com.decodex.br.application.dto.lancamento.LancamentoFilter;
import com.decodex.br.domain.model.Categoria;
import com.decodex.br.domain.model.Endereco;
import com.decodex.br.domain.model.Lancamento;
import com.decodex.br.domain.model.Pessoa;
import com.decodex.br.domain.model.TipoLancamento;
import com.decodex.br.domain.pagination.PageRequest;
import com.decodex.br.domain.pagination.PageResult;
import com.decodex.br.domain.port.in.LancamentoInputPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@WebMvcTest(controllers = LancamentoController.class)
@org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc(addFilters = false)
@DisplayName("Web - LancamentoController")
class LancamentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @MockitoBean
    private LancamentoInputPort lancamentoInputPort;

    @MockitoBean
    private com.decodex.br.config.security.TokenService tokenService;

    @MockitoBean
    private com.decodex.br.config.security.JpaUserDetailsService jpaUserDetailsService;

    @MockitoBean
    private com.decodex.br.config.ratelimit.RateLimitingFilter rateLimitingFilter;

    private Endereco enderecoFake() {
        return new Endereco("Rua das Flores", "10", null, "Centro", "01000-000", "São Paulo", "SP");
    }

    private Pessoa pessoaFake(UUID id, String nome) {
        return new Pessoa(id, nome, enderecoFake(), true);
    }

    private Categoria categoriaFake(UUID id, String nome) {
        return new Categoria(id, nome);
    }

    @Test
    @DisplayName("Deve retornar 201 Created ao criar lançamento válido")
    void create_DeveRetornar201() throws Exception {
        UUID lancId = UUID.randomUUID();
        UUID catId = UUID.randomUUID();
        UUID pesId = UUID.randomUUID();

        LancamentoCreateDTO requestDTO = new LancamentoCreateDTO(
            "Salário", LocalDate.of(2025, 6, 10), null, new BigDecimal("6500.00"),
            "Referente a maio", TipoLancamento.RECEITA, catId, pesId
        );

        Categoria categoria = categoriaFake(catId, "Alimentação");
        Pessoa pessoa = pessoaFake(pesId, "João Silva");
        
        Lancamento lancamentoSalvo = new Lancamento(
            lancId, "Salário", LocalDate.of(2025, 6, 10), null, new BigDecimal("6500.00"),
            "Referente a maio", TipoLancamento.RECEITA, categoria, pessoa
        );

        when(lancamentoInputPort.create(any(LancamentoCreateDTO.class))).thenReturn(lancamentoSalvo);

        mockMvc.perform(post("/lancamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", org.hamcrest.Matchers.endsWith("/lancamentos/" + lancId)))
            .andExpect(jsonPath("$.id").value(lancId.toString()))
            .andExpect(jsonPath("$.descricao").value("Salário"));
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request ao enviar lançamento sem descricao")
    void create_DeveRetornar400_QuandoDadosInvalidos() throws Exception {
        UUID catId = UUID.randomUUID();
        UUID pesId = UUID.randomUUID();

        LancamentoCreateDTO requestDTO = new LancamentoCreateDTO(
            null, LocalDate.of(2025, 6, 10), null, new BigDecimal("6500.00"),
            null, TipoLancamento.RECEITA, catId, pesId
        );

        mockMvc.perform(post("/lancamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar 200 OK ao buscar lançamento existente")
    void findById_DeveRetornar200() throws Exception {
        UUID lancId = UUID.randomUUID();
        UUID catId = UUID.randomUUID();
        UUID pesId = UUID.randomUUID();

        Lancamento lancamento = new Lancamento(
            lancId, "Cinema", LocalDate.of(2025, 6, 15), null, new BigDecimal("50.00"),
            null, TipoLancamento.DESPESA, categoriaFake(catId, "Lazer"), pessoaFake(pesId, "Maria")
        );

        when(lancamentoInputPort.findById(lancId)).thenReturn(lancamento);

        mockMvc.perform(get("/lancamentos/{id}", lancId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(lancId.toString()));
    }

    @Test
    @DisplayName("Deve retornar 200 OK e listar lançamentos paginados")
    void findAll_DeveRetornar200() throws Exception {
        UUID lancId = UUID.randomUUID();
        UUID catId = UUID.randomUUID();
        UUID pesId = UUID.randomUUID();

        Lancamento lancamento = new Lancamento(
            lancId, "Salário", LocalDate.of(2025, 6, 10), null, new BigDecimal("6500.00"),
            null, TipoLancamento.RECEITA, categoriaFake(catId, "Renda"), pessoaFake(pesId, "João")
        );

        PageResult<Lancamento> pageResult = new PageResult<>(
                List.of(lancamento), 0, 10, 1L, 1
            );

            when(lancamentoInputPort.findAll(any(LancamentoFilter.class), any(PageRequest.class)))
                .thenReturn(pageResult);

            mockMvc.perform(get("/lancamentos")
                    .param("page", "0")
                    .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].descricao").value("Salário"));
        }

    @Test
    @DisplayName("Deve retornar 200 OK ao atualizar lançamento")
    void update_DeveRetornar200() throws Exception {
        UUID lancId = UUID.randomUUID();
        UUID catId = UUID.randomUUID();
        UUID pesId = UUID.randomUUID();

        LancamentoUpdateDTO requestDTO = new LancamentoUpdateDTO(
            "Aluguel", LocalDate.of(2025, 6, 5), LocalDate.of(2025, 6, 5), new BigDecimal("1200.00"),
            null, TipoLancamento.DESPESA, catId, pesId
        );

        Categoria categoria = categoriaFake(catId, "Moradia");
        Pessoa pessoa = pessoaFake(pesId, "Proprietário");
        
        Lancamento lancamentoAtualizado = new Lancamento(
            lancId, "Aluguel", LocalDate.of(2025, 6, 5), LocalDate.of(2025, 6, 5), new BigDecimal("1200.00"),
            null, TipoLancamento.DESPESA, categoria, pessoa
        );

        when(lancamentoInputPort.update(eq(lancId), any(LancamentoUpdateDTO.class))).thenReturn(lancamentoAtualizado);

        mockMvc.perform(put("/lancamentos/{id}", lancId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.descricao").value("Aluguel"));
    }

    @Test
    @DisplayName("Deve retornar 204 No Content ao deletar lançamento")
    void delete_DeveRetornar204() throws Exception {
        UUID lancId = UUID.randomUUID();
        doNothing().when(lancamentoInputPort).delete(lancId);
        mockMvc.perform(delete("/lancamentos/{id}", lancId))
            .andExpect(status().isNoContent());
    }
}