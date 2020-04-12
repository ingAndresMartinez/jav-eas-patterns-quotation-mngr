package co.edu.javeriana.eas.patterns.quotation.services.impl;

import co.edu.javeriana.eas.patterns.quotation.dtos.email.MailDto;
import co.edu.javeriana.eas.patterns.quotation.services.IEmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class EmailServiceImpl implements IEmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);

    private JavaMailSender emailSender;
    private SpringTemplateEngine templateEngine;

    @Override
    public void sendSimpleMessage(MailDto mail) throws MessagingException, IOException {
        LOGGER.info("inicia envio de correo para cotización.");
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());
        Context context = new Context();
        context.setVariables(mail.getModel());
        String html = templateEngine.process("email-template", context);
        helper.setTo(mail.getTo());
        helper.setText(html, true);
        helper.setSubject(mail.getSubject());
        helper.setFrom(mail.getFrom());
        helper.addAttachment("Cotizacion.html", mail.getAttachmentFile());
        emailSender.send(message);
        LOGGER.info("finaliza envio de correo para cotización.");
    }

    @Autowired
    public void setEmailSender(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    @Autowired
    public void setTemplateEngine(SpringTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }
}