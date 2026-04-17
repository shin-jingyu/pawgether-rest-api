# Pawgether REST API

반려동물 박람회 정보를 조회하고, 게시글에 댓글·대댓글·좋아요·북마크를 남길 수 있는 Spring Boot 백엔드 프로젝트입니다.

이 프로젝트는 단순히 기능을 구현하는 것에서 끝나지 않고, REST를 다시 공부하면서 "왜 이런 URI와 HTTP 메서드를 써야 하는가"를 실제 코드에 적용해보는 과정까지 함께 담고 있습니다.

처음에는 REST를 "예쁜 URL 규칙" 정도로만 이해한 부분이 있었지만, 다시 정리해보면서 리소스 중심 설계, Uniform Interface, 자기서술적 메시지, 클라이언트-서버의 역할 분리 관점에서 API를 다시 바라보게 되었습니다.

## Tech Stack

- Java 21
- Kotlin Gradle DSL
- Spring Boot 3.5
- Spring Web
- Spring Validation
- Spring Data JPA
- QueryDSL
- PostgreSQL
- Valkey(Redis compatible)
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

## What I Re-learned About REST

### 1. REST는 URI 작명 규칙만이 아니다

예전에는 REST를 "동사가 아닌 명사를 써야 한다", "슬래시를 이렇게 써야 한다" 정도로만 이해한 부분이 있었습니다.  
다시 공부하면서 REST는 분산 하이퍼미디어 시스템을 위한 아키텍처 스타일이고, 핵심은 리소스를 일관된 인터페이스로 다루는 데 있다는 점을 다시 정리했습니다.

### 2. Uniform Interface가 핵심이다

REST에서 중요한 것은 URL이 예뻐 보이는가가 아니라, 클라이언트가 일관된 방식으로 리소스를 다룰 수 있는가입니다.

- 리소스는 URI로 식별한다.
- 행위는 HTTP Method로 표현한다.
- 메시지는 스스로 의미를 설명할 수 있어야 한다.
- 클라이언트는 서버 내부 구현이 아니라 표현(Representation)을 통해 리소스를 다룬다.

예를 들어 `signup`, `filter`, `exists`, `count`처럼 기능 중심 이름을 URI에 직접 드러내기보다,  
`users`, `sessions`, `petfairs`, `comments` 같은 리소스를 기준으로 설계하는 것이 REST에 더 가깝습니다.

### 3. 클라이언트와 서버는 독립적으로 진화해야 한다

REST는 클라이언트와 서버의 책임을 분리합니다.  
서버는 리소스와 상태 전이를 HTTP 인터페이스로 제공하고, 클라이언트는 그 인터페이스를 소비합니다.

이 관점이 중요했던 이유는, 서버 내부 구현이나 유스케이스 이름이 외부 API 설계에 그대로 노출되면 API가 기능 중심으로 굳어지고, 이후 프론트엔드나 다른 클라이언트와 함께 발전시키기 어려워지기 때문입니다.

### 4. 자기서술적 메시지(Self-descriptive Message)를 더 의식하게 되었다

REST API는 요청과 응답만 봐도 의미를 파악하기 쉬워야 합니다.

- `GET /api/v1/petfairs/3`
- `POST /api/v1/petfairs`
- `DELETE /api/v1/petfairs/3`

이런 형태는 URI와 메서드만 봐도 의도가 비교적 분명합니다.  
반면 `POST /filter`, `GET /exists/...`, `POST /count` 같은 형태는 동작은 알 수 있어도 "무슨 리소스를 다루는가"가 흐려질 수 있습니다.

### 5. HATEOAS까지 포함해야 REST가 완성되지만, 현재는 Richardson Level 2에 가까운 상태다

이번에 다시 정리하면서 REST의 이상적인 형태에는 하이퍼미디어 기반 상태 전이(HATEOAS)까지 포함된다는 점도 놓치고 있었다는 걸 알게 되었습니다.  
현재 이 프로젝트는 HTTP Method, 상태 코드, 리소스 중심 URI를 정리하는 단계에 집중하고 있기 때문에, 엄밀히 말하면 완전한 REST보다는 RESTful API에 가까운 상태입니다.

## How I Applied It To This Project

이 프로젝트는 내부적으로 `command/query` 분리와 `usecase` 중심 구조를 사용합니다.  
다만 외부 API는 한동안 기능 중심 URI가 섞여 있었고, 이번 정리를 통해 "외부에는 리소스 중심 인터페이스를 드러내자"는 방향을 더 분명히 하게 되었습니다.

현재 코드에서 이미 반영된 부분은 아래와 같습니다.

