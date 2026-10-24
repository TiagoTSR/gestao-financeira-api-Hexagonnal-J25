package com.decodex.br.testesunitarios.adapters.out.pdf;

import com.decodex.br.adapters.out.pdf.OpenHtmlToPdfAdapter;
import com.decodex.br.domain.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class OpenHtmlToPdfAdapterTest {

    @Autowired
    private OpenHtmlToPdfAdapter adapter;

    @Test
    void testGerarRelatorio() {
        Pessoa p = new Pessoa(UUID.randomUUID(), "Teste");
        Categoria c = new Categoria(UUID.randomUUID(), "Alimentacao");
        Lancamento l = new Lancamento(UUID.randomUUID(), "Almoco", LocalDate.now(), LocalDate.now(),
                new BigDecimal("50.00"), "", TipoLancamento.DESPESA, c, p, StatusLancamento.PAGO, new BigDecimal("50.00"), 1, 1);
        LancamentoEstatisticaPessoa est = new LancamentoEstatisticaPessoa(TipoLancamento.DESPESA, p, new BigDecimal("50.00"));

        byte[] pdf = adapter.gerarRelatorioLancamentosPorPessoa(List.of(est), List.of(l), LocalDate.now().minusDays(10), LocalDate.now());
        assertNotNull(pdf);
    }
}
