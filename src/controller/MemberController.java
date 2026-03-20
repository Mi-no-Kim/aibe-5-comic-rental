package controller;
import java.util.List;
import java.util.Scanner;

import domain.Member;
import repository.MemberRepository;
import util.Rq;
//콘솔 담당
public class MemberController {
	Scanner sc = new Scanner(System.in);
//	TODO: 회원 등록 (member-add)
	public void addMember(Rq rq) {
		System.out.print("이름: ");
		
		String name = sc.nextLine();
		
		System.out.print("전화번호: ");
		String phone = sc.nextLine();
		
		
		MemberRepository memberRepository = new MemberRepository();
		// (중복 체크)
	    if (memberRepository.existsByPhone(phone)) {
	        System.out.println("=> 이미 등록된 회원입니다.");
	        return;
	    }

	    // 중복 아닐 때만 실행됨
	    Member member = new Member();
	    member.setName(name);
	    member.setPhone(phone);

	    int id = memberRepository.insertMember(member);

	    System.out.println("=> 회원이 등록되었습니다. (id=" + id + ")");
		}
		
	
//	TODO: 회원 목록 조회 (member-list)
   public void showMembers(Rq rq) {
	   MemberRepository memberRepository = new MemberRepository();
	    List<Member> list = memberRepository.findAll();
	    System.out.println("\n회원 목록");
	    System.out.println("-------------------------------------");

	    if (list.isEmpty()) {
	        System.out.println("등록된 회원이 없습니다.");
	    } else {
	        System.out.println("회원id | 이름 | 전화번호");
	        System.out.println("-------------------------------------");

	        for (Member m : list) {
	            System.out.println(m.getId() + " | " + m.getName() + " | " + m.getPhone());
	        }
	    }

	    System.out.println("-------------------------------------");
   }
   
   
//	TODO: 회원 검색 (member-search)
   public void searchMember(Rq rq) {

	    MemberRepository memberRepository = new MemberRepository();
	    String keyword = rq.getParams().get(0);
	    List<Member> list = memberRepository.searchMember(keyword);

	    if (list.isEmpty()) {
	        System.out.println("=> 검색 결과가 없습니다.");
	        return;
	    }

	    System.out.println("\n검색 결과");
	    System.out.println("회원id | 이름 | 전화번호");
	    System.out.println("-------------------------------------");

	    for (Member m : list) {
	        System.out.println(m.getId() + " | " + m.getName() + " | " + m.getPhone());
	    }

	    System.out.println("-------------------------------------");
	}


//	TODO: 회원 수정 (member-update)
	public void updateMember(Rq rq) {
		List<String> params = rq.getParams();

	    if (params.size() == 0) {
	        System.out.println("회원 id를 입력해주세요.");
	        return;
	    }

	    int memberId = Integer.parseInt(params.get(0));

	    MemberRepository memberRepository = new MemberRepository();

	    Member m = memberRepository.findById(memberId);

	    if (m == null) {
	        System.out.println("=> 해당 회원이 없습니다.");
	        return;
	    }

	    System.out.println("현재 이름: " + m.getName());
	    System.out.print("새 이름: ");
	    String name = sc.nextLine();

	    System.out.println("현재 전화번호: " + m.getPhone());
	    System.out.print("새 전화번호: ");
	    String phone = sc.nextLine();

	    int result = memberRepository.updateMember(memberId, name, phone);

	    if (result > 0) {
	        System.out.println("=> 회원 정보가 수정되었습니다.");
	    } else {
	        System.out.println("=> 수정 실패");
	    }
	}

//	TODO: 회원 삭제 (member-delete)
   public void deleteMember(Rq rq) {
	   List<String> params = rq.getParams();

	    if (params.size() == 0) {
	        System.out.println("회원 id를 입력해주세요.");
	        return;
	    }

	    int memberId = Integer.parseInt(params.get(0));
	    MemberRepository memberRepository = new MemberRepository();

	    // 1. 먼저 조회 (SELECT)
	    Member member = memberRepository.findById(memberId);
	    
	    if (member == null) {
	        System.out.println("=> 해당 회원이 존재하지 않습니다.");
	        return;
	    }

	    // 2. 삭제 (UPDATE)
	    int result = memberRepository.deleteMember(memberId);

	    if (result > 0) {
	        System.out.println("=> " + member.getName() + " 회원이 삭제되었습니다.");
	    }
	}
   
}



