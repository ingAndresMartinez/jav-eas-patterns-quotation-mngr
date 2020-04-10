package co.edu.javeriana.eas.patterns.quotation.exceptions;

import co.edu.javeriana.eas.patterns.common.enums.EExceptionCode;
import co.edu.javeriana.eas.patterns.common.exceptions.QuotationCoreException;

public class QuotationException extends QuotationCoreException {

    public QuotationException(EExceptionCode exceptionCode) {
        super(exceptionCode);
    }

    public QuotationException(EExceptionCode exceptionCode, String causeMessage) {
        super(exceptionCode, causeMessage);
    }

}