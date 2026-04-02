# 개발환경 가이드

## 기술 스택
- Java 21
- Spring Boot 3.5.12
- Spring Security
- JWT Token
- MyBatis
- PostgreSQL
- Redis

## Getting Started (간략)

### 필요 설치 프로그램
- [JDK 21 (Temurin)](https://adoptium.net/temurin/releases/)
- [Git](https://git-scm.com/download/win)
- [VSCode](https://code.visualstudio.com/)
- [Docker Desktop](https://www.docker.com/products/docker-desktop/)
- PostgreSQL에 수동으로 스키마를 적용할 때(실행 3번 방법 A): [DBeaver](https://dbeaver.io/download/) 등 SQL 클라이언트(선택)

권장 VSCode 확장:
- `Extension Pack for Java`
- `Lombok Annotations Support for VS Code`

### 실행
> 인프라 Docker 명령은 `docker-compose.yml`이 있는 **`skax-scm-sample` 루트**에서, 백엔드·Gradle 검증 명령은 **`scm-common-service` 루트**에서 실행합니다.

1. 소스 클론
```bash
git clone https://github.com/skccmygit/skax-scm-sample.git
```

2. 인프라(PostgreSQL, Redis) 실행 — **저장소 루트**(`skax-scm-sample`, `docker-compose.yml`이 있는 디렉터리)에서 실행합니다.
```bash
cd skax-scm-sample
docker compose up -d postgres redis
```

3. PostgreSQL 스키마 준비 — 백엔드가 `users` 시퀀스·테이블, `sample_mybatis_item` 등을 전제로 하므로 아래 **한 가지**를 선택합니다.
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
     별도 클라이언트 없이 4번에서 `--spring.profiles.active=local`로 기동하면, `application-local.yml`의 `spring.sql.init`에 따라 `schema.sql`이 적용됩니다. (3번 수동 작업 생략)

4. 백엔드 실행 — **`scm-common-service` 모듈 루트**로 이동합니다. (저장소 기준 경로: `skax-scm-backend\common\scm-common-service`, `gradlew.bat`이 있는 디렉터리)
```bash
cd skax-scm-sample/skax-scm-backend/common/scm-common-service
```
   - **방법 A**를 택한 경우 (이미 DB에 스키마 적용됨):
   ```bash
   .\gradlew.bat bootRun
   ```
   - **방법 B**를 택한 경우 (기동 시 스키마 자동 적용):
   ```bash
   .\gradlew.bat bootRun --args="--spring.profiles.active=local"
   ```

### 중지
- 백엔드: 실행 터미널에서 `Ctrl + C`
- DB/Redis — 인프라 기동과 동일하게 `skax-scm-sample` 루트에서:
```bash
cd skax-scm-sample
docker compose stop postgres redis
```

### 참고사항
- 기본 설치 가이드 (JDK, Git, VSCode) [링크](./110-install-basic-guide.md)
- SK 사내망 가이드 (SK ROOT 인증서 추가) [링크](./120-install-sk-guide.md)
- 전체 Docker Compose 실행/재빌드 참고 (`README.md`의 `[참고사항]`) [링크](../README.md#참고사항)

---

### Navigation
- [가이드 목록으로 돌아가기](guide.md)
