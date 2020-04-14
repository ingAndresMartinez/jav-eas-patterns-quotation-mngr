package co.edu.javeriana.eas.patterns.quotation.services;

import co.edu.javeriana.eas.patterns.common.dto.quotation.RequestQuotationWrapperDto;
import co.edu.javeriana.eas.patterns.common.enums.ERequestStatus;
import co.edu.javeriana.eas.patterns.persistence.entities.RequestQuotationEntity;
import co.edu.javeriana.eas.patterns.quotation.dtos.FindRequestQuotationDto;
import co.edu.javeriana.eas.patterns.quotation.enums.ERequestFilter;
import co.edu.javeriana.eas.patterns.quotation.exceptions.RequestQuotationException;

import java.util.List;

public interface IRequestQuotationService {

    List<RequestQuotationWrapperDto> findRequestQuotationByFilter(ERequestFilter filter, FindRequestQuotationDto findRequestQuotationDto)
            throws RequestQuotationException;

    RequestQuotationWrapperDto createRequestQuotation(RequestQuotationWrapperDto requestQuotationWrapperDto) throws RequestQuotationException;

    void createRequestQuotationHistorical(RequestQuotationEntity requestQuotationEntity, ERequestStatus eRequestStatus) throws RequestQuotationException;

    void updateRequestStatusQuotation(ERequestStatus eRequestStatus, int requestId) throws RequestQuotationException;

}