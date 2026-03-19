package app;

import util.DateHolder;
import util.Rq;
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
		commandMap.put("member-add", rq -> memberController.addMember());
		commandMap.put("member-list", rq -> memberController.showMembers());
		commandMap.put("member-search", rq -> {
		    String keyword = rq.getParams().get(0);
		    memberController.searchMember(keyword);
		});
		commandMap.put("member-delete", rq -> {
		    int id = Integer.parseInt(rq.getParams().get(0));
		    memberController.deleteMember(id);
		});
		commandMap.put("member-update", rq -> {
		    if (rq.getParams().size() == 0) {
		        System.out.println("회원 id를 입력해주세요.");
		        return;
		    }

		    int id = Integer.parseInt(rq.getParams().get(0));
		    memberController.updateMember(id);
		});

		

		commandMap.put("exit", this::exit);
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
}