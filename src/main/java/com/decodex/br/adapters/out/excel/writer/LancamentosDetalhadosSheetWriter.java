package com.decodex.br.adapters.out.excel.writer;

import com.decodex.br.adapters.out.excel.style.ExcelStyleFactory.ExcelStyles;
import com.decodex.br.domain.model.Lancamento;
import com.decodex.br.domain.model.TipoLancamento;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public final class LancamentosDetalhadosSheetWriter {

    private static final DateTimeFormatter DATA_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATA_HORA_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm:ss");

    private static final String[] COLUNAS = {
            "Vencimento", "Pagamento", "Pessoa", "Categoria", "Descrição",
            "Parcela", "Tipo", "Status", "Valor (R$)", "Valor Pago (R$)"
    };

    private LancamentosDetalhadosSheetWriter() {
    }

    public static void escrever(
            XSSFWorkbook workbook, ExcelStyles estilos,
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

        // 3. Cards de Resumo Financeiro (KPIs)
        rowNum = preencherCardsKpi(sheet, estilos, lancamentos, rowNum);

        rowNum++; // Linha em branco

        // 4. Cabeçalho das Colunas
        Row headerRow = sheet.createRow(rowNum++);
        headerRow.setHeightInPoints(24);
        for (int i = 0; i < COLUNAS.length; i++) {
            Cell c = headerRow.createCell(i);
            c.setCellValue(COLUNAS[i]);
            c.setCellStyle(estilos.colunaHeader);
        }

        sheet.createFreezePane(0, rowNum);

        // 5. Linhas de Dados
        int startDataRow = rowNum + 1;
        for (Lancamento lancamento : lancamentos) {
            preencherLinhaLancamento(sheet.createRow(rowNum++), estilos, lancamento, rowNum % 2 == 0);
        }

        // 6. Linha de Totais da Tabela
        preencherLinhaTotalDetalhada(sheet.createRow(rowNum++), estilos, startDataRow, rowNum - 1, lancamentos.isEmpty());

        // 7. Autoajustar colunas
        ajustarLarguraColunas(sheet, COLUNAS.length, 1200, 3200);
    }

    private static int preencherCardsKpi(XSSFSheet sheet, ExcelStyles estilos, List<Lancamento> lancamentos, int rowNum) {
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
        configurarCelula(kpiHeaderRow, 0, "RECEITAS TOTAIS", estilos.kpiHeaderReceita);
        configurarCelula(kpiHeaderRow, 3, "DESPESAS TOTAIS", estilos.kpiHeaderDespesa);
        configurarCelula(kpiHeaderRow, 6, "SALDO LÍQUIDO", estilos.kpiHeaderSaldo);
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 2));
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 3, 5));
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 6, 8));

        Row kpiValueRow = sheet.createRow(rowNum++);
        kpiValueRow.setHeightInPoints(24);
        configurarCelula(kpiValueRow, 0, totalReceitas.doubleValue(), estilos.kpiValorReceita);
        configurarCelula(kpiValueRow, 3, totalDespesas.doubleValue(), estilos.kpiValorDespesa);
        configurarCelula(kpiValueRow, 6, saldoLiquido.doubleValue(), estilos.kpiValorSaldo);
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 2));
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 3, 5));
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 6, 8));

        return rowNum;
    }

    private static void preencherLinhaLancamento(Row r, ExcelStyles estilos, Lancamento l, boolean zebra) {
        r.setHeightInPoints(20);

        configurarCelula(r, 0, l.getDataVencimento() != null ? l.getDataVencimento().format(DATA_FORMATTER) : "-", zebra ? estilos.dataZebra : estilos.dataPadrao);
        configurarCelula(r, 1, l.getDataPagamento() != null ? l.getDataPagamento().format(DATA_FORMATTER) : "-", zebra ? estilos.dataZebra : estilos.dataPadrao);
        configurarCelula(r, 2, l.getPessoa() != null ? l.getPessoa().getNome() : "-", zebra ? estilos.textoZebra : estilos.textoPadrao);
        configurarCelula(r, 3, l.getCategoria() != null ? l.getCategoria().getNome() : "-", zebra ? estilos.textoZebra : estilos.textoPadrao);
        configurarCelula(r, 4, l.getDescricao() != null ? l.getDescricao() : "", zebra ? estilos.textoZebra : estilos.textoPadrao);

        String parcela = (l.getTotalParcelas() != null && l.getTotalParcelas() > 1)
                ? (l.getNumeroParcela() != null ? l.getNumeroParcela() : 1) + "/" + l.getTotalParcelas()
                : "Única";
        configurarCelula(r, 5, parcela, zebra ? estilos.centroZebra : estilos.centroPadrao);

        configurarCelula(r, 6, l.getTipo() != null ? l.getTipo().getDescricao() : "-", l.getTipo() == TipoLancamento.RECEITA ? estilos.tipoReceita : estilos.tipoDespesa);
        configurarCelula(r, 7, l.getStatus() != null ? l.getStatus().getDescricao() : "Pendente", zebra ? estilos.centroZebra : estilos.centroPadrao);

        double valor = l.getValor() != null ? l.getValor().doubleValue() : 0.0;
        configurarCelula(r, 8, valor, l.getTipo() == TipoLancamento.RECEITA
                ? (zebra ? estilos.moedaReceitaZebra : estilos.moedaReceita)
                : (zebra ? estilos.moedaDespesaZebra : estilos.moedaDespesa));

        double valorPago = l.getValorPago() != null ? l.getValorPago().doubleValue() : 0.0;
        configurarCelula(r, 9, valorPago, zebra ? estilos.moedaZebra : estilos.moedaPadrao);
    }

    private static void preencherLinhaTotalDetalhada(Row totalRow, ExcelStyles estilos, int startRow, int endRow, boolean vazio) {
        totalRow.setHeightInPoints(24);
        for (int i = 0; i < COLUNAS.length; i++) {
            Cell c = totalRow.createCell(i);
            c.setCellStyle(estilos.totalRow);
        }
        totalRow.getCell(0).setCellValue("TOTAL:");
        totalRow.getSheet().addMergedRegion(new CellRangeAddress(totalRow.getRowNum(), totalRow.getRowNum(), 0, 7));

        if (!vazio) {
            Cell totalValorCell = totalRow.getCell(8);
            totalValorCell.setCellFormula(String.format("SUM(I%d:I%d)", startRow, endRow));
            totalValorCell.setCellStyle(estilos.totalMoeda);

            Cell totalPagoCell = totalRow.getCell(9);
            totalPagoCell.setCellFormula(String.format("SUM(J%d:J%d)", startRow, endRow));
            totalPagoCell.setCellStyle(estilos.totalMoeda);
        }
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
