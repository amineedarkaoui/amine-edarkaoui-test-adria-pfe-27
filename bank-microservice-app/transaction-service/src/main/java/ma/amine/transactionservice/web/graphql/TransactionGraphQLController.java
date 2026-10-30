package ma.amine.transactionservice.web.graphql;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.amine.transactionservice.domain.Transaction;
import ma.amine.transactionservice.service.TransactionService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@Slf4j
public class TransactionGraphQLController {

    private final TransactionService transactionService;

    @QueryMapping
    public List<Transaction> allTransactions() {
        log.info("GraphQL: Query allTransactions");
        return transactionService.getAllTransactions();
    }

    @QueryMapping
    public Transaction transactionById(@Argument UUID id) {
        log.info("GraphQL: Query transactionById: {}", id);
        return transactionService.getTransactionById(id);
    }

    @QueryMapping
    public List<Transaction> transactionsByAccountId(@Argument UUID accountId) {
        log.info("GraphQL: Query transactionsByAccountId: {}", accountId);
        return transactionService.getTransactionsByAccountId(accountId);
    }

    @MutationMapping
    public Transaction recordTransaction(@Argument RecordTransactionInput input) {
        log.info("GraphQL: Mutation recordTransaction for account: {}, amount: {}", input.getAccountId(), input.getAmount());
        Transaction transaction = Transaction.builder()
                .accountId(input.getAccountId())
                .amount(input.getAmount() != null ? BigDecimal.valueOf(input.getAmount()) : null)
                .currency(input.getCurrency())
                .merchantCategory(input.getMerchantCategory())
                .country(input.getCountry())
                .createdAt(Instant.now())
                .build();
        return transactionService.recordTransaction(transaction);
    }

    @MutationMapping
    public Boolean executeTransfer(@Argument ExecuteTransferInput input) {
        log.info("GraphQL: Mutation executeTransfer from {} to {}, amount: {}",
                input.getSourceAccountId(), input.getDestinationAccountId(), input.getAmount());
        transactionService.executeTransfer(
                input.getSourceAccountId(),
                input.getDestinationAccountId(),
                BigDecimal.valueOf(input.getAmount()),
                input.getCurrency(),
                input.getCountry()
        );
        return true;
    }
}
