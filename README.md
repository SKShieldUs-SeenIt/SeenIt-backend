# SeenIt-backend
# 🎬 봤어? (SeenIt)

**"봤어?"는 영화·드라마 콘텐츠를 검색하고, 리뷰를 남기고, 유튜브 영상까지 공유할 수 있는 통합 리뷰 플랫폼입니다.**  
사용자 간 리뷰와 의견을 나누고, 유튜브 요약 영상도 함께 즐겨보세요!

---

## 🧩 주요 기능

### 사용자 인증
- 회원가입 / 로그인
- 이메일 인증 및 사용자 프로필

### 콘텐츠 검색
- 영화 및 드라마 제목 검색
- 외부 콘텐츠 API 연동 (예: TMDB)

### 리뷰 기능
- 콘텐츠에 대한 별점 평가
- 리뷰 텍스트 작성 및 수정
- 리뷰 댓글 기능

### 자유 게시판
- 일반 게시글 작성 및 토론
- 게시글에 댓글 작성 가능



---

## 🏗️ 기술 스택

| 분류 | 기술 |
|------|------|
| Frontend | React, React Router, Axios, Zustand/Redux (선택), Tailwind CSS |
| Backend | Spring Boot, Spring Data JPA, Spring Security, Spring Webflux, SpringDoc OpenAPI, Caffeine, Redis, JWT |

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
