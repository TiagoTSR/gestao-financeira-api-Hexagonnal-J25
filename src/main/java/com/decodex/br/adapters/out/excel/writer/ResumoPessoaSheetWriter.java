package com.decodex.br.adapters.out.excel.writer;

import com.decodex.br.adapters.out.excel.style.ExcelStyleFactory.ExcelStyles;
import com.decodex.br.domain.model.LancamentoEstatisticaPessoa;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.time.LocalDate;
import java.util.List;

public final class ResumoPessoaSheetWriter {

    private static final String[] COLUNAS = {
            "Pessoa", "Tipo de Lançamento", "Total Consolidado (R$)"
    };

    private ResumoPessoaSheetWriter() {
    }

    public static void escrever(
            XSSFWorkbook workbook, ExcelStyles estilos,
            List<LancamentoEstatisticaPessoa> estatisticas, LocalDate inicio, LocalDate fim) {

        XSSFSheet sheet = workbook.createSheet("Resumo por Pessoa");
        sheet.setDisplayGridlines(true);

        int ultimaColuna = COLUNAS.length - 1;

        int rowNum = 0;
        rowNum = ExcelSheetUtils.escreverTitulo(sheet, estilos, rowNum,
                "CONSOLIDAÇÃO DE VALORES POR PESSOA", ultimaColuna);
        rowNum = ExcelSheetUtils.escreverSubtituloPeriodo(sheet, estilos, rowNum, inicio, fim, ultimaColuna);

        rowNum++; // Linha em branco

        rowNum = ExcelSheetUtils.escreverCabecalho(sheet, estilos, rowNum, COLUNAS);

        int startDataRow = rowNum + 1;
        for (LancamentoEstatisticaPessoa est : estatisticas) {
            Row r = sheet.createRow(rowNum++);
            r.setHeightInPoints(20);
            boolean zebra = (rowNum % 2 == 0);

            ExcelSheetUtils.configurarCelula(r, 0,
                    est.getPessoa() != null ? est.getPessoa().getNome() : "-",
                    zebra ? estilos.textoZebra : estilos.textoPadrao);
            ExcelSheetUtils.configurarCelula(r, 1,
                    est.getTipo() != null ? est.getTipo().getDescricao() : "-",
                    est.getTipo() != null && est.getTipo().isReceita() ? estilos.tipoReceita : estilos.tipoDespesa);
            ExcelSheetUtils.configurarCelula(r, 2,
                    est.getTotal() != null ? est.getTotal().doubleValue() : 0.0,
                    zebra ? estilos.moedaZebra : estilos.moedaPadrao);
        }

        // Linha Total
        Row totalRow = ExcelSheetUtils.criarLinhaTotal(sheet, estilos, rowNum++, COLUNAS.length,
                "TOTAL GERAL:", ultimaColuna - 1);

        if (!estatisticas.isEmpty()) {
            Cell totalCell = totalRow.getCell(2);
            totalCell.setCellFormula(String.format("SUM(C%d:C%d)", startDataRow, rowNum - 1));
            totalCell.setCellStyle(estilos.totalMoeda);
        }

        ExcelSheetUtils.ajustarLarguraColunas(sheet, COLUNAS.length, 1500, 3200);
    }
}