package dtu.example.steps;

public class Payment {

    private Integer amount;
    private String customerId;
    private String merchantId;

    public Payment() {
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public void setCustomer(String customerId) {
        this.customerId = customerId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    @Override
    public String toString() {
        return "Payment [amount=" + amount + ", customerId=" + customerId + ", merchantId=" + merchantId + "]";
    }

}
