package com.decodex.br.adapters.in.web.documentation;

import org.springframework.http.ResponseEntity;
import com.decodex.br.application.dto.pessoa.PessoaCreateDTO;
import com.decodex.br.application.dto.pessoa.PessoaResponseDTO;
import com.decodex.br.application.dto.pessoa.PessoaUpdateDTO;
import com.decodex.br.domain.filter.PessoaFilter;
import com.decodex.br.domain.pagination.PageResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Pessoas", description = "Endpoints para gerenciamento de pessoas (clientes, fornecedores, etc.)")
public interface PessoaControllerDoc {

    @Operation(summary = "Listar pessoas com paginação e filtro", description = "Retorna uma página de pessoas cadastradas, com suporte a filtros de nome.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pessoas listadas com sucesso")
    })
    PageResult<PessoaResponseDTO> findAll(
            @Parameter(description = "Filtro para busca de pessoas") PessoaFilter filter,
            @Parameter(description = "Número da página (inicia em 0)") int page,
            @Parameter(description = "Quantidade de elementos por página") int size);

    @Operation(summary = "Buscar pessoa por ID", description = "Retorna uma pessoa específica através de seu identificador.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pessoa encontrada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Pessoa não encontrada", content = @Content)
    })
    ResponseEntity<PessoaResponseDTO> findById(
            @Parameter(description = "ID da pessoa a ser pesquisada", required = true) Long id);

    @Operation(summary = "Criar nova pessoa", description = "Cria e retorna uma nova pessoa baseada nos dados fornecidos.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Pessoa criada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos", content = @Content)
    })
    ResponseEntity<PessoaResponseDTO> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Dados para criação da nova pessoa", required = true) PessoaCreateDTO dto);

    @Operation(summary = "Atualizar pessoa", description = "Atualiza os dados de uma pessoa existente através do seu ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pessoa atualizada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Pessoa não encontrada", content = @Content),
        @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos", content = @Content)
    })
    ResponseEntity<PessoaResponseDTO> update(
            @Parameter(description = "ID da pessoa a ser atualizada", required = true) Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Novos dados da pessoa", required = true) PessoaUpdateDTO dto);

    @Operation(summary = "Excluir pessoa", description = "Remove uma pessoa do sistema permanentemente através do ID informado.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Pessoa excluída com sucesso"),
        @ApiResponse(responseCode = "404", description = "Pessoa não encontrada", content = @Content)
    })
    ResponseEntity<Void> delete(
            @Parameter(description = "ID da pessoa a ser excluída", required = true) Long id);
}
