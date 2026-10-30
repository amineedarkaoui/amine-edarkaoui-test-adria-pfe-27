package ma.amine.transactionservice.service;

import ma.amine.transactionservice.domain.Transaction;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface TransactionService {
    Transaction recordTransaction(Transaction transaction);
    Transaction getTransactionById(UUID id);
    List<Transaction> getAllTransactions();
    List<Transaction> getTransactionsByAccountId(UUID accountId);
    void executeTransfer(UUID sourceAccountId, UUID destinationAccountId, BigDecimal amount, String currency, String country);
}
