package co.edu.javeriana.eas.patterns.quotation.mappers;

import co.edu.javeriana.eas.patterns.common.dto.quotation.QuotationDetailDto;
import co.edu.javeriana.eas.patterns.common.dto.quotation.QuotationWrapperDto;
import co.edu.javeriana.eas.patterns.persistence.entities.*;

import java.util.ArrayList;
import java.util.List;

public class QuotationMapper {

    private QuotationMapper() {
    }

    public static QuotationWrapperDto quotationEntityMapperInQuotationWrapperDto(QuotationEntity quotationEntity) {
        QuotationWrapperDto quotationWrapper = new QuotationWrapperDto();
        quotationWrapper.setQuotationId(quotationEntity.getId());
        quotationWrapper.setRequestId(quotationEntity.getRequest().getId());
        quotationWrapper.setCategoryId(quotationEntity.getRequest().getCategory().getId());
        quotationWrapper.setCategoryDescription(quotationEntity.getRequest().getCategory().getCategoryDescription());
        quotationWrapper.setPersonId(quotationEntity.getRequest().getPerson().getId());
        quotationWrapper.setProviderId(quotationEntity.getProvider().getId());
        quotationWrapper.setProviderBusinessName(quotationEntity.getProvider().getBusinessName());
        quotationWrapper.setAmountTotal(quotationEntity.getAmount());
        return quotationWrapper;
    }

    public static List<QuotationDetailDto> quotationDetailListMapperInQuotationDetailList(List<QuotationDetailEntity> details) {
        List<QuotationDetailDto> detailsDto = new ArrayList<>();
        details.forEach(quotationDetailEntity -> detailsDto.add(quotationDetailMapperInQuotationDetail(quotationDetailEntity)));
        return detailsDto;
    }

    public static QuotationDetailDto quotationDetailMapperInQuotationDetail(QuotationDetailEntity detail) {
        QuotationDetailDto detailsDto = new QuotationDetailDto();
        detailsDto.setProductId(detail.getProductService().getId());
        detailsDto.setProductDescription(detail.getProductService().getDescription());
        detailsDto.setQuantity(detail.getQuantity());
        detailsDto.setAmount(detail.getAmount());
        detailsDto.setDiscount(detail.getDiscount());
        detailsDto.setDescription(detail.getDescription());
        return detailsDto;
    }

}