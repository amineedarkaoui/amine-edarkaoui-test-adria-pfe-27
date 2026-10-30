package ma.amine.accountservice.service;

import ma.amine.accountservice.domain.Account;
import ma.amine.accountservice.domain.AccountStatus;

import java.util.List;
import java.util.UUID;

public interface AccountService {
    Account createAccount(Account account);
    Account getAccountById(UUID id);
    List<Account> getAllAccounts();
    Account updateBalance(UUID id, double newBalance);
    Account credit(UUID id, double amount);
    Account debit(UUID id, double amount);
    void transfer(UUID sourceAccountId, UUID destinationAccountId, double amount);
    Account updateStatus(UUID id, AccountStatus status);
}
