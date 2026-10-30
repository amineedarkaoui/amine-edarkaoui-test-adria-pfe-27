package ma.amine.accountservice.web.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.amine.accountservice.domain.AccountStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusRequest {
    private AccountStatus status;
}
