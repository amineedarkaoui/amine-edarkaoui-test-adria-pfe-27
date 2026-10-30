package ma.amine.accountservice.web.graphql;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.amine.accountservice.domain.AccountStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateAccountInput {
    private String customerReference;
    private String currency;
    private Double initialBalance;
    private AccountStatus status;
}
