package domain;

import java.time.LocalDate;

public class Member {
	
	 private int id; // INT, PK
	 private String name; // varchar(100)
	 private String phone; // varchar(100)
	 private LocalDate regDate; // DATE
	 private LocalDate penaltyDate; // DATE, nullable
 //기본 생성자
	public Member() {}
	 // 전체 생성자
	public Member(int id, String name, String phone, LocalDate regDate, LocalDate penaltyDate) {
		this.id = id;
		this.name = name;
		this.phone = phone;
		this.regDate = regDate;
		this.penaltyDate = penaltyDate;
	}
	//getter
	public int getId() { return id; }
    public String getName() { return name; }
    
    
    public String getPhone() { return phone; }
    public LocalDate getRegDate() { return regDate; }
    public LocalDate getPenaltyDate() { return penaltyDate; }
    
	//setter
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setRegDate(LocalDate regDate) { this.regDate = regDate; }
    public void setPenaltyDate(LocalDate penaltyDate) { this.penaltyDate = penaltyDate; }
}
