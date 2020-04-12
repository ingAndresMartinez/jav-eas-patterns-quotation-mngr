package co.edu.javeriana.eas.patterns.quotation.services;

import co.edu.javeriana.eas.patterns.persistence.entities.QuotationDetailEntity;
import co.edu.javeriana.eas.patterns.persistence.entities.QuotationEntity;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

public interface INotificationService {

    void sendNotification(QuotationEntity quotationEntity, List<QuotationDetailEntity> listProductServices) throws IOException, MessagingException;

}