## 데브코스 3차팀 Team9 9조대

### 팀 활동 주제 1

> 만화책 대여점 시스템 구현

목표
- Java 기본 문법, 클래스, 객체지향 설계, 사용자 입력 처리 연습
- MySQL + JDBC 기반 데이터 저장/조회 연습

---

### Branch 네이밍 규칙

- 브랜치 이름은 모두 소문자로 케밥 케이스(-) 사용

- `/` 사용해 계층 나누기 (fix/search-bug)

- 기능 단위로 브랜치에 커밋하기

- main 브랜치에 바로 푸시하지 말고 PR 기능 활용하기
    1. main 브랜치에서 개발할 feature/기능명 브랜치 생성
    2. 작업 완료 후 github에서 **Pull Request**요청
    3. 팀원들과 코드 리뷰 후 main브랜치로 Merge!
    4. Merge 완료되어 사용하지 않는 브랜치는 삭제


| 이름     | 설명                                   | 예시                 |
| -------- | -------------------------------------- | -------------------- |
| Main     | 메인 브랜치                            | main                 |
| Feature  | 기능 개발 브랜치                       | feature/search       |
| Fix      | 버그 수정 시 사용                      | fix/issue#19          |
| Docs     | 문서 수정(README, 코드 주석 등)        | docs/readme   |
| Test     | 테스트 코드 작성 시 사용               | test/search-service  |
| Refactor | 기능 변화 없이 코드 구조개선           | refactor/login-logic |
| Chore    | 빌드 설정, 환경 설정, 기타 자잘한 수정 | chore/install        |
| Design   | 디자인만 수정                          | design/main-page     | 

---

### Commit 메세지 규칙

- 브랜치 이름과 동일하게 커밋 메시지 작성

- 예시
    - feature/search 브랜치 -> feat: 검색 기능 구현
    - docs/readme 브랜치 -> docs: README 업데이트