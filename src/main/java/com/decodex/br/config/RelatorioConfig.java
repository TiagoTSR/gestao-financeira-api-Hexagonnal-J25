package com.decodex.br.config;

import com.decodex.br.domain.port.out.LancamentoEstatisticaPort;
import com.decodex.br.domain.port.out.RelatorioPdfPort;
import com.decodex.br.domain.service.EstatisticaRelatorioService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RelatorioConfig {

    @Bean
    public EstatisticaRelatorioService estatisticaRelatorioService(
            LancamentoEstatisticaPort estatisticaPort,
            RelatorioPdfPort relatorioPdfPort) {

        return new EstatisticaRelatorioService(estatisticaPort, relatorioPdfPort);
    }
}