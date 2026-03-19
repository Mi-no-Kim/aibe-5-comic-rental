package albe5Comic;
import java.util.Scanner;
import contoller.MemberController;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        MemberController memberController = new MemberController();
        
       
        while (true) {
            System.out.print("명령어: ");
            String cmd = sc.nextLine();
            
            if (cmd.startsWith("member-search")) {
                String[] parts = cmd.split(" ");

                if (parts.length < 2) {
                    System.out.println("검색어를 입력해주세요.");
                    continue;
                }

                String keyword = parts[1];
                memberController.searchMember(keyword);
                continue;
            }
            
            if (cmd.startsWith("member-delete")) {
                String[] parts = cmd.split(" ");
                int memberId = Integer.parseInt(parts[1]);

                memberController.deleteMember(memberId);
                continue;
            }
            
            if (cmd.startsWith("member-update")) {
                String[] parts = cmd.split(" ");

                if (parts.length < 2) {
                    System.out.println("회원 id를 입력해주세요.");
                    continue;
                }

                int memberId = Integer.parseInt(parts[1]);
                memberController.updateMember(memberId);
                continue;
            }

            switch (cmd) {
                case "member-add":
                    memberController.addMember();
                    break;

                case "member-list":
                    memberController.showMembers();
                    break;

                case "exit":
                    System.out.println("프로그램 종료");
                    return;

                default:
                    System.out.println("잘못된 명령어입니다.");
            }
        }
    }
}