package co.edu.javeriana.eas.patterns.quotation.dtos;

import java.util.ArrayList;
import java.util.List;

public class QuotationWrapperDto {

    private int categoryId;
    private int personId;
    private int providerId;
    private int quotationId;
    private int requestId;
    private double amountTotal;
    private String categoryDescription;
    private String providerBusinessName;
    private List<QuotationDetailDto> details = new ArrayList<>();

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getPersonId() {
        return personId;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }

    public int getProviderId() {
        return providerId;
    }

    public void setProviderId(int providerId) {
        this.providerId = providerId;
    }

    public int getQuotationId() {
        return quotationId;
    }

    public void setQuotationId(int quotationId) {
        this.quotationId = quotationId;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public double getAmountTotal() {
        return amountTotal;
    }

    public void setAmountTotal(double amountTotal) {
        this.amountTotal = amountTotal;
    }

    public String getCategoryDescription() {
        return categoryDescription;
    }

    public void setCategoryDescription(String categoryDescription) {
        this.categoryDescription = categoryDescription;
    }

    public String getProviderBusinessName() {
        return providerBusinessName;
    }

    public void setProviderBusinessName(String providerBusinessName) {
        this.providerBusinessName = providerBusinessName;
    }

    public List<QuotationDetailDto> getDetails() {
        return details;
    }

    public void setDetails(List<QuotationDetailDto> details) {
        this.details = details;
    }

}