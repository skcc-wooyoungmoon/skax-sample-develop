# 예외 처리 가이드
> 이 가이드는 시스템에서 발생하는 예외를 일관되게 처리하며,  
> 사용자 정의 예외와 에러 코드를 활용해 가독성과 유지보수를 높이고,  
> **Spring의 `MessageSource`를 통해 메시지 키·파라미터 바인딩**을 지원하는 방식을 정리합니다.  
> **본 문서는 `scm-common-service` 현행 소스 기준입니다.**

---
## 목차

1. [예외 처리 구성 요소](#1-예외-처리-구성-요소)
   - [1.1 ExceptionDto](#11-exceptiondto)
   - [1.2 ErrorCode (Enum)](#12-errorcode-enum)
   - [1.3 message.properties](#13-messageproperties)
   - [1.4 사용자 정의 예외 CustomException](#14-사용자-정의-예외-customexception)
2. [전역 예외 처리](#2-전역-예외-처리)
   - [2.1 GlobalExceptionHandler](#21-globalexceptionhandler)
   - [2.2 ApiResponse와 HTTP 상태](#22-apiresponse와-http-상태)
3. [기타 참고사항](#3-기타-참고사항)
   - [3.1 MessageConfig](#31-messageconfig)
   - [3.2 메시지 파라미터 바인딩](#32-메시지-파라미터-바인딩)
   - [3.3 테스트 코드](#33-테스트-코드)
   - [3.4 Spring Security와의 연동](#34-spring-security와의-연동)

---

## 1. 예외 처리 구성 요소

### 1.1 `ExceptionDto`
발생한 예외를 API 응답 본문에 담기 위한 표준 객체입니다. `ApiResponse`의 `error` 필드 타입입니다.

**패키지:** `scm.common.app.dto.ExceptionDto`

- `code`: **`Integer`** — `CustomException`이면 `ErrorCode`의 숫자 코드, 그 외 예외이면 예외 클래스 단순 이름의 `hashCode()` (구분용)
- `message`: 사용자에게 노출할 메시지 문자열
- `CustomException` / 일반 `Exception` 생성자로 DTO를 만들 수 있습니다.

```java
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionDto {

    private Integer code;
    private String message;

    public ExceptionDto(CustomException e) {
        this.code = e.getErrorCode().getCode();
        this.message = e.getMessage();
    }

    public ExceptionDto(Exception e) {
        this.code = e.getClass().getSimpleName().hashCode();
        this.message = e.getMessage();
    }
}
```

**응답 JSON 예시** (`ApiResponse.fail(CustomException)`):

```json
{
  "success": false,
  "data": null,
  "error": {
    "code": 90005,
    "message": "유효하지 않은 요청입니다. [필드: codeName, 오류 메시지: 빈 값을 허용하지 않습니다]"
  }
}
```

---

### 1.2 `ErrorCode` (Enum)
시스템 공통 에러 코드는 **`scm.common.app.exception.ErrorCode`** 에서 `enum`으로 관리합니다.  
각 상수는 **정수 코드**, **`HttpStatus`**, **`messages*.properties`의 키(`messageKey`)** 를 가집니다.

| 필드 | 타입 | 설명 |
|:---:|:---:|:---|
| code | int | API·로그에서 구분하는 숫자 코드 |
| status | HttpStatus | `ApiResponse` 실패 시 사용할 HTTP 상태 (`CustomException` 경로) |
| messageKey | String | `messages.properties` 등의 메시지 키 |

**현행 상수 목록 (소스와 동일):**

| Enum 상수 | code | HttpStatus | messageKey |
|-----------|------|------------|------------|
| `NOT_FOUND_ELEMENT` | 10001 | NOT_FOUND | `error.notFoundElement` |
| `EXIST_ELEMENT` | 10002 | BAD_REQUEST | `error.existElement` |
| `NOT_MATCHED_PASSWORD` | 10003 | BAD_REQUEST | `error.notMatchedPassword` |
| `NOT_FOUND_FILE` | 10004 | NOT_FOUND | `error.notFoundFile` |
| `NOT_FOUND_END_POINT` | 90001 | NOT_FOUND | `error.notFoundEndPoint` |
| `INTERNAL_SERVER_ERROR` | 90002 | INTERNAL_SERVER_ERROR | `error.internalServerError` |
| `UNAUTHORIZED` | 90003 | UNAUTHORIZED | `error.unauthorized` |
| `ACCESS_DENIED` | 90004 | FORBIDDEN | `error.accessDenied` |
| `INVALID_REQUEST` | 90005 | BAD_REQUEST | `error.invalidRequest` |
| `JWT_EXPIRED_TOKEN` | 90006 | UNAUTHORIZED | `error.jwtExpiredToken` |
| `JWT_INVALID_SIGNATURE` | 90007 | UNAUTHORIZED | `error.jwtInvalidSignature` |
| `JWT_INVALID` | 90008 | UNAUTHORIZED | `error.jwtInvalid` |
| `JWT_NOT_FOUND` | 90009 | NOT_FOUND | `error.jwtNotFound` |

`getMessage(Object... args)`는 `MessageSource`가 `MessageConfig`에서 주입된 뒤에만 동작합니다. 미주입 시 `IllegalStateException`을 던집니다.

```java
public String getMessage(Object... args) {
    if (messageSource == null) {
        throw new IllegalStateException("MessageSource has not been set!");
    }
    return messageSource.getMessage(messageKey, args, LocaleContextHolder.getLocale());
}
```

JWT 관련 코드는 `JwtUtil`, `JwtRequestFilter` 등에서 `CustomException`으로 사용됩니다.

---

### 1.3 `messages.properties`
**위치:** `src/main/resources/messages.properties`, `messages_en.properties`  
검증 메시지 등과 함께 `error.*` 키를 정의합니다. `{0}`, `{1}` 형태로 인자를 둡니다.

**`messages.properties` (발췌):**
```properties
javax.validation.constraints.NotNull.message=빈 값을 허용하지 않습니다
error.notFoundElement=요소가 존재하지 않습니다.
error.existElement=이미 존재하는 요소입니다.
error.invalidRequest=유효하지 않은 요청입니다. [필드: {0}, 오류 메시지: {1}]
error.jwtExpiredToken=만료된 토큰 입니다.
error.jwtInvalidSignature=서명값이 유효하지 않습니다.
error.jwtInvalid=토큰이 유효하지 않습니다.
error.jwtNotFound=헤더에 토큰이 존재하지 않습니다.
```

**`messages_en.properties`:** 동일 키에 영문 메시지를 둡니다. (`Locale`에 따라 선택)

---

### 1.4 사용자 정의 예외 `CustomException`
비즈니스·인프라 코드에서는 **`scm.common.app.exception.CustomException`** 을 사용합니다.  
`ErrorCode`와 가변 인자 `Object... args`를 넘기면 `ErrorCode.getMessage(args)`로 메시지가 만들어지고, `RuntimeException`의 메시지로도 설정됩니다.

```java
@Getter
public class CustomException extends RuntimeException {
    public final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode, Object... args) {
        super(errorCode.getMessage(args));
        this.errorCode = errorCode;
    }

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
```

> **참고:** 가변 인자 `args`는 **메시지 포맷용**으로만 쓰이며, 예외 객체에 별도 필드로 저장되지는 않습니다.

---

## 2. 전역 예외 처리

### 2.1 `GlobalExceptionHandler`
**클래스:** `scm.common.app.exception.GlobalExceptionHandler` (`@RestControllerAdvice`)

| 처리 대상 | 동작 요약 |
|-----------|-----------|
| `MethodArgumentNotValidException` | 첫 필드 오류 기준으로 `INVALID_REQUEST` + 필드명·기본 메시지 인자 |
| `NoHandlerFoundException`, `HttpRequestMethodNotSupportedException` | `NOT_FOUND_END_POINT` |
| `CustomException` | `ApiResponse.fail(e)` |
| 그 외 `Exception` | `ApiResponse.fail(e)` → 내부적으로 일반 `ExceptionDto` (HTTP 500) |

```java
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ApiResponse<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        final String field = Objects.requireNonNull(e.getBindingResult().getFieldError()).getField();
        final String message = Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage();
        log.error("handleMethodArgumentNotValidException() in GlobalExceptionHandler throw MethodArgumentNotValidException : {}", e.getMessage());
        return ApiResponse.fail(new CustomException(ErrorCode.INVALID_REQUEST, field, message));
    }

    @ExceptionHandler(value = {NoHandlerFoundException.class, HttpRequestMethodNotSupportedException.class})
    public ApiResponse<?> handleNoPageFoundException(Exception e) {
        log.error("GlobalExceptionHandler catch NoHandlerFoundException : {}", e.getMessage());
        return ApiResponse.fail(new CustomException(ErrorCode.NOT_FOUND_END_POINT));
    }

    @ExceptionHandler(value = {CustomException.class})
    public ApiResponse<?> handleCustomException(CustomException e) {
        log.error("handleCustomException() in GlobalExceptionHandler throw CustomException : {}", e.getMessage());
        return ApiResponse.fail(e);
    }

    @ExceptionHandler(value = {Exception.class})
    public ApiResponse<?> handleException(Exception e) {
        return ApiResponse.fail(e);
    }
}
```

> `NoHandlerFoundException`을 쓰려면 Spring MVC에서 404를 예외로 던지도록 설정이 필요할 수 있습니다. (프로젝트 `application` 설정 참고)

---

### 2.2 `ApiResponse`와 HTTP 상태
**클래스:** `scm.common.app.dto.ApiResponse`

- `ApiResponse.fail(CustomException e)`  
  - 내부 `status`: `e.getErrorCode().getStatus()`  
  - `error`: `new ExceptionDto(e)`
- `ApiResponse.fail(Exception e)` (일반 예외)  
  - 내부 `status`: `HttpStatus.INTERNAL_SERVER_ERROR`  
  - `error`: `new ExceptionDto(e)` (`code`는 예외 클래스 단순 이름의 해시)
- `ApiResponse.fail(HttpStatus, ExceptionDto)` 오버로드도 있습니다. (예: 파일 업로드 실패 등)

`status` 필드는 **`@JsonIgnore`** 가 붙어 있어 JSON 본문에는 보통 나가지 않습니다. 실제 **HTTP 응답 코드**를 에러 코드와 맞추려면 `ResponseEntity`·필터 등 별도 패턴을 쓰는지 코드베이스를 확인하세요.

---

## 3. 기타 참고사항

### 3.1 `MessageConfig`
**클래스:** `scm.common.app.config.MessageConfig`

- `@PostConstruct`에서 `ReloadableResourceBundleMessageSource`를 만들고 `basename`을 `classpath:messages`로 지정합니다.
- `ErrorCode.setMessageSource(messageSource)`로 enum과 연결합니다.
- 실제 코드는 **`jakarta.annotation.PostConstruct`** 를 사용합니다.

```java
@Configuration
public class MessageConfig {

    @PostConstruct
    public void init() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        ErrorCode.setMessageSource(messageSource);
    }
}
```

---

### 3.2 메시지 파라미터 바인딩
`error.invalidRequest=유효하지 않은 요청입니다. [필드: {0}, 오류 메시지: {1}]` 처럼 정의한 뒤,  
`new CustomException(ErrorCode.INVALID_REQUEST, field, message)` 로 `{0}`, `{1}`에 값을 넘깁니다.  
`GlobalExceptionHandler`의 `@Valid` 실패 처리와 동일한 패턴입니다.

---

### 3.3 테스트 코드
**위치:** `src/test/java/scm/common/app/exception/CustomExceptionTest.java`

- `@BeforeAll`에서 `new MessageConfig().init()`으로 `MessageSource`를 초기화합니다.
- 한국어/영문 로케일별 메시지, `INVALID_REQUEST`에 인자 두 개를 넘기는 경우 등을 검증합니다.

클래스 이름은 `CustomExceptionTest`이며, 예전 문서에 있던 `CustomeExceptionTest` 철자는 실제 클래스명과 다릅니다.

---

### 3.4 Spring Security와의 연동
인증/인가 실패 시 MVC `@ControllerAdvice`만으로 잡히지 않는 경로가 있을 수 있습니다.  
`CustomAuthenticationEntryPoint`, `CustomAccessDeniedHandler` 등에서 **`ApiResponse` + `CustomException` + `ErrorCode`** 조합으로 JSON 응답을 쓰는 패턴이 있습니다. (상세는 해당 클래스 참고)

---

### 🔙 Navigation
- [가이드 목록으로 돌아가기](guide.md)
