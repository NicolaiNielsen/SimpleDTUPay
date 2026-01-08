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


    public String createMerchant(String name) {
        Merchant merchant = new Merchant();
        merchant.setName(name);
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

    

}
