package repository;

import domain.Rental;
import util.DBUtil;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// javadoc을 사용해 메소드 설명을 담았습니다.

public class RentalRepository {
    /**
     * 데이터베이스 rental 테이블을 대여일 기준으로 내림차순(최신일자 먼저) 조회합니다.
     * @return 전체 대여 기록이 담긴 리스트(대여 기록이 없다면 빈 리스트 반환)
     */
    public List<Rental> findAll() {
        // Rental 객체를 여러 개 담는 ArrayList rentalList를 만듦
        List<Rental> rentalList = new ArrayList<>();
        // rental 테이블 내림차순 정렬 쿼리문
        String query = "SELECT * FROM rental ORDER BY rent_date DESC";

        // db연결
        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query);
                ResultSet rs = pstmt.executeQuery()
        ) {
            while (rs.next()) {
                rentalList.add(mapResultSetToRental(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rentalList;
    }

    /**
     * 특정 대여기록 한 건을 조회합니다.
     * @param id 조회할 대여 기록 아이디(PK)
     * @return 조회된 대여 기록 정보를 담은 Optional 객체
     */
    public Optional<Rental> findOne(int id) {
        // id가 ?인 값만 조회하는 쿼리
        String query = "SELECT * FROM rental WHERE id = ?";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)
        ) {
            // 쿼리에서 ? 였던 부분(1번째 '?')에 id를 전달
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToRental(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    /**
     * db에 대여 정보를 저장합니다.
     * @param rental 책 대여 정보를 담고있는 객체
     * @return 실행결과 성공여부 boolean 값
     */
    public boolean saveRental(Rental rental) {
        String query = "INSERT INTO rental (comic_id, member_id, rent_date, due_date, return_date) VALUES (?, ?, ?, ?, ?)";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)
        ) {
            pstmt.setInt(1, rental.getComicId());
            pstmt.setInt(2, rental.getMemberId());
            pstmt.setDate(3, Date.valueOf(rental.getRentDate())); // db DATE 타입으로 변환해서 입력
            pstmt.setDate(4, Date.valueOf(rental.getDueDate()));

            // 대여 시점의 return_date null여부 처리
            if (rental.getReturnDate() != null) {
                pstmt.setDate(5, Date.valueOf(rental.getReturnDate()));
            } else {
                pstmt.setNull(5, java.sql.Types.DATE);
            }

            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 책 반납 함수;
     * db에서 오늘 날짜로 반납일을 업데이트합니다.
     * @param id 반납할 대여 내역 id
     * @param returnDate 반납일
     * @return 실행결과 성공여부 boolean
     */
    public boolean updateReturnDate(int id, LocalDate returnDate) {
        String query = "UPDATE rental SET return_date = ? WHERE id = ?";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)
        ) {
            pstmt.setDate(1, Date.valueOf(returnDate));
            pstmt.setInt(2, id);

            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // ResultSet을 Rental 객체로 변환하는 중복 로직을 분리한 헬퍼 메서드
    private Rental mapResultSetToRental(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int comicId = rs.getInt("comic_id");
        int memberId = rs.getInt("member_id");
        LocalDate rentDate = rs.getDate("rent_date").toLocalDate();
        LocalDate dueDate = rs.getDate("due_date").toLocalDate();

        // NULL 방지 처리
        java.sql.Date sqlReturnDate = rs.getDate("return_date");
        LocalDate returnDate = (sqlReturnDate != null) ? sqlReturnDate.toLocalDate() : null;

        // Rental 도메인의 생성자 파라미터 순서에 맞게 조정 필요
        return new Rental(id, comicId, memberId, rentDate, dueDate, returnDate);
    }
}