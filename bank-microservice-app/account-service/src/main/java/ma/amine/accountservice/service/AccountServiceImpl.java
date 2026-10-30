package ma.amine.accountservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.amine.accountservice.domain.Account;
import ma.amine.accountservice.domain.AccountStatus;
import ma.amine.accountservice.repository.AccountRepository;
import ma.amine.accountservice.service.exception.AccountBlockedException;
import ma.amine.accountservice.service.exception.AccountNotFoundException;
import ma.amine.accountservice.service.exception.InsufficientBalanceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Override
    public Account createAccount(Account account) {
        if (account.getId() == null) {
            account.setId(UUID.randomUUID());
        }
        if (account.getStatus() == null) {
            account.setStatus(AccountStatus.ACTIVE);
        }
        log.info("Creating account with ID: {}", account.getId());
        return accountRepository.save(account);
    }

    @Override
    @Transactional(readOnly = true)
    public Account getAccountById(UUID id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    @Override
    public Account updateBalance(UUID id, double newBalance) {
        Account account = getAccountById(id);
        ensureAccountIsActive(account);
        account.setBalance(newBalance);
        log.info("Updated balance for account {}: {}", id, newBalance);
        return accountRepository.save(account);
    }

    @Override
    public Account credit(UUID id, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Credit amount must be strictly positive");
        }
        Account account = getAccountById(id);
        ensureAccountIsActive(account);
        account.setBalance(account.getBalance() + amount);
        log.info("Credited {} to account {}. New balance: {}", amount, id, account.getBalance());
        return accountRepository.save(account);
    }

    @Override
    public Account debit(UUID id, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Debit amount must be strictly positive");
        }
        Account account = getAccountById(id);
        ensureAccountIsActive(account);
        if (account.getBalance() < amount) {
            throw new InsufficientBalanceException("Insufficient balance for account " + id + ": current balance = " + account.getBalance() + ", requested = " + amount);
        }
        account.setBalance(account.getBalance() - amount);
        log.info("Debited {} from account {}. New balance: {}", amount, id, account.getBalance());
        return accountRepository.save(account);
    }

    @Override
    public void transfer(UUID sourceAccountId, UUID destinationAccountId, double amount) {
        if (sourceAccountId.equals(destinationAccountId)) {
            throw new IllegalArgumentException("Source and destination accounts must be different");
        }
        log.info("Initiating transfer of {} from account {} to {}", amount, sourceAccountId, destinationAccountId);
        debit(sourceAccountId, amount);
        credit(destinationAccountId, amount);
        log.info("Transfer completed successfully from {} to {}", sourceAccountId, destinationAccountId);
    }

    @Override
    public Account updateStatus(UUID id, AccountStatus status) {
        Account account = getAccountById(id);
        account.setStatus(status);
        log.info("Updated status for account {} to {}", id, status);
        return accountRepository.save(account);
    }

    private void ensureAccountIsActive(Account account) {
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new AccountBlockedException("Account " + account.getId() + " is currently BLOCKED and cannot perform operations");
        }
    }
}
