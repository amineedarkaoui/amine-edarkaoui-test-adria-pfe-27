package ma.amine.transactionservice.service;

import ma.amine.transactionservice.domain.AccountDTO;

import java.util.UUID;

public interface AccountServiceClient {
    AccountDTO getAccountById(UUID accountId);
    void updateBalance(UUID accountId, double newBalance);
    void debit(UUID accountId, double amount);
    void credit(UUID accountId, double amount);
}
