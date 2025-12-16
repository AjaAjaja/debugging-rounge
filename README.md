
<img width="1013" height="502" alt="스크린샷 2025-11-02 오전 4 12 45" src="https://github.com/user-attachments/assets/e990e69f-8232-4255-b1e2-838ad9d7b314" />

### 개발 관련 지식을 묻고 답할 수 있는 Q&A 게시판 프로젝트

## 1. 기술 스택

- **Language**: `Java21`
- **Back-End**: `Spring Boot`, `Spring Data JPA`, `Spring Security`
- **Database**: `MySQL`

## 2. ERD
<img width="968" height="632" alt="스크린샷 2025-12-16 오후 6 02 24" src="https://github.com/user-attachments/assets/edb311d4-c843-4dc8-9b25-0b4f3e4a46b3" />

## 3. API 개요

### 기술 스택 / 스타일

- 스타일: RESTful JSON API
- 인증:
    - Google OAuth2 로그인
    - Access Token(JWT, Authorization 헤더) + Refresh Token(JWT, HttpOnly 쿠키)
- 주요 도메인:
    - Auth, User, Question, Answer, Recommend(추천)
- **전체 엔드포인트 / 요청·응답 스펙**은 Swagger UI에서 확인할 수 있습니다.
- Swagger UI: `GET /swagger-ui/index.html`
- OpenAPI JSON: `GET /v3/api-docs`

> 운영 환경에서는 실제 배포 도메인 기준으로
> 
> 
> `https://api.debugging-rounge.com/swagger-ui/index.html` 에서 확인 가능합니다.
> 

### 인증 / 토큰 구조

- **로그인**
    - `GET /oauth2/authorization/google` 로 Google OAuth2 로그인 시작
    - 로그인 성공 시:
        - HttpOnly 쿠키에 `refreshToken` 저장 (Path=/auth, 14일)
        - 응답 Body로 `accessToken` 반환
- **Access Token**
    - 전달 위치: `Authorization: Bearer <ACCESS_TOKEN>`
    - 만료: 약 30분
- **Refresh Token**
    - 전달 위치: HttpOnly 쿠키 `refreshToken`
    - 만료: 약 14일
    - 사용 API:
        - `POST /auth/refresh` : Access Token 재발급 (+ 필요 시 Refresh Token 회전)
        - `POST /auth/logout` : Refresh Token 블랙리스트 처리 + 쿠키 만료

## 4. 헥사고날(Ports & Adapters) 아키텍처 적용

### 4-1. 헥사고날 아키텍처 적용 배경

현재는 구글 OAuth 기반 로그인 + JWT만 사용하고 있지만,

실제 서비스라면 여러 소셜 로그인 제공자 추가, Refresh Token 저장 방식/저장소 변경(Redis 도입 등)과 같은 요구가 생길 수 있다고 보았습니다.

또한 나중에 질문/답변에 대한 알림 기능을 이메일·카카오톡·푸시 등으로 확장할 때도

Application 로직을 건드리지 않고, **알림 어댑터만 교체/추가하는 구조**를 만들고 싶었습니다.

이러한 변화 가능성을 Application 계층까지 전파시키지 않고 외부 인프라 변경에 대해 유연하게 대처하기 위해

**인증/토큰/알림 등 외부 인프라 의존성을 Port/Adapter로 분리하는 헥사고날 아키텍처를 적용했습니다.**

### 4-2. 헥사고날 아키텍처 적용

```java
[ Web(API) ] ──> (IN Port) ──> [ Application + Domain ] ──> (OUT Port) ──> [ OAuth / DB ]
     ↑  입력 어댑터                          코어                                 출력 어댑터
```

### 4-3. 전체 구조 개요

- **Application 계층 (Facade + Port)**
    - **`feature.question.application.QuestionFacade`**
    - **`feature.answer.application.AnswerFacade`**
    - **`feature.auth.application.AuthFacade`**
    - **`…Port`** 인터페이스들 (**`LoadQuestionPort`**, **`SaveQuestionPort`**, **`LoadAnswerPort`**, **`LoadQuestionRecommendPort`** 등)
- **Adapter 계층 (Infrastructure/Web)**
    - JPA 어댑터: **`…JpaAdapter`** (Port 구현체)
    - Web 어댑터: **`…Controller`** (REST API 진입점)
- **Domain/공통**
    - **`feature.question.domain.Question`**, **`feature.answer.domain.Answer`**
    - 추천 타입 **`RecommendType`**
    - 공통 예외, JWT, 보안 설정 등

### **예시: 질문 목록(최신/추천) 조회**

- **Controller**
    - HTTP 요청 파라미터(**`order`**, **`page`**, **`size`**, 로그인 사용자 id)를 파싱
    - **`QuestionFacade.getQuestionList(order, pageable, loginUserId)`** 호출
- **Application – Facade**
    - **`QuestionOrder`**에 따라 **`LoadQuestionPort`**의 어떤 메서드를 쓸지 결정
    - 질문 목록을 조회한 뒤, **`LoadQuestionRecommendPort`**로 추천 점수 + 내 추천 상태를 조회
    - 최종 **`Page<QuestionListDto>`** 조립

