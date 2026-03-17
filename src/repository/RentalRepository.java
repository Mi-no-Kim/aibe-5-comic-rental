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

public class RentalRepository {

    // 1. 대여 기록 전체 조회
    public List<Rental> findAll() {
        List<Rental> rentalList = new ArrayList<>();
        String query = "SELECT * FROM rental ORDER BY rent_date DESC";

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

    // 2. 특정 대여 기록 단건 조회
    public Optional<Rental> findOne(int id) {
        String query = "SELECT * FROM rental WHERE id = ?";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)
        ) {
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

    // 3. 새로운 대여 기록 저장 (대여하기)
    public boolean save(Rental rental) {
        String query = "INSERT INTO rental (comic_id, member_id, rent_date, due_date, return_date) VALUES (?, ?, ?, ?, ?)";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)
        ) {
            pstmt.setInt(1, rental.getComicId());
            pstmt.setInt(2, rental.getMemberId());
            pstmt.setDate(3, Date.valueOf(rental.getRentDate()));
            pstmt.setDate(4, Date.valueOf(rental.getDueDate()));

            // return_date는 대여 시점에는 null일 수 있으므로 분기 처리
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

    // 4. 반납 일자 업데이트 (반납하기)
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

        // return_date가 NULL일 수 있으므로 NPE 방지를 위한 처리
        java.sql.Date sqlReturnDate = rs.getDate("return_date");
        LocalDate returnDate = (sqlReturnDate != null) ? sqlReturnDate.toLocalDate() : null;

        // Rental 도메인의 생성자 파라미터 순서에 맞게 조정 필요
        return new Rental(id, comicId, memberId, rentDate, dueDate, returnDate);
    }
}