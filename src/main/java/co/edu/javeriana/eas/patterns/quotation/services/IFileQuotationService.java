package co.edu.javeriana.eas.patterns.quotation.services;

import java.io.File;
import java.util.Map;

public interface IFileQuotationService {

    File createFileToAttachment(Map<String, Object> model);

}