package com.decodex.br.adapters.out.excel.writer;

import com.decodex.br.adapters.out.excel.style.ExcelStyleFactory.ExcelStyles;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public final class ExcelSheetUtils {

    public static final DateTimeFormatter DATA_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private ExcelSheetUtils() {
    }

    public static void configurarCelula(Row row, int colIndex, String valor, CellStyle style) {
        Cell c = row.createCell(colIndex);
        c.setCellValue(valor);
        c.setCellStyle(style);
    }

    public static void configurarCelula(Row row, int colIndex, double valor, CellStyle style) {
        Cell c = row.createCell(colIndex);
        c.setCellValue(valor);
        c.setCellStyle(style);
    }

    public static int escreverTitulo(XSSFSheet sheet, ExcelStyles estilos, int rowNum,
                                     String titulo, int ultimaColuna) {
        Row titleRow = sheet.createRow(rowNum++);
        titleRow.setHeightInPoints(30);
        configurarCelula(titleRow, 0, titulo, estilos.titulo);
        sheet.addMergedRegion(new CellRangeAddress(titleRow.getRowNum(), titleRow.getRowNum(), 0, ultimaColuna));
        return rowNum;
    }

    public static int escreverSubtituloPeriodo(XSSFSheet sheet, ExcelStyles estilos, int rowNum,
                                               LocalDate inicio, LocalDate fim, int ultimaColuna) {
        Row metaRow = sheet.createRow(rowNum++);
        metaRow.setHeightInPoints(20);
        String texto = String.format("Período: %s até %s",
                inicio.format(DATA_FORMATTER), fim.format(DATA_FORMATTER));
        configurarCelula(metaRow, 0, texto, estilos.subtitulo);
        sheet.addMergedRegion(new CellRangeAddress(metaRow.getRowNum(), metaRow.getRowNum(), 0, ultimaColuna));
        return rowNum;
    }

    public static int escreverCabecalho(XSSFSheet sheet, ExcelStyles estilos, int rowNum, String[] colunas) {
        Row headerRow = sheet.createRow(rowNum++);
        headerRow.setHeightInPoints(24);
        for (int i = 0; i < colunas.length; i++) {
            configurarCelula(headerRow, i, colunas[i], estilos.colunaHeader);
        }
        return rowNum;
    }

    public static Row criarLinhaTotal(XSSFSheet sheet, ExcelStyles estilos, int rowNum,
                                      int totalColunas, String rotulo, int colunaRotuloFim) {
        Row totalRow = sheet.createRow(rowNum);
        totalRow.setHeightInPoints(24);
        for (int i = 0; i < totalColunas; i++) {
            Cell c = totalRow.createCell(i);
            c.setCellStyle(estilos.totalRow);
        }
        totalRow.getCell(0).setCellValue(rotulo);
        if (colunaRotuloFim > 0) {
            sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, colunaRotuloFim));
        }
        return totalRow;
    }

    public static void ajustarLarguraColunas(XSSFSheet sheet, int totalColunas,
                                             int margemExtra, int larguraMinima) {
        for (int i = 0; i < totalColunas; i++) {
            sheet.autoSizeColumn(i);
            int width = sheet.getColumnWidth(i) + margemExtra;
            sheet.setColumnWidth(i, Math.max(width, larguraMinima));
        }
    }
}