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
public class ExecuteTransferInput {
    private UUID sourceAccountId;
    private UUID destinationAccountId;
    private Double amount;
    private String currency;
    private String country;
}
