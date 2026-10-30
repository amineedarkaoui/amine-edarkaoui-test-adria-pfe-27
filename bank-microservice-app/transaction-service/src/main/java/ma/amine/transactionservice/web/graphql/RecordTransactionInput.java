package ma.amine.transactionservice.web.graphql;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecordTransactionInput {
    private UUID accountId;
    private Double amount;
    private String currency;
    private String merchantCategory;
    private String country;
}
