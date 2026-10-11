package com.decodex.br.adapters.out.excel.style;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public final class ExcelStyleFactory {

    private ExcelStyleFactory() {
    }

    public static ExcelStyles criar(XSSFWorkbook workbook) {
        DataFormat df = workbook.createDataFormat();
        short moedaFormat = df.getFormat("R$ #,##0.00;[Red]-R$ #,##0.00;\"R$ 0,00\"");

        ExcelStyles e = new ExcelStyles();

        // Fontes
        XSSFFont fonteTitulo = criarFonte(workbook, "Segoe UI", (short) 13, true, new byte[]{(byte) 255, (byte) 255, (byte) 255}, false);
        XSSFFont fonteSubtitulo = criarFonte(workbook, "Segoe UI", (short) 9, false, new byte[]{(byte) 100, (byte) 116, (byte) 139}, true);
        XSSFFont fonteHeader = criarFonte(workbook, "Segoe UI", (short) 10, true, new byte[]{(byte) 255, (byte) 255, (byte) 255}, false);
        XSSFFont fontePadrao = criarFonte(workbook, "Segoe UI", (short) 9, false, null, false);
        XSSFFont fonteNegrito = criarFonte(workbook, "Segoe UI", (short) 9, true, null, false);
        XSSFFont fonteReceita = criarFonte(workbook, "Segoe UI", (short) 9, true, new byte[]{(byte) 22, (byte) 163, (byte) 74}, false);
        XSSFFont fonteDespesa = criarFonte(workbook, "Segoe UI", (short) 9, true, new byte[]{(byte) 220, (byte) 38, (byte) 38}, false);
        XSSFFont fonteKpiHeader = criarFonte(workbook, "Segoe UI", (short) 8, true, null, false);
        XSSFFont fonteKpiValor = criarFonte(workbook, "Segoe UI", (short) 12, true, null, false);

        // Cores
        XSSFColor corNavy = new XSSFColor(new byte[]{(byte) 30, (byte) 41, (byte) 59}, null);
        XSSFColor corHeaderTabela = new XSSFColor(new byte[]{(byte) 51, (byte) 65, (byte) 85}, null);
        XSSFColor corZebra = new XSSFColor(new byte[]{(byte) 248, (byte) 250, (byte) 252}, null);
        XSSFColor corTotal = new XSSFColor(new byte[]{(byte) 241, (byte) 245, (byte) 249}, null);

        // Título e Subtítulo
        e.titulo = workbook.createCellStyle();
        e.titulo.setFont(fonteTitulo);
        e.titulo.setFillForegroundColor(corNavy);
        e.titulo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        e.titulo.setAlignment(HorizontalAlignment.LEFT);
        e.titulo.setVerticalAlignment(VerticalAlignment.CENTER);

        e.subtitulo = workbook.createCellStyle();
        e.subtitulo.setFont(fonteSubtitulo);
        e.subtitulo.setVerticalAlignment(VerticalAlignment.CENTER);

        // Header de Colunas
        e.colunaHeader = workbook.createCellStyle();
        e.colunaHeader.setFont(fonteHeader);
        e.colunaHeader.setFillForegroundColor(corHeaderTabela);
        e.colunaHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        e.colunaHeader.setAlignment(HorizontalAlignment.CENTER);
        e.colunaHeader.setVerticalAlignment(VerticalAlignment.CENTER);
        aplicarBordas(e.colunaHeader);

        // KPIs
        e.kpiHeaderReceita = criarEstiloKpi(workbook, fonteKpiHeader, HorizontalAlignment.CENTER, new XSSFColor(new byte[]{(byte) 240, (byte) 253, (byte) 244}, null));
        e.kpiHeaderDespesa = criarEstiloKpi(workbook, fonteKpiHeader, HorizontalAlignment.CENTER, new XSSFColor(new byte[]{(byte) 254, (byte) 242, (byte) 242}, null));
        e.kpiHeaderSaldo = criarEstiloKpi(workbook, fonteKpiHeader, HorizontalAlignment.CENTER, new XSSFColor(new byte[]{(byte) 248, (byte) 250, (byte) 252}, null));

        e.kpiValorReceita = criarEstiloKpi(workbook, fonteKpiValor, HorizontalAlignment.CENTER, new XSSFColor(new byte[]{(byte) 240, (byte) 253, (byte) 244}, null));
        e.kpiValorReceita.setDataFormat(moedaFormat);
        e.kpiValorDespesa = criarEstiloKpi(workbook, fonteKpiValor, HorizontalAlignment.CENTER, new XSSFColor(new byte[]{(byte) 254, (byte) 242, (byte) 242}, null));
        e.kpiValorDespesa.setDataFormat(moedaFormat);
        e.kpiValorSaldo = criarEstiloKpi(workbook, fonteKpiValor, HorizontalAlignment.CENTER, new XSSFColor(new byte[]{(byte) 248, (byte) 250, (byte) 252}, null));
        e.kpiValorSaldo.setDataFormat(moedaFormat);

        // Texto Padrão e Zebra
        e.textoPadrao = criarEstiloDado(workbook, fontePadrao, HorizontalAlignment.LEFT, null);
        e.textoZebra = criarEstiloDado(workbook, fontePadrao, HorizontalAlignment.LEFT, corZebra);

        e.dataPadrao = criarEstiloDado(workbook, fontePadrao, HorizontalAlignment.CENTER, null);
        e.dataZebra = criarEstiloDado(workbook, fontePadrao, HorizontalAlignment.CENTER, corZebra);

        e.centroPadrao = criarEstiloDado(workbook, fontePadrao, HorizontalAlignment.CENTER, null);
        e.centroZebra = criarEstiloDado(workbook, fontePadrao, HorizontalAlignment.CENTER, corZebra);

        // Tipos (Receita / Despesa)
        e.tipoReceita = criarEstiloDado(workbook, fonteReceita, HorizontalAlignment.CENTER, null);
        e.tipoDespesa = criarEstiloDado(workbook, fonteDespesa, HorizontalAlignment.CENTER, null);

        // Formatação Monetária
        e.moedaPadrao = criarEstiloDado(workbook, fontePadrao, HorizontalAlignment.RIGHT, null);
        e.moedaPadrao.setDataFormat(moedaFormat);
        e.moedaZebra = criarEstiloDado(workbook, fontePadrao, HorizontalAlignment.RIGHT, corZebra);
        e.moedaZebra.setDataFormat(moedaFormat);

        e.moedaReceita = criarEstiloDado(workbook, fonteReceita, HorizontalAlignment.RIGHT, null);
        e.moedaReceita.setDataFormat(moedaFormat);
        e.moedaReceitaZebra = criarEstiloDado(workbook, fonteReceita, HorizontalAlignment.RIGHT, corZebra);
        e.moedaReceitaZebra.setDataFormat(moedaFormat);

        e.moedaDespesa = criarEstiloDado(workbook, fonteDespesa, HorizontalAlignment.RIGHT, null);
        e.moedaDespesa.setDataFormat(moedaFormat);
        e.moedaDespesaZebra = criarEstiloDado(workbook, fonteDespesa, HorizontalAlignment.RIGHT, corZebra);
        e.moedaDespesaZebra.setDataFormat(moedaFormat);

        // Totais e Fórmulas
        e.totalRow = workbook.createCellStyle();
        e.totalRow.setFont(fonteNegrito);
        e.totalRow.setFillForegroundColor(corTotal);
        e.totalRow.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        e.totalRow.setAlignment(HorizontalAlignment.RIGHT);
        e.totalRow.setVerticalAlignment(VerticalAlignment.CENTER);
        e.totalRow.setBorderTop(BorderStyle.THIN);
        e.totalRow.setBorderBottom(BorderStyle.DOUBLE);

        e.totalMoeda = workbook.createCellStyle();
        e.totalMoeda.cloneStyleFrom(e.totalRow);
        e.totalMoeda.setDataFormat(moedaFormat);

        return e;
    }

    private static XSSFFont criarFonte(XSSFWorkbook workbook, String nome, short tamanho, boolean negrito, byte[] rgb, boolean italico) {
        XSSFFont fonte = workbook.createFont();
        fonte.setFontName(nome);
        fonte.setFontHeightInPoints(tamanho);
        fonte.setBold(negrito);
        fonte.setItalic(italico);
        if (rgb != null) {
            fonte.setColor(new XSSFColor(rgb, null));
        }
        return fonte;
    }

    private static CellStyle criarEstiloKpi(XSSFWorkbook workbook, XSSFFont font, HorizontalAlignment align, XSSFColor bg) {
        CellStyle s = workbook.createCellStyle();
        s.setFont(font);
        s.setAlignment(align);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        s.setFillForegroundColor(bg);
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        aplicarBordas(s);
        return s;
    }

    private static CellStyle criarEstiloDado(XSSFWorkbook workbook, XSSFFont font, HorizontalAlignment align, XSSFColor bg) {
        CellStyle s = workbook.createCellStyle();
        s.setFont(font);
        s.setAlignment(align);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        if (bg != null) {
            s.setFillForegroundColor(bg);
            s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        aplicarBordas(s);
        return s;
    }

    private static void aplicarBordas(CellStyle s) {
        s.setBorderTop(BorderStyle.THIN);
        s.setBorderBottom(BorderStyle.THIN);
        s.setBorderLeft(BorderStyle.THIN);
        s.setBorderRight(BorderStyle.THIN);
    }

    public static class ExcelStyles {
        public CellStyle titulo;
        public CellStyle subtitulo;
        public CellStyle colunaHeader;
        public CellStyle kpiHeaderReceita;
        public CellStyle kpiHeaderDespesa;
        public CellStyle kpiHeaderSaldo;
        public CellStyle kpiValorReceita;
        public CellStyle kpiValorDespesa;
        public CellStyle kpiValorSaldo;
        public CellStyle textoPadrao;
        public CellStyle textoZebra;
        public CellStyle dataPadrao;
        public CellStyle dataZebra;
        public CellStyle centroPadrao;
        public CellStyle centroZebra;
        public CellStyle tipoReceita;
        public CellStyle tipoDespesa;
        public CellStyle moedaPadrao;
        public CellStyle moedaZebra;
        public CellStyle moedaReceita;
        public CellStyle moedaReceitaZebra;
        public CellStyle moedaDespesa;
        public CellStyle moedaDespesaZebra;
        public CellStyle totalRow;
        public CellStyle totalMoeda;
    }
}
