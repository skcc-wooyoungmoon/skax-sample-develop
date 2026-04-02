# Java Spring 영속성 계층 가이드
> Spring 기반 애플리케이션에서 영속성 계층(Persistence Layer)은 데이터베이스와의 상호작용을 담당하며, 비즈니스 로직과 데이터를 분리하는 중요한 역할을 합니다.  
> 올바른 영속성 계층 설계는 유지보수성과 확장성을 높이는 기반이 됩니다.  
> **본 문서는 `scm-common-service` 현행 소스를 기준으로 작성합니다.** 이 모듈은 **MyBatis**로 SQL을 매핑하며, **JPA·QueryDSL은 사용하지 않습니다.**

---
## 목차

1. [기본 원칙 및 설계 철학](#1-기본-원칙-및-설계-철학)
   - [1.1 영속성 계층의 기본 역할](#11-영속성-계층의-기본-역할)
   - [1.2 Repository 패턴과 도메인 객체 분리](#12-repository-패턴과-도메인-객체-분리)
   - [1.3 도메인 Model과 MyBatis DTO의 역할 분리](#13-도메인-model과-mybatis-dto의-역할-분리)
   - [1.4 영속성 계층의 핵심 설계 원칙](#14-영속성-계층의-핵심-설계-원칙)
2. [Model 설계 규칙](#2-model-설계-규칙)
   - [2.1 Model의 역할](#21-model의-역할)
   - [2.2 설계 예시 (`User`)](#22-설계-예시-user)
3. [MyBatis DTO와 DB 매핑](#3-mybatis-dto와-db-매핑)
   - [3.1 DTO의 역할](#31-dto의-역할)
   - [3.2 설계 예시 (`UserDto`)](#32-설계-예시-userdto)
4. [Repository 설계 규칙 (Port + MyBatis)](#4-repository-설계-규칙-port--mybatis)
   - [4.1 Repository Port의 철학과 역할](#41-repository-port의-철학과-역할)
   - [4.2 설계 원칙](#42-설계-원칙)
   - [4.3 설계 예시](#43-설계-예시)
   - [4.4 서비스 계층에서의 사용](#44-서비스-계층에서의-사용)
   - [4.5 구현체 등록 방식](#45-구현체-등록-방식)
   - [4.6 해당 설계의 장점](#46-해당-설계의-장점)
5. [MyBatis·프로젝트 설정 참고](#5-mybatis프로젝트-설정-참고)

---

## 1. 기본 원칙 및 설계 철학

### **1.1 영속성 계층의 기본 역할**
영속성 계층은 데이터의 **저장, 조회, 수정, 삭제** 등의 CRUD 작업을 수행하며,

비즈니스 로직의 구현과 별개로 데이터 접근에 관한 세부 사항을 처리합니다.  

이를 통해 도메인 로직이 영속성 구현 기술(MyBatis 등) 세부에 직접 종속되지 않도록 합니다.

### **1.2 Repository 패턴과 도메인 객체 분리**
Repository는 애플리케이션의 **도메인 객체(Model)의 저장소** 역할을 수행합니다. 이 패턴은 데이터 접근의 추상화를 통해 코드의 응집도를 높이고, 영속성 기술 변경에 따른 코드를 격리할 수 있도록 설계됩니다.

#### Repository 설계의 기본 원칙:
- **비즈니스(서비스) 계층에서 사용할 `XXXRepositoryPort` 인터페이스**를 정의합니다.
- 구현체는 **MyBatis Mapper 인터페이스 + XML** 및 **`XXXRepositoryPortMybatisImpl`** 등으로 구성됩니다.
- 도메인 `Model`은 MyBatis Mapper 인터페이스나 XML에 직접 의존하지 않습니다.

### **1.3 도메인 Model과 MyBatis DTO의 역할 분리**

본 프로젝트는 **JPA `@Entity`를 사용하지 않습니다.** DB 테이블과의 매핑은 **MyBatis Mapper XML**과 **`UserDto`(등)**, **`SampleItemDto`(등)** 같은 **영속 전용 DTO**로 처리합니다.

#### **MyBatis DTO(예: `UserDto`)의 역할**
- Mapper 인터페이스·XML의 `resultType` / `parameterType`으로 사용됩니다.
- 테이블 컬럼과 필드가 1:1에 가깝게 매핑되며, `application.yml`의 `map-underscore-to-camel-case`로 스네이크 컬럼명과 자바 필드를 연결합니다.
- 도메인 `User`로 변환할 때는 `UserDto.from(User)`, `UserDto.toModel()`을 사용합니다.

#### **Model(도메인 모델, 예: `User`)의 역할**
- 비즈니스 규칙(가입 시 생성, 상태 변경 등)을 담습니다.
- MyBatis·JDBC 어노테이션에 의존하지 않는 **순수 POJO**입니다.

#### **변환**
- DTO ↔ Model 변환은 DTO 쪽 정적 메서드(`from` / `toModel`)로 두는 패턴을 사용합니다. (`UserDto`, `SampleItemDto` 참고)

### **1.4 영속성 계층의 핵심 설계 원칙**
#### **1) 기술 종속성 제거**
도메인 모델은 MyBatis Mapper 인터페이스에 의존하지 않도록 설계합니다. 서비스는 **`RepositoryPort`만** 알게 합니다.

#### **2) Repository Port 활용**
- `scm.common.biz.user.service.port.UserRepositoryPort`
- `scm.common.biz.sample.service.port.SampleRepositoryPort`
- `scm.common.biz.file.service.port.FileRepositoryPort`  
등으로 **유스케이스에 필요한 연산만** 노출합니다.

#### **3) SRP(단일 책임 원칙)**
- Mapper: SQL 실행과 DTO 입출력.
- `XXXRepositoryPortMybatisImpl`: DTO ↔ Model 변환, 트랜잭션 경계에서의 호출 조합.
- `Service`: 유스케이스·도메인 규칙.

#### **4) 테스트 가능성**
Port 인터페이스를 두면 서비스 단위 테스트에서 구현체를 대체하기 쉽습니다.

---

## 2. Model 설계 규칙

### 2.1 Model의 역할
- 도메인 규칙을 표현하는 **POJO**입니다.
- MyBatis·Spring Data 등 영속 프레임워크 어노테이션을 붙이지 않습니다.

### 2.2 설계 예시 (`User`)
`scm.common.biz.user.domain.User`는 가입·상태 변경 등의 팩토리/갱신 메서드를 제공합니다.

```java
// 개념 요약 — 실제 코드는 패키지 scm.common.biz.user.domain.User 참고
@Getter
public class User {
    private final Long id;
    private final String email;
    private final String password;
    private final String username;
    private final UserStatus status;
    private final LocalDateTime createdDate;
    private final LocalDateTime lastModifiedDate;

    public static User from(UserCreate userCreate, PasswordEncoder passwordEncoder) { /* 가입 */ }
    public User updateStatus(UserStatus requestStatus) { /* 상태 변경 */ }
    public User updateUser(User updateUser, PasswordEncoder passwordEncoder) { /* 정보 수정 */ }
}
```

샘플 도메인 `SampleItem`은 `scm.common.biz.sample.domain.SampleItem`에 정의되어 있습니다.

---

## 3. MyBatis DTO와 DB 매핑

### 3.1 DTO의 역할
- Mapper 인터페이스와 XML이 참조하는 **데이터 홀더**입니다.
- 도메인 `Model`과 분리하여, SQL 컬럼 구조 변경 시 영향 범위를 DTO·XML에 모을 수 있습니다.

### 3.2 설계 예시 (`UserDto`)
`scm.common.biz.user.infrastructure.mybatis.UserDto`:

```java
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String email;
    private String password;
    private String username;
    private UserStatus status;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;

    public static UserDto from(User user) { /* ... */ }
    public User toModel() { /* ... */ }
}
```

`SampleItemDto`는 `scm.common.biz.sample.infrastructure.mybatis.SampleItemDto`를 참고합니다.

---

## 4. Repository 설계 규칙 (Port + MyBatis)

### 4.1 Repository Port의 철학과 역할
- **`XXXRepositoryPort`**: 서비스가 의존하는 **도메인 중심 인터페이스** (`Model` 입출력).
- **`@Mapper` 인터페이스**: MyBatis가 구현체를 생성 (`UserRepositoryMybatis` 등).
- **`XXXRepositoryPortMybatisImpl`**: Port를 구현하고, Mapper를 호출하며 DTO ↔ Model 변환을 수행합니다.

### 4.2 설계 원칙
1. Port에는 `Optional<User>`, `Page<User>`, `List<SampleItem>`처럼 **도메인 타입**만 노출합니다.
2. Mapper 메서드는 **DTO**를 다루고, XML은 `namespace`를 Mapper FQCN과 맞춥니다.  
   예: `src/main/resources/mybatis/mapper/UserRepositoryMybatis.xml` → `namespace="...UserRepositoryMybatis"`.
3. 삽입/수정 시 감사 일시는 **`AuditingInterceptor`**(INSERT/UPDATE 시 `created_date`·`last_modified_date`)와 구현체에서의 보완 로직이 함께 쓰일 수 있습니다.

### 4.3 설계 예시

#### Port (도메인 API)
`scm.common.biz.user.service.port.UserRepositoryPort`:

```java
public interface UserRepositoryPort {
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    User save(User user);
    List<User> findAll();
    Page<User> findAll(Pageable pageable);
    Page<User> findAdminUsers(Pageable pageable, List<Long> userIds);
    User updateStatus(User user);
}
```

#### MyBatis Mapper 인터페이스
`scm.common.biz.user.infrastructure.mybatis.UserRepositoryMybatis`:

```java
@Mapper
public interface UserRepositoryMybatis {
    Optional<UserDto> findById(@Param("id") Long id);
    Optional<UserDto> findByEmail(@Param("email") String email);
    int save(UserDto user);
    int update(UserDto user);
    int updateStatus(UserDto user);
    List<UserDto> findAll();
    List<UserDto> findAllWithPageable(@Param("offset") long offset, @Param("pageSize") int pageSize);
    long countAll();
    // ...
}
```

#### Port 구현체 (요지)
`UserRepositoryPortMybatisImpl`은 Mapper 결과를 `UserDto::toModel`로 변환하고, `save` 시 신규/수기를 구분해 `save` 또는 `update`를 호출합니다. 페이징은 `findAllWithPageable` + `countAll` 후 `PageImpl`로 조립합니다.

**샘플 도메인**은 `SampleRepositoryPort` / `SampleRepositoryMybatis` / `SampleRepositoryPortMybatisImpl` / `SampleRepositoryMybatis.xml` 조합을 동일한 패턴으로 따릅니다.

### 4.4 서비스 계층에서의 사용
`UserService`는 `UserRepositoryPort`만 주입받습니다. (`scm.common.biz.user.service.UserService`)

```java
@Service
@RequiredArgsConstructor
public class UserService implements UserServicePort {
    private final UserRepositoryPort userRepositoryPort;
    // ...

    @Transactional
    public User signUp(UserCreate userCreate) {
        checkUserExistByEmail(userCreate.getEmail());
        User model = User.from(userCreate, passwordEncoder);
        return userRepositoryPort.save(model);
    }
}
```

### 4.5 구현체 등록 방식
- **`UserRepositoryPort`에 대한 구현체는 현재 `UserRepositoryPortMybatisImpl` 하나**입니다. Spring이 `@Repository` 빈을 등록하고, **생성자 주입으로 단일 구현체가 `UserRepositoryPort`에 매핑**됩니다.
- **`AppConfig`**(`scm.common.app.config.AppConfig`)에는 `PasswordEncoder`, 로깅 필터 등만 정의되어 있으며, **Repository 구현체를 수동 `@Bean`으로 고르는 코드는 없습니다.** 

### 4.6 해당 설계의 장점
1. **서비스는 Port만 알면 되어** Mapper XML 변경이 비즈니스 규칙과 분리됩니다.
2. **SQL은 XML에서 명시**되어 복잡 조회·튜닝에 유리합니다.
3. **도메인 모델이 영속 기술에 묶이지 않습니다.**

---

## 5. MyBatis·프로젝트 설정 참고

### 5.1 설정 (`application.yml`)
```yaml
mybatis:
  mapper-locations: classpath:mybatis/mapper/*.xml
  configuration:
    map-underscore-to-camel-case: true
```
- 매퍼 XML은 `src/main/resources/mybatis/mapper/` 아래에 둡니다. (예: `UserRepositoryMybatis.xml`, `SampleRepositoryMybatis.xml`)

### 5.2 Mapper 스캔
`SkaxSpringApplication`에 다음이 정의되어 있습니다.

```java
@MapperScan(value = "scm.common", annotationClass = Mapper.class)
```
- `org.apache.ibatis.annotations.Mapper`가 붙은 인터페이스가 스캔 대상입니다.

### 5.3 감사(Audit) 필드
`scm.common.biz.common.infrastructure.mybatis.AuditingInterceptor`가 MyBatis `Executor.update`를 가로채어 INSERT 시 `created_date`·`last_modified_date`, UPDATE 시 `last_modified_date`를 채울 수 있습니다. DTO/엔티티에 해당 필드명이 있어야 동작합니다.

### 5.4 트랜잭션
- `@Transactional`은 서비스(`UserService`, `SampleService` 등)와 필요 시 **`UserRepositoryPortMybatisImpl`** 등 구현체에 사용됩니다.

### 5.5 SQL 로깅 (p6spy)
JDBC 레벨 로깅은 `decorator.datasource.p6spy` 설정과 `CustomP6spySqlFormat` 등을 사용합니다. (Hibernate 전용이 아닌 **JDBC/MyBatis 공통** 경로입니다.)

### 5.6 현행 코드 위치 요약
| 구분 | 예시 패키지·파일 |
|------|------------------|
| Port | `...user.service.port.UserRepositoryPort`, `...sample.service.port.SampleRepositoryPort` |
| Mapper IF | `...user.infrastructure.mybatis.UserRepositoryMybatis` |
| Port 구현 | `...user.infrastructure.UserRepositoryPortMybatisImpl` |
| Mapper XML | `resources/mybatis/mapper/UserRepositoryMybatis.xml` |
| 도메인 | `...user.domain.User`, `...sample.domain.SampleItem` |
| DTO | `...user.infrastructure.mybatis.UserDto`, `...sample.infrastructure.mybatis.SampleItemDto` |

---

### 🔙 Navigation
- [가이드 목록으로 돌아가기](guide.md)
