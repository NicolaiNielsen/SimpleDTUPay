package dtu.service;

import dtu.ws.fastmoney.BankService_Service;
import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.User;
import dtu.ws.fastmoney.Account;
import dtu.ws.fastmoney.AccountInfo;

import java.math.BigDecimal;
import java.util.List;

public class BankServiceWrapper {
    BankService bank = new BankService_Service().getBankServicePort();
    private static final String API_KEY = "whale4307";

    /**
     * Get all users/accounts
     */
    public void getAllUsers() {
        try {
            List<AccountInfo> accounts = bank.getAccounts();
            System.out.println("\n=== All Users ===");
            for (AccountInfo account : accounts) {
                User user = account.getUser();
                System.out.println("Name: " + user.getFirstName() + " " + user.getLastName() +
                        " | CPR: " + user.getCprNumber() +
                        " | Account: " + account.getAccountId());
            }
            System.out.println("Total: " + accounts.size() + "\n");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    /**
     * Create a user and account with initial balance
     */
    public String createUser(String firstName, String lastName, String cpr, BigDecimal initialBalance) {
        try {
            User user = new User();
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setCprNumber(cpr);

            String accountId = bank.createAccountWithBalance(API_KEY, user, initialBalance);
            System.out.println("✓ Created: " + firstName + " " + lastName);
            return accountId;
        } catch (Exception e) {
            System.err.println("✗ Error: " + e.getMessage());
            return null;
        }
    }

    /**
     * Get account ID by CPR number
     */
    public String getAccountByCPR(String cpr) {
        try {
            List<AccountInfo> accounts = bank.getAccounts();
            for (AccountInfo account : accounts) {
                if (account.getUser().getCprNumber().equals(cpr)) {
                    System.out.println("Found account for CPR " + cpr + ": " + account.getAccountId());
                    return account.getAccountId();
                }
            }
            System.out.println("No account found for CPR: " + cpr);
            return null;
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return null;
        }
    }

    public boolean transferMoneyFromTo(String fromAccountId, String toAccountId, BigDecimal amount, String desc) {
        try {
            bank.transferMoneyFromTo(fromAccountId, toAccountId, amount, toAccountId);
            System.out.println("✓ Transfer successful: " + amount + " from " + fromAccountId + " to " + toAccountId);
            return true;
        } catch (Exception e) {
            System.err.println("✗ Error transferring money: " + e.getMessage());
            return false;
        }
    }

    /**
     * Delete/retire an account
     */
    public void deleteAccount(String accountId) {
        try {
            bank.retireAccount(API_KEY, accountId);
            System.out.println("✓ Account deleted: " + accountId);
        } catch (Exception e) {
            System.err.println("✗ Error deleting account: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        BankServiceWrapper wrapper = new BankServiceWrapper();

        try {
            Account account = wrapper.bank.getAccount("f2432c39-37b8-4980-be27-9fa2619cc55e");
            BigDecimal balance = account.getBalance();
            System.out.println("Account balance: " + balance + " kr");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

}