- 게시글 리소스를 `petfairs`로 통일해 컬렉션 조회, 단건 조회, 생성, 수정, 삭제를 한 리소스 축에서 다룸
- 댓글을 게시글의 하위 리소스로 두어 `petfair -> comments` 관계가 URI에 드러나도록 정리
- 대댓글을 댓글의 하위 리소스로 두어 `comment -> replies` 관계가 URI에 드러나도록 정리
- 조회는 `GET`, 생성은 `POST`, 수정은 `PATCH`, 삭제는 `DELETE`로 역할을 나눔
- 생성 요청의 상태 코드를 `201 Created`로 맞추며 HTTP 의미를 더 분명히 함

예:

- `GET /api/v1/petfairs`
- `GET /api/v1/petfairs/{petfairId}`
- `POST /api/v1/petfairs`
- `PATCH /api/v1/petfairs/{petfairId}`
- `DELETE /api/v1/petfairs/{petfairId}`
- `GET /api/v1/petfairs/{petfairId}/comments`
- `POST /api/v1/petfairs/{petfairId}/comments`
- `PATCH /api/v1/petfairs/{petfairId}/comments/{commentId}`
- `DELETE /api/v1/petfairs/{petfairId}/comments/{commentId}`
- `GET /api/v1/comments/{commentId}/replies`
- `POST /api/v1/comments/{commentId}/replies`
- `PATCH /api/v1/comments/{commentId}/replies/{replyId}`
- `DELETE /api/v1/comments/{commentId}/replies/{replyId}`
- `GET /api/v1/petfairs?filterStatus=PET_FAIR_ACTIVE`
- `GET /api/v1/petfairs?keyword=megazoo`

## Before vs Now

REST를 다시 공부하기 전에는 "기능이 동작하느냐"에 더 집중했고, API 이름도 자연스럽게 기능 중심으로 흘러가는 경우가 있었습니다.  
지금은 "이 요청이 어떤 리소스에 대한 조작인가?"를 먼저 생각한 뒤 URI와 메서드를 정리하려고 하고 있습니다.

| 관점 | 이전 | 현재 |
| --- | --- | --- |
| 설계 기준 | 기능 구현 중심 | 리소스 중심 + HTTP 의미 반영 |
| URI 표현 | `signup`, `filter`, `count`, `exists` 같은 동작/기능 중심 표현 | `petfairs`, `comments`, `replies` 같은 리소스와 관계 중심 표현 우선 |
| 메서드 사용 | URI가 행위를 설명하는 경우가 많았음 | `GET`, `POST`, `PATCH`, `DELETE`에 행위를 맡기고 URI는 리소스를 설명 |
| REST 이해 | URL 규칙 위주로 이해 | Uniform Interface, Self-descriptive Message, 역할 분리까지 고려 |
| 문서화 시선 | "무엇을 만들었는가" 중심 | "왜 이렇게 설계했는가"까지 설명 |

## What Actually Changed In Code

이번 리팩터링에서 실제로 바뀐 부분은 아래와 같습니다.

### 1. 댓글을 게시글의 하위 리소스로 변경

이전에는 댓글 API가 `/api/v1/comments/{petfairId}` 형태여서, URI만 보면 댓글 컬렉션인지 게시글별 댓글인지 바로 읽히지 않는 면이 있었습니다.  
현재는 게시글의 하위 리소스라는 의미를 드러내기 위해 아래처럼 바꾸었습니다.

- 이전: `POST /api/v1/comments/{petfairId}`
- 현재: `POST /api/v1/petfairs/{petfairId}/comments`

- 이전: `GET /api/v1/comments/{petfairId}`
- 현재: `GET /api/v1/petfairs/{petfairId}/comments`

- 이전: `PATCH /api/v1/comments/{petfairId}/{commentId}`
- 현재: `PATCH /api/v1/petfairs/{petfairId}/comments/{commentId}`

- 이전: `DELETE /api/v1/comments/{petfairId}/{commentId}`
- 현재: `DELETE /api/v1/petfairs/{petfairId}/comments/{commentId}`

### 2. 대댓글을 댓글의 하위 리소스로 변경

이전에는 대댓글 API가 `/api/v1/replies` 중심이어서 "어떤 댓글의 대댓글인가?"가 요청 본문이나 파라미터에 더 의존하는 구조였습니다.  
현재는 댓글의 하위 리소스로 옮겨 관계가 URI에서 바로 읽히도록 정리했습니다.

- 이전: `POST /api/v1/replies`
- 현재: `POST /api/v1/comments/{commentId}/replies`

- 이전: `GET /api/v1/replies/{commentId}`
- 현재: `GET /api/v1/comments/{commentId}/replies`

