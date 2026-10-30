package ma.amine.transactionservice.web.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.amine.transactionservice.domain.Transaction;
import ma.amine.transactionservice.service.TransactionService;
import ma.amine.transactionservice.web.rest.dto.CreateTransactionRequest;
import ma.amine.transactionservice.web.rest.dto.TransferTransactionRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
@CrossOrigin("*")
@Slf4j
public class TransactionRestController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<Transaction> recordTransaction(@RequestBody CreateTransactionRequest request) {
        log.info("REST: Recording transaction for account: {}, amount: {}", request.getAccountId(), request.getAmount());
        Transaction transaction = Transaction.builder()
                .accountId(request.getAccountId())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .merchantCategory(request.getMerchantCategory())
                .country(request.getCountry())
                .createdAt(Instant.now())
                .build();
        Transaction created = transactionService.recordTransaction(transaction);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        log.info("REST: Fetching all transactions");
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable UUID id) {
        log.info("REST: Fetching transaction by ID: {}", id);
        return ResponseEntity.ok(transactionService.getTransactionById(id));
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<Transaction>> getTransactionsByAccountId(@PathVariable UUID accountId) {
        log.info("REST: Fetching transactions for account: {}", accountId);
        return ResponseEntity.ok(transactionService.getTransactionsByAccountId(accountId));
    }

    @PostMapping("/transfer")
    public ResponseEntity<Map<String, String>> executeTransfer(@RequestBody TransferTransactionRequest request) {
        log.info("REST: Transfer request of {} {} from {} to {}",
                request.getAmount(), request.getCurrency(), request.getSourceAccountId(), request.getDestinationAccountId());
        transactionService.executeTransfer(
                request.getSourceAccountId(),
                request.getDestinationAccountId(),
                request.getAmount(),
                request.getCurrency(),
                request.getCountry()
        );
        return ResponseEntity.ok(Map.of("message", "Transfer executed and recorded successfully"));
    }
}
