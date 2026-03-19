package contoller;

import domain.Comic;
import domain.Member;
import domain.Rental;
import repository.ComicRepository;
import repository.MemberRepository;
import repository.RentalRepository;
import util.Rq;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

public class RentalController {

    private final RentalRepository rentalRepository;
    private final ComicRepository comicRepository;
    private final MemberRepository memberRepository;

    public RentalController() {
        this.rentalRepository = new RentalRepository();
        this.comicRepository = new ComicRepository();
        this.memberRepository = new MemberRepository();
    }

    /*
     * rental-rent [comic.id] [member.id]
     * 예) rental-rent 1 2 (1번 만화책을 2번 회원이 대여, 대여 기간은 기본 7일로 설정)
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

        // 1. 만화책 존재 및 대여 가능 여부 확인 (앞서 공유받은 Comic 도메인 메서드 기준)
        // 주의: ComicController에서는 findById를 썼고, Repository 예시에서는 findOne을 썼으므로 프로젝트 상황에 맞게 메서드명을 맞춰야 해.
        Optional<Comic> comicOpt = comicRepository.findOne(comicId);
        if (comicOpt.isEmpty()) {
            System.out.println("=> 해당 번호의 만화책이 존재하지 않습니다.");
            return;
        }
        Comic comic = comicOpt.get();
        if (comic.isRental()) {
            System.out.println("=> 현재 대여 중인 만화책입니다.");
            return;
        }

        // 2. 회원 존재 및 패널티 여부 확인
        Optional<Member> memberOpt = memberRepository.findOne(memberId);
        if (memberOpt.isEmpty()) {
            System.out.println("=> 해당 번호의 회원이 존재하지 않습니다.");
            return;
        }
        Member member = memberOpt.get();

        // 회원의 패널티 날짜가 오늘보다 뒤에 있다면 대여 불가 처리
        if (member.getPenaltyDate() != null && member.getPenaltyDate().isAfter(LocalDate.now())) {
            System.out.println("=> 해당 회원은 연체 패널티로 인해 " + member.getPenaltyDate() + " 까지 대여가 불가능합니다.");
            return;
        }

        // 3. 대여 기록 생성 및 DB 저장 (기본 대여일 7일)
        LocalDate dueDate = LocalDate.now().plusDays(7);
        Rental newRental = new Rental(comicId, memberId, dueDate);

        boolean isSaved = rentalRepository.save(newRental);

        if (isSaved) {
            // 4. 만화책 상태를 '대여 중'으로 업데이트
            comic.setRental(true);
            comicRepository.updateComic(comic);

            System.out.println("=> 대여가 완료되었습니다. 반납 예정일은 " + dueDate + " 입니다.");
        } else {
            System.out.println("=> 대여 처리에 실패했습니다.");
        }
    }

    /*
     * rental-return [rental.id]
     * 예) rental-return 1 (1번 대여 기록 반납)
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
        LocalDate today = LocalDate.now();
        boolean isUpdated = rentalRepository.updateReturnDate(rentalId, today);

        if (isUpdated) {
            System.out.println("=> 반납 처리가 완료되었습니다.");

            // 연체 패널티 계산 로직 (앞서 설명했던 내용 적용)
            LocalDate dueDate = rental.getDueDate();
            if (today.isAfter(dueDate)) {
                long overdueDays = ChronoUnit.DAYS.between(dueDate, today);
                LocalDate penaltyDate = today.plusDays(overdueDays);

                memberRepository.updatePenaltyDate(rental.getMemberId(), penaltyDate);
                System.out.println("=> [경고] " + overdueDays + "일 연체되어 " + penaltyDate + " 까지 대여가 정지됩니다.");
            }

            // 3. 만화책 상태를 '대여 가능'으로 원복
            Optional<Comic> comicOpt = comicRepository.findOne(rental.getComicId());
            if (comicOpt.isPresent()) {
                Comic comic = comicOpt.get();
                comic.setRental(false);
                comicRepository.updateComic(comic);
            }

        } else {
            System.out.println("=> 반납 처리에 실패했습니다.");
        }
    }

    /*
     * rental-list
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