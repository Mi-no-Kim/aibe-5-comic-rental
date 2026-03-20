package controller;

import domain.Member;
import domain.Rental;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import repository.MemberRepository;
import repository.RentalRepository;
import util.DateHolder;
import util.Rq;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RentalControllerTest {

    private RentalController rentalController;
    private MemberRepository memberRepository;
    private RentalRepository rentalRepository;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    private DateHolder dateHolder;

    @BeforeEach
    void setUp() {
        rentalController = new RentalController();
        memberRepository = new MemberRepository();
        rentalRepository = new RentalRepository();

        System.setOut(new PrintStream(outContent));

        // 1. 진짜 DateHolder 객체를 생성하고 컨트롤러에 주입합니다.
        dateHolder = new DateHolder();
        rentalController.setDateHolder(dateHolder);
    }

    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    @DisplayName("회원 패널티 상태일 때 대여 거절 테스트")
    void rentFailDueToPenaltyTest() {
        // [수정] penaltyDate 변수 선언 및 초기화
        LocalDate today = dateHolder.getDate();
        LocalDate penaltyDate = today.plusDays(5);

        // Given: 1번 회원에게 오늘 기준으로 5일 뒤까지 패널티를 강제 부여 (DB에 1번 회원이 있어야 함)
        memberRepository.updatePenaltyDate(1, penaltyDate);

        Rq mockRq = new Rq("rental-rent") {
            @Override
            public List<String> getParams() {
                return Arrays.asList("1", "1"); // 1번 만화책을 1번(패널티 상태) 회원이 대여 시도
            }
        };

        // When
        rentalController.rentalRent(mockRq);

        // Then
        String output = outContent.toString();

        System.setOut(originalOut);
        System.out.println("패널티 대여 테스트 출력: " + output);

        assertTrue(output.contains("대여가 불가능합니다."),
                "패널티 기간이 남은 회원은 대여가 거절되어야 합니다.");

        // 테스트가 끝난 후 다음 테스트를 위해 패널티 초기화 (null 처리)
        memberRepository.updatePenaltyDate(1, null);
    }

    @Test
    @DisplayName("연체 반납 시 패널티 부여 테스트 (setDate 활용)")
    void overdueReturnPenaltyTest() {
        // Given: 오늘 대여해서 7일 뒤 반납 예정인 상황
        LocalDate today = dateHolder.getDate();
        LocalDate dueDate = today.plusDays(7);

        Rental mockRental = new Rental(2, 2, dueDate);
        rentalRepository.saveRental(mockRental);
        int targetRentalId = 1;

        dateHolder.setDate(today.plusDays(10));

        Rq mockRq = new Rq("rental-return") {
            @Override
            public List<String> getParams() {
                return Arrays.asList(String.valueOf(targetRentalId));
            }
        };

        // When
        rentalController.rentalReturn(mockRq);

        // Then
        String output = outContent.toString();

        System.setOut(originalOut);
        System.out.println("연체 반납 테스트 출력: \n" + output);

        // 1. 콘솔에 경고 메시지가 잘 떴는지 확인
        assertTrue(output.contains("[경고]"), "연체 반납 시 경고 메시지가 출력되어야 합니다.");
        assertTrue(output.contains("3일 연체되어"), "정확한 연체일(3일)이 계산되어야 합니다.");

        // 2. 실제 회원 DB에 패널티 날짜가 잘 들어갔는지 교차 검증
        Member member = memberRepository.findById(2);

        // [수정] expectedPenaltyDate 계산 (반납한 시점인 10일 뒤 + 연체일수 3일 = 총 13일 뒤)
        LocalDate expectedPenaltyDate = today.plusDays(13);

        assertTrue(member.getPenaltyDate() != null && member.getPenaltyDate().equals(expectedPenaltyDate),
                "회원 DB에 예상된 패널티 종료일이 정확하게 업데이트 되어야 합니다.");
    }
}