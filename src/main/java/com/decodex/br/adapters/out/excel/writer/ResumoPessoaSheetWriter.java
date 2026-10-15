package com.decodex.br.adapters.out.excel.writer;

import com.decodex.br.adapters.out.excel.style.ExcelStyleFactory.ExcelStyles;
import com.decodex.br.domain.model.LancamentoEstatisticaPessoa;
import com.decodex.br.domain.model.TipoLancamento;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public final class ResumoPessoaSheetWriter {

    private static final DateTimeFormatter DATA_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

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

        int rowNum = 0;

        // Título
        Row titleRow = sheet.createRow(rowNum++);
        titleRow.setHeightInPoints(30);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("CONSOLIDAÇÃO DE VALORES POR PESSOA");
        titleCell.setCellStyle(estilos.titulo);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));

        // Subtítulo
        Row metaRow = sheet.createRow(rowNum++);
        metaRow.setHeightInPoints(20);
        Cell metaCell = metaRow.createCell(0);
        metaCell.setCellValue(String.format("Período: %s até %s", inicio.format(DATA_FORMATTER), fim.format(DATA_FORMATTER)));
        metaCell.setCellStyle(estilos.subtitulo);
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 2));

        rowNum++; // Linha em branco

        // Cabeçalho
        Row headerRow = sheet.createRow(rowNum++);
        headerRow.setHeightInPoints(24);
        for (int i = 0; i < COLUNAS.length; i++) {
            Cell c = headerRow.createCell(i);
            c.setCellValue(COLUNAS[i]);
            c.setCellStyle(estilos.colunaHeader);
        }

        int startDataRow = rowNum + 1;
        for (LancamentoEstatisticaPessoa est : estatisticas) {
            Row r = sheet.createRow(rowNum++);
            r.setHeightInPoints(20);
            boolean zebra = (rowNum % 2 == 0);

            configurarCelula(r, 0, est.getPessoa() != null ? est.getPessoa().getNome() : "-", zebra ? estilos.textoZebra : estilos.textoPadrao);
            configurarCelula(r, 1, est.getTipo() != null ? est.getTipo().getDescricao() : "-", est.getTipo() != null && est.getTipo().isReceita() ? estilos.tipoReceita : estilos.tipoDespesa);
            configurarCelula(r, 2, est.getTotal() != null ? est.getTotal().doubleValue() : 0.0, zebra ? estilos.moedaZebra : estilos.moedaPadrao);
        }

        // Linha Total
        Row totalRow = sheet.createRow(rowNum++);
        totalRow.setHeightInPoints(24);
        for (int i = 0; i < COLUNAS.length; i++) {
            Cell c = totalRow.createCell(i);
            c.setCellStyle(estilos.totalRow);
        }
        totalRow.getCell(0).setCellValue("TOTAL GERAL:");
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 1));

        if (!estatisticas.isEmpty()) {
            Cell totalCell = totalRow.getCell(2);
            totalCell.setCellFormula(String.format("SUM(C%d:C%d)", startDataRow, rowNum - 1));
            totalCell.setCellStyle(estilos.totalMoeda);
        }

        ajustarLarguraColunas(sheet, COLUNAS.length, 1500, 3200);
    }

    private static void configurarCelula(Row row, int colIndex, String valor, CellStyle style) {
        Cell c = row.createCell(colIndex);
        c.setCellValue(valor);
        c.setCellStyle(style);
    }

    private static void configurarCelula(Row row, int colIndex, double valor, CellStyle style) {
        Cell c = row.createCell(colIndex);
        c.setCellValue(valor);
        c.setCellStyle(style);
    }

    private static void ajustarLarguraColunas(XSSFSheet sheet, int totalColunas, int margemExtra, int larguraMinima) {
        for (int i = 0; i < totalColunas; i++) {
            sheet.autoSizeColumn(i);
            int width = sheet.getColumnWidth(i) + margemExtra;
            sheet.setColumnWidth(i, Math.max(width, larguraMinima));
        }
    }
}
