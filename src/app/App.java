package app;

import util.DateHolder;
import util.Rq;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Scanner;
import java.util.function.Consumer;

import controller.ComicController;
import controller.MemberController;
import controller.RentalController;

public class App {

	private final Scanner sc;
	private final ComicController comicController;
	private final RentalController rentalController;
	private final MemberController memberController;
	private final DateHolder dateHolder = new DateHolder();

	private final HashMap<String, Consumer<Rq>> commandMap = new HashMap<>();
	private final HashMap<String, String> helpMap = new HashMap<>();

	private boolean runState = true;

	public App() {
		this.sc = new Scanner(System.in);
		this.comicController = new ComicController();
		this.rentalController = new RentalController();
		this.memberController = new MemberController();
		this.rentalController.setDateHolder(this.dateHolder);

		commandMap.put("comic-add", comicController::comicAdd);
		commandMap.put("comic-list", comicController::comicList);
		commandMap.put("comic-detail", comicController::comicDetail);
		commandMap.put("comic-search", comicController::comicSearch);
		commandMap.put("comic-update", comicController::comicUpdate);
		commandMap.put("comic-delete", comicController::comicDelete);
		
		// rental 명령어
		commandMap.put("rental-rent", rentalController::rentalRent);
		commandMap.put("rental-return", rentalController::rentalReturn);
		commandMap.put("rental-list", rentalController::rentalList);
		
		// member
		commandMap.put("member-add", memberController::addMember);
		commandMap.put("member-list",memberController::showMembers);
		commandMap.put("member-search",memberController::searchMember);
		commandMap.put("member-delete", memberController::deleteMember);
		commandMap.put("member-update",memberController::updateMember);

		commandMap.put("exit", this::exit);
		commandMap.put("help", this::help);
		commandMap.put("date-show", this::dateShow);
		commandMap.put("date-change", this::dateChange);

		// setHelpMap();
	}

	public void run() {
		System.out.println("-------------------------------------");
		System.out.println("만화 대여 시스템");
		System.out.println("명령어 목록을 보려면 help 입력");
		System.out.println("-------------------------------------");

		while (true) {
			System.out.print("명령어: ");
			String cmd = sc.nextLine().trim();

			if (cmd.isBlank()) {
				continue;
			}

			Rq rq;
			try {
				rq = new Rq(cmd);
				String command = rq.getCommand();
				Consumer<Rq> function = commandMap.get(command);
				function.accept(rq);

				if (!runState) break;
			} catch (Exception e) {
				System.out.println("=> 명령어 형식이 올바르지 않습니다.");

				continue;
			}
		}
	}

	private void exit(Rq rq) {
		runState = false;
		System.out.println("종료합니다...");
	}

	private void setHelpMap() {
		String command;
		StringBuilder sb = new StringBuilder();

		// template
		// command = "comic-add";
		// sb.setLength(0);
		//
		// helpMap.put(command, sb.toString());

		// [만화]
		command = "comic-list";
		sb.setLength(0);
		sb.append("■ comic-list\n");
		sb.append("    OPTIONS\n");
		sb.append(String.format("      %-40s->", "--asc"))
			.append(" 오름차순 정렬 (기본 정렬 방향)\n");
		sb.append(String.format("      %-40s->", "--desc"))
			.append(" 내림차순 정렬\n");
		sb.append("\n");
		sb.append("    OUTPUT\n");
		sb.append("      성공: 만화 목록 출력\n");
		sb.append("      실패: 에러 메세지\n");
		sb.append("\n");
		sb.append("    SORT RULE\n");
		sb.append("      [comic.regDate]에 대해 --asc 또는 --desc가 적용된다.\n");
		helpMap.put(command, sb.toString());
	}

	private void help(Rq rq) {
		StringBuilder sb = new StringBuilder();
		// if (rq.getParams().size() == 0)
		// sb.append("help 뒤에 궁금한 command를 입력해주세요.\n");
		// sb.append("예시: help comic-add\n");
		// sb.append("\n");
		sb.append("------------------ 명령어 목록 ------------------\n");
		sb.append("[만화]\n");
		sb.append(String.format("%-20s: %s\n", "comic-add", "만화책 등록"));
		sb.append(String.format("%-20s: %s\n", "comic-list", "만화책 목록"));
		sb.append(String.format("%-20s: %s\n", "comic-detail", "만화책 상세 보기"));
		sb.append(String.format("%-20s: %s\n", "comic-search", "만화책 수정"));
		sb.append(String.format("%-20s: %s\n", "comic-update", "만화책 등록"));
		sb.append(String.format("%-20s: %s\n", "comic-delete", "만화책 삭제"));
		sb.append("\n");
		sb.append("[회원]\n");
		sb.append(String.format("%-20s: %s\n", "member-add", "회원 등록"));
		sb.append(String.format("%-20s: %s\n", "member-list", "회원 목록"));
		sb.append(String.format("%-20s: %s\n", "member-search", "회원 검색"));
		sb.append(String.format("%-20s: %s\n", "member-update", "회원 정보 수정"));
		sb.append(String.format("%-20s: %s\n", "member-delete", "회원 삭제"));
		sb.append("\n");
		sb.append("[대여]\n");
		sb.append(String.format("%-20s: %s\n", "rental-rent", "만화 대여"));
		sb.append(String.format("%-20s: %s\n", "rental-return", "만화 반납"));
		sb.append(String.format("%-20s: %s\n", "rental-list", "대여 목록"));
		sb.append("\n");
		sb.append("[기타]\n");
		sb.append(String.format("%-20s: %s\n", "date-show", "현재 날짜"));
		sb.append(String.format("%-20s: %s\n", "date-change", "날짜 변경"));
		sb.append(String.format("%-20s: %s\n", "exit", "종료"));

		// else {
		// 	String param = rq.getParams().get(0);
		// 	System.out.println(helpMap.get(param));
		// }
		System.out.println(sb);
	}

	private void dateShow(Rq rq) {
		System.out.println(dateHolder.getDate());
	}

	private void dateChange(Rq rq) {
		String param = rq.getParams().get(0);

		StringBuilder sb = new StringBuilder();
		sb.append(dateHolder.getDate()).append(" -> ");

		// now
		if ("--now".equals(param)) {
			dateHolder.setDate(LocalDate.now());
		} else {
			String[] date = rq.getParams().get(0).split("-");
			LocalDate newDate = LocalDate.of(
				Integer.parseInt(date[0]),
				Integer.parseInt(date[1]),
				Integer.parseInt(date[2])
			);
			dateHolder.setDate(newDate);
		}
		sb.append(dateHolder.getDate());
		System.out.println(sb);
	}
}