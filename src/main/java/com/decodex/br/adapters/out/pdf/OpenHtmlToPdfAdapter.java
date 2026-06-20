package com.decodex.br.adapters.out.pdf;

import com.decodex.br.domain.exeption.RelatorioPdfException;
import com.decodex.br.domain.model.LancamentoEstatisticaPessoa;
import com.decodex.br.domain.port.out.RelatorioPdfPort;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class OpenHtmlToPdfAdapter implements RelatorioPdfPort {

    private final TemplateEngine templateEngine;
    private static final String TEMPLATE_NAME = "lancamentos-por-pessoa";

    public OpenHtmlToPdfAdapter(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @Override
    public byte[] gerarRelatorioLancamentosPorPessoa(
            List<LancamentoEstatisticaPessoa> dados, LocalDate inicio, LocalDate fim) {

        Context context = new Context();
        context.setVariable("estatisticas", dados);
        context.setVariable("dataInicio", inicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        context.setVariable("dataFim", fim.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        String htmlContent;
        try {
            htmlContent = templateEngine.process(TEMPLATE_NAME, context);
        } catch (Exception e) {
            throw new RelatorioPdfException(
                    "Template não encontrado em: templates/" + TEMPLATE_NAME + ".html", e);
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withHtmlContent(htmlContent, null);
            builder.toStream(outputStream);
            builder.run();
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RelatorioPdfException(
                    "Falha ao renderizar PDF a partir de: templates/" + TEMPLATE_NAME + ".html", e);
        }
    }
}