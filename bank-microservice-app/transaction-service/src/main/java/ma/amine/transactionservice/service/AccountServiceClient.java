package ma.amine.transactionservice.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import ma.amine.transactionservice.domain.AccountDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;
import java.util.UUID;

@FeignClient(name = "account-service")
public interface AccountServiceClient {

    @GetMapping("/accounts/{id}")
    @CircuitBreaker(name = "accountService", fallbackMethod = "getDefaultAccount")
    AccountDTO getAccountById(@PathVariable UUID id);

    default AccountDTO getDefaultAccount(UUID id, Exception e) {
        return AccountDTO.builder()
                .id(id)
                .customerReference("Not available")
                .status("BLOCKED")
                .currency("EUR")
                .balance(0.0)
                .build();
    }

    @PutMapping("/accounts/{id}/balance")
    @CircuitBreaker(name = "accountService", fallbackMethod = "getDefaultUpdateBalance")
    AccountDTO updateBalance(@PathVariable UUID id, @RequestBody Map<String, Double> request);

    default AccountDTO getDefaultUpdateBalance(UUID id, Map<String, Double> request, Exception e) {
        return getDefaultAccount(id, e);
    }

    @PostMapping("/accounts/{id}/debit")
    @CircuitBreaker(name = "accountService", fallbackMethod = "getDefaultDebit")
    AccountDTO debit(@PathVariable UUID id, @RequestBody Map<String, Double> request);

    default AccountDTO getDefaultDebit(UUID id, Map<String, Double> request, Exception e) {
        return getDefaultAccount(id, e);
    }

    @PostMapping("/accounts/{id}/credit")
    @CircuitBreaker(name = "accountService", fallbackMethod = "getDefaultCredit")
    AccountDTO credit(@PathVariable UUID id, @RequestBody Map<String, Double> request);

    default AccountDTO getDefaultCredit(UUID id, Map<String, Double> request, Exception e) {
        return getDefaultAccount(id, e);
    }

    default void updateBalance(UUID id, double newBalance) {
        updateBalance(id, Map.of("amount", newBalance));
    }

    default void debit(UUID id, double amount) {
        debit(id, Map.of("amount", amount));
    }

    default void credit(UUID id, double amount) {
        credit(id, Map.of("amount", amount));
    }
}
