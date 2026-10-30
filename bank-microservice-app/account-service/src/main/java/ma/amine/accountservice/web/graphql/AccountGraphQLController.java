package ma.amine.accountservice.web.graphql;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.amine.accountservice.domain.Account;
import ma.amine.accountservice.domain.AccountStatus;
import ma.amine.accountservice.service.AccountService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AccountGraphQLController {

    private final AccountService accountService;

    @QueryMapping
    public List<Account> allAccounts() {
        log.info("GraphQL: Query allAccounts");
        return accountService.getAllAccounts();
    }

    @QueryMapping
    public Account accountById(@Argument UUID id) {
        log.info("GraphQL: Query accountById: {}", id);
        return accountService.getAccountById(id);
    }

    @MutationMapping
    public Account createAccount(@Argument CreateAccountInput input) {
        log.info("GraphQL: Mutation createAccount for customer: {}", input.getCustomerReference());
        Account account = Account.builder()
                .customerReference(input.getCustomerReference())
                .currency(input.getCurrency() != null ? input.getCurrency() : "EUR")
                .balance(input.getInitialBalance() != null ? input.getInitialBalance() : 0.0)
                .status(input.getStatus() != null ? input.getStatus() : AccountStatus.ACTIVE)
                .build();
        return accountService.createAccount(account);
    }

    @MutationMapping
    public Account updateBalance(@Argument UUID id, @Argument Float balance) {
        log.info("GraphQL: Mutation updateBalance for account: {} to {}", id, balance);
        return accountService.updateBalance(id, balance.doubleValue());
    }

    @MutationMapping
    public Account creditAccount(@Argument UUID id, @Argument Float amount) {
        log.info("GraphQL: Mutation creditAccount: {} on account: {}", amount, id);
        return accountService.credit(id, amount.doubleValue());
    }

    @MutationMapping
    public Account debitAccount(@Argument UUID id, @Argument Float amount) {
        log.info("GraphQL: Mutation debitAccount: {} on account: {}", amount, id);
        return accountService.debit(id, amount.doubleValue());
    }

    @MutationMapping
    public Boolean transfer(@Argument UUID sourceAccountId, @Argument UUID destinationAccountId, @Argument Float amount) {
        log.info("GraphQL: Mutation transfer of {} from {} to {}", amount, sourceAccountId, destinationAccountId);
        accountService.transfer(sourceAccountId, destinationAccountId, amount.doubleValue());
        return true;
    }

    @MutationMapping
    public Account updateAccountStatus(@Argument UUID id, @Argument AccountStatus status) {
        log.info("GraphQL: Mutation updateAccountStatus for account: {} to {}", id, status);
        return accountService.updateStatus(id, status);
    }
}
