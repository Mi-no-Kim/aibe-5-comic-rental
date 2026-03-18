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
}
