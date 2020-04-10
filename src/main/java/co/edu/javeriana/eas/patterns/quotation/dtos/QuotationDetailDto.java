package co.edu.javeriana.eas.patterns.quotation.dtos;

public class QuotationDetailDto {

    private int productId;
    private String productDescription;
    private int quantity;
    private double amount;
    private double discount;
    private String description;

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

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "QuotationDetailDto{" +
                "productId=" + productId +
                ", productDescription='" + productDescription + '\'' +
                ", quantity=" + quantity +
                ", amount=" + amount +
                ", discount=" + discount +
                ", description='" + description + '\'' +
                '}';
    }

}