# SeenIt-backend
# 🎬 봤어? (SeenIt)

**"봤어?"는 영화·드라마 콘텐츠를 검색하고, 리뷰와 의견을 공유할 수 있는 통합 리뷰 플랫폼입니다.**  
사용자 간 리뷰와 의견을 나누고 최신 콘텐츠와 인기 컨텐츠를 빠르게 찾아보세요!
---

## 🧩 주요 기능

### 사용자 인증
- 로그인
- 사용자 프로필 관리

### 콘텐츠 검색
- 영화 및 드라마 제목 검색
- 외부 콘텐글 작성 가능
- 외부 콘텐츠 API 연동 (예: TMDB)

### 리뷰 및 별점 기능
- 콘텐츠에 대한 리뷰 작성, 수정, 삭제
- 별점 등록 및 수정 (1~5점)
- 리뷰별 댓글 작성 및 삭제
- 콘텐츠별 평균 별점 자동 계산 및 표시
- 리뷰 정렬 기능 (최신순, 평점순 등)

### 게시판
- 콘텐츠 별 게시글 작성 및 토론
- 게시글 별 댓글 및 대댓글 작성 가능
---

## 🗂️ 프로젝트 구조
─ src.main.java.com.basic.miniPjt5\
│\
├──config # 애플리케이션 전반의 설정 및 환경 구성 \
├─controller \ # 클라이언트 요청을 처리하는 REST API 엔드포인트
├─converter\ # DTO ↔ Entity 간 변환 로직 담당
├─DTO \ # 요청(Request) 및 응답(Response)에 사용되는 데이터 전송 객체
├─entity\ # DB 테이블과 매핑되는 JPA 엔티티 클래스
├─enums # 공통으로 사용되는 상수\
├─exception # 애플리케이션에서 발생하는 예외 정의 및 처리\
├─jwt #JWT(JSON Web Token) 관련 보안 설정\
├─mapper Response와 Entity 간 전환 맵핑\
├─repository\ # JPA를 이용한 DB 접근 계층 (DAO)
├─response TMDB 응답(JSON)을 엔티티로 변환하기 위한 Reponse 객체\
├─security # Spring Security를 이용한 인증 및 권한 부여 설정\
├─service\ # 비즈니스 로직 처리 계층
├─specification 복합 검색을 위한 동적 쿼리 생성\
├─util 데이터 검증, 이미지 불러오기 등 기능 구현\
├─resources # 정적 파일, 설정 파일 (application.properties 등)

## 👨‍👩‍👧‍👦 백엔드 팀 소개 (5조 봤어?)
| 이름  | 역할                           |
|-----|------------------------------|
| 김연주 | 로그인 기능, 유저 정보 관리 및 관리자 기능 구현 |                            |
| 임종섭 | TMDB API 연동 작업, 리뷰 및 별점 작성 기능 |
| 정재운 | 게시글 기능, 댓글 및 대댓글 기능          |

## 🚀 설치 및 실행 방법 

### 1. 저장소 클론
```
git clone https://github.com/your-username/SeenIt-backend.git
cd SeenIt-backend
```

### 2. 환경 변수 설정
```
SPRING_DATASOURCE_URL=jdbc:mariadb://localhost:3306/seenit
SPRING_DATASOURCE_USERNAME=your_db_username
SPRING_DATASOURCE_PASSWORD=your_db_password
KAKAO_REST_API_KEY=your_kakao_api_key
JWT_SECRET=your_jwt_secret_key'''
```

### 3. 빌드 및 실행 (Maven 기준)
```
./mvnw clean package
java -jar target/seenit-backend.jar
```


### 4. 서버 접속
- 기본 서버 주소: http://localhost:8080
- API 문서: http://localhost:8080/swagger-ui.html

---

## 🏗️ 기술 스택

| 분류       | 기술 |
|----------|------|
| Frontend | React, React Router, Axios, Zustand/Redux (선택), Tailwind CSS |
| Backend  | Spring Boot, Spring Security, JPA (Hibernate), MariaDB, JWT, Caffeine, Redis |
| 기타       | TMDB API, Kakao Social Login, Swagger (API 문서) |

---

## 🛠️ 개발 도구

| 도구 종류     | 도구명 |
|-------------|--------|
| DB 클라이언트 | DBeaver |
| API 문서화   | Swagger |
| API 테스트   | Postman (또는 Thunder Client 등 사용 중인 도구) |
| 형상 관리    | Git, GitHub |
| IDE         | IntelliJ IDEA, VSCode |

---

##  코딩 컨벤션 (Coding Convention)

| 항목 | 규칙 |
|------|------|
| **들여쓰기** | 2칸 (space 2개) |
| **따옴표** | `'싱글쿼트'` 사용 |
| **세미콜론** | 항상 줄 끝에 `;` 붙이기 |
| **변수 이름** | `camelCase` 사용 (예: `userName`, `isLoggedIn`) |
| **컴포넌트 이름** | `PascalCase` 사용 (예: `ReviewCard`, `UserProfile`) |
| **파일 이름** | 소문자 또는 카멜케이스 사용 (예: `review-card.jsx`, `userProfile.jsx`) |

---


> 예시: `feat: 리뷰 작성 기능 추가`

---

### 🔤 커밋 타입 종류

| 타입 | 의미 | 예시 |
|------|------|------|
| `feat:` | 기능 추가 | `feat: 별점 등록 기능 구현` |
| `fix:` | 버그 수정 | `fix: 로그인 시 토큰 오류 해결` |
| `style:` | 코드 스타일 수정 (기능 변화 없음) | `style: 세미콜론 누락 수정` |
| `refactor:` | 리팩토링 (기능 변화 없음) | `refactor: 리뷰 모듈 구조 개선` |
| `docs:` | 문서 수정 | `docs: README에 코딩 컨벤션 추가` |
| `test:` | 테스트 코드 추가/수정 | `test: 리뷰 서비스 테스트 추가` |
| `chore:` | 설정, 패키지 관리 등 잡일 | `chore: ESLint 설정 추가` |
| `build:` | 빌드 시스템 변경 | `build: Vite 설정 변경` |
| `ci:` | CI 설정 변경 | `ci: GitHub Actions 워크플로우 추가` |

---

### ✅ 커밋 메시지 작성 예시

```bash
feat: 유튜브 링크 등록 기능 추가
fix: 리뷰 수정 시 페이지 새로고침 문제 해결
chore: Prettier 설치 및 설정 파일 추가
docs: 커밋 메시지 규칙 문서화
```
