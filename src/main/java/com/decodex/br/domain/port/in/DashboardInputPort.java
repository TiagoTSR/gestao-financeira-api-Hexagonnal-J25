package com.decodex.br.domain.port.in;

import java.time.LocalDate;

import com.decodex.br.domain.model.DashboardResumo;

public interface DashboardInputPort {
    DashboardResumo obterDashboard(LocalDate mesReferencia);
}
