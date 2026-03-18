package repository;

import domain.Comic;
import util.DBUtil;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ComicRepository {

    /*
     * 만화책 등록
     * 성공하면 생성된 id 반환
     * 실패하면 -1 반환
     */
    public int addComic(Comic comic) {
        String sql = """
                INSERT INTO comic (title, author, volume, is_rental, reg_date)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            pstmt.setString(1, comic.getTitle());
            pstmt.setString(2, comic.getAuthor());
            pstmt.setInt(3, comic.getVolume());
            pstmt.setBoolean(4, comic.isRented());
            pstmt.setDate(5, Date.valueOf(comic.getRegDate()));

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                return -1;
            }

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                System.out.println("=> 이미 등록된 만화책입니다.");
            } else {
                System.out.println("=> 만화책 등록 중 오류가 발생했습니다.");
                e.printStackTrace();
            }
        }

        return -1;
    }
    /*
     * 전체 목록 조회
     * asc / desc 정렬 지원
     */
    public List<Comic> findAll(String order) {
        List<Comic> comics = new ArrayList<>();

        String sortOrder = "DESC";
        if ("asc".equalsIgnoreCase(order)) {
            sortOrder = "ASC";
        }

        String sql = "SELECT * FROM comic ORDER BY reg_date " + sortOrder + ", id " + sortOrder;

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()
        ) {
            while (rs.next()) {
                Comic comic = new Comic();
                comic.setId(rs.getInt("id"));
                comic.setTitle(rs.getString("title"));
                comic.setAuthor(rs.getString("author"));
                comic.setVolume(rs.getInt("volume"));
                comic.setRented(rs.getBoolean("is_rental"));
                comic.setRegDate(rs.getDate("reg_date").toLocalDate());

                comics.add(comic);
            }
        } catch (SQLException e) {
            System.out.println("=> 만화책 목록 조회 중 오류가 발생했습니다.");
            e.printStackTrace();
        }

        return comics;
    }
    /*
     * id로 만화책 1권 조회
     * 없으면 null 반환
     */
    public Comic findById(int id) {
        String sql = "SELECT * FROM comic WHERE id = ?";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Comic comic = new Comic();
                    comic.setId(rs.getInt("id"));
                    comic.setTitle(rs.getString("title"));
                    comic.setAuthor(rs.getString("author"));
                    comic.setVolume(rs.getInt("volume"));
                    comic.setRented(rs.getBoolean("is_rental"));
                    comic.setRegDate(rs.getDate("reg_date").toLocalDate());
                    return comic;
                }
            }
        } catch (SQLException e) {
            System.out.println("=> 만화책 상세 조회 중 오류가 발생했습니다.");
            e.printStackTrace();
        }

        return null;
    }

    /*
     * 제목 또는 작가 기준 검색
     */
    public List<Comic> searchByKeyword(String keyword) {
        List<Comic> comics = new ArrayList<>();

        String sql = """
                SELECT * FROM comic
                WHERE title LIKE ? OR author LIKE ?
                ORDER BY reg_date DESC, id DESC
                """;

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            String likeKeyword = "%" + keyword + "%";
            pstmt.setString(1, likeKeyword);
            pstmt.setString(2, likeKeyword);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Comic comic = new Comic();
                    comic.setId(rs.getInt("id"));
                    comic.setTitle(rs.getString("title"));
                    comic.setAuthor(rs.getString("author"));
                    comic.setVolume(rs.getInt("volume"));
                    comic.setRented(rs.getBoolean("is_rental"));
                    comic.setRegDate(rs.getDate("reg_date").toLocalDate());

                    comics.add(comic);
                }
            }
        } catch (SQLException e) {
            System.out.println("=> 만화책 검색 중 오류가 발생했습니다.");
            e.printStackTrace();
        }

        return comics;
    }
    /*
     * 만화책 수정
     * 제목, 작가, 권수 수정
     */
    public boolean updateComic(Comic comic) {
        String sql = """
                UPDATE comic
                SET title = ?, author = ?, volume = ?
                WHERE id = ?
                """;

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setString(1, comic.getTitle());
            pstmt.setString(2, comic.getAuthor());
            pstmt.setInt(3, comic.getVolume());
            pstmt.setInt(4, comic.getId());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                System.out.println("=> 같은 제목, 작가, 권수의 만화책이 이미 존재합니다.");
            } else {
                System.out.println("=> 만화책 수정 중 오류가 발생했습니다.");
                e.printStackTrace();
            }
        }

        return false;
    }

    /*
     * id로 만화책 삭제
     */
    public boolean deleteById(int id) {
        String sql = "DELETE FROM comic WHERE id = ?";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("=> 만화책 삭제 중 오류가 발생했습니다.");
            e.printStackTrace();
        }

        return false;
    }

    /*
     * 대여 상태 변경
     * Rental 파트에서 사용할 수 있게 미리 작성
     */
    public boolean updateRentalStatus(int comicId, boolean isRental) {
        String sql = "UPDATE comic SET is_rental = ? WHERE id = ?";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setBoolean(1, isRental);
            pstmt.setInt(2, comicId);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("=> 대여 상태 변경 중 오류가 발생했습니다.");
            e.printStackTrace();
        }

        return false;
    }
}
