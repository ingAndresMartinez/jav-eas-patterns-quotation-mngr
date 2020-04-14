package co.edu.javeriana.eas.patterns.quotation.mappers;

import co.edu.javeriana.eas.patterns.common.dto.quotation.RequestQuotationDetailDto;
import co.edu.javeriana.eas.patterns.common.dto.quotation.RequestQuotationWrapperDto;
import co.edu.javeriana.eas.patterns.common.enums.ERequestStatus;
import co.edu.javeriana.eas.patterns.persistence.entities.RequestQuotationDetailEntity;
import co.edu.javeriana.eas.patterns.persistence.entities.RequestQuotationEntity;
import co.edu.javeriana.eas.patterns.persistence.entities.RequestStatusEntity;

import java.util.ArrayList;
import java.util.List;

public class RequestQuotationMapper {

    private RequestQuotationMapper() {
    }

    public static RequestQuotationWrapperDto requestQuotationEntityMapperInRequestQuotationWrapperDto(RequestQuotationEntity requestQuotationEntity) {
        RequestQuotationWrapperDto requestQuotationWrapper = new RequestQuotationWrapperDto();
        requestQuotationWrapper.setRequestQuotationId(requestQuotationEntity.getId());
        requestQuotationWrapper.setCategoryId(requestQuotationEntity.getCategory().getId());
        requestQuotationWrapper.setCategoryDescription(requestQuotationEntity.getCategory().getCategoryDescription());
        requestQuotationWrapper.seteRequestStatus(getRequestStatus(requestQuotationEntity.getStatus()));
        requestQuotationWrapper.setPersonId(requestQuotationEntity.getPerson().getId());
        requestQuotationWrapper.setAdditionalInfo(requestQuotationEntity.getAdditionalInfo());
        return requestQuotationWrapper;
    }

    public static List<RequestQuotationDetailDto> requestQuotationDetailListMapperInRequestQuotationDetailList(List<RequestQuotationDetailEntity> details) {
        List<RequestQuotationDetailDto> detailsDto = new ArrayList<>();
        details.forEach(requestQuotationDetailEntity -> detailsDto.add(requestQuotationDetailMapperInRequestQuotationDetail(requestQuotationDetailEntity)));
        return detailsDto;
    }

    public static RequestQuotationDetailDto requestQuotationDetailMapperInRequestQuotationDetail(RequestQuotationDetailEntity detail) {
        RequestQuotationDetailDto detailsDto = new RequestQuotationDetailDto();
        detailsDto.setProductId(detail.getProductService().getId());
        detailsDto.setProductDescription(detail.getProductService().getDescription());
        detailsDto.setQuantity(detail.getQuantity());
        detailsDto.setAdditionalInformation(detail.getAdditionalInfo());
        return detailsDto;
    }

    private static ERequestStatus getRequestStatus(RequestStatusEntity requestStatusEntity) {
        ERequestStatus eRequestStatus = null;
        int status = requestStatusEntity.getId();
        if (ERequestStatus.REGISTERED.getStatus() == status) {
            eRequestStatus = ERequestStatus.REGISTERED;
        }
        if (ERequestStatus.IN_PROCESS.getStatus() == status) {
            eRequestStatus = ERequestStatus.IN_PROCESS;
        }
        if (ERequestStatus.IN_QUOTATION.getStatus() == status) {
            eRequestStatus = ERequestStatus.IN_QUOTATION;
        }
        if (ERequestStatus.FINISHED.getStatus() == status) {
            eRequestStatus = ERequestStatus.FINISHED;
        }
        return eRequestStatus;
    }

}