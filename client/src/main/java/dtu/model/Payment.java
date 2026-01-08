package dtu.model;

public class Payment {
    private String id;
    private Integer amount;
    private String customerId;
    private String merchantId;

    public Payment() {
    }

    public Payment(Integer amount, String customerId, String merchantId) {
        this.amount = amount;
        this.customerId = customerId;
        this.merchantId = merchantId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }
}
