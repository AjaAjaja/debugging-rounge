# Debugging Rounge (Backend)
개발 관련 질문을 올리면 답변을 달 수 있는 Q&A 게시판 백엔드 프로젝트

## 프로젝트 핵심
- 질문/답변 게시판 CRUD
- Google 로그인 (OAuth2 Client) → 로그인 성공 시 JWT Access/Refresh 토큰 발급
- Refresh 토큰 Hash 저장 + Pepper 적용(서버 유출 대비), 블랙리스트(강제 만료) 운영
- 헥사고날(Ports & Adapters): 도메인/애플리케이션/인프라 분리 및 포트 기반 의존 역전
- 시크릿 분리: Vault로 비밀키 관리

## 기술 스택
- Language: Java 21
- Framework: Spring Boot 3.5.x
- Security: Spring Security 6.x, OAuth2 Client, JWT (Nimbus JOSE)
- DB: Spring Data JPA, MySQL 8.x
- Secrets: Spring Cloud Vault 4.x
- Testing: JUnit 5, Spring Boot Test

## 아키텍처 (Hexagonal / Ports & Adapters)
- In Port: 시스템 외부 요청(웹, OAuth 요청)을 유즈케이스 단위 별로 나누어 모은 인터페이스 (예: IssueTokensUseCase, CreateQuestionUseCase 등)
- Out Port: 시스템이 외부 의존성(DB, Hasher, SHA-256 등)을 모은 인터페이스 (예: JwtPort, UserPort, QuestionPort 등)
- Adapter: Port 구현체 (예: JPA 리포지토리, JWT 발급기, 해시/암호화 모듈)
- Facade: Facade는 여러 **In Port(유즈케이스)**를 처리하는 진입점으로, 한 곳에서 정책 일관성과 트랜잭션 경계를 유지하며 구현합니다. 반면 Service라는 이름은 도메인 서비스인지 애플리케이션 서비스인지 모호해지기 쉬워 설계 의도가 흐려질 수 있어 Facade를 사용(AuthFacade, QuestionFacade, UserFacade 등)

## 인증/인가 설계 개요
- 로그인 흐름: Google OAuth2 → 사용자 조회/등록(findOrRegister) → JWT 발급 (Access, Refresh)
- Access 토큰: Authorization-Bearer {access} 헤더, 만료 ~30분
- Refresh 토큰: HttpOnly/Secure 쿠키(SameSite=Lax/Strict 권장), 만료 ~2주
- 토큰 저장: Refresh 토큰 해시(HMAC‑SHA‑256 + pepper) 를 DB에 저장, 원문 미보관
- 재발급(/auth/refresh): 쿠키의 Refresh로 인증 → 새 토큰쌍(Access/Refresh) 발급(회전) → 이전 Refresh 블랙리스트 처리
- 로그아웃: 현재 세션의 Refresh 블랙리스트
- Kill All Sessions: 사용자 모든 Refresh 무효화(블랙리스트/단건 삭제 등 구현 전략 택1)
- 쿠키 도난·재사용 공격 대비를 위해 토큰 회전 + 이전 토큰 즉시 무효화
