# Debugging Rounge (Backend)
<img width="1013" height="502" alt="스크린샷 2025-11-02 오전 4 12 45" src="https://github.com/user-attachments/assets/c75dbecb-c6da-4a18-ada2-388242127272" />

### 개발 관련 지식을 묻고 답할 수 있는 Q&A 게시판 프로젝트

## 1. 기술 스택

- **Language**: `Java21`
- **Back-End**: `Spring Boot`, `Spring Data JPA`, `Spring Security`
- **Database**: `MySQL`

## 2. 헥사고날(Ports & Adapters) 아키텍처 적용

### 2-1. 헥사고날 아키텍처 적용 배경

현재는 구글 OAuth 기반 로그인 + JWT만 사용하고 있지만,

실제 서비스라면 여러 소셜 로그인 제공자 추가, Refresh Token 저장 방식/저장소 변경(Redis 도입 등)과 같은 요구가 생길 수 있다고 보았습니다.

또한 나중에 질문/답변에 대한 알림 기능을 이메일·카카오톡·푸시 등으로 확장할 때도

Application 로직을 건드리지 않고, **알림 어댑터만 교체/추가하는 구조**를 만들고 싶었습니다.

이러한 변화 가능성을 Application 계층까지 전파시키지 않고 외부 인프라 변경에 대해 유연하게 대처하기 위해

**인증/토큰/알림 등 외부 인프라 의존성을 Port/Adapter로 분리하는 헥사고날 아키텍처를 적용했습니다.**

### 2-2. 헥사고날 아키텍처 적용

```java
[ Web(API) ] ──> (IN Port) ──> [ Application + Domain ] ──> (OUT Port) ──> [ OAuth / DB ]
     ↑  입력 어댑터                          코어                                 출력 어댑터
```

### 2-3. 전체 구조 개요

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

### 2-3. 헥사고날 아키텍처를 적용함으로써 얻은 장점

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

## 3. 리프레쉬 토큰 전략

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
