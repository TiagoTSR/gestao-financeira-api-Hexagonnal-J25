package com.decodex.br.adapters.in.web.documentation;

import org.springframework.http.ResponseEntity;
import com.decodex.br.application.dto.categoria.CategoriaCreateDTO;
import com.decodex.br.application.dto.categoria.CategoriaResponseDTO;
import com.decodex.br.application.dto.categoria.CategoriaUpdateDTO;
import com.decodex.br.domain.filter.CategoriaFilter;
import com.decodex.br.domain.pagination.PageResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Categorias", description = "Endpoints para gerenciamento de categorias de lançamentos")
public interface CategoriaControllerDoc {

    @Operation(summary = "Listar categorias com paginação e filtro", description = "Retorna uma página de categorias cadastradas, com suporte a filtros de nome.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Categorias listadas com sucesso")
    })
    PageResult<CategoriaResponseDTO> findAll(
            @Parameter(description = "Número da página (inicia em 0)") int page,
            @Parameter(description = "Quantidade de elementos por página") int size,
            @Parameter(description = "Filtro para busca de categorias") CategoriaFilter filter);

    @Operation(summary = "Buscar categoria por ID", description = "Retorna uma categoria específica através de seu identificador.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Categoria encontrada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Categoria não encontrada", content = @Content)
    })
    ResponseEntity<CategoriaResponseDTO> findById(
            @Parameter(description = "ID da categoria a ser pesquisada", required = true) Long id);

    @Operation(summary = "Criar nova categoria", description = "Cria e retorna uma nova categoria baseada nos dados fornecidos.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Categoria criada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos", content = @Content)
    })
    ResponseEntity<CategoriaResponseDTO> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Dados para criação da nova categoria", required = true) CategoriaCreateDTO dto);

    @Operation(summary = "Atualizar categoria", description = "Atualiza os dados de uma categoria existente através do seu ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Categoria atualizada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Categoria não encontrada", content = @Content),
        @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos", content = @Content)
    })
    ResponseEntity<CategoriaResponseDTO> update(
            @Parameter(description = "ID da categoria a ser atualizada", required = true) Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Novos dados da categoria", required = true) CategoriaUpdateDTO dto);

    @Operation(summary = "Excluir categoria", description = "Remove uma categoria do sistema permanentemente através do ID informado.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Categoria excluída com sucesso"),
        @ApiResponse(responseCode = "404", description = "Categoria não encontrada", content = @Content)
    })
    ResponseEntity<Void> delete(
            @Parameter(description = "ID da categoria a ser excluída", required = true) Long id);
}
