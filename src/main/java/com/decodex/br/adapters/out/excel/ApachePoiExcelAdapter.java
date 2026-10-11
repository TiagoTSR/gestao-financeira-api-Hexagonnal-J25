package com.decodex.br.adapters.out.excel;

import com.decodex.br.adapters.out.excel.style.ExcelStyleFactory;
import com.decodex.br.adapters.out.excel.style.ExcelStyleFactory.ExcelStyles;
import com.decodex.br.domain.model.Lancamento;
import com.decodex.br.domain.model.LancamentoEstatisticaPessoa;
import com.decodex.br.domain.model.TipoLancamento;
import com.decodex.br.domain.port.out.RelatorioExcelPort;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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

    private static final String[] COLUNAS_DETALHADAS = {
            "Vencimento", "Pagamento", "Pessoa", "Categoria", "Descrição",
            "Parcela", "Tipo", "Status", "Valor (R$)", "Valor Pago (R$)"
    };

    private static final String[] COLUNAS_RESUMO_PESSOA = {
            "Pessoa", "Tipo de Lançamento", "Total Consolidado (R$)"
    };

    @Override
    public byte[] gerarRelatorioLancamentosExcel(
            List<LancamentoEstatisticaPessoa> estatisticas,
            List<Lancamento> lancamentos,
            LocalDate inicio,
            LocalDate fim) {

        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            ExcelStyles estilos = ExcelStyleFactory.criar(workbook);

            criarAbaLancamentosDetalhados(workbook, estilos, lancamentos, inicio, fim);
            criarAbaResumoPessoa(workbook, estilos, estatisticas, inicio, fim);

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar relatório Excel de lançamentos", e);
        }
    }

    private void criarAbaLancamentosDetalhados(
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
        for (int i = 0; i < COLUNAS_DETALHADAS.length; i++) {
            Cell c = headerRow.createCell(i);
            c.setCellValue(COLUNAS_DETALHADAS[i]);
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
        ajustarLarguraColunas(sheet, COLUNAS_DETALHADAS.length, 1200, 3200);
    }

    private int preencherCardsKpi(XSSFSheet sheet, ExcelStyles estilos, List<Lancamento> lancamentos, int rowNum) {
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

    private void preencherLinhaLancamento(Row r, ExcelStyles estilos, Lancamento l, boolean zebra) {
        r.setHeightInPoints(20);

        // Vencimento e Pagamento
        configurarCelula(r, 0, l.getDataVencimento() != null ? l.getDataVencimento().format(DATA_FORMATTER) : "-", zebra ? estilos.dataZebra : estilos.dataPadrao);
        configurarCelula(r, 1, l.getDataPagamento() != null ? l.getDataPagamento().format(DATA_FORMATTER) : "-", zebra ? estilos.dataZebra : estilos.dataPadrao);

        // Pessoa, Categoria e Descrição
        configurarCelula(r, 2, l.getPessoa() != null ? l.getPessoa().getNome() : "-", zebra ? estilos.textoZebra : estilos.textoPadrao);
        configurarCelula(r, 3, l.getCategoria() != null ? l.getCategoria().getNome() : "-", zebra ? estilos.textoZebra : estilos.textoPadrao);
        configurarCelula(r, 4, l.getDescricao() != null ? l.getDescricao() : "", zebra ? estilos.textoZebra : estilos.textoPadrao);

        // Parcela
        String parcela = (l.getTotalParcelas() != null && l.getTotalParcelas() > 1)
                ? (l.getNumeroParcela() != null ? l.getNumeroParcela() : 1) + "/" + l.getTotalParcelas()
                : "Única";
        configurarCelula(r, 5, parcela, zebra ? estilos.centroZebra : estilos.centroPadrao);

        // Tipo e Status
        configurarCelula(r, 6, l.getTipo() != null ? l.getTipo().getDescricao() : "-", l.getTipo() == TipoLancamento.RECEITA ? estilos.tipoReceita : estilos.tipoDespesa);
        configurarCelula(r, 7, l.getStatus() != null ? l.getStatus().getDescricao() : "Pendente", zebra ? estilos.centroZebra : estilos.centroPadrao);

        // Valor
        double valor = l.getValor() != null ? l.getValor().doubleValue() : 0.0;
        configurarCelula(r, 8, valor, l.getTipo() == TipoLancamento.RECEITA
                ? (zebra ? estilos.moedaReceitaZebra : estilos.moedaReceita)
                : (zebra ? estilos.moedaDespesaZebra : estilos.moedaDespesa));

        // Valor Pago
        double valorPago = l.getValorPago() != null ? l.getValorPago().doubleValue() : 0.0;
        configurarCelula(r, 9, valorPago, zebra ? estilos.moedaZebra : estilos.moedaPadrao);
    }

    private void preencherLinhaTotalDetalhada(Row totalRow, ExcelStyles estilos, int startRow, int endRow, boolean vazio) {
        totalRow.setHeightInPoints(24);
        for (int i = 0; i < COLUNAS_DETALHADAS.length; i++) {
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

    private void criarAbaResumoPessoa(
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
        for (int i = 0; i < COLUNAS_RESUMO_PESSOA.length; i++) {
            Cell c = headerRow.createCell(i);
            c.setCellValue(COLUNAS_RESUMO_PESSOA[i]);
            c.setCellStyle(estilos.colunaHeader);
        }

        int startDataRow = rowNum + 1;
        for (LancamentoEstatisticaPessoa est : estatisticas) {
            Row r = sheet.createRow(rowNum++);
            r.setHeightInPoints(20);
            boolean zebra = (rowNum % 2 == 0);

            configurarCelula(r, 0, est.getPessoa() != null ? est.getPessoa().getNome() : "-", zebra ? estilos.textoZebra : estilos.textoPadrao);
            configurarCelula(r, 1, est.getTipo() != null ? est.getTipo().getDescricao() : "-", est.getTipo() == TipoLancamento.RECEITA ? estilos.tipoReceita : estilos.tipoDespesa);
            configurarCelula(r, 2, est.getTotal() != null ? est.getTotal().doubleValue() : 0.0, zebra ? estilos.moedaZebra : estilos.moedaPadrao);
        }

        // Linha Total
        Row totalRow = sheet.createRow(rowNum++);
        totalRow.setHeightInPoints(24);
        for (int i = 0; i < COLUNAS_RESUMO_PESSOA.length; i++) {
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

        ajustarLarguraColunas(sheet, COLUNAS_RESUMO_PESSOA.length, 1500, 3200);
    }

    private void configurarCelula(Row row, int colIndex, String valor, org.apache.poi.ss.usermodel.CellStyle style) {
        Cell c = row.createCell(colIndex);
        c.setCellValue(valor);
        c.setCellStyle(style);
    }

    private void configurarCelula(Row row, int colIndex, double valor, org.apache.poi.ss.usermodel.CellStyle style) {
        Cell c = row.createCell(colIndex);
        c.setCellValue(valor);
        c.setCellStyle(style);
    }

    private void ajustarLarguraColunas(XSSFSheet sheet, int totalColunas, int margemExtra, int larguraMinima) {
        for (int i = 0; i < totalColunas; i++) {
            sheet.autoSizeColumn(i);
            int width = sheet.getColumnWidth(i) + margemExtra;
            sheet.setColumnWidth(i, Math.max(width, larguraMinima));
        }
    }
}
