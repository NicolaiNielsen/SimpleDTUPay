package dtu.example.steps;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import dtu.model.Customer;
import dtu.model.Merchant;
import dtu.model.Payment;
import dtu.service.CustomerServiceClient;
import dtu.service.MerchantServiceClient;
import dtu.service.PaymentException;
import dtu.service.PaymentServiceClient;
import java.util.Collection;

public class SimpleDTUPaySteps {
    private Customer customer;
    private Merchant merchant;
    private String customerId, merchantId;
    private boolean successful = false;
    private Collection<Payment> allPayments;
    private PaymentException exception;
    private String customerID;

    CustomerServiceClient customerService = new CustomerServiceClient();
    MerchantServiceClient merchantService = new MerchantServiceClient();
    PaymentServiceClient paymentService = new PaymentServiceClient();

    @Given("a customer with name {string}")
    public void aCustomerWithName(String name) {
        customerId = customerService.createCustomer(name, "LastName", "123456-7890");
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

        try {
            successful = paymentService.pay(amount, customerId, merchantId);
        } catch (PaymentException e) {
            exception = e;
            successful = false;
        }
    }

    @Then("the payment is successful")
    public void thePaymentIsSuccessful() {
        assertTrue(successful);
    }

    @Given("a customer with name {string}, who is registered with Simple DTU Pay")
    public void aCustomerWithNameWhoIsRegisteredWithSimpleDTUPay(String name) {
        customerId = customerService.createCustomer(name, "LastName", "123456-7890");
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
        try {
            successful = paymentService.pay(Integer.parseInt(amount), customerId, merchantId);
        } catch (PaymentException e) {
            exception = e;
            successful = false;
        }
    }

    @When("the manager asks for a list of payments")
    public void theManagerAsksForAListOfPayments() {
        allPayments = paymentService.findAll();
        System.out.println(allPayments);
    }

    @Then("the list contains a payments where customer {string} paid {string} kr to merchant {string}")
    public void theListContainsAPaymentsWhereCustomerPaidKrToMerchant(String string, String string2, String string3) {
        for (Payment payment : allPayments) {
            if (payment.getCustomerId().equals(customerId) && payment.getMerchantId().equals(merchantId)
                    && payment.getAmount().equals(Integer.parseInt(string2))) {
                return;
            }
        }
        throw new AssertionError("No matching payment found");
    }

    @When("the merchant initiates a payment for {string} kr using customer id {string}")
    public void theMerchantInitiatesAPaymentForKrUsingCustomerId(String amount, String customerid) {
        try {
            successful = paymentService.pay(Integer.parseInt(amount), customerid, merchantId);
        } catch (PaymentException e) {
            exception = e;
            successful = false;
        }
    }

    @Then("the payment is not successful")
    public void thePaymentIsNotSuccessful() {
        assertEquals(successful, false);
    }

    @Then("an error message is returned saying {string}")
    public void anErrorMessageIsReturnedSaying(String expectedMessage) {
        System.out.println(exception);
        assertTrue(exception != null);
        assertTrue(exception instanceof PaymentException);
        assertEquals(expectedMessage, exception.getMessage());
    }
}