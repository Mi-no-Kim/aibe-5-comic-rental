package domain;

import java.time.LocalDate;

/*
 * Comic
 * - 만화책 1권의 정보를 저장하는 클래스
 * - DB의 comic 테이블 한 줄과 대응된다.
 */
public class Comic {
    private int id;
    private String title;
    private String author;
    private int volume;
    private boolean isRented;
    private LocalDate regDate;

    public Comic() {
    }

    public Comic(int id, String title, String author, int volume, boolean isRented, LocalDate regDate) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.volume = volume;
        this.isRented = isRented;
        this.regDate = regDate;
    }

    public Comic(String title, String author, int volume, boolean isRented, LocalDate regDate) {
        this.title = title;
        this.author = author;
        this.volume = volume;
        this.isRented = isRented;
        this.regDate = regDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public boolean isRented() {
        return isRented;
    }

    public void setRented(boolean rented) {
        isRented = rented;
    }

    public LocalDate getRegDate() {
        return regDate;
    }

    public void setRegDate(LocalDate regDate) {
        this.regDate = regDate;
    }
}