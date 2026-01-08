package dtu.client.service;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import dtu.Customer;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class CustomerServiceClient {

    private final Client client;
    private final WebTarget base; 


    public CustomerServiceClient() {
        this("http://localhost:8080/customer");
    }

    public CustomerServiceClient(String baseUrl) {
        this.client = ClientBuilder.newClient();
        this.base = client.target(baseUrl);
    }

    public String createCustomer(String name) {
        Customer customer = new Customer();
        customer.setName(name);

        try (Response r = base.request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(customer, MediaType.APPLICATION_JSON))) {
            if (r.getStatus() == Response.Status.CREATED.getStatusCode()) {
                return r.readEntity(String.class);
            }
            throw new RuntimeException("Failed to register student: HTTP " + r.getStatus());
        }
    }

    public Optional<Customer> getCustomerById(String id) {
        try (Response r = base.path(id)
                .request(MediaType.APPLICATION_JSON)
                .get()) {
            if (r.getStatus() == Response.Status.OK.getStatusCode()) {
                return Optional.ofNullable(r.readEntity(Customer.class));
            }
            return Optional.empty();
        }
    }

    public Collection<Customer> findAll() {
        try (Response r = base.request(MediaType.APPLICATION_JSON).get()) {
            if (r.getStatus() == Response.Status.OK.getStatusCode()) {
                List<Customer> customers = r. readEntity(new GenericType<List<Customer>>() {});
                return List.copyOf(customers);
            }
            throw new RuntimeException("Failed to fetch customers: HTTP " + r.getStatus());
        }
    }


    public static void main(String[] args) {
        CustomerServiceClient service = new CustomerServiceClient();
        service.createCustomer("Test Customer");
        service.getCustomerById("36dc9fac-c8aa-41aa-bd44-33803e4ff4ac");
    }

}
