package controller;


import domain.Comic;
import domain.Member;
import domain.Rental;
import repository.ComicRepository;
import repository.MemberRepository;
import repository.RentalRepository;
import util.DateHolder;
import util.Rq;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

public class RentalController {
    private DateHolder dateHolder;

    private final RentalRepository rentalRepository;
    private final ComicRepository comicRepository;
    private final MemberRepository memberRepository;

    public RentalController() {
        this.rentalRepository = new RentalRepository();
        this.comicRepository = new ComicRepository();
        this.memberRepository = new MemberRepository();
    }

    public void setDateHolder(DateHolder dateHolder) {
        this.dateHolder = dateHolder;
    }

    /**
     * 도서 대여 기능. 유효한 대여인지와 연체 패널티 등 검사 포함
     * @param rq 문자열 파싱객체 rq
     */
    public void rentalRent(Rq rq) {
        List<String> params = rq.getParams();

        if (params.size() < 2) {
            System.out.println("=> 사용법: rental-rent [comic.id] [member.id]");
            return;
        }

        int comicId;
        int memberId;

        try {
            comicId = Integer.parseInt(params.get(0));
            memberId = Integer.parseInt(params.get(1));
        } catch (NumberFormatException e) {
            System.out.println("=> 만화책 번호와 회원 번호는 숫자로 입력해야 합니다.");
            return;
        }

        // 1. 만화책 존재 및 대여 가능 여부 확인
        Comic comic = comicRepository.findById(comicId);

        if (comic == null) {
            System.out.println("=> 해당 번호의 만화책이 존재하지 않습니다.");
            return;
        }

        // 도메인 메서드명에 따라 isRented() 혹은 isRental() 사용
        if (comic.isRented()) {
            System.out.println("=> 현재 대여 중인 만화책입니다.");
            return;
        }

        // 회원 존재 여부 + 패널티 확인
        Member member = memberRepository.findById(memberId);

        if (member == null) {
            System.out.println("=> 해당 번호의 회원이 존재하지 않습니다.");
            return;
        }

        if (member.getPenaltyDate() != null && member.getPenaltyDate().isAfter(dateHolder.getDate())) {
            System.out.println("=> 해당 회원은 연체 패널티로 인해 " + member.getPenaltyDate() + " 까지 대여가 불가능합니다.");
            return;
        }

        // 3. 대여 기록 생성 및 저장
        LocalDate dueDate = dateHolder.getDate().plusDays(7);
        Rental newRental = new Rental(comicId, memberId, dueDate);

        boolean isSaved = rentalRepository.saveRental(newRental);

        if (isSaved) {
            // 4. 만화책 상태를 '대여 중(true)'으로 업데이트
            comicRepository.updateRentalStatus(comicId, true);
            System.out.println("=> 대여가 완료되었습니다. 반납 예정일은 " + dueDate + " 입니다.");
        } else {
            System.out.println("=> 대여 처리에 실패했습니다.");
        }
    }

    /**
     * 대여한 책 반납하는 기능, 대여기록을 확인하는 로직과 연체 패널티 부여 로직 포함
     * @param rq 문자열 파싱객체 rq
     */
    public void rentalReturn(Rq rq) {
        List<String> params = rq.getParams();

        if (params.isEmpty()) {
            System.out.println("=> 사용법: rental-return [rental.id]");
            return;
        }

        int rentalId;
        try {
            rentalId = Integer.parseInt(params.get(0));
        } catch (NumberFormatException e) {
            System.out.println("=> 대여 번호는 숫자로 입력해야 합니다.");
            return;
        }

        // 1. 대여 기록 확인
        Optional<Rental> rentalOpt = rentalRepository.findOne(rentalId);
        if (rentalOpt.isEmpty()) {
            System.out.println("=> 해당 번호의 대여 기록이 존재하지 않습니다.");
            return;
        }

        Rental rental = rentalOpt.get();

        if (rental.getReturnDate() != null) {
            System.out.println("=> 이미 반납 처리된 기록입니다.");
            return;
        }

        // 2. 반납 처리 및 연체 계산
        LocalDate today = dateHolder.getDate();
        boolean isUpdated = rentalRepository.updateReturnDate(rentalId, today);

        if (isUpdated) {
            System.out.println("=> 반납 처리가 완료되었습니다.");

            LocalDate dueDate = rental.getDueDate();
            if (today.isAfter(dueDate)) {
                long overdueDays = ChronoUnit.DAYS.between(dueDate, today);
                LocalDate penaltyDate = today.plusDays(overdueDays);

                // 연체 패널티
                memberRepository.updatePenaltyDate(rental.getMemberId(), penaltyDate);
                System.out.println("=> [경고] " + overdueDays + "일 연체되어 " + penaltyDate + " 까지 대여가 정지됩니다.");
            }

            // 3. 만화책 상태를 '대여 가능(false)'으로 업데이트
            comicRepository.updateRentalStatus(rental.getComicId(), false);

        } else {
            System.out.println("=> 반납 처리에 실패했습니다.");
        }
    }

    /**
     * 전체 대여 기록 조회기능
     * @param rq 문자열 파싱 객체
     */
    public void rentalList(Rq rq) {
        List<Rental> rentals = rentalRepository.findAll();

        if (rentals.isEmpty()) {
            System.out.println("=> 대여 기록이 없습니다.");
            return;
        }

        System.out.println("대여번호 | 만화책번호 | 회원번호 | 대여일 | 반납예정일 | 실제반납일");
        System.out.println("-----------------------------------------------------------------");

        for (Rental rental : rentals) {
            String returnStatus = (rental.getReturnDate() == null) ? "미반납" : rental.getReturnDate().toString();

            System.out.printf("%d | %d | %d | %s | %s | %s%n",
                    rental.getId(),
                    rental.getComicId(),
                    rental.getMemberId(),
                    rental.getRentDate(),
                    rental.getDueDate(),
                    returnStatus
            );
        }
        System.out.println("-----------------------------------------------------------------");
    }
}
