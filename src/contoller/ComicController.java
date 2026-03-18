package contoller;

import domain.Comic;
import repository.ComicRepository;
import util.Rq;

import java.time.LocalDate;
import java.util.List;

public class ComicController {

    private final ComicRepository comicRepository;

    public ComicController() {
        this.comicRepository = new ComicRepository();
    }

    /*
     * comic-add [title] [author] [volume]
     * 예) comic-add "슬램 덩크" "이노우에 다케히코" 1
     */
    public void comicAdd(Rq rq) {
        List<String> params = rq.getParams();

        if (params.size() < 3) {
            System.out.println("=> 사용법: comic-add [title] [author] [volume]");
            return;
        }

        String title = params.get(0);
        String author = params.get(1);

        int volume;
        try {
            volume = Integer.parseInt(params.get(2));
        } catch (NumberFormatException e) {
            System.out.println("=> 권수는 숫자로 입력해야 합니다.");
            return;
        }

        if (title.isBlank()) {
            System.out.println("=> 제목을 입력해야 합니다.");
            return;
        }

        if (author.isBlank()) {
            System.out.println("=> 작가를 입력해야 합니다.");
            return;
        }

        if (title.startsWith("-")) {
            System.out.println("=> 제목은 '-'로 시작할 수 없습니다.");
            return;
        }

        if (author.startsWith("-")) {
            System.out.println("=> 작가명은 '-'로 시작할 수 없습니다.");
            return;
        }

        if (volume <= 0) {
            System.out.println("=> 권수는 1 이상의 숫자여야 합니다.");
            return;
        }

        Comic comic = new Comic(title, author, volume, false, LocalDate.now());

        int newId = comicRepository.addComic(comic);

        if (newId != -1) {
            System.out.println("=> 만화책이 등록되었습니다. (id=" + newId + ")");
        }
    }

    /*
     * comic-list
     * comic-list asc
     * comic-list desc
     */
    public void comicList(Rq rq) {
        List<String> params = rq.getParams();

        String order = "desc";

        if (!params.isEmpty()) {
            String inputOrder = params.get(0).toLowerCase();
            if (inputOrder.equals("asc") || inputOrder.equals("desc")) {
                order = inputOrder;
            }
        }

        List<Comic> comics = comicRepository.findAll(order);

        if (comics.isEmpty()) {
            System.out.println("=> 등록된 만화책이 없습니다.");
            return;
        }

        System.out.println("번호 | 제목 | 권수 | 작가 | 상태 | 등록일");
        System.out.println("--------------------------------------------------------");

        for (Comic comic : comics) {
            String status = comic.isRented() ? "대여중" : "대여가능";

            System.out.printf("%d | %s | %d | %s | %s | %s%n",
                    comic.getId(),
                    comic.getTitle(),
                    comic.getVolume(),
                    comic.getAuthor(),
                    status,
                    comic.getRegDate());
        }

        System.out.println("--------------------------------------------------------");
    }
    /*
     * comic-detail [comic.id]
     * 예) comic-detail 1
     */
    public void comicDetail(Rq rq) {
        List<String> params = rq.getParams();

        if (params.isEmpty()) {
            System.out.println("=> 사용법: comic-detail [comic.id]");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(params.get(0));
        } catch (NumberFormatException e) {
            System.out.println("=> 만화책 번호는 숫자로 입력해야 합니다.");
            return;
        }

        if (id <= 0) {
            System.out.println("=> 올바른 만화책 번호를 입력해야 합니다.");
            return;
        }

        Comic comic = comicRepository.findById(id);

        if (comic == null) {
            System.out.println("=> 해당 번호의 만화책이 존재하지 않습니다.");
            return;
        }

        System.out.println("-------------------------------------");
        System.out.println("만화책 상세 정보");
        System.out.println("번호 : " + comic.getId());
        System.out.println("제목 : " + comic.getTitle());
        System.out.println("권수 : " + comic.getVolume());
        System.out.println("작가 : " + comic.getAuthor());
        System.out.println("상태 : " + (comic.isRented() ? "대여중" : "대여가능"));
        System.out.println("등록일 : " + comic.getRegDate());
        System.out.println("-------------------------------------");
    }

    /*
     * comic-search [keyword]
     * 예) comic-search 슬램
     * 예) comic-search "오다 에이치로"
     */
    public void comicSearch(Rq rq) {
        List<String> params = rq.getParams();

        if (params.isEmpty()) {
            System.out.println("=> 사용법: comic-search [keyword]");
            return;
        }

        String keyword = params.get(0);

        if (keyword.isBlank()) {
            System.out.println("=> 검색어를 입력해야 합니다.");
            return;
        }

        List<Comic> comics = comicRepository.searchByKeyword(keyword);

        if (comics.isEmpty()) {
            System.out.println("=> 검색 결과가 없습니다.");
            return;
        }

        System.out.println("검색 결과");
        System.out.println("번호 | 제목 | 권수 | 작가 | 상태");
        System.out.println("---------------------------------------------------------");

        for (Comic comic : comics) {
            String status = comic.isRented() ? "대여중" : "대여가능";

            System.out.printf("%d | %s | %d | %s | %s%n",
                    comic.getId(),
                    comic.getTitle(),
                    comic.getVolume(),
                    comic.getAuthor(),
                    status);
        }

        System.out.println("---------------------------------------------------------");
    }
}
