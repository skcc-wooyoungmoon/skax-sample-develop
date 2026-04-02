# Backend 개발환경 표준 및 개발 가이드
> Spring Boot 기반 프로젝트의 빠른 구축과 초기 세팅을 위한 템플릿 제공 <br>

## Tech Stack
> 기준 파일: `build.gradle`, `settings.gradle`, `gradle.properties`, `gradle/wrapper/gradle-wrapper.properties`, `application.yml`  
> 버전 표기 규칙: `Spring Boot BOM 관리`는 `gradle.properties`의 `springBootVersion`(현재 **3.5.12**) 기준 dependency management를 따름  
> 사용 여부 기준: `src/main/java` 구현 코드와 설정 파일을 함께 확인해 현행화

| 카테고리 | 기술 | 버전 |
|---|---|---|
| 플랫폼 | Java | 21 (Gradle toolchain) |
| 플랫폼 | Gradle Wrapper | 9.4.0 |
| 프레임워크 | Spring Boot | 3.5.12 |
| 프레임워크 | Spring Dependency Management Plugin | 1.1.7 |
| 프레임워크 | Spring Boot Starter Web / Validation / WebFlux / AOP | Spring Boot BOM 관리 (3.5.12) |
| 프레임워크 | Spring Boot Starter Security / Actuator | Spring Boot BOM 관리 (3.5.12) |
| 프레임워크 | springdoc-openapi-starter-webmvc-ui (Swagger UI) | 2.8.5 |
| 보안 | JWT (`jjwt-api`, `jjwt-impl`, `jjwt-jackson`) | 0.11.5 |
| 데이터 | PostgreSQL JDBC Driver (runtime) | Spring Boot BOM 관리 (3.5.12) |
| 데이터 | Spring Boot Starter Data Redis (Spring Data Redis) | Spring Boot BOM 관리 (3.5.12) |
| 데이터 | MyBatis Spring Boot Starter | 3.0.4 |
| 유틸 | p6spy Spring Boot Starter | 1.10.0 |
| 유틸 | Guava / Commons CSV | 33.4.0-jre / 1.10.0 |
| 유틸 | Apache POI / POI-OOXML | Spring Boot BOM 관리 / 5.2.3 |
| 유틸 | Lombok | Spring Boot BOM 관리 (3.5.12) |
| 테스트 | Spring Boot Starter Test / Spring Security Test | Spring Boot BOM 관리 (3.5.12) |
| 테스트 | MyBatis Spring Boot Starter Test | 3.0.4 |
| 테스트 | JUnit Platform Launcher | Spring Boot BOM 관리 (3.5.12) |


## Feature List
| 기능 | 설명 | 관련 패키지 |
|---|---|---|
| Logging | TraceId 필터 + AOP 기반 요청/응답 로그 포맷팅 제공 | `scm.common.app.aop.LogFormatAop`, `scm.common.app.filter.LogTraceIdFilter`, `scm.common.app.util.LogFormatUtil` |
| Caching | Redis 캐시 조회/삭제 서비스와 캐시 비우기/evict 운영 API 제공 | `scm.common.app.cache`, `scm.common.biz.common.controller.CacheRestController` |
| Context Storage | Redis 기반 컨텍스트 key-value 저장/조회 API 제공 | `scm.common.app.context`, `scm.common.biz.context.controller.ContextController` |
| Spring Security + JWT | JWT 필터 체인, 인증/인가 규칙, 인증 실패/권한 오류 핸들러 구성 | `scm.common.app.config.SecurityConfig`, `scm.common.app.filter.JwtRequestFilter`, `scm.common.app.handler` |
| JWT Allowlist | 로그인 성공 토큰 Redis allowlist 등록 및 요청 시 allowlist 검증 | `scm.common.app.auth`, `scm.common.biz.user.service.UserService` |
| Exception Handling | 전역 예외 처리(검증 오류, 엔드포인트 미존재 등)와 표준 에러 응답 제공 | `scm.common.app.exception.GlobalExceptionHandler`, `scm.common.app.dto.ApiResponse` |
| Message Resource | `ErrorCode` 메시지를 `messages*.properties`와 Locale 기반으로 해석 | `scm.common.app.config.MessageConfig`, `scm.common.app.exception.ErrorCode`, `src/main/resources/messages*` |
| HTTP Client Wrapper | WebClient 기반 공통 HTTP 호출 래퍼(GET/POST/PUT/DELETE, 재시도/타임아웃 옵션) | `scm.common.app.http.HttpClient` |
| Pageable Resolver | 요청 파라미터를 `Pageable`로 변환하는 커스텀 Resolver 제공 | `scm.common.app.resolver.PageArgumentResolver`, `scm.common.app.config.WebConfig` |
| User API | 회원가입/로그인, 사용자 목록/상세/관리자 조회, 사용자 수정 API 제공 | `scm.common.biz.user.controller.UserRestController`, `scm.common.biz.user.service` |
| File Upload/Download | 정책 기반 파일 업로드/다운로드 및 선택적 DB 메타데이터 저장 지원 | `scm.common.biz.file.controller.FileController`, `scm.common.biz.file.service.FileService` |
| Log API | 로깅 동작 확인용 테스트 API 제공 | `scm.common.biz.log.controller.LogController` |
| Data Export | 사용자 데이터 CSV/Excel 다운로드 API 제공 | `scm.common.biz.export.controller.ExportController`, `scm.common.biz.util.FileExportUtil` |
| MyBatis Sample CRUD | `sample_mybatis_item` 대상 MyBatis 샘플 CRUD API 제공 | `scm.common.biz.sample`, `SampleRepositoryMybatis.xml`, `schema.sql` |

