package ma.amine.transactionservice;

import lombok.extern.slf4j.Slf4j;
import ma.amine.transactionservice.domain.Transaction;
import ma.amine.transactionservice.repository.TransactionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@SpringBootApplication
@Slf4j
public class TransactionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransactionServiceApplication.class, args);
    }

    @Bean
    public CommandLineRunner initTransactions(TransactionRepository transactionRepository) {
        return args -> {
            log.info("--- Seeding Initial Transactions ---");

            // Valid Account IDs corresponding to accounts seeded in account-service
            UUID account1Id = UUID.fromString("11111111-1111-1111-1111-111111111111");
            UUID account2Id = UUID.fromString("22222222-2222-2222-2222-222222222222");

            Transaction tx1 = Transaction.builder()
                    .id(UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"))
                    .accountId(account1Id)
                    .amount(BigDecimal.valueOf(150.00))
                    .currency("EUR")
                    .merchantCategory("GROCERY")
                    .country("MA")
                    .createdAt(Instant.now().minus(2, ChronoUnit.DAYS))
                    .build();

            Transaction tx2 = Transaction.builder()
                    .id(UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"))
                    .accountId(account1Id)
                    .amount(BigDecimal.valueOf(45.50))
                    .currency("EUR")
                    .merchantCategory("RESTAURANT")
                    .country("MA")
                    .createdAt(Instant.now().minus(1, ChronoUnit.DAYS))
                    .build();

            Transaction tx3 = Transaction.builder()
                    .id(UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc"))
                    .accountId(account2Id)
                    .amount(BigDecimal.valueOf(800.00))
                    .currency("EUR")
                    .merchantCategory("ELECTRONICS")
                    .country("FR")
                    .createdAt(Instant.now().minus(5, ChronoUnit.HOURS))
                    .build();

            Transaction tx4 = Transaction.builder()
                    .id(UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd"))
                    .accountId(account2Id)
                    .amount(BigDecimal.valueOf(120.00))
                    .currency("EUR")
                    .merchantCategory("TRAVEL")
                    .country("ES")
                    .createdAt(Instant.now().minus(30, ChronoUnit.MINUTES))
                    .build();

            transactionRepository.saveAll(List.of(tx1, tx2, tx3, tx4));

            log.info("Successfully seeded {} transactions:", transactionRepository.count());
            transactionRepository.findAll().forEach(tx -> log.info(" -> Transaction: [ID: {}, AccountID: {}, Amount: {} {}, Category: {}, Country: {}, Date: {}]",
                    tx.getId(), tx.getAccountId(), tx.getAmount(), tx.getCurrency(), tx.getMerchantCategory(), tx.getCountry(), tx.getCreatedAt()));
        };
    }
}
