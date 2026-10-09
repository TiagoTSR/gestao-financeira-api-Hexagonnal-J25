package com.decodex.br.testesunitarios.adapters.in.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.decodex.br.adapters.in.web.DashboardController;
import com.decodex.br.domain.model.DashboardResumo;
import com.decodex.br.domain.port.in.DashboardInputPort;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes unitários para DashboardController")
class DashboardControllerUnitTest {

    @Mock
    private DashboardInputPort dashboardInputPort;

    @InjectMocks
    private DashboardController controller;

    @Test
    @DisplayName("Deve retornar 200 OK com resumo do dashboard")
    void deveRetornar200ComResumo() {
        LocalDate mes = LocalDate.of(2026, 10, 1);
        DashboardResumo resumoFake = new DashboardResumo(
                BigDecimal.valueOf(5000), BigDecimal.valueOf(2000), BigDecimal.valueOf(3000),
                BigDecimal.valueOf(3000), BigDecimal.ZERO, BigDecimal.ZERO, 0,
                List.of(), List.of(), List.of()
        );

        when(dashboardInputPort.obterDashboard(mes)).thenReturn(resumoFake);

        ResponseEntity<DashboardResumo> response = controller.obterDashboard(mes);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(resumoFake, response.getBody());
    }
}
