# Team9 9조대

- 김민호, 이건희, 이근찬, 지영재

## 팀 활동 주제 1

> 만화책 대여점 시스템 구현

**목표**

- Java 기본 문법, 클래스, 객체지향 설계, 사용자 입력 처리 연습
- MySQL + JDBC 기반 데이터 저장/조회 연습

---
## 프로젝트 구조
```text

comic-project/
├── src/
│   ├── aibe5Comic/
│   │   └── Main.java                 # 프로그램 최초 실행 진입점
│   ├── app/
│   │   └── App.java                  # CLI 명령어 라우팅 및 전반적인 프로그램 흐름 제어
│   ├── controller/                   # 사용자 요청을 받아 처리하는 컨트롤러 계층
│   │   ├── ComicController.java      # 만화책 관련 비즈니스 로직
│   │   ├── MemberController.java     # 회원 관련 비즈니스 로직
│   │   └── RentalController.java     # 대여/반납 및 연체 패널티 비즈니스 로직
│   ├── domain/                       # 데이터베이스 테이블과 매핑되는 도메인 객체
│   │   ├── Comic.java
│   │   ├── Member.java
│   │   └── Rental.java
│   ├── repository/                   # JDBC를 이용해 데이터베이스와 통신하는 메소드
│   │   ├── ComicRepository.java
│   │   ├── MemberRepository.java
│   │   └── RentalRepository.java
│   └── util/                         # 공통으로 사용하는 유틸리티 모음
│       ├── ConnectionConst.java      # DB 연결 정보
│       ├── ConnectionConstForm.java  # DB 연결 정보 작성 양식
│       ├── DateHolder.java           # 시스템 날짜 제어
│       ├── DBUtil.java               # JDBC 커넥션 연결 및 자원 반납 유틸
│       └── Rq.java                   # 사용자 입력 명령어 파싱 유틸
├── test/                             # JUnit 단위 및 통합 테스트 폴더
│   ├── controller/
│   │   └── RentalControllerTest.java # 대여 로직 및 시간 제어(DateHolder) 연동 테스트
│   └── repository/
│       ├── MemberRepositoryTest.java # 회원 DB 연동 테스트
│       └── RentalRepositoryTest.java # 대여 DB 연동 테스트
├── lib/                              # JDBC 드라이버 등 외부 라이브러리 폴더
├── .gitignore                        # Git 버전 관리 제외 목록
└── README.md                         # 프로젝트 설명서

```

---

## database

MySQL을 사용합니다.

개인 데이터베이스에서 작동합니다.

**테스트를 위한 공통 SQL쿼리**

```sql
use comic;

-- database 삭제 후 연결
DROP DATABASE IF EXISTS comic;

CREATE DATABASE comic
DEFAULT CHARACTER SET utf8mb4
COLLATE utf8mb4_0900_ai_ci;

-- create comic table
DROP TABLE IF EXISTS comic.comic;
CREATE TABLE comic.comic (
     id INT auto_increment NOT NULL,
     title varchar(100) NOT NULL,
     author varchar(100) NOT NULL,
     volume INT NOT NULL,
     is_rental BOOL DEFAULT false NOT NULL,
     reg_date DATE NOT NULL,
     CONSTRAINT comic_pk PRIMARY KEY (id),
     CONSTRAINT comic_unique UNIQUE KEY (title,author,volume)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;

-- create member table
DROP TABLE IF EXISTS comic.`member`;
CREATE TABLE comic.`member` (
    id INT auto_increment NOT NULL,
    name varchar(100) NOT NULL,
    phone varchar(100) NOT NULL,
    reg_date DATE NOT NULL,
    penalty_date DATE NULL,
    CONSTRAINT member_pk PRIMARY KEY (id),
    is_deleted BOOLEAN DEFAULT FALSE
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;

-- create rental table
DROP TABLE IF EXISTS comic.rental;
CREATE TABLE comic.rental (
    id INT auto_increment NOT NULL,
    comic_id INT NOT NULL,
    member_id INT NOT NULL,
    rent_date DATE NOT NULL,
    due_date DATE NOT NULL,
    return_date DATE NULL,
    CONSTRAINT rental_pk PRIMARY KEY (id),
    CONSTRAINT rental_comic_FK FOREIGN KEY (comic_id) REFERENCES comic.comic(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT rental_member_FK FOREIGN KEY (member_id) REFERENCES comic.`member`(id) ON DELETE RESTRICT ON UPDATE RESTRICT
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO comic.comic (title, author, volume, is_rental, reg_date) VALUES
    ('원피스', '오다 에이치로', 1, false, '2024-01-10'),
    ('원피스', '오다 에이치로', 2, false, '2024-01-10'),
    ('원피스', '오다 에이치로', 3, false, '2024-01-10'),
    ('원피스', '오다 에이치로', 4, false, '2024-01-10'),
    ('원피스', '오다 에이치로', 5, false, '2024-01-10'),
    ('나루토', '키시모토 마사시', 1, false, '2024-02-01'),
    ('나루토', '키시모토 마사시', 2, false, '2024-02-01'),
    ('나루토', '키시모토 마사시', 3, false, '2024-02-01'),
    ('나루토', '키시모토 마사시', 4, false, '2024-02-01'),
    ('나루토', '키시모토 마사시', 5, false, '2024-02-01'),
    ('진격의 거인', '이사야마 하지메', 1, false, '2024-03-15'),
    ('원피스', '오다 에이치로', 6, false, '2024-01-10'),
    ('진격의 거인', '이사야마 하지메', 2, false, '2024-03-15');

INSERT INTO comic.member (name, phone, reg_date, penalty_date) VALUES
    ('김민호', '010-1234-5678', '2024-01-01', NULL),
    ('이근찬', '010-2222-3333', '2024-01-15', NULL),
    ('이건희', '010-4444-5555', '2024-02-10', NULL),
    ('지영재', '010-7777-8888', '2024-03-01', NULL);
```

---

## Branch 네이밍 규칙

- 브랜치 이름은 모두 소문자로 케밥 케이스(`delicious-kebab`) 사용

- `/` 사용해 계층 나누기 (fix/search-bug)

- 기능 단위로 브랜치에 커밋하기

- main 브랜치에 바로 푸시하지 말고 PR 기능 활용하기
    1. main 브랜치에서 개발할 feature/기능명 브랜치 생성
    2. 작업 완료 후 github에서 **Pull Request**요청
    3. 팀원들과 코드 리뷰 후 main브랜치로 Merge!
    4. Merge 완료되어 사용하지 않는 브랜치는 삭제하기


| 이름     | 설명                                   | 예시                 |
| -------- | -------------------------------------- | -------------------- |
| Main     | 메인 브랜치                            | `main`                 |
| Feature  | 기능 개발 브랜치                       | `feature/search`       |
| Fix      | 버그 수정 시 사용                      | `fix/issue#19`          |
| Docs     | 문서 수정(README, 코드 주석 등)        | `docs/readme`   |
| Test     | 테스트 코드 작성 시 사용               | `test/search-service`  |
| Refactor | 기능 변화 없이 코드 구조개선           | `refactor/login-logic` |
| Chore    | 빌드 설정, 환경 설정, 기타 자잘한 수정 | `chore/install`        |
| Design   | 디자인만 수정                          | `design/main-page`     | 

---

### Commit 메세지 규칙

- 브랜치 이름과 동일하게 커밋 메시지 작성

- 예시
    - feature/search 브랜치 -> feat: 검색 기능 구현
    - docs/readme 브랜치 -> docs: README 업데이트