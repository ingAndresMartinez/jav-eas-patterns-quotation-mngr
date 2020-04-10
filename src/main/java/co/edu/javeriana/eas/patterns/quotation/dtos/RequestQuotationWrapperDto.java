package co.edu.javeriana.eas.patterns.quotation.dtos;

import co.edu.javeriana.eas.patterns.quotation.enums.ERequestStatus;

import java.util.ArrayList;
import java.util.List;

public class RequestQuotationWrapperDto {

    private int categoryId;
    private int requestQuotationId;
    private int personId;
    private String categoryDescription;
    private String additionalInfo;
    private ERequestStatus eRequestStatus;
    private List<RequestQuotationDetailDto> details = new ArrayList<>();

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getRequestQuotationId() {
        return requestQuotationId;
    }

    public void setRequestQuotationId(int requestQuotationId) {
        this.requestQuotationId = requestQuotationId;
    }

    public int getPersonId() {
        return personId;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }

    public String getCategoryDescription() {
        return categoryDescription;
    }

    public void setCategoryDescription(String categoryDescription) {
        this.categoryDescription = categoryDescription;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public ERequestStatus geteRequestStatus() {
        return eRequestStatus;
    }

    public void seteRequestStatus(ERequestStatus eRequestStatus) {
        this.eRequestStatus = eRequestStatus;
    }

    public List<RequestQuotationDetailDto> getDetails() {
        return details;
    }

    public void setDetails(List<RequestQuotationDetailDto> details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "RequestQuotationWrapperDto{" +
                "categoryId=" + categoryId +
                ", requestQuotationId=" + requestQuotationId +
                ", personId=" + personId +
                ", categoryDescription='" + categoryDescription + '\'' +
                ", additionalInfo='" + additionalInfo + '\'' +
                ", eRequestStatus=" + eRequestStatus +
                ", details=" + details +
                '}';
    }
    
}