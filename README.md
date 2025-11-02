![스크린샷 2025-11-02 오전 4.12.45.png](attachment:57204259-722b-4c52-bebe-b8d60238a40d:스크린샷_2025-11-02_오전_4.12.45.png)
# Debugging Rounge (Backend)
개발 관련 질문을 묻고 답할 수 있는 Q&A 게시판 백엔드 프로젝트

### 개발 관련 지식을 묻고 답할 수 있는 Q&A 게시판 프로젝트

## 기술 스택

- **Language**: `Java21`
- **Back-End**: `Spring Boot`, `Spring Data JPA`, `Spring Security`
- **Database**: `MySQL`

## 프로젝트 핵심 관심사

### 헥사고날(Ports & Adapters) 아키텍처 적용

- DB/OAuth 등 **외부 기술로부터 코어 로직을 격리**해 **테스트 용이성**과 **교체 가능성**을 확보하기 위해 도입
- `Controller → Service → Repository` 구조의 결합도를 낮추고, **의존 방향을 코어로만**

```java
[ Web(API) ] ──> (IN Port) ──> [ Application + Domain ] ──> (OUT Port) ──> [ OAuth / DB ]
     ↑  입력 어댑터                          코어                                 출력 어댑터
```

**질문 목록(최신/추천) 조회**

- 입력 어댑터: `question.api.QuestionController`
    
    → In-Port `GetQuestionListWithPreviewQuery` 호출
    
- 유스케이스: `question.application.QuestionFacade`
    
    → Out-Port `LoadQuestionPort` 로 정렬 기준(`LATEST`/`RECOMMEND`)과 페이지 정보를 전달
    
- 출력 어댑터: `question.infrastructure.persistence.adapter.QuestionRepositoryAdapter`
    
    → JPA/QueryDSL/네이티브로 집계 점수 포함 정렬 구현
    
    → `projection.QuestionListView`로 조회 후 `application.dto.QuestionListDto` 변환
    

### 리프레쉬 토큰 전략

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
