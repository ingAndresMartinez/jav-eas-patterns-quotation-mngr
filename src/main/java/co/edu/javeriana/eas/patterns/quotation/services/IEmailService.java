package co.edu.javeriana.eas.patterns.quotation.services;

import co.edu.javeriana.eas.patterns.quotation.dtos.email.MailDto;

import javax.mail.MessagingException;
import java.io.IOException;

public interface IEmailService {

    void sendSimpleMessage(MailDto mail) throws MessagingException, IOException;

}