package site.marrymo.restapi.card.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import site.marrymo.restapi.card.entity.Card;
import site.marrymo.restapi.card.exception.CardErrorCode;
import site.marrymo.restapi.card.exception.CardException;
import site.marrymo.restapi.card.repository.CardRepository;
import site.marrymo.restapi.user.entity.User;
import site.marrymo.restapi.user.exception.UserErrorCode;
import site.marrymo.restapi.user.exception.UserException;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {
    @InjectMocks
    private CardService cardService;

    @Mock
    private CardRepository cardRepository;

    @Test
    @DisplayName("스케쥴러가 03:00에 makeExcel() api 호출할 때 호출 날짜와 결혼식 날짜가 일치하고 청첩장 발행 여부가 true라면 findUserSequenceByWeddingDateAndIsIssued() 호출")
    void findUserSequenceByWeddingDateAndIsIssued() {
        //given
        User user = User.builder()
                .kakaoId("ehduszkdzkd@naver.com")
                .isAgreement(true)
                .isRequired(true)
                .build();

        List<User> userList = new ArrayList<>();
        userList.add(user);

        Card card = Card.builder()
                .user(user)
                .groomName("doyeon")
                .brideName("doyeon")
                .groomContact("010-1111-1111")
                .brideContact("010-2222-2222")
                .weddingDate(LocalDate.now(ZoneId.of("Asia/Seoul")))
                .weddingDay("목")
                .location("Seoul")
                .isIssued(true)
                .build();

        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        when(cardRepository.findUserByWeddingDateAndIsIssued(today))
                .thenReturn(Optional.of(userList));        //when

        when(cardRepository.findByUser(user)).thenReturn(Optional.of(card));

        //when
        List<User> resultUserList = cardRepository.findUserByWeddingDateAndIsIssued(LocalDate.now(ZoneId.of("Asia/Seoul")))
                .orElseThrow(() -> new CardException(CardErrorCode.CARD_NOT_FOUND));

        LocalDate weddingDate = null;
        for (User getUser : resultUserList) {
            Card getCard = cardRepository.findByUser(getUser)
                    .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
            weddingDate = getCard.getWeddingDate();
        }

        //then
        //엑셀을 발행한 날짜와 결혼식 날짜는 같아야 한다.
        assertEquals(today, weddingDate);
    }
}