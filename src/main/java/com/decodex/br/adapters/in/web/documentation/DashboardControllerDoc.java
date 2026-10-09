package com.decodex.br.adapters.in.web.documentation;

import java.time.LocalDate;

import org.springframework.http.ResponseEntity;

import com.decodex.br.domain.model.DashboardResumo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Dashboard", description = "Endpoints para obtenção de métricas, indicadores e fluxo financeiro")
public interface DashboardControllerDoc {

    @Operation(summary = "Obter resumo do dashboard financeiro", description = "Retorna os totais de receitas, despesas, saldo, pendências, vencimentos, distribuição por categoria e fluxo mensal.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Resumo do dashboard calculado com sucesso")
    })
    ResponseEntity<DashboardResumo> obterDashboard(
        @Parameter(description = "Data ou mês de referência (formato YYYY-MM-DD). Se não informado, utiliza o mês atual.")
        LocalDate mes
    );
}
