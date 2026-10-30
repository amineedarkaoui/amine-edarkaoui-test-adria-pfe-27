package ma.amine.accountservice.web.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.amine.accountservice.domain.AccountStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateAccountRequest {
    private String customerReference;
    private String currency;
    private double initialBalance;
    private AccountStatus status;
}
