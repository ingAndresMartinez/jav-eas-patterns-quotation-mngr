package co.edu.javeriana.eas.patterns.quotation.dtos;

import co.edu.javeriana.eas.patterns.persistence.entities.RequestQuotationDetailEntity;
import co.edu.javeriana.eas.patterns.persistence.entities.RequestQuotationEntity;

import java.util.List;

public class RequestQuotationEntityWrapper {

    private RequestQuotationEntity requestQuotationEntity;
    private List<RequestQuotationDetailEntity> requestQuotationDetailEntity;

    public RequestQuotationEntity getRequestQuotationEntity() {
        return requestQuotationEntity;
    }

    public void setRequestQuotationEntity(RequestQuotationEntity requestQuotationEntity) {
        this.requestQuotationEntity = requestQuotationEntity;
    }

    public List<RequestQuotationDetailEntity> getRequestQuotationDetailEntity() {
        return requestQuotationDetailEntity;
    }

    public void setRequestQuotationDetailEntity(List<RequestQuotationDetailEntity> requestQuotationDetailEntity) {
        this.requestQuotationDetailEntity = requestQuotationDetailEntity;
    }

    @Override
    public String toString() {
        return "RequestQuotationEntityWrapper{" +
                "requestQuotationEntity=" + requestQuotationEntity +
                ", requestQuotationDetailEntity=" + requestQuotationDetailEntity +
                '}';
    }

}