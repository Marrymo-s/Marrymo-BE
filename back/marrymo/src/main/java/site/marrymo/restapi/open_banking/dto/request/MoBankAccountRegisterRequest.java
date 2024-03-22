package site.marrymo.restapi.open_banking.dto.request;

import lombok.Builder;
import lombok.Data;
import site.marrymo.restapi.user.dto.Who;

@Data
@Builder
public class MoBankAccountRegisterRequest {
    String bankCode;
    String accountNum;
    String fintechUseNum;
    String username;
}
