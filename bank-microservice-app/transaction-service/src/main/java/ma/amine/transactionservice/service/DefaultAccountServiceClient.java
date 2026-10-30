package ma.amine.transactionservice.service;

import lombok.extern.slf4j.Slf4j;
import ma.amine.transactionservice.domain.AccountDTO;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Default implementation of AccountServiceClient used as fallback or until OpenFeign is wired.
 */
@Component
@ConditionalOnMissingBean(name = "feignAccountServiceClient")
@Slf4j
public class DefaultAccountServiceClient implements AccountServiceClient {

    @Override
    public AccountDTO getAccountById(UUID accountId) {
        log.warn("Using DefaultAccountServiceClient. Fetching accountId: {}", accountId);
        // Default placeholder representation (ACTIVE with 5000 EUR default balance for standalone testing)
        return AccountDTO.builder()
                .id(accountId)
                .customerReference("CUST-" + accountId.toString().substring(0, 8))
                .status("ACTIVE")
                .currency("EUR")
                .balance(5000.0)
                .build();
    }

    @Override
    public void updateBalance(UUID accountId, double newBalance) {
        log.info("DefaultAccountServiceClient: Updating balance for account {} to {}", accountId, newBalance);
    }

    @Override
    public void debit(UUID accountId, double amount) {
        log.info("DefaultAccountServiceClient: Debiting {} from account {}", amount, accountId);
    }

    @Override
    public void credit(UUID accountId, double amount) {
        log.info("DefaultAccountServiceClient: Crediting {} to account {}", amount, accountId);
    }
}
