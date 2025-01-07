package site.marrymo.restapi.user.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import site.marrymo.restapi.card.entity.Card;
import site.marrymo.restapi.card.exception.CardErrorCode;
import site.marrymo.restapi.card.exception.CardException;
import site.marrymo.restapi.card.repository.CardRepository;
import site.marrymo.restapi.global.exception.MarrymoException;
import site.marrymo.restapi.user.dto.Who;
import site.marrymo.restapi.user.dto.request.InvitationIssueRequest;
import site.marrymo.restapi.user.entity.User;
import site.marrymo.restapi.user.exception.UserErrorCode;
import site.marrymo.restapi.user.repository.UserRepository;
import site.marrymo.restapi.wedding_img.entity.WeddingImg;
import site.marrymo.restapi.wedding_img.repository.WeddingImgRepository;

import javax.swing.text.html.Option;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private WeddingImgRepository weddingImgRepository;

    //stubbing을 이용한 테스트 코드
    @Test
    @DisplayName("회원 청첩장 정보 저장")
    void registUserInfoTest() {
        // Given(준비)
        // 회원 고유 번호 (pk)
        Long userSequence = 1L;

        // marrymo가 회원에게 자체적으로 발급해준 유니크 코드
        String userCode = "abc123";

        String email = "pdy6519@naver.com";

        // 임의의 User 객체 생성
        User user = User.builder()
                .kakaoId("pdy6519@naver.com")
                .userCode(userCode)
                .email(email)
                .isRequired(true)
                .build();

        // 임의의 img 정보를 담은 String 배열 생성
        String imgUrl = "https://marrymo.site/wedding_img/abc123/1";

        // 임의의 Card 객체 생성
        Card card = Card.builder()
                .user(user)
                .groomName("김자바")
                .brideName("김씨샵")
                .groomContact("010-1234-5678")
                .brideContact("010-1234-7890")
                .weddingDate(LocalDate.parse("2024-07-31", DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .weddingTime(LocalTime.parse("12:00:00", DateTimeFormatter.ofPattern("HH:mm:ss")))
                .weddingDay("월")
                .invitationUrl("https://marrymo.site/" + user.getUserCode())
                .location("역삼동")
                .groomFather("김파이")
                .groomMother("김노드")
                .brideFather("김알고")
                .brideMother("김운영")
                .greeting("부부에게 행운이 깃들기를")
                .isIssued(Boolean.valueOf(false))
                .build();

        // userRepository에서 Long 타입의 1 값을 파라미터에 넣은 후
        // findByUserSequence(1)를 호출하면 위에 생성한 User 타입의 user 객체가 return 된다고 정의
        when(userRepository.findByUserSequence(userSequence)).thenReturn(Optional.ofNullable(user));

        // userRepository에서 user 파라미터를 넣어 save를 호출하면 User 타입의 user 객체가 return 된다고 정의
        when(userRepository.save(user)).thenReturn(user);

        // cardRespository에서 user 파라미터를 넣어 findByUser를 호출하면 Card 타입의 card 객체가 return 된다고 정의
        when(cardRepository.findByUser(user)).thenReturn(Optional.ofNullable(card));

        // userRepository에서 WeddingImg 객체에 대한 파라미터를 넣어 save를 호출하면 WeddingImg 객체인 weddingImg가 return 된다고 정의
        WeddingImg weddingImg = WeddingImg.builder()
                .card(card)
                .imgUrl(imgUrl)
                .build();

        when(weddingImgRepository.save(weddingImg)).thenReturn(weddingImg);

        //When (실행)
        User resultUser = userRepository.findByUserSequence(userSequence)
                .orElseThrow(() -> new MarrymoException(UserErrorCode.USER_NOT_FOUND));

        User saveUser = userRepository.save(resultUser);

        Card resultCard = cardRepository.findByUser(saveUser)
                .orElseThrow(() -> new CardException(CardErrorCode.CARD_NOT_FOUND));

        WeddingImg saveWeddingImg = weddingImgRepository.save(weddingImg);

        //Then (검증 및 결과 확인)
        assertEquals(resultUser, saveUser);
        assertEquals(card, resultCard);
        assertEquals(weddingImg, saveWeddingImg);
    }

    @Test
    @DisplayName("청첩장 정보 수정")
    void modifyUserInfoTest() {
        // Given(준비)
        // 회원 고유 번호 (pk)
        Long userSequence = 1L;

        // marrymo가 회원에게 자체적으로 발급해준 유니크 코드
        String userCode = "abc123";

        String email = "pdy6519@naver.com";

        // 임의의 User 객체 생성
        User user = User.builder()
                .kakaoId("pdy6519@naver.com")
                .userCode(userCode)
                .email(email)
                .isRequired(true)
                .build();

        when(userRepository.findByUserSequence(userSequence)).thenReturn(Optional.ofNullable(user));
        when(userRepository.save(user)).thenReturn(user);

        // 임의의 Card 객체 생성
        Card card = Card.builder()
                .user(user)
                .groomName("김자바")
                .brideName("김씨샵")
                .groomContact("010-1234-5678")
                .brideContact("010-1234-7890")
                .weddingDate(LocalDate.parse("2024-07-31", DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .weddingTime(LocalTime.parse("12:00:00", DateTimeFormatter.ofPattern("HH:mm:ss")))
                .weddingDay("월")
                .invitationUrl("https://marrymo.site/" + user.getUserCode())
                .location("역삼동")
                .groomFather("김파이")
                .groomMother("김노드")
                .brideFather("김알고")
                .brideMother("김운영")
                .greeting("부부에게 행운이 깃들기를")
                .isIssued(Boolean.valueOf(false))
                .build();

        when(cardRepository.findByUser(user)).thenReturn(Optional.ofNullable(card));
        when(cardRepository.save(card)).thenReturn(card);

        //wedding_img table에 저장되어있는 모든 imgUrl 데이터를 날리고
        //새로운 imgUrl 데이터를 저장한다.
        WeddingImg weddingImg = WeddingImg.builder()
                .card(card)
                .imgUrl("https://marrymo.site/wedding_img/abc123/1")
                .build();

        when(weddingImgRepository.save(weddingImg)).thenReturn(weddingImg);

        //When(실행)
        User resultUser = userRepository.findByUserSequence(userSequence)
                .orElseThrow(() -> new MarrymoException(UserErrorCode.USER_NOT_FOUND));
        String oldEmail = resultUser.getEmail();

        //회원 이메일 변경
        String newEmail = "ehduszkdzkd@naver.com";
        resultUser.modifyUserEmail(newEmail);
        User saveUser = userRepository.save(resultUser);

        Card resultCard = cardRepository.findByUser(saveUser)
                .orElseThrow(() -> new CardException(CardErrorCode.CARD_NOT_FOUND));

        String oldGroomFather = resultCard.getGroomFather();

        //신랑부 이름 변경
        String newGroomFather = "김부부";
        resultCard.modifyGroomFather(newGroomFather);
        Card saveCard = cardRepository.save(resultCard);

        WeddingImg saveWeddingImg = weddingImgRepository.save(weddingImg);

        //then (검증 및 결과 확인)
        //바뀌기 전 이메일인 "pdy6519@naver.com"과 일치해야 한다.
        assertEquals("pdy6519@naver.com", oldEmail);
        //바뀐 후 이메일인 "ehduszkdzkd@naver.com"과 일치해야 한다.
        assertEquals("ehduszkdzkd@naver.com", newEmail);

        //바뀌기 전 신랑부 이름인 "김파이"와 일치해야 한다.
        assertEquals("김파이", oldGroomFather);
        //바뀐 후 신랑부 이름인 "김부부"와 일치해야 한다.
        assertEquals("김부부", newGroomFather);

        assertEquals(weddingImg, saveWeddingImg);
    }

    @Test
    @DisplayName("회원 정보 조회")
    void getUserInfoTest() {
        //Given
        // 임의의 User 객체 생성
        User user = User.builder()
                .kakaoId("pdy6519@naver.com")
                .userCode("abc123")
                .email("pdy6519@naver.com")
                .isRequired(true)
                .build();

        when(userRepository.findByUserSequence(1L)).thenReturn(Optional.ofNullable(user));

        // 임의의 Card 객체 생성
        Card card = Card.builder()
                .user(user)
                .groomName("김자바")
                .brideName("김씨샵")
                .groomContact("010-1234-5678")
                .brideContact("010-1234-7890")
                .weddingDate(LocalDate.parse("2024-07-31", DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .weddingTime(LocalTime.parse("12:00:00", DateTimeFormatter.ofPattern("HH:mm:ss")))
                .weddingDay("월")
                .invitationUrl("https://marrymo.site/" + user.getUserCode())
                .location("역삼동")
                .groomFather("김파이")
                .groomMother("김노드")
                .brideFather("김알고")
                .brideMother("김운영")
                .greeting("부부에게 행운이 깃들기를")
                .isIssued(Boolean.valueOf(false))
                .build();

        when(cardRepository.findByUser(user)).thenReturn(Optional.ofNullable(card));

        // 임의의 WeddingImg 객체 생성
        WeddingImg weddingImg = WeddingImg.builder()
                .card(card)
                .imgUrl("https://marrymo.site/abc123/1")
                .build();

        List<WeddingImg> weddingImgList = new ArrayList<>();
        weddingImgList.add(weddingImg);

        when(weddingImgRepository.findByCard(card)).thenReturn(weddingImgList);

        //When
        User resultUser = userRepository.findByUserSequence(1L)
                .orElseThrow(() -> new MarrymoException(UserErrorCode.USER_NOT_FOUND));

        Card resultCard = cardRepository.findByUser(user)
                .orElseThrow(() -> new MarrymoException(CardErrorCode.CARD_NOT_FOUND));

        List<WeddingImg> resultWeddingImgList = weddingImgRepository.findByCard(card);

        //Then
        assertEquals(user, resultUser);
        assertEquals(card, resultCard);
        assertEquals(weddingImgList.get(0).getImgUrl(), resultWeddingImgList.get(0).getImgUrl());
    }

    @Test
    @DisplayName("회원 탈퇴")
    void deleteUserTest() {
        //Given
        // 임의의 User 객체 생성
        User user = User.builder()
                .kakaoId("pdy6519@naver.com")
                .userCode("abc123")
                .email("pdy6519@naver.com")
                .isRequired(true)
                .build();

        // 임의의 Card 객체 생성
        Card card = Card.builder()
                .user(user)
                .groomName("김자바")
                .brideName("김씨샵")
                .groomContact("010-1234-5678")
                .brideContact("010-1234-7890")
                .weddingDate(LocalDate.parse("2024-07-31", DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .weddingTime(LocalTime.parse("12:00:00", DateTimeFormatter.ofPattern("HH:mm:ss")))
                .weddingDay("월")
                .invitationUrl("https://marrymo.site/" + user.getUserCode())
                .location("역삼동")
                .groomFather("김파이")
                .groomMother("김노드")
                .brideFather("김알고")
                .brideMother("김운영")
                .greeting("부부에게 행운이 깃들기를")
                .isIssued(Boolean.valueOf(false))
                .build();

        when(userRepository.findByUserSequence(1L)).thenReturn(Optional.ofNullable(user));
        when(cardRepository.findByUser(user)).thenReturn(Optional.ofNullable(card));
        when(cardRepository.save(card)).thenReturn(card);

        doNothing().when(userRepository).delete(user);

        //When
        User resultUser = userRepository.findByUserSequence(1L)
                .orElseThrow(() -> new MarrymoException(UserErrorCode.USER_NOT_FOUND));

        Card resultCard = cardRepository.findByUser(user)
                .orElseThrow(() -> new MarrymoException(CardErrorCode.CARD_NOT_FOUND));
        String oldInvitationUrl = resultCard.getInvitationUrl();

        resultCard.modifyInvitationUrl(null);
        Card saveCard = cardRepository.save(resultCard);
        String newInvitationUrl = saveCard.getInvitationUrl();

        userRepository.delete(user);

        //Then
        assertEquals("abc123", resultUser.getUserCode());
        assertEquals(oldInvitationUrl, "https://marrymo.site/abc123");
        assertEquals(newInvitationUrl, null);

        verify(userRepository, times(1)).delete(user);
    }

    @Test
    @DisplayName("청첩장 발급 완료 여부 수정")
    void invitationIssuedTest() {

        // Given
        // 임의의 User 객체 생성(필수 필드 초기화)

        Boolean resultIsIssued = true;

        User user = User.builder()
                .kakaoId("kimjaeyun")
                .isAgreement(true)
                .isRequired(true)
                .build();

        // 임의의 Card 객체 생성
        Card card = Card.builder()
                .user(user)
                .groomName("김철수")
                .brideName("김영희")
                .groomContact("010-1234-5678")
                .brideContact("010-9876-5432")
                .weddingDate(LocalDate.of(2024, 3, 3))
                .weddingDay("수요일")
                .weddingTime(LocalTime.of(12, 0))
                .location("서울 강남구")
                .isIssued(false)
                .greeting("결혼합니다.")
                .build();

        InvitationIssueRequest invitationIssueRequest = InvitationIssueRequest.builder()
                .isIssued(true)
                .build();

        when(userRepository.findByUserSequence(1L)).thenReturn(Optional.ofNullable(user));
        when(cardRepository.findByUser(user)).thenReturn(Optional.ofNullable(card));
        when(cardRepository.save(card)).thenReturn(card);

        // When
        User resultUser = userRepository.findByUserSequence(1L)
                .orElseThrow(() -> new MarrymoException(UserErrorCode.USER_NOT_FOUND));

        Card resultCard = cardRepository.findByUser(user)
                .orElseThrow(() -> new MarrymoException(CardErrorCode.CARD_NOT_FOUND));

        resultCard.modifyIsIssued(invitationIssueRequest.getIsIssued());

        cardRepository.save(card);

        // Then
        assertEquals(resultIsIssued, card.getIsIssued());
        verify(userRepository, times(1)).findByUserSequence(1L);
        verify(cardRepository, times(1)).findByUser(user);
        verify(cardRepository, times(1)).save(card);
    }

    @Test
    @DisplayName("축의금 송금할 계좌주(신랑, 신부, 둘다) 등록")
    void registWhoTest() {
        // Given
        User user = User.builder()
                .kakaoId("kimjaeyun")
                .isAgreement(true)
                .isRequired(true)
                .who(null)
                .build();

        when(userRepository.findByUserSequence(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        Who[] resultWho = {Who.BRIDE, Who.GROOM, Who.BOTH};

        for (Who who : resultWho) {
            // When
            User resultUser = userRepository.findByUserSequence(1L)
                    .orElseThrow(() -> new MarrymoException(UserErrorCode.USER_NOT_FOUND));

            resultUser.modifyUserWho(who);

            userRepository.save(resultUser);

            // Then
            assertEquals(who, resultUser.getWho());
            verify(userRepository, atLeastOnce()).findByUserSequence(1L);
            verify(userRepository, atLeastOnce()).save(resultUser);
        }
    }

}