package com.decodex.br.adapters.out.excel.writer;

import com.decodex.br.adapters.out.excel.style.ExcelStyleFactory.ExcelStyles;
import com.decodex.br.domain.model.Lancamento;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public final class LancamentosDetalhadosSheetWriter {

    private static final DateTimeFormatter DATA_HORA_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm:ss");

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

        int ultimaColuna = COLUNAS.length - 1;

        int rowNum = 0;
        rowNum = ExcelSheetUtils.escreverTitulo(sheet, estilos, rowNum,
                "GESTÃO FINANCEIRA - RELATÓRIO DETALHADO DE LANÇAMENTOS", ultimaColuna);

        // Subtítulo estendido com hora e total de registros
        Row metaRow = sheet.createRow(rowNum++);
        metaRow.setHeightInPoints(20);
        String periodoTexto = String.format(
                "Período: %s até %s  |  Gerado em: %s  |  Total de Registros: %d",
                inicio.format(ExcelSheetUtils.DATA_FORMATTER),
                fim.format(ExcelSheetUtils.DATA_FORMATTER),
                LocalDateTime.now(ZoneId.systemDefault()).format(DATA_HORA_FORMATTER),
                lancamentos.size());
        ExcelSheetUtils.configurarCelula(metaRow, 0, periodoTexto, estilos.subtitulo);
        sheet.addMergedRegion(new CellRangeAddress(metaRow.getRowNum(), metaRow.getRowNum(), 0, ultimaColuna));

        rowNum++; // Linha em branco

        rowNum = preencherCardsKpi(sheet, estilos, lancamentos, rowNum);

        rowNum++; // Linha em branco

        rowNum = ExcelSheetUtils.escreverCabecalho(sheet, estilos, rowNum, COLUNAS);

        sheet.createFreezePane(0, rowNum);

        int startDataRow = rowNum + 1;
        for (Lancamento lancamento : lancamentos) {
            preencherLinhaLancamento(sheet.createRow(rowNum++), estilos, lancamento, rowNum % 2 == 0);
        }

        preencherLinhaTotalDetalhada(sheet, estilos, rowNum, startDataRow, rowNum, lancamentos.isEmpty());

        ExcelSheetUtils.ajustarLarguraColunas(sheet, COLUNAS.length, 1200, 3200);
    }

    private static int preencherCardsKpi(XSSFSheet sheet, ExcelStyles estilos,
                                         List<Lancamento> lancamentos, int rowNum) {
        BigDecimal totalReceitas = lancamentos.stream()
                .filter(l -> l.getTipo() != null && l.getTipo().isReceita())
                .map(Lancamento::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDespesas = lancamentos.stream()
                .filter(l -> l.getTipo() != null && l.getTipo().isDespesa())
                .map(Lancamento::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal saldoLiquido = totalReceitas.subtract(totalDespesas);

        Row kpiHeaderRow = sheet.createRow(rowNum++);
        kpiHeaderRow.setHeightInPoints(18);
        ExcelSheetUtils.configurarCelula(kpiHeaderRow, 0, "RECEITAS TOTAIS", estilos.kpiHeaderReceita);
        ExcelSheetUtils.configurarCelula(kpiHeaderRow, 3, "DESPESAS TOTAIS", estilos.kpiHeaderDespesa);
        ExcelSheetUtils.configurarCelula(kpiHeaderRow, 6, "SALDO LÍQUIDO", estilos.kpiHeaderSaldo);
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 2));
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 3, 5));
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 6, 8));

        Row kpiValueRow = sheet.createRow(rowNum++);
        kpiValueRow.setHeightInPoints(24);
        ExcelSheetUtils.configurarCelula(kpiValueRow, 0, totalReceitas.doubleValue(), estilos.kpiValorReceita);
        ExcelSheetUtils.configurarCelula(kpiValueRow, 3, totalDespesas.doubleValue(), estilos.kpiValorDespesa);
        ExcelSheetUtils.configurarCelula(kpiValueRow, 6, saldoLiquido.doubleValue(), estilos.kpiValorSaldo);
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 2));
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 3, 5));
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 6, 8));

        return rowNum;
    }

    private static void preencherLinhaLancamento(Row r, ExcelStyles estilos, Lancamento l, boolean zebra) {
        r.setHeightInPoints(20);

        ExcelSheetUtils.configurarCelula(r, 0, dataOuTraco(l.getDataVencimento()),
                estiloZebra(zebra, estilos.dataPadrao, estilos.dataZebra));
        ExcelSheetUtils.configurarCelula(r, 1, dataOuTraco(l.getDataPagamento()),
                estiloZebra(zebra, estilos.dataPadrao, estilos.dataZebra));
        ExcelSheetUtils.configurarCelula(r, 2, nomePessoa(l),
                estiloZebra(zebra, estilos.textoPadrao, estilos.textoZebra));
        ExcelSheetUtils.configurarCelula(r, 3, nomeCategoria(l),
                estiloZebra(zebra, estilos.textoPadrao, estilos.textoZebra));
        ExcelSheetUtils.configurarCelula(r, 4, textoOuVazio(l.getDescricao()),
                estiloZebra(zebra, estilos.textoPadrao, estilos.textoZebra));

        ExcelSheetUtils.configurarCelula(r, 5, formatarParcela(l),
                estiloZebra(zebra, estilos.centroPadrao, estilos.centroZebra));

        ExcelSheetUtils.configurarCelula(r, 6, descricaoTipo(l),
                estiloTipo(l, estilos));
        ExcelSheetUtils.configurarCelula(r, 7, descricaoStatus(l),
                estiloZebra(zebra, estilos.centroPadrao, estilos.centroZebra));

        ExcelSheetUtils.configurarCelula(r, 8, valorOuZero(l.getValor()),
                estiloValor(l, estilos, zebra));

        ExcelSheetUtils.configurarCelula(r, 9, valorOuZero(l.getValorPago()),
                estiloZebra(zebra, estilos.moedaPadrao, estilos.moedaZebra));
    }

    // ---------- Auxiliares de formatação e estilo ----------

    private static CellStyle estiloZebra(boolean zebra, CellStyle padrao, CellStyle zebraStyle) {
        return zebra ? zebraStyle : padrao;
    }

    private static String dataOuTraco(LocalDate data) {
        return data != null ? data.format(ExcelSheetUtils.DATA_FORMATTER) : "-";
    }

    private static String textoOuVazio(String texto) {
        return texto != null ? texto : "";
    }

    private static String nomePessoa(Lancamento l) {
        return l.getPessoa() != null ? l.getPessoa().getNome() : "-";
    }

    private static String nomeCategoria(Lancamento l) {
        return l.getCategoria() != null ? l.getCategoria().getNome() : "-";
    }

    private static String descricaoTipo(Lancamento l) {
        return l.getTipo() != null ? l.getTipo().getDescricao() : "-";
    }

    private static String descricaoStatus(Lancamento l) {
        return l.getStatus() != null ? l.getStatus().getDescricao() : "Pendente";
    }

    private static double valorOuZero(BigDecimal valor) {
        return valor != null ? valor.doubleValue() : 0.0;
    }

    private static String formatarParcela(Lancamento l) {
        if (l.getTotalParcelas() == null || l.getTotalParcelas() <= 1) {
            return "Única";
        }
        int numero = l.getNumeroParcela() != null ? l.getNumeroParcela() : 1;
        return numero + "/" + l.getTotalParcelas();
    }

    private static boolean isReceita(Lancamento l) {
        return l.getTipo() != null && l.getTipo().isReceita();
    }

    private static CellStyle estiloTipo(Lancamento l, ExcelStyles estilos) {
        return isReceita(l) ? estilos.tipoReceita : estilos.tipoDespesa;
    }

    private static CellStyle estiloValor(Lancamento l, ExcelStyles estilos, boolean zebra) {
        if (isReceita(l)) {
            return estiloZebra(zebra, estilos.moedaReceita, estilos.moedaReceitaZebra);
        }
        return estiloZebra(zebra, estilos.moedaDespesa, estilos.moedaDespesaZebra);
    }

    private static void preencherLinhaTotalDetalhada(XSSFSheet sheet, ExcelStyles estilos,
                                                     int rowNum, int startRow, int endRow, boolean vazio) {
        Row totalRow = ExcelSheetUtils.criarLinhaTotal(sheet, estilos, rowNum, COLUNAS.length, "TOTAL:", 7);

        if (!vazio) {
            Cell totalValorCell = totalRow.getCell(8);
            totalValorCell.setCellFormula(String.format("SUM(I%d:I%d)", startRow, endRow));
            totalValorCell.setCellStyle(estilos.totalMoeda);

            Cell totalPagoCell = totalRow.getCell(9);
            totalPagoCell.setCellFormula(String.format("SUM(J%d:J%d)", startRow, endRow));
            totalPagoCell.setCellStyle(estilos.totalMoeda);
        }
    }
}