package dtu.example.steps;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dtu.Customer;
import dtu.Merchant;
import dtu.Payment;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import dtu.client.service.CustomerServiceClient;
import dtu.service.MerchantService;
import dtu.service.PaymentService;
import java.util.Collection;

public class SimpleDTUPaySteps {
    private Customer customer;
    private Merchant merchant;
    private String customerId, merchantId;
    private boolean successful = false;
    private Collection<Payment> allPayments;

    CustomerServiceClient customerService = new CustomerServiceClient();
    MerchantService merchantService = new MerchantService();
    PaymentService PaymentService = new PaymentService();

    @Given("a customer with name {string}")
    public void aCustomerWithName(String name) {
        customerId = customerService.createCustomer(name);
    }

    @Given("the customer is registered with Simple DTU Pay")
    public void theCustomerIsRegisteredWithSimpleDTUPay() {
        customer = customerService.getCustomerById(customerId).orElse(null);
        assertEquals(customerId, customer.getId());
    }

    @Given("a merchant with name {string}")
    public void aMerchantWithName(String name) {
        merchantId = merchantService.createMerchant(name);
    }

    @Given("the merchant is registered with Simple DTU Pay")
    public void theMerchantIsRegisteredWithSimpleDTUPay() {
        merchant = merchantService.getMerchantById(merchantId).orElse(null);
        assertEquals(merchantId, merchant.getId());
    }

    @When("the merchant initiates a payment for {int} kr by the customer")
    public void theMerchantInitiatesAPaymentForKrByTheCustomer(Integer amount) {
            successful = PaymentService.pay(amount, customerId, merchantId);

    }

    @Then("the payment is successful")
    public void thePaymentIsSuccessful() {
        assertTrue(successful);
    }

    @Given("a customer with name {string}, who is registered with Simple DTU Pay")
    public void aCustomerWithNameWhoIsRegisteredWithSimpleDTUPay(String name) {
        customerId = customerService.createCustomer(name);
        customer = customerService.getCustomerById(customerId).orElse(null);
        assertEquals(customerId, customer.getId());
    }

    @Given("a merchant with name {string}, who is registered with Simple DTU Pay")
    public void aMerchantWithNameWhoIsRegisteredWithSimpleDTUPay(String name) {
        merchantId = merchantService.createMerchant(name);
        merchant = merchantService.getMerchantById(merchantId).orElse(null);
        assertEquals(merchantId, merchant.getId());
    }

    @Given("a successful payment of {string} kr from the customer to the merchant")
    public void aSuccessfulPaymentOfKrFromTheCustomerToTheMerchant(String amount) {

            successful = PaymentService.pay(Integer.parseInt(amount), customerId, merchantId);

    }

    @When("the manager asks for a list of payments")
    public void theManagerAsksForAListOfPayments() {
        allPayments = PaymentService.findAll();
        System.out.println(allPayments);
    }

    @Then("the list contains a payments where customer {string} paid {string} kr to merchant {string}")
    public void theListContainsAPaymentsWhereCustomerPaidKrToMerchant(String string, String string2, String string3) {
        for (Payment payment : allPayments) {
            if (payment.getCustomerId().equals(customerId) && payment.getMerchantId().equals(merchantId) && payment.getAmount().equals(Integer.parseInt(string2))) {
                return;
            }
        }
        throw new AssertionError("No matching payment found");
    }

    @When("the merchant initiates a payment for {string} kr using customer id {string}")
    public void theMerchantInitiatesAPaymentForKrUsingCustomerId(String amount, String customerid) {
        successful = PaymentService.pay(Integer.parseInt(amount), customerid, merchantId);
    }

    @Then("the payment is not successful")
    public void thePaymentIsNotSuccessful() {
        assertEquals(successful, false);
    }

    @Then("an error message is returned saying {string}")
    public void anErrorMessageIsReturnedSaying(String expectedMessage) {

    }



}