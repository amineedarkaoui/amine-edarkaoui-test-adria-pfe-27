package ma.amine.accountservice.web.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.amine.accountservice.domain.Account;
import ma.amine.accountservice.service.AccountService;
import ma.amine.accountservice.web.rest.dto.AmountRequest;
import ma.amine.accountservice.web.rest.dto.CreateAccountRequest;
import ma.amine.accountservice.web.rest.dto.StatusRequest;
import ma.amine.accountservice.web.rest.dto.TransferRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
@CrossOrigin("*")
@Slf4j
public class AccountRestController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestBody CreateAccountRequest request) {
        log.info("REST: Creating account for customer: {}", request.getCustomerReference());
        Account account = Account.builder()
                .customerReference(request.getCustomerReference())
                .currency(request.getCurrency() != null ? request.getCurrency() : "EUR")
                .balance(request.getInitialBalance())
                .status(request.getStatus())
                .build();
        Account created = accountService.createAccount(account);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<Account>> getAllAccounts() {
        log.info("REST: Fetching all accounts");
        return ResponseEntity.ok(accountService.getAllAccounts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccountById(@PathVariable UUID id) {
        log.info("REST: Fetching account with ID: {}", id);
        return ResponseEntity.ok(accountService.getAccountById(id));
    }

    @PutMapping("/{id}/balance")
    public ResponseEntity<Account> updateBalance(@PathVariable UUID id, @RequestBody AmountRequest request) {
        log.info("REST: Updating balance for account: {} to {}", id, request.getAmount());
        return ResponseEntity.ok(accountService.updateBalance(id, request.getAmount()));
    }

    @PostMapping("/{id}/credit")
    public ResponseEntity<Account> credit(@PathVariable UUID id, @RequestBody AmountRequest request) {
        log.info("REST: Crediting {} to account: {}", request.getAmount(), id);
        return ResponseEntity.ok(accountService.credit(id, request.getAmount()));
    }

    @PostMapping("/{id}/debit")
    public ResponseEntity<Account> debit(@PathVariable UUID id, @RequestBody AmountRequest request) {
        log.info("REST: Debiting {} from account: {}", request.getAmount(), id);
        return ResponseEntity.ok(accountService.debit(id, request.getAmount()));
    }

    @PostMapping("/transfer")
    public ResponseEntity<Map<String, String>> transfer(@RequestBody TransferRequest request) {
        log.info("REST: Transferring {} from {} to {}", request.getAmount(), request.getSourceAccountId(), request.getDestinationAccountId());
        accountService.transfer(request.getSourceAccountId(), request.getDestinationAccountId(), request.getAmount());
        return ResponseEntity.ok(Map.of("message", "Transfer executed successfully"));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Account> updateStatus(@PathVariable UUID id, @RequestBody StatusRequest request) {
        log.info("REST: Updating status of account: {} to {}", id, request.getStatus());
        return ResponseEntity.ok(accountService.updateStatus(id, request.getStatus()));
    }
}
