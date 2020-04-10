package co.edu.javeriana.eas.patterns.quotation.services;

import co.edu.javeriana.eas.patterns.common.exceptions.QuotationCoreException;
import co.edu.javeriana.eas.patterns.quotation.dtos.FindQuotationDto;
import co.edu.javeriana.eas.patterns.quotation.dtos.QuotationWrapperDto;
import co.edu.javeriana.eas.patterns.quotation.enums.EQuotationFilter;

import java.util.List;

public interface IQuotationService {

    List<QuotationWrapperDto> findQuotationByFilter(EQuotationFilter filter, FindQuotationDto findQuotationDto)
            throws QuotationCoreException;

    QuotationWrapperDto createQuotation(QuotationWrapperDto quotationWrapperDto) throws QuotationCoreException;

}