package co.edu.javeriana.eas.patterns.quotation.services.impl;

import co.edu.javeriana.eas.patterns.persistence.entities.QuotationDetailEntity;
import co.edu.javeriana.eas.patterns.persistence.entities.QuotationEntity;
import co.edu.javeriana.eas.patterns.quotation.dtos.email.MailDto;
import co.edu.javeriana.eas.patterns.quotation.services.IEmailService;
import co.edu.javeriana.eas.patterns.quotation.services.INotificationService;
import co.edu.javeriana.eas.patterns.quotation.services.IFileQuotationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NotificationServiceImpl implements INotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private String emailFrom;

    private IEmailService emailService;
    private IFileQuotationService pdfQuotationService;

    @Override
    public void sendNotification(QuotationEntity quotationEntity, List<QuotationDetailEntity> listProductServices) throws IOException, MessagingException {
        LOGGER.info("inicia proceso de notificación para cotización [{}].", quotationEntity.getId());
        File attachmentFile = createQuotationFile(quotationEntity, listProductServices);
        sendEmail(quotationEntity, attachmentFile);
        LOGGER.info("finaliza proceso de notificación para cotización [{}].", quotationEntity.getId());
    }

    private File createQuotationFile(QuotationEntity quotationEntity, List<QuotationDetailEntity> listProductServices) {
        Map<String, Object> model = new HashMap<>();
        model.put("quotationId", quotationEntity.getId());
        model.put("requestQuotationId", quotationEntity.getRequest().getId());
        model.put("firstName", quotationEntity.getRequest().getPerson().getFirstName());
        model.put("lastName", quotationEntity.getRequest().getPerson().getLastName());
        model.put("email", quotationEntity.getRequest().getPerson().getEmail());
        model.put("phone", quotationEntity.getRequest().getPerson().getPhoneNumber());
        model.put("providerId", quotationEntity.getProvider().getId());
        model.put("providerName", quotationEntity.getProvider().getBusinessName());
        model.put("providerPhone", quotationEntity.getProvider().getPhoneNumber());
        model.put("itemsList", listProductServices);
        model.put("totalAmount", quotationEntity.getAmount());
        return pdfQuotationService.createFileToAttachment(model);
    }

    private void sendEmail(QuotationEntity quotationEntity, File attachmentFile) throws IOException, MessagingException {
        LOGGER.info("inicia proceso de construcción de email para cotización [{}].", quotationEntity.getId());
        MailDto mail = new MailDto();
        mail.setFrom(emailFrom);
        mail.setTo(quotationEntity.getRequest().getPerson().getEmail());
        mail.setSubject("Notificación de Cotización");
        mail.setAttachmentFile(attachmentFile);
        Map<String, Object> model = new HashMap<>();
        model.put("firstName", quotationEntity.getRequest().getPerson().getFirstName());
        model.put("lastName", quotationEntity.getRequest().getPerson().getLastName());
        model.put("businessName", quotationEntity.getProvider().getBusinessName());
        mail.setModel(model);
        LOGGER.info("Datos de envio [{}] -> [{}].", mail, model);
        emailService.sendSimpleMessage(mail);
        LOGGER.info("finaliza proceso de construcción de email para cotización [{}].", quotationEntity.getId());
    }

    @Autowired
    public void setEmailService(IEmailService emailService) {
        this.emailService = emailService;
    }

    @Autowired
    public void setPdfQuotationService(IFileQuotationService pdfQuotationService) {
        this.pdfQuotationService = pdfQuotationService;
    }

    @Value("${spring.mail.username}")
    public void setEmailFrom(String emailFrom) {
        this.emailFrom = emailFrom;
    }
}