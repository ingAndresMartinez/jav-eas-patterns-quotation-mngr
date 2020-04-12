package co.edu.javeriana.eas.patterns.quotation.services.impl;

import co.edu.javeriana.eas.patterns.quotation.services.IFileQuotationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

@Service
public class FileQuotationServiceImpl implements IFileQuotationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileQuotationServiceImpl.class);

    private String pdfPath;

    private SpringTemplateEngine templateEngine;

    @Override
    public File createFileToAttachment(Map<String, Object> model) {
        LOGGER.info("inicia creaciónde archivo adjunto.");
        Context context = new Context();
        context.setVariables(model);
        String html = templateEngine.process("quotation-pdf-template", context);
        String quotationFileName = UUID.randomUUID().toString();
        String htmlFileName = quotationFileName.concat(".html");
        String pathHtmlFile = pdfPath.concat(htmlFileName);
        File hFile = new File(pathHtmlFile);
        try (OutputStream outputStreamHtml = new FileOutputStream(hFile)) {
            outputStreamHtml.write(html.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            LOGGER.error("Error en creación de archivo cotización:", e);
        }
        LOGGER.info("finiza creación de archivo adjunto.");
        return hFile;
    }

    @Autowired
    public void setTemplateEngine(SpringTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @Value("${quotation.pdf.path}")
    public void setPdfPath(String pdfPath) {
        this.pdfPath = pdfPath;
    }
}