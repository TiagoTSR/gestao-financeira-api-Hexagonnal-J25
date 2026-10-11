package com.decodex.br.adapters.out.excel;

import com.decodex.br.adapters.out.excel.style.ExcelStyleFactory;
import com.decodex.br.adapters.out.excel.style.ExcelStyleFactory.ExcelStyles;
import com.decodex.br.adapters.out.excel.writer.LancamentosDetalhadosSheetWriter;
import com.decodex.br.adapters.out.excel.writer.ResumoPessoaSheetWriter;
import com.decodex.br.domain.model.Lancamento;
import com.decodex.br.domain.model.LancamentoEstatisticaPessoa;
import com.decodex.br.domain.port.out.RelatorioExcelPort;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Component
public class ApachePoiExcelAdapter implements RelatorioExcelPort {

    @Override
    public byte[] gerarRelatorioLancamentosExcel(
            List<LancamentoEstatisticaPessoa> estatisticas,
            List<Lancamento> lancamentos,
            LocalDate inicio,
            LocalDate fim) {

        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            ExcelStyles estilos = ExcelStyleFactory.criar(workbook);

            LancamentosDetalhadosSheetWriter.escrever(workbook, estilos, lancamentos, inicio, fim);
            ResumoPessoaSheetWriter.escrever(workbook, estilos, estatisticas, inicio, fim);

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar relatório Excel de lançamentos", e);
        }
    }
}
