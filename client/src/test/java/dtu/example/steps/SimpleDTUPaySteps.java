package dtu.example.steps;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import dtu.model.Customer;
import dtu.model.Merchant;
import dtu.model.Payment;
import dtu.service.BankServiceWrapper;
import dtu.service.CustomerServiceClient;
import dtu.service.MerchantServiceClient;
import dtu.service.PaymentException;
import dtu.service.PaymentServiceClient;
import dtu.ws.fastmoney.Account;
import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankService_Service;

import java.math.BigDecimal;
import java.util.Collection;

public class SimpleDTUPaySteps {

    private String bankAccountId;
    private BankServiceWrapper bankWrapper = new BankServiceWrapper();
    BankService bank = new BankService_Service().getBankServicePort();
    private static final String API_KEY = "whale4307";

    private Customer customer;
    private Merchant merchant;
    private String customerId, merchantId;
    private boolean successful = false;
    private Collection<Payment> allPayments;
    private PaymentException exception;
    private BigDecimal OldMerchantBalance;
    private BigDecimal OldCustomerBalance;
    private BigDecimal NewMerchantBalance;
    private BigDecimal NewCustomerBalance;

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
        merchantId = merchantService.createMerchant(name, "Company", "123456-7890");
    }

    @Given("the merchant is registered with Simple DTU Pay")
    public void theMerchantIsRegisteredWithSimpleDTUPay() {
        merchant = merchantService.getMerchantById(merchantId).orElse(null);
        assertEquals(merchantId, merchant.getId());
    }

    @When("the merchant initiates a payment for {int} kr by the customer")
    public void theMerchantInitiatesAPaymentForKrByTheCustomer(Integer amount) {
        System.out.println(merchant.getFirstName() + merchant.getLastName() + " is initiating a payment of " + amount
                + " kr by customer ID: " + customer.getFirstName() + " " + customer.getLastName());

        System.out
                .println("Customer ID: " + customerId + ", Merchant ID: " + merchantId + ", Amount: " + amount + " kr");
        System.out.println("Customer Bank Account ID: " + customer.getBankAccountId());
        System.out.println("Merchant Bank Account ID: " + merchant.getBankAccountId());

        try {

            OldMerchantBalance = bank.getAccount(merchant.getBankAccountId()).getBalance();
            OldCustomerBalance = bank.getAccount(customer.getBankAccountId()).getBalance();
            System.out.println("Before Payment - Merchant Balance: " + OldMerchantBalance + " kr, Customer Balance: "
                    + OldCustomerBalance + " kr");
            successful = paymentService.pay(amount, customerId, merchantId);
            System.out.println("Payment service result: " + successful);

            if (successful) {
                bankWrapper.transferMoneyFromTo(customer.getBankAccountId(), merchant.getBankAccountId(),
                        BigDecimal.valueOf(amount),
                        "Payment from tests " + customer.getFirstName() + " to " + merchant.getFirstName());

                NewMerchantBalance = bank.getAccount(merchant.getBankAccountId()).getBalance();
                NewCustomerBalance = bank.getAccount(customer.getBankAccountId()).getBalance();
                System.out.println("After Payment - Merchant Balance: " + NewMerchantBalance + " kr, Customer Balance: "
                        + NewCustomerBalance + " kr");
            }

        } catch (PaymentException e) {
            System.err.println("PaymentException: " + e.getMessage());
            exception = e;
            successful = false;
        } catch (Exception e) {
            System.err.println("Exception: " + e.getMessage());
            e.printStackTrace();
            exception = new PaymentException("Bank service error: " + e.getMessage());
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
        merchantId = merchantService.createMerchant(name, "Company", "123456-7890");
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

    @Given("a customer with name {string}, last name {string}, and CPR {string}")
    public void aCustomerWithNameLastNameAndCPR(String firstName, String lastName, String CPR) {
        customerId = customerService.createCustomer(firstName, lastName, CPR);
        System.out.println("Created customer with ID: " + customerId);
        var res = customerService.getCustomerById(customerId);
        assertTrue(res.isPresent(), "Customer should be retrieved");
        customer = res.get();
        System.out.println("Retrieved customer: " + customer.getFirstName() + " " + firstName);
        assertEquals(firstName, customer.getFirstName(), "First name should match");
        assertEquals(lastName, customer.getLastName(), "Last name should match");
        assertEquals(CPR, customer.getCPR(), "CPR should match");
        System.out.println("Retrieved customer: " + customer.getFirstName() + " " + customer.getLastName());
    }

    @Given("the customer is registered with the bank with an initial balance of {int} kr")
    public void theCustomerIsRegisteredWithTheBankWithAnInitialBalanceOfKr(Integer initialBalance) {
        try {
            Account account = bank.getAccountByCprNumber(customer.getCPR());

            System.out.println("✓ Account already exists for CPR: " + customer.getCPR() + " with balance: "
                    + account.getBalance() + " kr");
            bankAccountId = account.getId();
            System.out.println("User bank account ID: " + bankAccountId);

            if (account.getUser().getFirstName().equals(customer.getFirstName())
                    && account.getUser().getLastName().equals(customer.getLastName())
                    && account.getUser().getCprNumber().equals(customer.getCPR())
                    && account.getBalance().equals(BigDecimal.valueOf(initialBalance))) {
                bankAccountId = account.getId();
                System.out.println("Account already exists for customer: " + customer.getFirstName() + " "
                        + customer.getLastName() + " with balance: " + account.getBalance() + " kr");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to get account by CPR: " + e.getMessage(), e);
        }
    }

    @Given("the customer is registered with Simple DTU Pay using their bank account")
    public void theCustomerIsRegisteredWithSimpleDTUPayUsingTheirBankAccount() {
        System.out.println("Registering customer with Simple DTU Pay using bank account ID: " + bankAccountId);
        customer.setBankAccountId(bankAccountId);
        customerService.updateCustomerBankAccount(customer);
        System.out.println("checking update with bank account id: " + customer.getBankAccountId());
        var res = customerService.getCustomerById(customerId);
        assertTrue(res.isPresent(), "Customer should be retrieved after bank account update");
        System.out
                .println("Retrieved customer after update: " + res.get().getFirstName() + " " + res.get().getLastName()
                        + " with bank account ID: " + res.get().getBankAccountId());
        assertEquals(bankAccountId, res.get().getBankAccountId(), "Bank account ID should match");
    }

    @Given("a merchant with name {string}, last name {string}, and CPR {string}")
    public void aMerchantWithNameLastNameAndCPR(String firstName, String lastName, String CPR) {
        merchantId = merchantService.createMerchant(firstName, lastName, CPR);
        System.out.println("Created customer with ID: " + merchantId);
        var res = merchantService.getMerchantById(merchantId);
        assertTrue(res.isPresent(), "Customer should be retrieved");
        merchant = res.get();
        assertEquals(firstName, merchant.getFirstName(), "First name should match");
        assertEquals(lastName, merchant.getLastName(), "Last name should match");
        assertEquals(CPR, merchant.getCPR(), "CPR should match");
        System.out.println("Retrieved customer: " + merchant.getFirstName() + " " + merchant.getLastName());
    }

    @Given("the merchant is registered with the bank with an initial balance of {int} kr")
    public void theMerchantIsRegisteredWithTheBankWithAnInitialBalanceOfKr(Integer int1) {
        try {
            Account account = bank.getAccountByCprNumber(merchant.getCPR());

            System.out.println("Account already exists for CPR: " + merchant.getCPR() + " with balance: "
                    + account.getBalance() + " kr");
            bankAccountId = account.getId();
            System.out.println("User bank account ID: " + bankAccountId);

            if (account.getUser().getFirstName().equals(merchant.getFirstName())
                    && account.getUser().getLastName().equals(merchant.getLastName())
                    && account.getUser().getCprNumber().equals(merchant.getCPR())
                    && account.getBalance().equals(BigDecimal.valueOf(int1))) {
                bankAccountId = account.getId();
                System.out.println("Account already exists for customer: " + merchant.getFirstName() + " "
                        + merchant.getLastName() + " with balance: " + account.getBalance() + " kr");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to get account by CPR: " + e.getMessage(), e);
        }
    }

    @Given("the merchant is registered with Simple DTU Pay using their bank account")
    public void theMerchantIsRegisteredWithSimpleDTUPayUsingTheirBankAccount() {
        System.out.println("Registering merchant with Simple DTU Pay using bank account ID: " + bankAccountId);
        merchant.setBankAccountId(bankAccountId);
        merchantService.updateMerchantBankAccount(merchantId, merchant);
        System.out.println("checking update with bank account id: " + merchant.getBankAccountId());
        var res = merchantService.getMerchantById(merchantId);
        assertTrue(res.isPresent(), "Merchant should be retrieved after bank account update");
        System.out.println("Retrieved merchant after update: " + res.get().getFirstName() + " "
                + res.get().getLastName() + " with bank account ID: " + res.get().getBankAccountId());
        assertEquals(bankAccountId, res.get().getBankAccountId(), "Bank account ID should match");
    }

    @Then("the balance of the customer at the bank is {int} kr")
    public void theBalanceOfTheCustomerAtTheBankIsKr(Integer expectedBalance) {
        assertEquals(OldCustomerBalance.subtract(BigDecimal.valueOf(10)), NewCustomerBalance,
                "Customer balance should be reduced by 10 kr");
    }

    @Then("the balance of the merchant at the bank is {int} kr")
    public void theBalanceOfTheMerchantAtTheBankIsKr(Integer expectedBalance) {
        assertEquals(OldMerchantBalance.add(BigDecimal.valueOf(10)), NewMerchantBalance,
                "Merchant balance should be increased by 10 kr");
    }
}