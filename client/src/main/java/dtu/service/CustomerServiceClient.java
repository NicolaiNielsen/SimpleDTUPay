package dtu.service;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import dtu.model.Customer;

public class CustomerServiceClient {

    private final Client client;
    private final WebTarget base;

    public CustomerServiceClient() {
        this("http://localhost:8008/customer");
    }

    public CustomerServiceClient(String baseUrl) {
        this.client = ClientBuilder.newClient();
        this.base = client.target(baseUrl);
    }

    public String createCustomer(String name, String lastName, String CPR) {
        Customer customer = new Customer();
        customer.setFirstName(name);
        customer.setLastName(lastName);
        customer.setCPR(CPR);

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
                List<Customer> customers = r.readEntity(new GenericType<List<Customer>>() {
                });
                return List.copyOf(customers);
            }
            throw new RuntimeException("Failed to fetch customers: HTTP " + r.getStatus());
        }
    }

    public Optional<Customer> updateCustomerBankAccount(Customer customer) {
        try (Response r = base.path(customer.getId()).path("bankaccount")
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(customer, MediaType.APPLICATION_JSON))) {
            if (r.getStatus() == Response.Status.OK.getStatusCode()) {
                return Optional.ofNullable(r.readEntity(Customer.class));
            }
            return Optional.empty();
        }
    }

    public static void main(String[] args) {
        CustomerServiceClient client = new CustomerServiceClient();
        Customer customer = new Customer();
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setCPR("123456-7890");
        customer.setBankAccountId("bank-account-123");
        String customerId = client.createCustomer(customer.getFirstName(), customer.getLastName(), customer.getCPR());
        customer.setId(customerId);
        System.out.println(client.updateCustomerBankAccount(customer));
    }
}
