package test.repository;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import domain.Member;
import repository.MemberRepository;

public class MemberRepositoryTest {

    // 테스트 대상이 되는 Repository 객체 생성
    MemberRepository repository = new MemberRepository();

    @Test
    void findByIdTest() {
        Member foundMember = repository.findById(1);
        assertNotNull(foundMember);
        assertEquals(1, foundMember.getId(), "ID가 1번이 아닙니다!");
        
        assertNotNull(foundMember);
        assertEquals("이건희", foundMember.getName()); // 실제 이름과 비교
        assertEquals("010-2222-1111", foundMember.getPhone());
    }
    
    @Test
    void findByIdTest_Member2() {
        // 2번 회원 정보로 테스트
        Member foundMember = repository.findById(2);
        
        assertNotNull(foundMember);
        assertEquals("이상이", foundMember.getName());
        assertEquals("010-3333-4444", foundMember.getPhone());
    }
}