- 이전: `PATCH /api/v1/replies`
- 현재: `PATCH /api/v1/comments/{commentId}/replies/{replyId}`

- 이전: `DELETE /api/v1/replies/{commentId}/{replyId}`
- 현재: `DELETE /api/v1/comments/{commentId}/replies/{replyId}`

이 과정에서 대댓글 생성/수정 요청도 path variable을 더 적극적으로 사용하도록 바꿨고, 요청 body는 실제 수정 대상의 내용에 집중하도록 정리했습니다.

### 3. 게시글 목록 조회를 쿼리 파라미터 중심으로 정리

이전에는 목록 조회가 `/filter`, `/condition`처럼 기능 이름이 URI에 드러나는 구조였습니다.  
현재는 같은 리소스 컬렉션인 `/api/v1/petfairs`에서 쿼리 파라미터로 조건을 표현하도록 바꾸었습니다.

- 이전: `GET /api/v1/petfairs/filter?filterStatus=...`
- 현재: `GET /api/v1/petfairs?filterStatus=...`

- 이전: `GET /api/v1/petfairs/condition?keyword=...`
- 현재: `GET /api/v1/petfairs?keyword=...`

### 4. 상태 코드도 함께 정리

댓글/대댓글 생성 API는 기존에 성공 시 `200 OK`에 가까운 흐름이었지만,  
현재는 생성 요청에 맞게 `201 Created`를 반환하도록 수정했습니다.

## Remaining Gaps

아직 모든 API가 완전히 RESTful하게 정리된 것은 아닙니다. 현재 코드에는 아래와 같은 개선 여지가 남아 있습니다.

- `/api/v1/account/signup`
- `/api/v1/account/signup/email`
- `/api/v1/account/signup/nickname`
- `/api/v1/bookmark`
- `/api/v1/bookmark/exists`
- `/api/v1/likes/exists/{targetType}/{targetId}`
- `/api/v1/likes/counts/{targetType}/{targetId}`
- `/api/v1/comments/reply-counts`
- `/api/v1/main/carousel`
- `/api/v1/main/comments`

이 엔드포인트들은 기능 중심 URI가 일부 남아 있는 예시입니다.  
예를 들어 아래와 같은 방향으로 더 정리할 수 있습니다.

- `POST /api/v1/users`
- `POST /api/v1/sessions`
- `GET /api/v1/users/email-availability?email=...`
- `GET /api/v1/users/nickname-availability?nickname=...`
- `GET /api/v1/users/me/bookmarks`
- `GET /api/v1/petfairs/{petfairId}/likes/count`
- `GET /api/v1/comments/{commentId}/replies/count`

즉, 이번 리드미 정리는 "REST를 적용 완료했다"는 선언보다,  
"REST를 다시 공부하면서 어떤 부분을 놓쳤는지 이해했고, 그 관점으로 API를 계속 다듬고 있다"는 개발 기록에 더 가깝습니다.

## Runtime Requirements

- JDK 21
- Docker / Docker Compose
- PostgreSQL
- Redis 호환 스토리지

로컬 개발 기준 기본 포트는 아래와 같습니다.

- API 서버: `8080`
- PostgreSQL: `5433`
- Valkey: `6379`

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

- `src/main/resources/application.yml`

현재 로컬 개발 설정에는 DB, Redis, JWT 시크릿 값이 포함되어 있습니다.

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5433/demo
    username: root
    password: root

  data:
    redis:
      host: localhost
      port: 6379
```

실제 협업 또는 배포 환경에서는 민감 정보를 코드에 직접 두기보다 아래 방식으로 분리하는 편이 더 안전합니다.

- 환경 변수 사용
- 비공개 설정 파일 분리
- 프로필별 설정 분리

### 3. 서버 실행

```bash
./gradlew bootRun
```

## Test

전체 테스트 실행:

```bash
./gradlew test
```

## API Domains

현재 컨트롤러 기준 주요 도메인은 아래와 같습니다.

- Account
- PetFair
- Comment
- Reply
- Like
- Bookmark

관련 패키지:

- `src/main/java/com/example/pawgetherbe/controller/command`
- `src/main/java/com/example/pawgetherbe/controller/query`

## Future Improvements

- 기능 중심 URI를 리소스 중심 URI로 계속 정리
- 중첩 리소스 구조 정리
- 상태 코드와 응답 메시지 규칙 통일
- 민감 정보 설정 분리
- Swagger/OpenAPI 문서화
- 테스트 범위 확대

## Entry Point

애플리케이션 시작 클래스:

- `src/main/java/com/example/pawgetherbe/PawgetherBeApplication.java`
