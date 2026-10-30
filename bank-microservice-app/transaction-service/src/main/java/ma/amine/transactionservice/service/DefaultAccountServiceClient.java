package ma.amine.transactionservice.service;

import lombok.extern.slf4j.Slf4j;
import ma.amine.transactionservice.domain.AccountDTO;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default implementation of AccountServiceClient used as fallback or until OpenFeign is wired.
 */
@Component
@ConditionalOnMissingBean(name = "feignAccountServiceClient")
@Slf4j
public class DefaultAccountServiceClient implements AccountServiceClient {

    private final Map<UUID, AccountDTO> mockAccounts = new ConcurrentHashMap<>();

    public DefaultAccountServiceClient() {
        // Pre-populate with valid seed accounts matching account-service
        UUID acc1 = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID acc2 = UUID.fromString("22222222-2222-2222-2222-222222222222");
        UUID acc3 = UUID.fromString("33333333-3333-3333-3333-333333333333");

        mockAccounts.put(acc1, AccountDTO.builder().id(acc1).customerReference("CUST-1001").status("ACTIVE").currency("EUR").balance(5400.0).build());
        mockAccounts.put(acc2, AccountDTO.builder().id(acc2).customerReference("CUST-1002").status("ACTIVE").currency("EUR").balance(12500.50).build());
        mockAccounts.put(acc3, AccountDTO.builder().id(acc3).customerReference("CUST-1003").status("BLOCKED").currency("USD").balance(300.0).build());
    }

    @Override
    public AccountDTO getAccountById(UUID accountId) {
        log.info("DefaultAccountServiceClient: Fetching accountId: {}", accountId);
        return mockAccounts.computeIfAbsent(accountId, id -> AccountDTO.builder()
                .id(id)
                .customerReference("CUST-" + id.toString().substring(0, 8))
                .status("ACTIVE")
                .currency("EUR")
                .balance(5000.0)
                .build());
    }

    @Override
    public void updateBalance(UUID accountId, double newBalance) {
        AccountDTO acc = getAccountById(accountId);
        acc.setBalance(newBalance);
        log.info("DefaultAccountServiceClient: Updated balance for account {} to {}", accountId, newBalance);
    }

    @Override
    public void debit(UUID accountId, double amount) {
        AccountDTO acc = getAccountById(accountId);
        acc.setBalance(acc.getBalance() - amount);
        log.info("DefaultAccountServiceClient: Debited {} from account {}. New balance: {}", amount, accountId, acc.getBalance());
    }

    @Override
    public void credit(UUID accountId, double amount) {
        AccountDTO acc = getAccountById(accountId);
        acc.setBalance(acc.getBalance() + amount);
        log.info("DefaultAccountServiceClient: Credited {} to account {}. New balance: {}", amount, accountId, acc.getBalance());
    }
}
