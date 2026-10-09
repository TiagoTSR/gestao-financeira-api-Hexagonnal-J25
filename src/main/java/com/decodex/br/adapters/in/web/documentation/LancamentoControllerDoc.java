package com.decodex.br.adapters.in.web.documentation;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import com.decodex.br.application.dto.lancamento.LancamentoCreateDTO;
import com.decodex.br.application.dto.lancamento.LancamentoResponseDTO;
import com.decodex.br.application.dto.lancamento.LancamentoUpdateDTO;
import com.decodex.br.application.dto.lancamento.LancamentoFilter;
import com.decodex.br.domain.pagination.PageResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Lançamentos", description = "Endpoints para gerenciamento de lançamentos financeiros (receitas e despesas)")
public interface LancamentoControllerDoc {

    @Operation(summary = "Listar lançamentos com paginação e filtro", description = "Retorna uma página de lançamentos financeiros cadastrados, com suporte a filtros e ordenação.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lançamentos listados com sucesso")
    })
    ResponseEntity<PageResult<LancamentoResponseDTO>> findAll(
            @Parameter(description = "Filtro para busca de lançamentos") LancamentoFilter filter,
            @Parameter(description = "Número da página (inicia em 0)") int page,
            @Parameter(description = "Quantidade de elementos por página") int size,
            @Parameter(description = "Campo para ordenação (ex: id, descricao, dataVencimento, valor)") String sort,
            @Parameter(description = "Direção da ordenação (asc/desc)") String direction);

    @Operation(summary = "Buscar lançamento por ID", description = "Retorna um lançamento específico através de seu identificador.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lançamento encontrado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Lançamento não encontrado", content = @Content)
    })
    ResponseEntity<LancamentoResponseDTO> findById(
            @Parameter(description = "ID do lançamento a ser pesquisado", required = true) UUID id);

    @Operation(summary = "Criar novo lançamento", description = "Cria e retorna um novo lançamento financeiro baseado nos dados fornecidos.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Lançamento criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos", content = @Content)
    })
    ResponseEntity<LancamentoResponseDTO> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Dados para criação do lançamento", required = true) LancamentoCreateDTO dto);

    @Operation(summary = "Atualizar lançamento", description = "Atualiza os dados de um lançamento existente através do seu ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lançamento atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Lançamento não encontrado", content = @Content),
        @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos", content = @Content)
    })
    ResponseEntity<LancamentoResponseDTO> update(
            @Parameter(description = "ID do lançamento a ser atualizado", required = true) UUID id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Novos dados do lançamento", required = true) LancamentoUpdateDTO dto);

    @Operation(summary = "Excluir lançamento", description = "Remove um lançamento do sistema permanentemente através do ID informado.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Lançamento excluído com sucesso"),
        @ApiResponse(responseCode = "404", description = "Lançamento não encontrado", content = @Content)
    })
    ResponseEntity<Void> delete(
            @Parameter(description = "ID do lançamento a ser excluído", required = true) UUID id);

    @Operation(summary = "Dar baixa / quitar lançamento", description = "Altera o status do lançamento para PAGO ou RECEBIDO e registra data e valor do pagamento.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lançamento quitado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Lançamento não encontrado", content = @Content)
    })
    ResponseEntity<LancamentoResponseDTO> quitar(
            @Parameter(description = "ID do lançamento a ser quitado", required = true) UUID id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Dados complementares de quitação") com.decodex.br.application.dto.lancamento.LancamentoBaixaDTO baixaDTO);

    @Operation(summary = "Cancelar lançamento", description = "Altera o status do lançamento para CANCELADO.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lançamento cancelado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Lançamento não encontrado", content = @Content)
    })
    ResponseEntity<LancamentoResponseDTO> cancelar(
            @Parameter(description = "ID do lançamento a ser cancelado", required = true) UUID id);

    @Operation(summary = "Reabrir lançamento", description = "Reverte o status do lançamento para PENDENTE e limpa dados de pagamento.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lançamento reaberto com sucesso"),
        @ApiResponse(responseCode = "404", description = "Lançamento não encontrado", content = @Content)
    })
    ResponseEntity<LancamentoResponseDTO> reabrir(
            @Parameter(description = "ID do lançamento a ser reaberto", required = true) UUID id);
}
