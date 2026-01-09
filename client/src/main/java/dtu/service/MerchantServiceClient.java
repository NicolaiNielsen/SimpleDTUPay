package dtu.service;

import java.util.Optional;

import dtu.model.Customer;
import dtu.model.Merchant;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public class MerchantServiceClient {

    private final Client client;
    private final WebTarget base;

    public MerchantServiceClient() {
        this("http://localhost:8080/merchants");
    }

    public MerchantServiceClient(String baseUrl) {
        this.client = ClientBuilder.newClient();
        this.base = client.target(baseUrl);
    }

    /**
     * Test connection to the server
     * 
     * @return true if server is reachable, false otherwise
     */
    public boolean testConnection() {
        try (Response r = base.request(MediaType.APPLICATION_JSON).get()) {
            boolean isConnected = r.getStatus() < 500;
            if (isConnected) {
                System.out.println("✓ Successfully connected to Merchant Service");
            } else {
                System.err.println("✗ Server error: HTTP " + r.getStatus());
            }
            return isConnected;
        } catch (Exception e) {
            System.err.println("✗ Failed to connect to Merchant Service: " + e.getMessage());
            return false;
        }
    }

    public String createMerchant(String firstName, String lastName, String CPR) {
        Merchant merchant = new Merchant();
        merchant.setFirstName(firstName);
        merchant.setLastName(lastName);
        merchant.setCPR(CPR);
        try (Response r = base.request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(merchant, MediaType.APPLICATION_JSON))) {
            if (r.getStatus() == Response.Status.CREATED.getStatusCode()) {
                return r.readEntity(String.class);
            }
            throw new RuntimeException("Failed to register merchant: HTTP " + r.getStatus());
        }
    }

    public Optional<Merchant> getMerchantById(String id) {
        try (Response r = base.path(id)
                .request(MediaType.APPLICATION_JSON)
                .get()) {
            if (r.getStatus() == Response.Status.OK.getStatusCode()) {
                return Optional.ofNullable(r.readEntity(Merchant.class));
            }
            return Optional.empty();
        }
    }

    public Optional<Merchant> updateMerchantBankAccount(String id, Merchant merchant) {
        try (Response r = base.path(id).path("bankaccount")
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(merchant, MediaType.APPLICATION_JSON))) {
            if (r.getStatus() == Response.Status.OK.getStatusCode()) {
                return Optional.ofNullable(r.readEntity(Merchant.class));
            }
            return Optional.empty();
        }
    }

}
