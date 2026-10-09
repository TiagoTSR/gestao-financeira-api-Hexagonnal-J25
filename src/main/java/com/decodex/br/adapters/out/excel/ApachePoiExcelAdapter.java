package com.decodex.br.adapters.out.excel;

import com.decodex.br.domain.model.Lancamento;
import com.decodex.br.domain.model.LancamentoEstatisticaPessoa;
import com.decodex.br.domain.model.TipoLancamento;
import com.decodex.br.domain.port.out.RelatorioExcelPort;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class ApachePoiExcelAdapter implements RelatorioExcelPort {

    private static final DateTimeFormatter DATA_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATA_HORA_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm:ss");

    @Override
    public byte[] gerarRelatorioLancamentosExcel(
            List<LancamentoEstatisticaPessoa> estatisticas,
            List<Lancamento> lancamentos,
            LocalDate inicio,
            LocalDate fim) {

        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            // Estilos
            EstilosExcel estilos = criarEstilos(workbook);

            // Aba 1: Lançamentos Detalhados
            criarAbaLancamentosDetalhados(workbook, estilos, lancamentos, inicio, fim);

            // Aba 2: Resumo por Pessoa
            criarAbaResumoPessoa(workbook, estilos, estatisticas, inicio, fim);

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar relatório Excel de lançamentos", e);
        }
    }

    private void criarAbaLancamentosDetalhados(
            XSSFWorkbook workbook, EstilosExcel estilos,
            List<Lancamento> lancamentos, LocalDate inicio, LocalDate fim) {

        XSSFSheet sheet = workbook.createSheet("Lançamentos Detalhados");
        sheet.setDisplayGridlines(true);

        int rowNum = 0;

        // 1. Banner de Título
        Row titleRow = sheet.createRow(rowNum++);
        titleRow.setHeightInPoints(32);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("GESTÃO FINANCEIRA - RELATÓRIO DETALHADO DE LANÇAMENTOS");
        titleCell.setCellStyle(estilos.titulo);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 9));

        // 2. Metadados / Período
        Row metaRow = sheet.createRow(rowNum++);
        metaRow.setHeightInPoints(20);
        Cell metaCell = metaRow.createCell(0);
        String periodoTexto = String.format("Período: %s até %s  |  Gerado em: %s  |  Total de Registros: %d",
                inicio.format(DATA_FORMATTER), fim.format(DATA_FORMATTER),
                LocalDateTime.now().format(DATA_HORA_FORMATTER), lancamentos.size());
        metaCell.setCellValue(periodoTexto);
        metaCell.setCellStyle(estilos.subtitulo);
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 9));

        rowNum++; // Linha em branco

        // 3. Cards de Resumo Financeiro
        BigDecimal totalReceitas = lancamentos.stream()
                .filter(l -> l.getTipo() == TipoLancamento.RECEITA)
                .map(Lancamento::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDespesas = lancamentos.stream()
                .filter(l -> l.getTipo() == TipoLancamento.DESPESA)
                .map(Lancamento::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal saldoLiquido = totalReceitas.subtract(totalDespesas);

        Row kpiHeaderRow = sheet.createRow(rowNum++);
        kpiHeaderRow.setHeightInPoints(18);
        criarKpiHeaderCell(kpiHeaderRow, 0, "RECEITAS TOTAIS", estilos.kpiHeaderReceita);
        criarKpiHeaderCell(kpiHeaderRow, 3, "DESPESAS TOTAIS", estilos.kpiHeaderDespesa);
        criarKpiHeaderCell(kpiHeaderRow, 6, "SALDO LÍQUIDO", estilos.kpiHeaderSaldo);
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 2));
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 3, 5));
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 6, 8));

        Row kpiValueRow = sheet.createRow(rowNum++);
        kpiValueRow.setHeightInPoints(24);
        criarKpiValueCell(kpiValueRow, 0, totalReceitas.doubleValue(), estilos.kpiValorReceita);
        criarKpiValueCell(kpiValueRow, 3, totalDespesas.doubleValue(), estilos.kpiValorDespesa);
        criarKpiValueCell(kpiValueRow, 6, saldoLiquido.doubleValue(), estilos.kpiValorSaldo);
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 2));
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 3, 5));
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 6, 8));

        rowNum++; // Linha em branco

        // 4. Cabeçalho das Colunas de Dados
        String[] colunas = {
                "Vencimento", "Pagamento", "Pessoa", "Categoria", "Descrição",
                "Parcela", "Tipo", "Status", "Valor (R$)", "Valor Pago (R$)"
        };

        Row headerRow = sheet.createRow(rowNum++);
        headerRow.setHeightInPoints(24);
        for (int i = 0; i < colunas.length; i++) {
            Cell c = headerRow.createCell(i);
            c.setCellValue(colunas[i]);
            c.setCellStyle(estilos.colunaHeader);
        }

        // Congelar painel abaixo do cabeçalho
        sheet.createFreezePane(0, rowNum);

        // 5. Linhas de Dados
        int startDataRow = rowNum + 1;
        for (Lancamento lancamento : lancamentos) {
            Row r = sheet.createRow(rowNum++);
            r.setHeightInPoints(20);
            boolean zebra = (rowNum % 2 == 0);

            // Vencimento
            Cell c0 = r.createCell(0);
            if (lancamento.getDataVencimento() != null) {
                c0.setCellValue(lancamento.getDataVencimento().format(DATA_FORMATTER));
            }
            c0.setCellStyle(zebra ? estilos.dataZebra : estilos.dataPadrao);

            // Pagamento
            Cell c1 = r.createCell(1);
            if (lancamento.getDataPagamento() != null) {
                c1.setCellValue(lancamento.getDataPagamento().format(DATA_FORMATTER));
            } else {
                c1.setCellValue("-");
            }
            c1.setCellStyle(zebra ? estilos.dataZebra : estilos.dataPadrao);

            // Pessoa
            Cell c2 = r.createCell(2);
            c2.setCellValue(lancamento.getPessoa() != null ? lancamento.getPessoa().getNome() : "-");
            c2.setCellStyle(zebra ? estilos.textoZebra : estilos.textoPadrao);

            // Categoria
            Cell c3 = r.createCell(3);
            c3.setCellValue(lancamento.getCategoria() != null ? lancamento.getCategoria().getNome() : "-");
            c3.setCellStyle(zebra ? estilos.textoZebra : estilos.textoPadrao);

            // Descrição
            Cell c4 = r.createCell(4);
            c4.setCellValue(lancamento.getDescricao() != null ? lancamento.getDescricao() : "");
            c4.setCellStyle(zebra ? estilos.textoZebra : estilos.textoPadrao);

            // Parcela
            Cell c5 = r.createCell(5);
            if (lancamento.getTotalParcelas() != null && lancamento.getTotalParcelas() > 1) {
                int num = lancamento.getNumeroParcela() != null ? lancamento.getNumeroParcela() : 1;
                c5.setCellValue(num + "/" + lancamento.getTotalParcelas());
            } else {
                c5.setCellValue("Única");
            }
            c5.setCellStyle(zebra ? estilos.centroZebra : estilos.centroPadrao);

            // Tipo
            Cell c6 = r.createCell(6);
            c6.setCellValue(lancamento.getTipo() != null ? lancamento.getTipo().getDescricao() : "-");
            c6.setCellStyle(lancamento.getTipo() == TipoLancamento.RECEITA ? estilos.tipoReceita : estilos.tipoDespesa);

            // Status
            Cell c7 = r.createCell(7);
            c7.setCellValue(lancamento.getStatus() != null ? lancamento.getStatus().getDescricao() : "Pendente");
            c7.setCellStyle(zebra ? estilos.centroZebra : estilos.centroPadrao);

            // Valor
            Cell c8 = r.createCell(8);
            if (lancamento.getValor() != null) {
                c8.setCellValue(lancamento.getValor().doubleValue());
            } else {
                c8.setCellValue(0.0);
            }
            c8.setCellStyle(lancamento.getTipo() == TipoLancamento.RECEITA
                    ? (zebra ? estilos.moedaReceitaZebra : estilos.moedaReceita)
                    : (zebra ? estilos.moedaDespesaZebra : estilos.moedaDespesa));

            // Valor Pago
            Cell c9 = r.createCell(9);
            if (lancamento.getValorPago() != null) {
                c9.setCellValue(lancamento.getValorPago().doubleValue());
            } else {
                c9.setCellValue(0.0);
            }
            c9.setCellStyle(zebra ? estilos.moedaZebra : estilos.moedaPadrao);
        }

        // 6. Linha de Totais da Tabela
        Row totalRow = sheet.createRow(rowNum++);
        totalRow.setHeightInPoints(24);
        for (int i = 0; i < colunas.length; i++) {
            Cell c = totalRow.createCell(i);
            c.setCellStyle(estilos.totalRow);
        }
        totalRow.getCell(0).setCellValue("TOTAL:");
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 7));

        if (!lancamentos.isEmpty()) {
            Cell totalValorCell = totalRow.getCell(8);
            totalValorCell.setCellFormula(String.format("SUM(I%d:I%d)", startDataRow, rowNum - 1));
            totalValorCell.setCellStyle(estilos.totalMoeda);

            Cell totalPagoCell = totalRow.getCell(9);
            totalPagoCell.setCellFormula(String.format("SUM(J%d:J%d)", startDataRow, rowNum - 1));
            totalPagoCell.setCellStyle(estilos.totalMoeda);
        }

        // Autoajustar largura das colunas
        for (int i = 0; i < colunas.length; i++) {
            sheet.autoSizeColumn(i);
            int width = sheet.getColumnWidth(i) + 1200;
            sheet.setColumnWidth(i, Math.max(width, 3200));
        }
    }

    private void criarAbaResumoPessoa(
            XSSFWorkbook workbook, EstilosExcel estilos,
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

        Row metaRow = sheet.createRow(rowNum++);
        metaRow.setHeightInPoints(20);
        Cell metaCell = metaRow.createCell(0);
        metaCell.setCellValue(String.format("Período: %s até %s", inicio.format(DATA_FORMATTER), fim.format(DATA_FORMATTER)));
        metaCell.setCellStyle(estilos.subtitulo);
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 2));

        rowNum++; // Linha em branco

        // Cabeçalho da Tabela
        Row headerRow = sheet.createRow(rowNum++);
        headerRow.setHeightInPoints(24);

        String[] cols = {"Pessoa", "Tipo de Lançamento", "Total Consolidado (R$)"};
        for (int i = 0; i < cols.length; i++) {
            Cell c = headerRow.createCell(i);
            c.setCellValue(cols[i]);
            c.setCellStyle(estilos.colunaHeader);
        }

        int startDataRow = rowNum + 1;
        for (LancamentoEstatisticaPessoa est : estatisticas) {
            Row r = sheet.createRow(rowNum++);
            r.setHeightInPoints(20);
            boolean zebra = (rowNum % 2 == 0);

            Cell c0 = r.createCell(0);
            c0.setCellValue(est.getPessoa() != null ? est.getPessoa().getNome() : "-");
            c0.setCellStyle(zebra ? estilos.textoZebra : estilos.textoPadrao);

            Cell c1 = r.createCell(1);
            c1.setCellValue(est.getTipo() != null ? est.getTipo().getDescricao() : "-");
            c1.setCellStyle(est.getTipo() == TipoLancamento.RECEITA ? estilos.tipoReceita : estilos.tipoDespesa);

            Cell c2 = r.createCell(2);
            c2.setCellValue(est.getTotal() != null ? est.getTotal().doubleValue() : 0.0);
            c2.setCellStyle(zebra ? estilos.moedaZebra : estilos.moedaPadrao);
        }

        // Total
        Row totalRow = sheet.createRow(rowNum++);
        totalRow.setHeightInPoints(24);
        for (int i = 0; i < cols.length; i++) {
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

        for (int i = 0; i < cols.length; i++) {
            sheet.autoSizeColumn(i);
            sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 1500);
        }
    }

    private void criarKpiHeaderCell(Row row, int colIndex, String label, CellStyle style) {
        Cell c = row.createCell(colIndex);
        c.setCellValue(label);
        c.setCellStyle(style);
    }

    private void criarKpiValueCell(Row row, int colIndex, double value, CellStyle style) {
        Cell c = row.createCell(colIndex);
        c.setCellValue(value);
        c.setCellStyle(style);
    }

    private EstilosExcel criarEstilos(XSSFWorkbook workbook) {
        DataFormat df = workbook.createDataFormat();
        short moedaFormat = df.getFormat("R$ #,##0.00;[Red]-R$ #,##0.00;\"R$ 0,00\"");

        EstilosExcel e = new EstilosExcel();

        // Fontes
        XSSFFont fonteTitulo = workbook.createFont();
        fonteTitulo.setFontName("Segoe UI");
        fonteTitulo.setFontHeightInPoints((short) 13);
        fonteTitulo.setBold(true);
        fonteTitulo.setColor(new XSSFColor(new byte[]{(byte) 255, (byte) 255, (byte) 255}, null));

        XSSFFont fonteSubtitulo = workbook.createFont();
        fonteSubtitulo.setFontName("Segoe UI");
        fonteSubtitulo.setFontHeightInPoints((short) 9);
        fonteSubtitulo.setItalic(true);
        fonteSubtitulo.setColor(new XSSFColor(new byte[]{(byte) 100, (byte) 116, (byte) 139}, null));

        XSSFFont fonteHeader = workbook.createFont();
        fonteHeader.setFontName("Segoe UI");
        fonteHeader.setFontHeightInPoints((short) 10);
        fonteHeader.setBold(true);
        fonteHeader.setColor(new XSSFColor(new byte[]{(byte) 255, (byte) 255, (byte) 255}, null));

        XSSFFont fontePadrao = workbook.createFont();
        fontePadrao.setFontName("Segoe UI");
        fontePadrao.setFontHeightInPoints((short) 9);

        XSSFFont fonteNegrito = workbook.createFont();
        fonteNegrito.setFontName("Segoe UI");
        fonteNegrito.setFontHeightInPoints((short) 9);
        fonteNegrito.setBold(true);

        XSSFFont fonteReceita = workbook.createFont();
        fonteReceita.setFontName("Segoe UI");
        fonteReceita.setFontHeightInPoints((short) 9);
        fonteReceita.setBold(true);
        fonteReceita.setColor(new XSSFColor(new byte[]{(byte) 22, (byte) 163, (byte) 74}, null));

        XSSFFont fonteDespesa = workbook.createFont();
        fonteDespesa.setFontName("Segoe UI");
        fonteDespesa.setFontHeightInPoints((short) 9);
        fonteDespesa.setBold(true);
        fonteDespesa.setColor(new XSSFColor(new byte[]{(byte) 220, (byte) 38, (byte) 38}, null));

        XSSFFont fonteKpiHeader = workbook.createFont();
        fonteKpiHeader.setFontName("Segoe UI");
        fonteKpiHeader.setFontHeightInPoints((short) 8);
        fonteKpiHeader.setBold(true);

        XSSFFont fonteKpiValor = workbook.createFont();
        fonteKpiValor.setFontName("Segoe UI");
        fonteKpiValor.setFontHeightInPoints((short) 12);
        fonteKpiValor.setBold(true);

        // Cores
        XSSFColor corNavy = new XSSFColor(new byte[]{(byte) 30, (byte) 41, (byte) 59}, null);
        XSSFColor corHeaderTabela = new XSSFColor(new byte[]{(byte) 51, (byte) 65, (byte) 85}, null);
        XSSFColor corZebra = new XSSFColor(new byte[]{(byte) 248, (byte) 250, (byte) 252}, null);
        XSSFColor corTotal = new XSSFColor(new byte[]{(byte) 241, (byte) 245, (byte) 249}, null);

        // Titulo
        e.titulo = workbook.createCellStyle();
        e.titulo.setFont(fonteTitulo);
        e.titulo.setFillForegroundColor(corNavy);
        e.titulo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        e.titulo.setAlignment(HorizontalAlignment.LEFT);
        e.titulo.setVerticalAlignment(VerticalAlignment.CENTER);

        // Subtitulo
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

        // Tipos
        e.tipoReceita = criarEstiloDado(workbook, fonteReceita, HorizontalAlignment.CENTER, null);
        e.tipoDespesa = criarEstiloDado(workbook, fonteDespesa, HorizontalAlignment.CENTER, null);

        // Moeda
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

        // Totais
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

    private CellStyle criarEstiloKpi(XSSFWorkbook workbook, XSSFFont font, HorizontalAlignment align, XSSFColor bg) {
        CellStyle s = workbook.createCellStyle();
        s.setFont(font);
        s.setAlignment(align);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        s.setFillForegroundColor(bg);
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        aplicarBordas(s);
        return s;
    }

    private CellStyle criarEstiloDado(XSSFWorkbook workbook, XSSFFont font, HorizontalAlignment align, XSSFColor bg) {
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

    private void aplicarBordas(CellStyle s) {
        s.setBorderTop(BorderStyle.THIN);
        s.setBorderBottom(BorderStyle.THIN);
        s.setBorderLeft(BorderStyle.THIN);
        s.setBorderRight(BorderStyle.THIN);
    }

    private static class EstilosExcel {
        CellStyle titulo;
        CellStyle subtitulo;
        CellStyle colunaHeader;
        CellStyle kpiHeaderReceita;
        CellStyle kpiHeaderDespesa;
        CellStyle kpiHeaderSaldo;
        CellStyle kpiValorReceita;
        CellStyle kpiValorDespesa;
        CellStyle kpiValorSaldo;
        CellStyle textoPadrao;
        CellStyle textoZebra;
        CellStyle dataPadrao;
        CellStyle dataZebra;
        CellStyle centroPadrao;
        CellStyle centroZebra;
        CellStyle tipoReceita;
        CellStyle tipoDespesa;
        CellStyle moedaPadrao;
        CellStyle moedaZebra;
        CellStyle moedaReceita;
        CellStyle moedaReceitaZebra;
        CellStyle moedaDespesa;
        CellStyle moedaDespesaZebra;
        CellStyle totalRow;
        CellStyle totalMoeda;
    }
}
