# Swagger(OpenAPI) 활용 가이드
> 본 가이드는 `springdoc-openapi` 기반 Swagger 문서 접근 경로와 기본 설정을 설명합니다.

---

## 1. 목적
- REST API를 브라우저에서 쉽게 확인하고 테스트할 수 있도록 Swagger UI를 제공합니다.
- OpenAPI 스펙(`/common/v3/api-docs`)을 통해 API 문서 자동화를 지원합니다.

## 2. 적용 내용

### 2.1 의존성
- `build.gradle`에 아래 의존성이 추가되어야 합니다.

```gradle
implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.5'
```

### 2.2 접근 URL
- Swagger UI: `http://localhost:8082/common/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8082/common/v3/api-docs`

### 2.3 application.yml 설정
- 경로를 명시적으로 지정하여 운영 시에도 고정 경로로 접근하도록 권장합니다.

```yaml
springdoc:
  api-docs:
    path: /common/v3/api-docs
  swagger-ui:
    path: /common/swagger-ui
```

### 2.4 보안 화이트리스트
- Spring Security 사용 시 아래 경로를 허용해야 Swagger 접근이 가능합니다.
- 기준 파일: `SecurityConfig.java`

```java
"/common/v3/api-docs/**",
"/common/swagger-ui/**"
```

### 2.5 OpenAPI 메타정보
- API 문서의 제목/버전/설명은 설정 클래스로 관리합니다.
- 기준 파일: `OpenApiConfig.java`

```java
new OpenAPI()
    .info(new Info()
        .title("scm-common-service API")
        .version("v1.0.0")
        .description("SCM Common Service REST API documentation"));
```

### 2.6 Authorize(Bearer JWT) 설정
- Swagger UI 우측 상단 `Authorize` 버튼에서 토큰을 1회 입력하면 이후 API 호출에 공통 적용됩니다.
- 전역 Security Requirement가 설정되어 있어 보호 API 테스트 시 매번 헤더를 수동 입력할 필요가 없습니다.

```text
Authorize Value 예시:
- eyJhbGciOi... (권장)
- Bearer eyJhbGciOi... (환경에 따라 허용)
```

### 2.7 전역 Security Requirement 설정
- `OpenApiConfig.java`에 아래 설정이 적용되어 있습니다.
- 설정 시 Swagger 화면에서 토큰을 1회 입력하면 보호 API 전반에 자동 반영됩니다.

```java
private static final String SECURITY_SCHEME_NAME = "bearerAuth";

new OpenAPI()
    .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
    .components(new Components().addSecuritySchemes(
        SECURITY_SCHEME_NAME,
        new SecurityScheme()
            .name("Authorization")
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")
    ));
```

---

## 3. 확인 방법
아래 순서대로 진행하면 Swagger에서 로그인부터 보호 API 호출까지 한 번에 확인할 수 있습니다.

### 3.1 사전 조건
- `scm-common-service`가 기동 가능한 상태(로컬 DB·프로필 등 [개발 환경 가이드](100-developer-environment-guide.md) 참고).
- 기본 HTTP 포트는 `application.yml`의 `server.port` 기준 **`8082`** 입니다. (`SERVER_PORT` 환경 변수로 변경한 경우 그 포트를 사용합니다.)
- Swagger·OpenAPI 경로는 `springdoc` 설정에 따라 **`/common` 접두**가 붙습니다. (예: Swagger UI는 `…/common/swagger-ui/…`)

### 3.2 애플리케이션 기동
- IDE에서 `SkaxSpringApplication` 실행, 또는 모듈 루트에서 `./gradlew bootRun` 등으로 기동합니다.
- 콘솔에 오류 없이 서버가 떴는지 확인합니다.

### 3.3 Swagger UI 열기
1. 브라우저 주소창에 아래 URL을 입력합니다. (포트가 8082가 아니면 해당 포트로 바꿉니다.)
   - **Swagger UI**: `http://localhost:8082/common/swagger-ui/index.html`
2. 화면에 API 그룹(태그)과 엔드포인트 목록이 보이면 정상입니다.
3. (선택) OpenAPI JSON을 직접 보려면: `http://localhost:8082/common/v3/api-docs`

### 3.4 로그인으로 Access Token 발급
1. 목록에서 **`POST /api/v1/common/users/authenticate`** 를 찾습니다.
2. **Try it out** → **Request body**에 JSON을 입력합니다. 필드명은 요청 DTO와 동일해야 합니다.

```json
{
  "email": "가입한_사용자_이메일",
  "password": "비밀번호"
}
```

3. **Execute**를 누릅니다.
4. **응답 본문**은 `ApiResponse` 형태이며, 성공 시 `data` 안에 JWT가 들어 있습니다. 예시 구조는 다음과 같습니다.

```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOi..."
  }
}
```

5. **`data.accessToken` 값만 복사**해 둡니다. (아직 DB에 사용자가 없다면 먼저 **`POST /api/v1/common/users/signup`** 으로 가입한 뒤 동일 이메일·비밀번호로 로그인합니다.)

### 3.5 Authorize에 토큰 등록
1. Swagger UI **우측 상단**의 **`Authorize`** 버튼을 클릭합니다.
2. `bearerAuth`(또는 `Authorization`) 입력란에 **복사한 JWT 문자열**을 붙여 넣습니다.  
   - 가이드 [2.6](#26-authorizebearer-jwt-설정)과 같이 **`Bearer ` 접두 없이** 토큰만 넣어도 되고, 환경에 따라 `Bearer eyJ...` 형식도 동작할 수 있습니다.
3. **Authorize** → **Close**로 창을 닫습니다. 이후 같은 브라우저 탭에서 **Try it out**으로 호출하는 API 요청에 자동으로 `Authorization` 헤더가 붙습니다.

### 3.6 보호 API 호출 확인
1. 인증이 필요한 API를 하나 고릅니다. 예: **`GET /api/v1/common/users`** (목록 조회).
2. **Try it out** → **Execute**합니다.
3. **401**이 나오면 토큰 만료·복사 오류·Authorize 미입력 여부를 확인합니다. **200**과 응답 본문이 오면 Swagger + JWT 연동이 정상입니다.

### 3.7 자주 막히는 경우
- **Swagger 페이지가 401/403**: `SecurityConfig`에 `/common/v3/api-docs/**`, `/common/swagger-ui/**` 가 허용돼 있는지 확인합니다.
- **로그인 401/실패**: 이메일·비밀번호 오타, 미가입 계정, DB 미기동 등을 점검합니다.
- **포트 불일치**: 실행 로그의 `Tomcat started on port(s): …` 와 브라우저 URL의 포트가 같은지 확인합니다.

---

## 4. 참고사항
- 서버 포트가 변경되면 Swagger URL의 포트도 동일하게 변경됩니다.
- 배포 환경에서 Swagger 노출 여부는 보안 정책에 따라 제한하는 것을 권장합니다.
- 토큰 없이 보호 API를 호출하면 인증 오류 응답이 반환됩니다.
  - 예시: `"헤더에 토큰이 존재하지 않습니다."`

---

### 🔙 Navigation
- [가이드 목록으로 돌아가기](guide.md)
