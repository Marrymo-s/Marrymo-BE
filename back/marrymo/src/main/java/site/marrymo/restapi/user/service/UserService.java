package site.marrymo.restapi.user.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import site.marrymo.restapi.global.redis.service.RedisService;
import site.marrymo.restapi.global.smtp.dto.request.SmtpVerifyRequest;
import site.marrymo.restapi.global.smtp.service.SmtpService;
import site.marrymo.restapi.user.repository.BlackListRepository;
import site.marrymo.restapi.card.entity.Card;
import site.marrymo.restapi.card.exception.CardErrorCode;
import site.marrymo.restapi.card.exception.CardException;
import site.marrymo.restapi.card.repository.CardRepository;
import site.marrymo.restapi.global.jwt.entity.BlackList;
import site.marrymo.restapi.global.s3.service.AwsS3Service;
import site.marrymo.restapi.global.util.UserCodeGenerator;
import site.marrymo.restapi.user.dto.UserDTO;
import site.marrymo.restapi.user.dto.Who;
import site.marrymo.restapi.user.dto.request.*;
import site.marrymo.restapi.user.dto.response.InvitationIssueResponse;
import site.marrymo.restapi.user.dto.response.PermissionResponse;
import site.marrymo.restapi.user.dto.response.UserGetResponse;
import site.marrymo.restapi.user.dto.response.VerifyAccountResponse;
import site.marrymo.restapi.user.entity.User;
import site.marrymo.restapi.user.exception.UserErrorCode;
import site.marrymo.restapi.user.exception.UserException;
import site.marrymo.restapi.user.repository.UserRepository;
import site.marrymo.restapi.wedding_img.entity.WeddingImg;
import site.marrymo.restapi.wedding_img.repository.WeddingImgRepository;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private static final String AUTH_CODE_PREFIX = "AuthCode ";
    private final UserRepository userRepository;
    private final CardRepository cardRepository;
    private final WeddingImgRepository weddingImgRepository;
    private final AwsS3Service awsS3Service;
    private final BlackListRepository blackListRepository;
    private final RedisService redisService;
    private final SmtpService smtpService;

    @Value("${spring.mail.auth-code-expiration-millis}")
    private long authCodeExpirationMillis;

    public String makeUniqueUserCode() {
        UserCodeGenerator userCodeGenerator = new UserCodeGenerator();

        String uniqueUserCode = "";
        while (true) {
            String userCode = userCodeGenerator.makeUserCode();

            //userCode가 유니크 한지 확인
            if (userRepository.findByUserCode(userCode).isEmpty()) {
                uniqueUserCode = userCode;
                break;
            }
        }

        return uniqueUserCode;
    }

    public void registUserInfo(UserDTO userDTO, UserRegistRequest userRegistRequest) {
        //user table에 email 정보 저장
        User user = userRepository.findByUserSequence(userDTO.getUserSequence())
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        user.setIsRequired(true);

        user.modifyUserEmail(userRegistRequest.getEmail());
        userRepository.save(user);

        //card table에 청첩장 정보 저장
        cardRepository.save(Card.builder()
                .user(user)
                .groomName(userRegistRequest.getGroomName())
                .brideName(userRegistRequest.getBrideName())
                .groomContact(userRegistRequest.getGroomContact())
                .brideContact(userRegistRequest.getBrideContact())
                .weddingDate(LocalDate.parse(userRegistRequest.getWeddingDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .weddingTime(LocalTime.parse(userRegistRequest.getWeddingTime(), DateTimeFormatter.ofPattern("HH:mm:ss")))
                .weddingDay(userRegistRequest.getWeddingDay())
                .invitationUrl("https://marrymo.site/" + user.getUserCode())
                .location(userRegistRequest.getLocation())
                .groomFather(userRegistRequest.getGroomFather())
                .groomMother(userRegistRequest.getGroomMother())
                .brideFather(userRegistRequest.getBrideFather())
                .brideMother(userRegistRequest.getBrideMother())
                .greeting(userRegistRequest.getGreeting())
                .isIssued(Boolean.valueOf(false))
                .build());

        //웨딩 이미지에 이미지 정보 저장
        Card card = cardRepository.findByUser(user)
                .orElseThrow(() -> new CardException(CardErrorCode.CARD_NOT_FOUND));

        if (userRegistRequest.getImgUrl() != null) {
            for (MultipartFile file : userRegistRequest.getImgUrl()) {
                try {
                    String imgUrl = awsS3Service.uploadFileImage("wedding_img", file, user.getUserCode());
                    System.out.println("imgUrl:" + imgUrl);

                    weddingImgRepository.save(
                            WeddingImg.builder()
                                    .card(card)
                                    .imgUrl(imgUrl)
                                    .build());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void modifyUserInfo(UserDTO userDTO, UserModifyRequest userModifyRequest) {
        //user table에 email 정보 저장
        User user = userRepository.findByUserSequence(userDTO.getUserSequence())
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        user.modifyUserEmail(userModifyRequest.getEmail());
        userRepository.save(user);

        //card table에 청첩장 정보 저장
        Card card = cardRepository.findByUser(user)
                .orElseThrow(() -> new CardException(CardErrorCode.CARD_NOT_FOUND));

        card.modifyGroomName(userModifyRequest.getGroomName());
        card.modifyBrideName(userModifyRequest.getBrideName());
        card.modifyGroomContact(userModifyRequest.getGroomContact());
        card.modifyBrideContact(userModifyRequest.getBrideContact());
        card.modifyWeddingDate(LocalDate.parse(userModifyRequest.getWeddingDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        card.modifyWeddingTime(LocalTime.parse(userModifyRequest.getWeddingTime(), DateTimeFormatter.ofPattern("HH:mm:ss")));
        card.modifyWeddingDay(userModifyRequest.getWeddingDay());
        card.modifyLocation(userModifyRequest.getLocation());
        card.modifyGreeting(userModifyRequest.getGreeting());
        card.modifyGroomFather(userModifyRequest.getGroomFather());
        card.modifyGroomMother(userModifyRequest.getGroomMother());
        card.modifyBrideFather(userModifyRequest.getBrideFather());
        card.modifyBrideMother(userModifyRequest.getBrideMother());

        cardRepository.save(card);

        //웨딩 이미지에 이미지 정보 저장

        //저장되어 있는 이미지 정보 모두 삭제
        weddingImgRepository.deleteAll();

        if (userModifyRequest.getImgUrl() != null) {
            for (MultipartFile file : userModifyRequest.getImgUrl()) {
                try {
                    String imgUrl = awsS3Service.uploadFileImage("wedding_img", file, user.getUserCode());

                    weddingImgRepository.save(
                            WeddingImg.builder()
                                    .card(card)
                                    .imgUrl(imgUrl)
                                    .build());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public UserGetResponse getUserInfo(UserDTO userDTO, String userCode) {
        boolean isMem = true;

        //회원이 접근 할 경우
        //쿠키를 까봤을 때 나온 userCode와 프론트에서 보낸 userCode가 다르다면
        if (userDTO != null && !userDTO.getUserCode().equals(userCode))
            isMem = false;

        //비회원이 접근할 경우
        if (userDTO == null)
            isMem = false;

        User user = userRepository.findByUserCode(userCode)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        Card card = cardRepository.findByUser(user)
                .orElseThrow(() -> new CardException(CardErrorCode.CARD_NOT_FOUND));

        List<WeddingImg> weddingImgList = weddingImgRepository.findByCard(card);
        List<String> imgUrlList = new ArrayList<>();

        for (WeddingImg weddingImg : weddingImgList) {
            //img가 삭제된 시간이 찍혀 있으면 얻어오지 않는다
            if (weddingImg.getDeletedAt() != null)
                continue;

            imgUrlList.add(weddingImg.getImgUrl());
        }

        return UserGetResponse.toDto(user, card, imgUrlList, Boolean.valueOf(isMem));
    }

    public void deleteUser(UserDTO userDTO) {
        User user = userRepository.findByUserSequence(userDTO.getUserSequence())
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        if (user.getDeletedAt() != null)
            throw new UserException(UserErrorCode.USER_ALREADY_DELETE);

        Card card = user.getCard();
        card.modifyInvitationUrl(null);
        cardRepository.save(card);

        userRepository.delete(user);
    }

    public InvitationIssueResponse invitationIssued(UserDTO userDTO, InvitationIssueRequest invitationIssueRequest) {
        User user = userRepository.findByUserSequence(userDTO.getUserSequence())
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        Card card = user.getCard();
        if (card == null) {
            throw new CardException(CardErrorCode.CARD_NOT_FOUND);
        }

        card.modifyIsIssued(invitationIssueRequest.getIsIssued());
        cardRepository.save(card);

        return InvitationIssueResponse.builder().isIssued(invitationIssueRequest.getIsIssued()).build();
    }

    public void registWho(UserDTO userDTO, WhoRegistRequest whoRegistRequest) {
        User user = userRepository.findByUserSequence(userDTO.getUserSequence())
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        Who who = null;

        if (whoRegistRequest.getWho().equals("GROOM")) {
            who = Who.GROOM;
        } else if (whoRegistRequest.getWho().equals("BRIDE")) {
            who = Who.BRIDE;
        } else if (whoRegistRequest.getWho().equals("BOTH")) {
            who = Who.BOTH;
        }

        user.modifyUserWho(who);
        userRepository.save(user);
    }

    public VerifyAccountResponse verifyAccount(UserDTO userDTO) {
        boolean isVerify = false;

        User user = userRepository.findByUserSequence(userDTO.getUserSequence())
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        if (user.getWho() == Who.GROOM) {
            if (user.getGroomAccount() != null && !user.getGroomAccount().equals(""))
                isVerify = true;
        } else if (user.getWho() == Who.BRIDE) {
            if (user.getBrideAccount() != null && !user.getBrideAccount().equals(""))
                isVerify = true;

        } else if (user.getWho() == Who.BOTH) {
            if (user.getGroomAccount() != null && !user.getGroomAccount().equals("") &&
                    user.getBrideAccount() != null && !user.getBrideAccount().equals("")) {
                isVerify = true;
            }
        }

        return VerifyAccountResponse.builder().isVerify(isVerify).build();
    }

    public void patchAgreement(UserDTO userDTO, PrivacyRegistRequest privacyRegistRequest) {
        User user = userRepository.findByUserSequence(userDTO.getUserSequence())
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        user.setIsAgreement(privacyRegistRequest.getIsAgreement());
        userRepository.save(user);
    }

    public PermissionResponse getUserPermission(UserDTO userDTO) {
        User user = userRepository.findByUserSequence(userDTO.getUserSequence())
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        Boolean isAgreement = user.isAgreement();
        Boolean isRequired = user.isRequired();

        return PermissionResponse.builder()
                .isAgreement(Boolean.valueOf(user.isAgreement()))
                .isRequired(Boolean.valueOf(user.isRequired()))
                .build();
    }

    public void logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        String refreshToken = "";
        //access token, refresh token 정보를 담고 있는 cookie 모두 제거
        Cookie[] cookies = httpServletRequest.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refreshToken")) {
                refreshToken = cookie.getValue();
            }
            cookie.setMaxAge(0);
            httpServletResponse.addCookie(cookie);
        }

        //black_list 테이블에 만료된 refresh token 정보를 저장
        blackListRepository.save(BlackList.builder().invalidRefreshToken(refreshToken).build());
    }

    public void sendCodeToEmail(String toEmail) {
        log.debug("sendCodeToEmail Function...");
        this.checkDuplicatedEmail(toEmail);
        String title = "Marrymo 이메일 인증 번호";
        String authCode = this.createCode();
        String content =
                "안녕하세요, Marrymo 입니다. <br>"
                        + "<strong>" + toEmail + "</strong>님께 이메일 인증번호를 발송해드립니다.<br/><br/>"
                        + "<h3>" + authCode + "</h3>";
        log.debug("send mail start...");
        smtpService.sendEmail(toEmail, title, content);
        log.debug("send mail end...");

        //이메일 요청 시 인증 번호를 Redis에 저장
        //(key = Email / value = AuthCode)
        redisService.setValue(toEmail, authCode, Long.valueOf(authCodeExpirationMillis));
    }

    public Boolean verifiedAuthCode(SmtpVerifyRequest smtpVerifyRequest) {
        String redisAuthCode = redisService.getValue(smtpVerifyRequest.getEmail());

        if (smtpVerifyRequest.getCode().equals(redisAuthCode))
            return Boolean.valueOf(true);
        else
            return Boolean.valueOf(false);
    }

    private void checkDuplicatedEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        //탈퇴하지 않은 회원 중 해당 email이 존재하다면
        if (user.isPresent() && user.get().getDeletedAt() == null) {
            throw new UserException(UserErrorCode.USER_ALREADY_EXIST);
        }
    }

    private String createCode() {
        StringBuilder sb = new StringBuilder();

        for (int len = 0; len < 6; len++) {
            int num = (int) (Math.random() * 10);
            sb.append(num);
        }

        return sb.toString();
    }
}