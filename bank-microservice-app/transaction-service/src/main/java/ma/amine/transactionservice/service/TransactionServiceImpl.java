package ma.amine.transactionservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.amine.transactionservice.domain.AccountDTO;
import ma.amine.transactionservice.domain.Transaction;
import ma.amine.transactionservice.repository.TransactionRepository;
import ma.amine.transactionservice.service.exception.AccountNotActiveException;
import ma.amine.transactionservice.service.exception.AccountNotFoundException;
import ma.amine.transactionservice.service.exception.CurrencyMismatchException;
import ma.amine.transactionservice.service.exception.InsufficientBalanceException;
import ma.amine.transactionservice.service.exception.InvalidTransactionException;
import ma.amine.transactionservice.service.exception.TransactionAmountExceededException;
import ma.amine.transactionservice.service.exception.TransactionNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountServiceClient accountServiceClient;

    @Value("${bank.transaction.max-amount:1000.0}")
    private double maxTransactionAmount;

    @Override
    public Transaction recordTransaction(Transaction transaction) {
        log.info("Processing transaction for account: {}", transaction.getAccountId());

        // 1. Validation du montant (strictement positif)
        if (transaction.getAmount() == null || transaction.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionException("Transaction amount must be strictly positive");
        }

        // 1. Respect du plafond configuré
        BigDecimal maxAmount = BigDecimal.valueOf(maxTransactionAmount);
        if (transaction.getAmount().compareTo(maxAmount) > 0) {
            throw new TransactionAmountExceededException(
                    "Transaction amount " + transaction.getAmount() + " exceeds maximum allowed limit of " + maxAmount
            );
        }

        if (transaction.getAccountId() == null) {
            throw new InvalidTransactionException("Account ID is mandatory");
        }

        // 2. Consultation et vérification du compte (doit exister et être actif)
        AccountDTO account = accountServiceClient.getAccountById(transaction.getAccountId());
        if (account == null) {
            throw new AccountNotFoundException("Account not found with ID: " + transaction.getAccountId());
        }

        if (!"ACTIVE".equalsIgnoreCase(account.getStatus())) {
            throw new AccountNotActiveException(
                    "Account " + transaction.getAccountId() + " is not active (current status: " + account.getStatus() + ")"
            );
        }

        // 3. Correspondance de la devise
        if (transaction.getCurrency() == null || !transaction.getCurrency().equalsIgnoreCase(account.getCurrency())) {
            throw new CurrencyMismatchException(
                    "Transaction currency (" + transaction.getCurrency() + ") does not match account currency (" + account.getCurrency() + ")"
            );
        }

        // 4. Consultation du solde du compte et vérification de provision
        double transactionAmount = transaction.getAmount().doubleValue();
        if (account.getBalance() < transactionAmount) {
            throw new InsufficientBalanceException(
                    "Insufficient balance on account " + account.getId() + ". Available: " + account.getBalance() + ", Requested: " + transactionAmount
            );
        }

        // Initialisation des métadonnées
        if (transaction.getId() == null) {
            transaction.setId(UUID.randomUUID());
        }
        if (transaction.getCreatedAt() == null) {
            transaction.setCreatedAt(Instant.now());
        }

        // Enregistrement de la transaction
        Transaction savedTransaction = transactionRepository.save(transaction);
        log.info("Transaction {} persisted successfully", savedTransaction.getId());

        // Mise à jour du solde du compte (débit)
        accountServiceClient.debit(transaction.getAccountId(), transactionAmount);
        log.info("Balance updated for account {}", transaction.getAccountId());

        return savedTransaction;
    }

    @Override
    @Transactional(readOnly = true)
    public Transaction getTransactionById(UUID id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found with ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByAccountId(UUID accountId) {
        return transactionRepository.findByAccountId(accountId);
    }

    @Override
    public void executeTransfer(UUID sourceAccountId, UUID destinationAccountId, BigDecimal amount, String currency, String country) {
        log.info("Executing transfer of {} {} from {} to {}", amount, currency, sourceAccountId, destinationAccountId);

        if (sourceAccountId.equals(destinationAccountId)) {
            throw new InvalidTransactionException("Source and destination accounts must be distinct");
        }

        // Enregistrement de la transaction de débit sur le compte source
        Transaction debitTx = Transaction.builder()
                .id(UUID.randomUUID())
                .accountId(sourceAccountId)
                .amount(amount)
                .currency(currency)
                .merchantCategory("TRANSFER_OUT")
                .country(country != null ? country : "MA")
                .createdAt(Instant.now())
                .build();
        recordTransaction(debitTx);

        // Crédit sur le compte destinataire
        AccountDTO destAccount = accountServiceClient.getAccountById(destinationAccountId);
        if (destAccount == null) {
            throw new AccountNotFoundException("Destination account not found with ID: " + destinationAccountId);
        }
        if (!"ACTIVE".equalsIgnoreCase(destAccount.getStatus())) {
            throw new AccountNotActiveException("Destination account is not active");
        }

        accountServiceClient.credit(destinationAccountId, amount.doubleValue());

        // Enregistrement de la transaction de crédit
        Transaction creditTx = Transaction.builder()
                .id(UUID.randomUUID())
                .accountId(destinationAccountId)
                .amount(amount)
                .currency(currency)
                .merchantCategory("TRANSFER_IN")
                .country(country != null ? country : "MA")
                .createdAt(Instant.now())
                .build();
        transactionRepository.save(creditTx);

        log.info("Transfer completed successfully from {} to {}", sourceAccountId, destinationAccountId);
    }
}
