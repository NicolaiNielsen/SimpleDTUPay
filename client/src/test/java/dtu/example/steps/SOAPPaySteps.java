package dtu.example.steps;
import dtu.model.Customer;
import dtu.service.CustomerServiceClient;
import io.cucumber.java.en.Given;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SOAPPaySteps {

    private String customerID;
    private CustomerServiceClient customerService = new CustomerServiceClient();
    Customer customer;

    @Given("a test")
    public void aTest() {
        System.out.println("This is a test step.");
    }

    @Given("a customer with name {string}, last name {string}, and CPR {string}")
    public void aCustomerWithNameLastNameAndCPR(String firstName, String lastName, String CPR) {
        customerID = customerService.createCustomer(firstName, lastName, CPR);
        System.out.println("Created customer with ID: " + customerID);
        var res = customerService.getCustomerById(customerID);
        assertTrue(res.isPresent(), "Customer should be retrieved");
        customer = res.get();
        assertEquals(firstName, customer.getFirstName(), "First name should match");
        assertEquals(lastName, customer.getLastName(), "Last name should match");
        assertEquals(CPR, customer.getCPR(), "CPR should match");
        System.out.println("Retrieved customer: " + customer.getFirstName() + " " + customer.getLastName());
    }

    @Given("the customer is registered with the bank with an initial balance of {int} kr")
    public void theCustomerIsRegisteredWithTheBankWithAnInitialBalanceOfKr(Integer int1) {
        
    }
}
