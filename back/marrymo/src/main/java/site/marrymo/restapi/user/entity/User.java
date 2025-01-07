package site.marrymo.restapi.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import site.marrymo.restapi.card.entity.Card;
import site.marrymo.restapi.global.entity.BaseTimeEntity;

import org.antlr.v4.runtime.misc.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.boot.context.properties.bind.DefaultValue;
import site.marrymo.restapi.user.dto.Who;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE user SET deleted_at = NOW() WHERE user_sequence = ?")
@Table(name = "user")
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_sequence")
    private Long userSequence;

    @NotNull
    @Column(name = "kakao_id", nullable = false)
    private String kakaoId;

    @Column(name = "bank_code")
    private String bankCode;

    @Column(name = "user_code", length = 10)
    private String userCode;

    private String email;

    @Column
    private Who who;

    @Column(name = "bride_account")
    private String brideAccount;

    @Column(name = "bride_fintech_use_num")
    private String brideFintechUseNum;

    @Column(name = "groom_account")
    private String groomAccount;

    @Column(name = "groom_fintech_use_num")
    private String groomFintechUseNum;

    @Column(name = "is_bride_once")
    @ColumnDefault("false")
    private Boolean isBrideOnce;

    @Column(name = "is_groom_once")
    @ColumnDefault("false")
    private Boolean isGroomOnce;

    @Column(name = "is_withdraw")
    private Boolean isWithdraw;

    @Column(name = "withdraw_at")
    private LocalDateTime withdrawAt;

    @OneToOne(mappedBy = "user")
    private Card card;

    @NotNull
    @Column(name = "is_agreement", nullable = false)
    private boolean isAgreement;

    @NotNull
    @Column(name = "is_required", nullable = false)
    private boolean isRequired;

    @Builder
    public User(String kakaoId,
                String bankCode,
                String userCode,
                String email,
                Who who,
                String brideAccount,
                String brideFintechUseNum,
                String groomAccount,
                String groomFintechUseNum,
                Boolean isBrideOnce,
                Boolean isGroomOnce,
                Boolean isWithdraw,
                LocalDateTime withdrawAt,
                boolean isAgreement,
                boolean isRequired
    ) {

        this.kakaoId = kakaoId;
        this.bankCode = bankCode;
        this.userCode = userCode;
        this.email = email;
        this.who = who;
        this.brideAccount = brideAccount;
        this.brideFintechUseNum = brideFintechUseNum;
        this.groomAccount = groomAccount;
        this.groomFintechUseNum = groomFintechUseNum;
        this.isBrideOnce = isBrideOnce;
        this.isGroomOnce = isGroomOnce;
        this.isWithdraw = isWithdraw;
        this.withdrawAt = withdrawAt;
        this.isAgreement = isAgreement;
        this.isRequired = isRequired;
    }

    public void modifyUserEmail(String email) {
        this.email = email;
    }

    public void modifyUserWho(Who who) {
        this.who = who;
    }

    public void setIsAgreement(boolean isAgreement) {
        this.isAgreement = isAgreement;
    }

    public void setIsRequired(boolean isRequired) {
        this.isRequired = isRequired;
    }

    public void setbrideAccount(String brideAccount) {
        this.brideAccount = brideAccount;
    }

    public void setBrideFintechUseNum(String brideFintechUseNum) {
        this.brideFintechUseNum = brideFintechUseNum;
    }

    public void setGroomAccount(String groomAccount) {
        this.groomAccount = groomAccount;
    }

    public void setGroomFintechUseNum(String fintechUseNum) {
        this.groomFintechUseNum = groomFintechUseNum;
    }

}