### 4-4. 헥사고날 아키텍처를 적용함으로써 얻은 장점

**장점 1 – 코어 로직이 인프라에서 분리되어 유지보수가 용이**

- 코어 로직은 Port 인터페이스를 의존하기 때문에
- 외부 인프라가 변경되어도 코어 로직의 코드 뿐만 아니라 테스트 코드 또한 변경이 거의 없음
- 덕분에 외부 인프라의 변경에 대한 높은 유연성

**장점 2 - 유즈 케이스 중심 코드 구조**

기능이 늘어날수록 “이 기능의 진입점이 어디인지, 전체 흐름이 어떤지”를 찾는 일이 중요

헥사고날 구조에서 **Facade를 유즈케이스의 집합**으로 보이도록 설계

- **`QuestionFacade`**
    - 질문 작성(`CreateQuestionUseCase`), 수정(`UpdateQuestionUseCase`),
    삭제(`DeleteQuestionUseCase`)
    - 질문 목록 최신순/추천순(`GetQuestionListWithPreviewQuery`)
    - 질문 상세 + 추천 정보(`GetQuestionWithAnswersQuery`)
- **`AnswerFacade`**
    - 답변 작성(`CreateAnswerUseCase`), 수정(`UpdateAnswerUseCase`), 
    삭제(`DeleteAnswerUseCase`)
    - 답변 목록 + 추천 정보(`GetAnswersQuery`)
- Port 인터페이스의 네이밍을 통해 어떤 역할을 하는지 파악하기 쉬움
- UseCase(상태변경), Query(조회)
- 새로운 개발자가 들어와도 “이 기능 로직은 Facade 쪽을 먼저 보면 된다”라고 설명하기 쉽고,
- 기능 변경/추가 시 어느 클래스를 수정해야 하는지가 명확

## 5. 리프레쉬 토큰 전략

이번 프로젝트에서는 JWT의 한계와 단점을 보완하는데 관심을 집중

- **단일 토큰 원칙**
사용자가 새로 로그인하거나 재발급을 받는다면 즉시 기존 Refresh Token을 폐기 
→ 동시에 여러 개의 토큰 생성 불가
- **재사용 감지 시 전량 폐기**
폐기된 토큰 재사용이 감지가 되면 해당 유저의 모든 리프레쉬 토큰 폐기 
→ 이 후 다중 기기 정책 도입 시에도 일관된 제어가 가능
- **보안을 고려한 저장**
Refresh Token 원문을 저장하지 않고, 단방향 해시 값으로 저장 
→ 유출 시에도 원문 노출을 최소화

이 전략은 **`RefreshTokenPort`**, **`BlacklistedRefreshTokenPort`**, **`TokenHasherPort`**와 같은 Port를 통해 구현하여, 향후 저장소를 MySQL에서 Redis로 옮기거나 관리 정책을 변경할 때에도 Application 로직 변경을 최소화할 수 있도록 했습니다.

## **6. 테스트**

이 프로젝트에서는 `AuthFacade`같이 토큰 정책과 흐름 분기가 복잡한 클래스를 중심으로 단위 테스트를 구성했습니다.

또한, Mocking이 아니라 실제 DB가 필요한 복잡한 DB 쿼리(집계, 정렬)같은 경우에는 JPA 슬라이스 테스트로 테스트를 구성하였다.

### **6-1. 단위 테스트**

- AuthFacade, QuestionFacade, JwtProvider 등 헥사고날 아키텍처의 Application(Facade) 계층을 기준으로 검증
- JWT 발급/검증, 토큰 해시 처리, 리프레시 토큰 회전/블랙리스트 처리

### **6-2. 통합 테스트**

- Testcontainers로 MySQL 컨테이너를 띄워 실제 DB 환경과 유사한 조건에서 JPA 쿼리와 Native Query 동작을 검증
- 질문/답변 목록 정렬(최신순/추천순), 추천 점수 집계와 같이 SQL·인덱스 전략에 영향을 받는 부분을 집중적으로 테스트
- 

### **6-3 주요 테스트**

- **인증 / 토큰 흐름**
    - Access/Refresh Token 발급·만료, 단일 Refresh Token 원칙, 회전(rotate) 및 재사용 감지
    - 재사용된 Refresh Token 발생 시 블랙리스트 등록 및 세션 만료 처리
- **질문 / 답변 도메인**
    - 질문·답변 생성/수정/삭제 유스케이스
    - 소유자 검증(작성자만 수정/삭제 가능) 로직
    - 질문/답변 추천(UP/DOWN/NONE) 상태 변경 및 추천 점수 집계
- **DB 쿼리 / 정렬·집계 로직**
    - 최신순/추천순 정렬 시 페이지네이션 및 정렬 우선순위
    - Native Query 기반 추천 점수 집계 및 upsert(`ON DUPLICATE KEY UPDATE`) 동작
