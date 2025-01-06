package site.marrymo.restapi.user.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvitationIssueRequest {
    @NotNull(message = "청첩장 발급 여부 정보는 null 값이면 안됩니다")
    private Boolean isIssued;
}
