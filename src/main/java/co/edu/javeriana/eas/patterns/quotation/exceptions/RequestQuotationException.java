package co.edu.javeriana.eas.patterns.quotation.exceptions;

import co.edu.javeriana.eas.patterns.common.enums.EExceptionCode;
import co.edu.javeriana.eas.patterns.common.exceptions.QuotationCoreException;

public class RequestQuotationException extends QuotationCoreException {

    public RequestQuotationException(EExceptionCode exceptionCode) {
        super(exceptionCode);
    }

    public RequestQuotationException(EExceptionCode exceptionCode, String causeMessage) {
        super(exceptionCode, causeMessage);
    }

}