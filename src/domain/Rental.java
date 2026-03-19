package domain;

import java.time.LocalDate;

public class Rental {
    private int id;
    private int comicId;
    private int memberId;
    private LocalDate rentDate;
    private LocalDate dueDate;
    private LocalDate returnDate; // DB에서 NULL 허용

    // 1. DB에서 기존 대여 기록을 조회할 때 사용하는 생성자
    public Rental(int id, int comicId, int memberId, LocalDate rentDate, LocalDate dueDate, LocalDate returnDate) {
        this.id = id;
        this.comicId = comicId;
        this.memberId = memberId;
        this.rentDate = rentDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
    }

    // 2. 새로운 대여 기록을 등록할 때 사용하는 생성자 (id는 DB에서 자동 생성, returnDate는 아직 없음)
    public Rental(int comicId, int memberId, LocalDate dueDate) {
        this.comicId = comicId;
        this.memberId = memberId;
        this.rentDate = LocalDate.now(); // 대여일은 현재 날짜로 자동 설정
        this.dueDate = dueDate;
        this.returnDate = null;          // 반납 전이므로 null 처리
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getComicId() {
        return comicId;
    }

    public void setComicId(int comicId) {
        this.comicId = comicId;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public LocalDate getRentDate() {
        return rentDate;
    }

    public void setRentDate(LocalDate rentDate) {
        this.rentDate = rentDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    @Override
    public String toString() {
        // 반납일이 null일 경우 출력 시 "미반납" 등으로 예쁘게 처리해주면 CLI 화면에서 보기 좋습니다.
        String returnStatus = (returnDate == null) ? "미반납" : returnDate.toString();

        return "대여번호: " + id +
                " | 만화책번호: " + comicId +
                " | 회원번호: " + memberId +
                " | 대여일: " + rentDate +
                " | 반납예정일: " + dueDate +
                " | 실제반납일: " + returnStatus;
    }
}