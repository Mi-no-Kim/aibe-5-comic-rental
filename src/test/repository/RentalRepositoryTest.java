package test.repository;

import domain.Rental;
import repository.RentalRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*; // 검증을 위한 도구들

class RentalRepositoryTest {

    // 테스트 객체
    RentalRepository rentalRepository = new RentalRepository();

    @Test
    @DisplayName("findAll(전체 rental기록 조회) 테스트")
    void findAllTest() {
        // 실행 (When)
        List<Rental> rentals = rentalRepository.findAll();

        // 검증 (Then)
        assertNotNull(rentals, "리스트 객체가 null임");

        System.out.println("현재 DB에 있는 대여 기록 수: " + rentals.size());
        for (Rental rental : rentals) {
            System.out.println(rental.toString());
        }
    }

    @Test
    @DisplayName("존재하지 않는 ID 조회 시 빈 Optional 반환 테스트")
    void findOneEmptyTest() {
        // 실행 (When) - db에 없는 9999번 조회하기
        Optional<Rental> result = rentalRepository.findOne(9999);

        // 검증 (Then)
        assertTrue(result.isEmpty(), "데이터가 없음 = Optional 비어야 함");
        System.out.println("id 9999번 데이터: " + result.isPresent()); // false 출력 예상
    }

    @Test
    @DisplayName("새로운 대여 기록 저장 테스트")
    void saveTest() {
        // Given (준비)
        // DB에서 comic 테이블의 id:1 member 테이블의 id:1로 대여
        // 오늘 대여해서 7일 뒤에 반납하는 상황을 가정
        LocalDate dueDate = LocalDate.now().plusDays(7);
        Rental newRental = new Rental(1, 1, dueDate);

        // When (실행)
        boolean isSaved = rentalRepository.saveRental(newRental);

        // Then (검증)
        assertTrue(isSaved, "데이터베이스에 정상적으로 INSERT 되면 true를 반환해야 합니다.");
    }

    @Test
    @DisplayName("대여한 책 반납(반납일 업데이트) 테스트")
    void updateReturnDateTest() {
        // Given (준비)
        // 앞선 saveTest()로 DB에 대여 번호(id) 1번 기록이 생성되었다고 가정
        int rentalId = 1;
        LocalDate today = LocalDate.now();

        // When (실행)
        // 1번 대여 기록의 반납일을 오늘 날짜로 업데이트
        boolean isUpdated = rentalRepository.updateReturnDate(rentalId, today);

        // Then (검증 1: 쿼리 실행 성공 여부)
        assertTrue(isUpdated, "정상적으로 업데이트 쿼리가 실행되면 true를 반환해야 합니다.");

        // Then (검증 2: 실제 DB에 값이 잘 반영되었는지 확인)
        // 이전에 만들어둔 findOne() 메서드를 재사용하여 검증합니다.
        Optional<Rental> returnedRental = rentalRepository.findOne(rentalId);

        // 일단 데이터가 존재하는지 상자부터 확인합니다.
        assertTrue(returnedRental.isPresent(), "업데이트한 데이터가 DB에 존재해야 합니다.");

        // 상자에서 꺼낸 객체의 반납일이 우리가 방금 넣은 '오늘 날짜'와 일치하는지 비교합니다.
        assertEquals(today, returnedRental.get().getReturnDate(), "DB에 저장된 반납일이 오늘 날짜와 일치해야 합니다.");
    }

}