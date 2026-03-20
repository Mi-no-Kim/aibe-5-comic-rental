package repository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import domain.Member;
import util.DBUtil;

//DB담당
public class MemberRepository {
//	TODO: 회원 DB 저장
	public int insertMember(Member member) {
		String sql = "INSERT INTO member (name, phone, reg_date) VALUES(?,?,CURDATE())";
		
		try (
		        Connection conn = DBUtil.getConnection();
		        PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
		    ) {

		        pstmt.setString(1, member.getName());
		        pstmt.setString(2, member.getPhone());

		        pstmt.executeUpdate();

		        try (ResultSet rs = pstmt.getGeneratedKeys()) {
		            if (rs.next()) {
		                return rs.getInt(1);
		            }
		        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return -1;
	}
	
// TODO: 이미 등록 된 회원 전화번호 중복 체크
	public boolean existsByPhone(String phone) {

	    String sql = "SELECT COUNT(*) FROM member WHERE phone = ?";

	    try (Connection conn = DBUtil.getConnection();
		        PreparedStatement pstmt = conn.prepareStatement(sql);
	    	){
	        pstmt.setString(1, phone);

	        try( ResultSet rs = pstmt.executeQuery()){;

	        if (rs.next()) {
	            return rs.getInt(1) > 0; // 1개라도 있으면 true
	        }
	    }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return false;
	}
//
//	TODO: 전체 회원 조회)
	public List<Member> findAll(){
		 List<Member> list = new ArrayList<>();

		    String sql = "SELECT * FROM member WHERE is_deleted = FALSE";

		    try(
		    	Connection conn = DBUtil.getConnection();
			    PreparedStatement pstmt = conn.prepareStatement(sql);		
		    ) {
		        
		       try( ResultSet rs = pstmt.executeQuery()){;

		        while (rs.next()) {
		            Member m = new Member();

		            m.setId(rs.getInt("id"));
		            m.setName(rs.getString("name"));
		            m.setPhone(rs.getString("phone"));
		         // DB에서 penalty_date 값을 가져와 Member 객체에 세팅 
		            java.sql.Date date = rs.getDate("penalty_date");
		            if (date != null) {
		                m.setPenaltyDate(date.toLocalDate());
		            }

		            list.add(m);
		        }
		       }

		    } catch (Exception e) {
		        e.printStackTrace();
		    }

		    return list;
	}

//	TODO: 이름 or 전화번호 검색
	public List<Member> searchMember(String keyword) {

	    List<Member> list = new ArrayList<>();

	    String sql = "SELECT * FROM member WHERE is_deleted = FALSE AND (name LIKE ? OR phone LIKE ?)";

	    try(
	        Connection conn = DBUtil.getConnection();
		    PreparedStatement pstmt = conn.prepareStatement(sql);
	    		) {
	       

	        String searchKeyword = "%" + keyword + "%";

	        pstmt.setString(1, searchKeyword);
	        pstmt.setString(2, searchKeyword);

	       try( ResultSet rs = pstmt.executeQuery()){;

	        while (rs.next()) {
	            Member m = new Member();
	            m.setId(rs.getInt("id"));
	            m.setName(rs.getString("name"));
	            m.setPhone(rs.getString("phone"));

	            list.add(m);
	        }
	       }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return list;
	}

//	TODO: ID로 회원 조회
	public Member findById(int memberId) {

	    String sql = "SELECT * FROM member WHERE id = ? AND is_deleted = FALSE";

	    try(
	    	Connection conn = DBUtil.getConnection();
		    PreparedStatement pstmt = conn.prepareStatement(sql);
	    	) {
	       
	        pstmt.setInt(1, memberId);

	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) {
	                Member m = new Member();
	                m.setId(rs.getInt("id"));
	                m.setName(rs.getString("name"));
	                m.setPhone(rs.getString("phone"));
	                
	                java.sql.Date date = rs.getDate("penalty_date");
	                if (date != null) {
	                    m.setPenaltyDate(date.toLocalDate());
	                }
	                return m; // 여기서 return해도 conn, pstmt, rs는 모두 자동으로 닫힘!
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return null;
	}

//	TODO: 회원 정보 수정
	public int updateMember(int memberId, String name, String phone) {

	    String sql = "UPDATE member SET name = ?, phone = ? WHERE id = ?";

	    try(
	    	 Connection conn = DBUtil.getConnection();
		     PreparedStatement pstmt = conn.prepareStatement(sql);
	       ) {

	        pstmt.setString(1, name);
	        pstmt.setString(2, phone);
	        pstmt.setInt(3, memberId);

	        return pstmt.executeUpdate(); // 영향 받은 행 수

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return 0;
	}

//	TODO: 회원 삭제
 	public int deleteMember(int memberId) {
 		String sql="UPDATE member SET is_deleted = TRUE WHERE id = ?";
 		
 		try(
 				Connection conn = DBUtil.getConnection();
 	 			PreparedStatement pstmt = conn.prepareStatement(sql);	
 		   ) {
 			
 			
 			pstmt.setInt(1, memberId);
 			
 			return pstmt.executeUpdate();
 			
 		} catch (Exception e) {
 	        e.printStackTrace();
 	    }

 	    return 0;
 		}

//  TODO: penaltyDate
 	public void updatePenaltyDate(int memberId, LocalDate penaltyDate) {

 	    String sql = "UPDATE member SET penalty_date = ? WHERE id = ?";

 	    try(
 	    	 Connection conn = DBUtil.getConnection();
 	 	      PreparedStatement pstmt = conn.prepareStatement(sql);
 	      ) {
 	       

 	        pstmt.setDate(1, java.sql.Date.valueOf(penaltyDate));
 	        pstmt.setInt(2, memberId);

 	        pstmt.executeUpdate();

 	    } catch (Exception e) {
 	        e.printStackTrace();
 	    }
 	}
 	

}
