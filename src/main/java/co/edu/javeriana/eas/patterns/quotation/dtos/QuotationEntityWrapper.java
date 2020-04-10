package co.edu.javeriana.eas.patterns.quotation.dtos;

import co.edu.javeriana.eas.patterns.persistence.entities.QuotationDetailEntity;
import co.edu.javeriana.eas.patterns.persistence.entities.QuotationEntity;

import java.util.List;

public class QuotationEntityWrapper {

    private QuotationEntity quotationEntity;
    private List<QuotationDetailEntity> quotationDetailEntity;

    public QuotationEntity getQuotationEntity() {
        return quotationEntity;
    }

    public void setQuotationEntity(QuotationEntity quotationEntity) {
        this.quotationEntity = quotationEntity;
    }

    public List<QuotationDetailEntity> getQuotationDetailEntity() {
        return quotationDetailEntity;
    }

    public void setQuotationDetailEntity(List<QuotationDetailEntity> quotationDetailEntity) {
        this.quotationDetailEntity = quotationDetailEntity;
    }

    @Override
    public String toString() {
        return "QuotationEntityWrapper{" +
                "quotationEntity=" + quotationEntity +
                ", quotationDetailEntity=" + quotationDetailEntity +
                '}';
    }

}