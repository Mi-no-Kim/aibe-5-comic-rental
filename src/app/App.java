package app;

import contoller.ComicController;
import util.Rq;

import java.util.Scanner;

public class App {

	private final Scanner sc;
	private final ComicController comicController;

	public App() {
		this.sc = new Scanner(System.in);
		this.comicController = new ComicController();
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
			} catch (Exception e) {
				System.out.println("=> 명령어 형식이 올바르지 않습니다.");
				continue;
			}

			String command = rq.getCommand();

			switch (command) {
				case "comic-add":
					comicController.comicAdd(rq);
					break;
				case "comic-list":
					comicController.comicList(rq);
					break;
				case "exit":
					System.out.println("프로그램을 종료합니다.");
					return;
				default:
					System.out.println("=> 존재하지 않는 명령어입니다.");
			}
		}
	}
}