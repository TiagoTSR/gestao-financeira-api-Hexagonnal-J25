package com.decodex.br.testesunitarios.adapters.out.excel;

import com.decodex.br.adapters.out.excel.ApachePoiExcelAdapter;
import com.decodex.br.domain.model.Categoria;
import com.decodex.br.domain.model.Lancamento;
import com.decodex.br.domain.model.LancamentoEstatisticaPessoa;
import com.decodex.br.domain.model.Pessoa;
import com.decodex.br.domain.model.StatusLancamento;
import com.decodex.br.domain.model.TipoLancamento;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitários - ApachePoiExcelAdapter")
class ApachePoiExcelAdapterTest {

    private final ApachePoiExcelAdapter adapter = new ApachePoiExcelAdapter();

    @Test
    @DisplayName("Deve gerar arquivo Excel (.xlsx) válido com duas abas formatadas")
    void deveGerarArquivoExcelValido() throws IOException {
        Pessoa pessoa = new Pessoa(UUID.randomUUID(), "Maria Oliveira");
        Categoria categoria = new Categoria(UUID.randomUUID(), "Alimentação");

        Lancamento lancamento = new Lancamento(
                UUID.randomUUID(),
                "Supermercado Mensal",
                LocalDate.of(2026, 10, 15),
                LocalDate.of(2026, 10, 14),
                new BigDecimal("650.00"),
                "Compras do mês",
                TipoLancamento.DESPESA,
                categoria,
                pessoa,
                StatusLancamento.PAGO,
                new BigDecimal("650.00"),
                1,
                1
        );

        LancamentoEstatisticaPessoa est = new LancamentoEstatisticaPessoa(
                TipoLancamento.DESPESA, pessoa, new BigDecimal("650.00")
        );

        byte[] bytes = adapter.gerarRelatorioLancamentosExcel(
                List.of(est),
                List.of(lancamento),
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 10, 31)
        );

        assertNotNull(bytes);
        assertTrue(bytes.length > 0);

        try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(bytes))) {
            assertEquals(2, workbook.getNumberOfSheets());
            assertEquals("Lançamentos Detalhados", workbook.getSheetAt(0).getSheetName());
            assertEquals("Resumo por Pessoa", workbook.getSheetAt(1).getSheetName());

            var sheetLancamentos = workbook.getSheetAt(0);
            assertNotNull(sheetLancamentos.getRow(0)); // Banner
            assertTrue(sheetLancamentos.getRow(0).getCell(0).getStringCellValue().contains("GESTÃO FINANCEIRA"));
        }
    }
}
