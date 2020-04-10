package co.edu.javeriana.eas.patterns.quotation.dtos;

import co.edu.javeriana.eas.patterns.quotation.enums.ERequestStatus;

public class FindRequestQuotationDto {

    private int categoryId;
    private int personId;
    private int requestQuotationId;
    private ERequestStatus statusId;

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

    public int getRequestQuotationId() {
        return requestQuotationId;
    }

    public void setRequestQuotationId(int requestQuotationId) {
        this.requestQuotationId = requestQuotationId;
    }

    public ERequestStatus getStatusId() {
        return statusId;
    }

    public void setStatusId(ERequestStatus statusId) {
        this.statusId = statusId;
    }

    @Override
    public String toString() {
        return "FindRequestQuotationDto{" +
                "categoryId=" + categoryId +
                ", personId=" + personId +
                ", requestQuotationId=" + requestQuotationId +
                ", statusId=" + statusId +
                '}';
    }

}