package dtu.service;

import java.util.Collection;
import java.util.Optional;

import dtu.model.Payment;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public class PaymentServiceClient {
    private final Client client;
    private final WebTarget base;
    private String lastErrorMessage;

    public PaymentServiceClient() {
        this("http://localhost:8008/payments");
    }

    public PaymentServiceClient(String baseUrl) {
        this.client = ClientBuilder.newClient();
        this.base = client.target(baseUrl);
    }

    public boolean pay(Integer amount, String customerId, String merchantId) throws PaymentException {
        Payment payment = new Payment();
        payment.setAmount(amount);
        payment.setCustomerId(customerId);
        payment.setMerchantId(merchantId);

        try (Response r = base.request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(payment, MediaType.APPLICATION_JSON))) {
            if (r.getStatus() == Response.Status.CREATED.getStatusCode()) {
                lastErrorMessage = null;
                return true;
            } else {
                throw new PaymentException(r.readEntity(String.class));
            }

        }
    }

    public Optional<Payment> getPaymentById(String id) {
        try (Response r = base.path(id)
                .request(MediaType.APPLICATION_JSON)
                .get()) {
            if (r.getStatus() == Response.Status.OK.getStatusCode()) {
                return Optional.ofNullable(r.readEntity(Payment.class));
            }
            return Optional.empty();
        }
    }

    public Collection<Payment> findAll() {
        try (Response r = base.request(MediaType.APPLICATION_JSON).get()) {
            if (r.getStatus() == Response.Status.OK.getStatusCode()) {
                java.util.List<?> list = r.readEntity(java.util.List.class);
                java.util.List<Payment> payments = new java.util.ArrayList<>();
                for (Object item : list) {
                    if (item instanceof java.util.Map) {
                        @SuppressWarnings("unchecked")
                        java.util.Map<String, Object> map = (java.util.Map<String, Object>) item;
                        Payment payment = new Payment();
                        payment.setId((String) map.get("id"));
                        payment.setCustomerId((String) map.get("customerId"));
                        payment.setMerchantId((String) map.get("merchantId"));
                        Object amount = map.get("amount");
                        if (amount instanceof Number) {
                            payment.setAmount(((Number) amount).intValue());
                        }
                        payments.add(payment);
                    }
                }
                return payments;
            }
            return java.util.Collections.emptyList();
        }
    }

    public Optional<Payment> getPaymentByIdAndParties(String id, String customerId, String merchantId) {
        try (Response r = base.path(id)
                .queryParam("customerId", customerId)
                .queryParam("merchantId", merchantId)
                .request(MediaType.APPLICATION_JSON)
                .get()) {
            if (r.getStatus() == Response.Status.OK.getStatusCode()) {
                return Optional.ofNullable(r.readEntity(Payment.class));
            }
            return Optional.empty();
        }
    }

    public String getLastErrorMessage() {
        return lastErrorMessage;
    }
}
