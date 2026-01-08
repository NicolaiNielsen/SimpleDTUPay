package dtu.example.steps;

import dtu.service.PaymentException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public class PaymentService {

    private final Client client;
    private final WebTarget base;
    private String lastErrorMessage;


    public PaymentService() {
        this("http://localhost:8080/payments");
    }

    public PaymentService(String baseUrl) {
        this.client = ClientBuilder.newClient();
        this.base = client.target(baseUrl);
    }

    public boolean pay(Integer amount, String customerId, String merchantId) throws PaymentException{
        Payment payment = new Payment();
        payment.setAmount(amount);
        payment.setCustomer(customerId);
        payment.setMerchantId(merchantId);

        try (Response r = base.request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(payment, MediaType.APPLICATION_JSON))) {
            if (r.getStatus() == Response.Status.CREATED.getStatusCode()) {
                return true;
            } else if (r.getStatus() == Response.Status.BAD_REQUEST.getStatusCode()) {
                throw new PaymentException(r.readEntity(String.class));
            }
            throw new PaymentException("Unexpected response: " + r.getStatus());
        }
    }

    public String getLastErrorMessage() {
        return lastErrorMessage;
    }

}
