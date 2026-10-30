package ma.amine.accountservice;

import lombok.extern.slf4j.Slf4j;
import ma.amine.accountservice.domain.Account;
import ma.amine.accountservice.domain.AccountStatus;
import ma.amine.accountservice.repository.AccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.UUID;

@SpringBootApplication
@Slf4j
public class AccountServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountServiceApplication.class, args);
    }

    @Bean
    public CommandLineRunner initAccounts(AccountRepository accountRepository) {
        return args -> {
            log.info("--- Seeding Initial Accounts ---");

            Account account1 = Account.builder()
                    .id(UUID.fromString("11111111-1111-1111-1111-111111111111"))
                    .customerReference("CUST-1001")
                    .status(AccountStatus.ACTIVE)
                    .currency("EUR")
                    .balance(5400.0)
                    .build();

            Account account2 = Account.builder()
                    .id(UUID.fromString("22222222-2222-2222-2222-222222222222"))
                    .customerReference("CUST-1002")
                    .status(AccountStatus.ACTIVE)
                    .currency("EUR")
                    .balance(12500.50)
                    .build();

            Account account3 = Account.builder()
                    .id(UUID.fromString("33333333-3333-3333-3333-333333333333"))
                    .customerReference("CUST-1003")
                    .status(AccountStatus.BLOCKED)
                    .currency("USD")
                    .balance(300.0)
                    .build();

            accountRepository.saveAll(List.of(account1, account2, account3));

            log.info("Successfully seeded {} accounts:", accountRepository.count());
            accountRepository.findAll().forEach(acc -> log.info(" -> Account: [ID: {}, Customer: {}, Status: {}, Currency: {}, Balance: {}]",
                    acc.getId(), acc.getCustomerReference(), acc.getStatus(), acc.getCurrency(), acc.getBalance()));
        };
    }
}
