package com.decodex.br.adapters.in.web;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.decodex.br.adapters.in.web.documentation.DashboardControllerDoc;
import com.decodex.br.domain.model.DashboardResumo;
import com.decodex.br.domain.port.in.DashboardInputPort;

@RestController
@RequestMapping("/dashboard")
public class DashboardController implements DashboardControllerDoc {

    private final DashboardInputPort dashboardInputPort;

    public DashboardController(DashboardInputPort dashboardInputPort) {
        this.dashboardInputPort = dashboardInputPort;
    }

    @Override
    @GetMapping
    public ResponseEntity<DashboardResumo> obterDashboard(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate mes) {
        DashboardResumo resumo = dashboardInputPort.obterDashboard(mes);
        return ResponseEntity.ok(resumo);
    }
}
