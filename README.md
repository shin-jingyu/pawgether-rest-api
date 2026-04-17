# Pawgether BE

반려동물 박람회 정보를 조회하고, 게시글에 댓글·대댓글·좋아요·북마크를 남길 수 있는 Spring Boot 백엔드 프로젝트입니다.

현재 구조는 `command/query` 분리와 `usecase` 중심 설계를 사용하고 있으며, 이후 외부 API를 더 RESTful하게 다듬는 방향으로 리팩터링할 예정입니다.

## Tech Stack

- Java 21
- Kotlin Gradle DSL
- Spring Boot 3.5
- Spring Web
- Spring Validation
- Spring Data JPA
- QueryDSL
- PostgreSQL
- Redis
- JWT
- OAuth2 (Google, Naver, Kakao)
- AWS SDK S3 compatible client
- Cloudflare R2
- MapStruct
- Lombok
- JUnit5, Kotest, MockK

## Main Features

- 회원가입, 로그인, 로그아웃, 회원 탈퇴
- JWT 액세스 토큰 + 리프레시 토큰 기반 인증
- Google / Naver / Kakao OAuth 로그인
- 박람회 게시글 생성, 조회, 수정, 삭제
- 댓글 / 대댓글 생성, 조회, 수정, 삭제
- 좋아요 / 북마크 생성 및 취소
- 메인 캐러셀, 메인 댓글, 캘린더 조회

## Project Structure

```text
src/main/java/com/example/pawgetherbe
├── common        # 공통 필터, 예외 처리, OAuth 유틸
├── config        # Spring 설정, Redis/JPA/R2/OAuth 설정
├── controller    # 외부 HTTP API
│   ├── command   # 쓰기 API
│   └── query     # 읽기 API
├── domain        # 엔티티, 상태값, 사용자 컨텍스트
├── exception     # 도메인별 에러 코드
├── mapper        # DTO <-> 응답/엔티티 변환
├── repository    # command/query 저장소
├── service       # 비즈니스 서비스
└── usecase       # 도메인별 유스케이스
```

## Runtime Requirements

- JDK 21
- Docker / Docker Compose
- PostgreSQL
- Redis 호환 스토리지

로컬 개발 기준 기본 포트는 아래와 같습니다.

- API 서버: `8080`
- PostgreSQL: `5433`
- Redis/Valkey: `6379`

## Getting Started

### 1. Infrastructure 실행

```bash
docker compose up -d
```

`docker-compose.yml` 기준으로 아래 서비스가 실행됩니다.

- PostgreSQL 14
- Valkey 7.2

### 2. 애플리케이션 설정 확인

기본 설정 파일:

- [application.yml](/Users/jingyu/Documents/jingyu/pawgether-be/src/main/resources/application.yml)
- [application-local.yml](/Users/jingyu/Documents/jingyu/pawgether-be/src/main/resources/application-local.yml)

현재 프로젝트는 `local` 프로필이 기본 활성화되어 있습니다.

```yaml
spring:
  profiles:
    active: local
```

### 3. 서버 실행

```bash
./gradlew bootRun
```

## Test

전체 테스트 실행:

```bash
./gradlew test
```

## Configuration Overview

현재 설정상 로컬 실행에 필요한 주요 항목은 아래와 같습니다.

- DB 연결 정보
- Redis 연결 정보
- JWT 시크릿 키
- OAuth 제공자별 클라이언트 정보
- Cloudflare R2 접근 정보

실제 협업 환경에서는 민감 정보를 `application-local.yml`에 직접 커밋하기보다 아래 방식으로 분리하는 것을 권장합니다.

- 환경 변수 사용
- `.env` 또는 비공개 설정 파일 분리
- `application-local.yml`은 예시 템플릿만 유지

예시:

```yaml
spring:
  datasource:
    url: ${DB_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}

jwt:
  secret-key: ${JWT_SECRET_KEY}
```

## API Domains

현재 컨트롤러 기준 주요 도메인은 아래와 같습니다.

- Account
- PetFair
- Comment
- Reply
- Like
- Bookmark

컨트롤러 위치:

- [controller/command](/Users/jingyu/Documents/jingyu/pawgether-be/src/main/java/com/example/pawgetherbe/controller/command)
- [controller/query](/Users/jingyu/Documents/jingyu/pawgether-be/src/main/java/com/example/pawgetherbe/controller/query)

## Design Notes

이 프로젝트는 내부적으로는 유스케이스가 잘 분리되어 있지만, 외부 API는 아직 일부가 기능 중심 URI를 사용하고 있습니다.

예:

- `/api/v1/account/signup`
- `/api/v1/petfairs/filter`
- `/api/v1/comments/count/{petfairId}`
- `/api/v1/likes/exists/{targetType}/{targetId}`

향후에는 아래 방향으로 RESTful하게 정리할 계획입니다.

- URI는 리소스 중심으로 구성
- 동작은 HTTP Method로 표현
- 조회 API는 `GET`으로 정리
- 하위 리소스는 관계 중심 URI로 재구성

예:

- `POST /api/v1/users`
- `POST /api/v1/sessions`
- `GET /api/v1/petfairs?status=...`
- `GET /api/v1/petfairs/{petfairId}/comments`
- `POST /api/v1/comments/{commentId}/replies`

## Future Improvements

- 외부 API URI RESTful 리팩터링
- 민감 정보 설정 분리
- Swagger/OpenAPI 문서화
- 배포 환경별 설정 정리
- 테스트 범위 확대

## Entry Point

애플리케이션 시작 클래스:

- [PawgetherBeApplication.java](/Users/jingyu/Documents/jingyu/pawgether-be/src/main/java/com/example/pawgetherbe/PawgetherBeApplication.java)
