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

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.decodex.br.adapters.in.web.PessoaController;
import com.decodex.br.application.dto.pessoa.PessoaCreateDTO;
import com.decodex.br.application.dto.pessoa.PessoaUpdateDTO;
import com.decodex.br.domain.filter.PessoaFilter;
import com.decodex.br.domain.model.Endereco;
import com.decodex.br.domain.model.Pessoa;
import com.decodex.br.domain.pagination.PageRequest;
import com.decodex.br.domain.pagination.PageResult;
import com.decodex.br.domain.port.in.PessoaUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@WebMvcTest(controllers = PessoaController.class)
@DisplayName("Web - PessoaController")
class PessoaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @MockitoBean
    private PessoaUseCase pessoaUseCase;

    @Test
    @DisplayName("Deve retornar 201 Created ao criar pessoa válida")
    void create_DeveRetornar201() throws Exception {
        PessoaCreateDTO requestDTO = new PessoaCreateDTO(
            "João Silva", "Rua das Flores", "10", null, "Centro",
            "01000-000", "São Paulo", "SP", true
        );

        Endereco endereco = new Endereco("Rua das Flores", "10", null, "Centro", "01000-000", "São Paulo", "SP");
        Pessoa pessoaSalva = new Pessoa(1L, "João Silva", endereco, true);

        when(pessoaUseCase.create(any(Pessoa.class))).thenReturn(pessoaSalva);

        mockMvc.perform(post("/pessoas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", org.hamcrest.Matchers.endsWith("/pessoas/1")))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.nome").value("João Silva"))
            .andExpect(jsonPath("$.logradouro").value("Rua das Flores"))
            .andExpect(jsonPath("$.cidade").value("São Paulo"))
            .andExpect(jsonPath("$.ativo").value(true));
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request ao enviar pessoa com dados incompletos")
    void create_DeveRetornar400_QuandoDadosInvalidos() throws Exception {
        PessoaCreateDTO requestDTO = new PessoaCreateDTO(
            "", "Rua das Flores", "10", null, "Centro",
            "01000-000", "São Paulo", null, true
        );

        mockMvc.perform(post("/pessoas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.nome").exists())
            .andExpect(jsonPath("$.estado").exists());
    }

    @Test
    @DisplayName("Deve retornar 200 OK ao buscar pessoa existente")
    void findById_DeveRetornar200() throws Exception {
        Endereco endereco = new Endereco("Rua X", "S/N", null, "Bairro Y", "12345-000", "Cidade Z", "MG");
        Pessoa pessoa = new Pessoa(1L, "Maria", endereco, true);

        when(pessoaUseCase.findById(1L)).thenReturn(pessoa);

        mockMvc.perform(get("/pessoas/{id}", 1L))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.nome").value("Maria"))
            .andExpect(jsonPath("$.logradouro").value("Rua X"));
    }

    @Test
    @DisplayName("Deve retornar 200 OK e listar pessoas paginadas")
    void findAll_DeveRetornar200() throws Exception {
        Endereco endereco = new Endereco("Rua X", "123", null, "Bairro Y", "12345-000", "Cidade Z", "MG");
        Pessoa pessoa = new Pessoa(1L, "Maria", endereco, false);

        PageResult<Pessoa> pageResult = new PageResult<>(
            List.of(pessoa), 0, 10, 1L, 1
        );
        when(pessoaUseCase.findAll(any(PessoaFilter.class), any(PageRequest.class)))
        .thenReturn(pageResult);

    mockMvc.perform(get("/pessoas")
            .param("page", "0")
            .param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(1));
}
    @Test
    @DisplayName("Deve retornar 200 OK ao atualizar pessoa")
    void update_DeveRetornar200() throws Exception {
        PessoaUpdateDTO requestDTO = new PessoaUpdateDTO(
            "Maria Atualizada", "Rua Nova", "100", "Apt 2", "Centro",
            "11111-000", "Belo Horizonte", "MG", false
        );

        Endereco enderecoAtualizado = new Endereco("Rua Nova", "100", "Apt 2", "Centro", "11111-000", "Belo Horizonte", "MG");
        Pessoa pessoaAtualizada = new Pessoa(1L, "Maria Atualizada", enderecoAtualizado, false);

        when(pessoaUseCase.update(eq(1L), any(Pessoa.class))).thenReturn(pessoaAtualizada);

        mockMvc.perform(put("/pessoas/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nome").value("Maria Atualizada"))
            .andExpect(jsonPath("$.logradouro").value("Rua Nova"))
            .andExpect(jsonPath("$.ativo").value(false));
    }

    @Test
    @DisplayName("Deve retornar 204 No Content ao deletar pessoa")
    void delete_DeveRetornar204() throws Exception {
        doNothing().when(pessoaUseCase).delete(1L);

        mockMvc.perform(delete("/pessoas/{id}", 1L))
            .andExpect(status().isNoContent());
    }
}