## Quick Start + Validation Commands
> 인프라 Docker 명령은 `docker-compose.yml`이 있는 **`skax-scm-sample` 루트**에서, 백엔드·검증 명령은 **`scm-common-service` 루트**에서 실행합니다.

### 1) Quick Start
간략 실행 순서(Windows 기준):
1. 인프라(PostgreSQL, Redis) 실행 — **`docker-compose.yml`이 있는 저장소 루트**(`skax-scm-sample`)에서 실행합니다.
```bash
docker compose up -d postgres redis
```
2. PostgreSQL 스키마 준비 — 백엔드가 `users` 시퀀스·테이블, `sample_mybatis_item` 등을 전제로 하므로 아래 **한 가지**를 선택합니다.
   - **방법 A (DBeaver 등 SQL 클라이언트)**  
     아래로 접속한 뒤, 이 모듈의 `src/main/resources/schema.sql` 내용을 **통째로 실행**합니다.  
     | 항목 | 값 |
     |---|---|
     | Host | `localhost` |
     | Port | `5432` |
     | Database | `scm` |
     | User | `postgres` |
     | Password | `changeit` |  
     (`docker-compose.yml`의 `postgres` 서비스 `environment`와 동일합니다.)
   - **방법 B (자동)**  
     별도 클라이언트 없이 3번에서 `--spring.profiles.active=local`로 기동하면, `application-local.yml`의 `spring.sql.init`에 따라 `schema.sql`이 적용됩니다. (2번 수동 작업 생략)
3. 로컬 터미널에서 백엔드 실행 — **`scm-common-service` 모듈 루트**에서 실행합니다. (저장소 기준 경로: `skax-scm-backend\common\scm-common-service`, `gradlew.bat`이 있는 디렉터리)
   - **방법 A**를 택한 경우 (이미 DB에 스키마 적용됨):
   ```bash
   .\gradlew.bat bootRun
   ```
   - **방법 B**를 택한 경우 (기동 시 스키마 자동 적용):
   ```bash
   .\gradlew.bat bootRun --args="--spring.profiles.active=local"
   ```

중지 방법:
- 백엔드 중지: 실행 중 터미널에서 `Ctrl + C`
- 인프라 중지 — 인프라 기동과 동일하게 `skax-scm-sample` 루트에서:
```bash
docker compose stop postgres redis
```

### 2) Validation Commands
Quick Start 실행 후 검증(Windows 기준):
1. 애플리케이션 헬스체크
```bash
curl http://localhost:8082/actuator/health
```
2. 단위 테스트 실행
```bash
.\gradlew.bat clean test
```

## [참고사항]
- 전체 서비스(Postgres, Redis, `scm-common-service(백앤드)`, `scm-admin-web(프론트앤드)`, `scm-api-gateway(게이트웨이이)`)를 한 번에 실행하려면 `docker-compose.yml`이 있는 `skax-scm-sample` 루트에서 아래 명령을 실행합니다.
```bash
docker compose up -d
```
- 소스 수정 후 변경사항을 이미지/컨테이너에 반영하려면 재빌드가 필요합니다.
```bash
docker compose up -d --build
```
- 특정 서비스만 재빌드하려면 서비스명을 지정합니다. (예: 백엔드만)
```bash
docker compose up -d --build scm-common-service
```

## Documents
- [가이드 목록](docs/guide.md)
