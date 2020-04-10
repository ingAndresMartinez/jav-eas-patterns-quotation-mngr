package co.edu.javeriana.eas.patterns.quotation.dtos;

public class RequestQuotationDetailDto {

    private int productId;
    private String productDescription;
    private int quantity;
    private String additionalInformation;

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    @Override
    public String toString() {
        return "RequestQuotationDetailDto{" +
                "productId=" + productId +
                ", productDescription='" + productDescription + '\'' +
                ", quantity=" + quantity +
                ", additionalInformation='" + additionalInformation + '\'' +
                '}';
    }

}