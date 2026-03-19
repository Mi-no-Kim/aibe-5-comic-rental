package app;

import contoller.ComicController;
import contoller.RentalController;
import util.DateHolder;
import util.Rq;

import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

public class App {

	private final Scanner sc;
	private final ComicController comicController;
	private final RentalController rentalController;
	private final DateHolder dateHolder = new DateHolder();

	private static final HashMap<String, Consumer<Rq>> commandMap = new HashMap<>();

	private boolean runState = true;

	public App() {
		this.sc = new Scanner(System.in);
		this.comicController = new ComicController();
		this.rentalController = new RentalController();
		this.rentalController.setDateHolder(this.dateHolder);

		commandMap.put("comic-add", comicController::comicAdd);
		commandMap.put("comic-list", comicController::comicList);
		commandMap.put("comic-detail", comicController::comicDetail);
		commandMap.put("comic-search", comicController::comicSearch);
		commandMap.put("comic-update", comicController::comicUpdate);
		commandMap.put("comic-delete", comicController::comicDelete);

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
			}
		}
	}

	private void exit(Rq rq) {
		runState = false;
		System.out.println("종료합니다...");
	}
}