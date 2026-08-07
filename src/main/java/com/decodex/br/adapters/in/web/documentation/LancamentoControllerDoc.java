package com.decodex.br.adapters.in.web.documentation;

import org.springframework.http.ResponseEntity;
import com.decodex.br.application.dto.lancamento.LancamentoCreateDTO;
import com.decodex.br.application.dto.lancamento.LancamentoResponseDTO;
import com.decodex.br.application.dto.lancamento.LancamentoUpdateDTO;
import com.decodex.br.domain.filter.LancamentoFilter;
import com.decodex.br.domain.pagination.PageResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Lançamentos", description = "Endpoints para gerenciamento de lançamentos financeiros (receitas e despesas)")
public interface LancamentoControllerDoc {

    @Operation(summary = "Listar lançamentos com paginação e filtro", description = "Retorna uma página de lançamentos de acordo com os filtros de descrição, datas, pessoa e categoria.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lançamentos listados com sucesso")
    })
    PageResult<LancamentoResponseDTO> findAll(
            @Parameter(description = "Número da página (inicia em 0)") int page,
            @Parameter(description = "Quantidade de elementos por página") int size,
            @Parameter(description = "Filtro para busca de lançamentos") LancamentoFilter filter);

    @Operation(summary = "Buscar lançamento por ID", description = "Retorna um lançamento específico através de seu identificador.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lançamento encontrado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Lançamento não encontrado", content = @Content)
    })
    ResponseEntity<LancamentoResponseDTO> findById(
            @Parameter(description = "ID do lançamento a ser pesquisado", required = true) Long id);

    @Operation(summary = "Criar novo lançamento", description = "Cria e retorna um novo lançamento financeiro baseado nos dados fornecidos.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Lançamento criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos (ex: Categoria ou Pessoa não encontradas)", content = @Content)
    })
    ResponseEntity<LancamentoResponseDTO> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Dados para criação do novo lançamento", required = true) LancamentoCreateDTO dto);

    @Operation(summary = "Atualizar lançamento", description = "Atualiza os dados de um lançamento existente através do seu ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lançamento atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Lançamento não encontrado", content = @Content),
        @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos", content = @Content)
    })
    ResponseEntity<LancamentoResponseDTO> update(
            @Parameter(description = "ID do lançamento a ser atualizado", required = true) Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Novos dados do lançamento", required = true) LancamentoUpdateDTO dto);

    @Operation(summary = "Excluir lançamento", description = "Remove um lançamento do sistema permanentemente através do ID informado.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Lançamento excluído com sucesso"),
        @ApiResponse(responseCode = "404", description = "Lançamento não encontrado", content = @Content)
    })
    ResponseEntity<Void> delete(
            @Parameter(description = "ID do lançamento a ser excluído", required = true) Long id);
}
