# Java Naming Rule 가이드
> Java 애플리케이션 개발 시 가독성과 유지보수성을 높이기 위해 일관된 Naming Rule을 적용하는 것이 중요합니다.
> 본 가이드는 Naming Rule의 표준을 정의하며, 이를 통해 협업 시 코드 퀄리티를 향상시키는 데 도움을 제공합니다.

---
## 목차
1. [Naming 표기법](#1-naming-표기법)  
   1.1 [기본 Naming Rule](#11-기본-naming-rule)  
   1.2 [특수 약어 규칙](#12-특수-약어-규칙)

2. [Java Naming 규칙](#2-java-naming-규칙)  
   2.1 [Package 명명 규칙](#21-package-명명-규칙)  
   2.2 [Method 명명 규칙](#22-method-명명-규칙)  
   2.3 [변수 명명 규칙](#23-변수-명명-규칙)  
   2.4 [상수(Constant) 명명 규칙](#24-상수constant-명명-규칙)  
   2.5 [SQL ID 명명 규칙](#25-sql-id-명명-규칙)  
   2.5.1 [MyBatis 기반 명명 규칙](#251-mybatis-기반-명명-규칙)  
   2.5.2 [금지 사항](#252-금지-사항)  
   2.5.3 [권고 사항](#253-권고-사항)

3. [추가 권장사항](#3-추가-권장사항)

--- 
## **1. Naming 표기법**

### 1.1 기본 Naming Rule
- **이해 가능한 Full English Description 사용**: 이름만 보고 역할을 명확히 알 수 있어야 합니다.
- **CamelCase 또는 Snake_Case 사용 규칙**에 따라 일관되게 작성합니다.
- **두 단어가 조합될 경우**: 두 번째 단어의 첫 문자를 대문자로 작성합니다.  
  예: `getCodeList`, `toModelWithChild`
- **이니셜이나 약어는 이미 널리 사용되는 형태를 따른다**.  
  예: `HTML`, `URL`, `DAO`는 그대로 대문자로 작성.
- **길이는 30자를 넘기지 않는다**:
    - 클래스, 인터페이스, 메서드, 변수, 상수 및 파일 이름은 가독성을 위해 30자 이하로 작성.
    - 예: `findAllWithPageable`(O), `findAllUserListWithCurrentLoginStatus`(X)
- Java 예약어(`final`, `class`, `void` 등)를 사용하지 않습니다.
  - 예: `class`(X), `userClassName`(O)

---
### 1.2 특수 약어 규칙
- 범용 약어(`URL`, `HTML` 등)는 대문자로 유지합니다.
- 특수 약어가 단어 사이에 혼합될 경우 일반적인 CamelCase를 따릅니다.  
  예: `parseHTML`, `encodeURL`

---

## **2. Java Naming 규칙**

### 2.1 Package 명명 규칙
- Package 명은 반드시 **소문자**만 사용합니다.
- Package 구조는 계층적으로 설계하여 모듈화를 명확히 나타냅니다.  
  예: `scm.common.app.config`, `scm.common.biz.user.service`
- 각 단어는 2~15자 내외의 영문 소문자로 작성합니다.
- 업무영역(비즈니스 도메인)과 기술 영역을 나눠 상세하게 설계합니다.

#### **Package 레벨 구조 예시**

| Level  | 명칭             | 설명                     |
|--------|------------------|--------------------------|
| 1      | scm.common.app   | 공통/프레임워크 영역       |
| 1      | scm.common.biz   | 업무(도메인) 영역          |
| 2      | controller       | Web Controller 관련       |
| 2      | service          | Service 로직 영역         |
| 2      | domain           | 도메인 모델               |
| 2      | infrastructure   | 데이터/외부 연동 계층      |
| 3      | mybatis          | MyBatis 구현 모듈         |

실제 패키지 예시:
- `scm.common.app.config`
- `scm.common.biz.user.controller`
- `scm.common.biz.user.service`
- `scm.common.biz.user.infrastructure.mybatis`

---
### 2.2 Method 명명 규칙
- Method는 클래스 내 **행위를 설명**하는 역할을 합니다.
- 일반적으로 **동사**로 시작하며, **CamelCase**를 사용합니다.
- 명확한 의미를 전달하기 위해 `동사 + 명사` 형태를 권장합니다.

| Method 명칭    | 설명                        |
|----------------|-----------------------------|
| `getCode`      | `Code Entity` 상세 조회       |
| `getCodeList`  | 목록 조회 Method            |
| `validUser`    | 사용자 유효성 검사 Method       |
| `isAvailable`  | 상태나 속성 검사에 사용        |
| `hasPermission`| 권한 여부 확인 Method        |

---

### 2.3 변수 명명 규칙
- 변수명은 약어 사용을 **지양**하며, 의미를 명확히 알 수 있도록 씁니다.
- **CamelCase**로 작성하며, 첫 글자는 소문자로 시작합니다.
- 첫 글자에 밑줄(_)이나 특수문자(예: `$`)를 사용하지 않습니다.

| 예시 변수 명칭   | 설명                     |
|------------------|--------------------------|
| `userName`       | 사용자 이름                |
| `userId`         | 사용자 ID                 |
| `totalCount`     | 총 개수                   |
| `isLoggedIn`     | 로그인 여부 확인 플래그     |

---

### 2.4 상수(Constant) 명명 규칙
- 상수는 반드시 `static final`로 선언합니다.
- 단어는 전부 **대문자 스네이크 표기법**을 사용하며, 단어 사이를 밑줄(`_`)로 연결합니다.
- 첫 글자는 밑줄(_) 또는 특수문자($)로 시작하지 않습니다.

| 예시 상수 명칭           | 설명                           |
|--------------------------|--------------------------------|
| `DEFAULT_USER_ROLE`      | 기본 사용자 역할                 |
| `MAX_PAGE_SIZE`          | 페이지 최대 크기                 |
| `DATE_FORMAT_YYYYMMDD`   | 날짜 형식(YYYY-MM-DD)           |

---

### 2.5 SQL ID 명명 규칙
현재 `scm-common-service`는 MyBatis 기반이며 SQL 식별자(ID) 명명 규칙은 아래를 따릅니다.
- SQL ID 또는 쿼리 명은 **기능, 목적, 대상 테이블**을 명확히 표현해야 함
- **카멜 케이스(camelCase)** 또는 **스네이크 케이스(snake_case)** 중 하나로 일관되게 사용
- **중복 방지**를 위해 도메인 또는 모듈명 기반 접두어 사용 권장

### 2.5.1 MyBatis 기반 명명 규칙
현재 `scm-common-service` 소스 기준으로 다음을 따릅니다.

#### Mapper 인터페이스
- 패키지: 업무·샘플별 **`...infrastructure.mybatis`** (예: `scm.common.biz.user.infrastructure.mybatis`, `scm.common.biz.sample.infrastructure.mybatis`)
- 클래스명: 역할에 맞게 **`…Mapper`** 또는 **`…RepositoryMybatis`** (`SampleRepositoryMybatis`, `UserRepositoryMybatis`)
- 타입에 `@Mapper` (org.apache.ibatis.annotations.Mapper) 사용

#### Mapper XML
- 위치: `src/main/resources/mybatis/mapper/` (`application.yml`의 `mybatis.mapper-locations: classpath:mybatis/mapper/*.xml`)
- 파일명: Mapper 인터페이스 단순 클래스명과 맞춥니다.  
  예: `UserRepositoryMybatis` → `UserRepositoryMybatis.xml`, `SampleRepositoryMybatis` → `SampleRepositoryMybatis.xml`

#### `namespace`
- **반드시 Mapper 인터페이스의 FQCN**과 동일해야 함  
  예: `scm.common.biz.sample.infrastructure.mybatis.SampleRepositoryMybatis`
```xml
<mapper namespace="scm.common.biz.sample.infrastructure.mybatis.SampleRepositoryMybatis">
```

#### SQL `id` (statement id)
- **Mapper 인터페이스의 메서드명과 1:1로 동일한 camelCase**를 사용 (중복·불일치 금지)  
- 현재 샘플·유저 모듈에서 사용 중인 패턴:

| SQL `id` | 용도(예시) |
|----------|------------|
| `findAll` | 전체 목록 |
| `findById` | PK 등 단건 |
| `findByEmail` | 조건 단건 |
| `findAllWithPageable` | 페이징 목록 |
| `countAll` | 건수 |
| `insert` / `save` | 등록 |
| `update` | 수정 |
| `delete` | 삭제 |

#### `resultMap` / 파라미터
- `resultMap`의 `id`: **`{도메인}ResultMap`** 형태 권장 (예: `SampleItemResultMap`, `UserResultMap`)
- 컬럼은 DB **`snake_case`**, DTO/Java 프로퍼티는 **`camelCase`** — `column` / `property` 매핑으로 연결
- 메서드 인자가 여러 개인 경우 **`@Param("이름")`** 과 XML의 `#{이름}` 이름을 일치
```xml
<resultMap id="UserResultMap" type="scm.common.biz.user.infrastructure.mybatis.UserDto">
  <result column="created_date" property="createdDate"/>
</resultMap>

<select id="findAllWithPageable" resultMap="UserResultMap">
  SELECT * FROM users LIMIT #{size} OFFSET #{offset}
</select>
```
```java
List<UserDto> findAllWithPageable(@Param("size") int size, @Param("offset") int offset);
```

#### 동적 SQL·주석
- XML 내 한글 주석으로 쿼리 의도를 남겨도 무방 (예: `<!-- ID로 유저 조회 -->`)

### 2.5.2 금지 사항
- query1, selectTemp 등 **의미 없는 이름 사용 금지**
- **SQL ID 중복 정의 금지** (특히 MyBatis에서)
- 표준화된 접미어 없이 **기능만 나열하는 명명 방식 지양** (예: userList → x)
  - 예: `query1`, `doSelect`, `tempMapper` (X)

### 2.5.3 권고 사항
- 기능 단위로 Mapper XML, Repository 인터페이스를 **도메인 단위로 모듈화**
- SQL ID → Controller/Service → DB 테이블 간 추적이 쉽도록 네이밍 정렬
  - 예: `UserController.getUsers` → `UserService.findAllWithPageable` → `UserRepositoryMybatis.findAllWithPageable` → XML `findAllWithPageable`

---

## **3. 추가 권장사항**

- **이름에서 목적을 명확히 전달**: 메서드, 변수, 클래스의 이름이 해야 할 일과 목적을 명확히 드러내도록 작성합니다.
- **목적에 따라 단어를 선택**:
    - **동작(행위)를 나타낼 때**: `get`, `find`, `update` 등을 시작 단어로 사용.
    - **상태 확인 시**: `is`, `has`, `validate` 등의 단어 사용.
- **일관성 유지**: 같은 역할을 하는 변수나 메서드는 전역적으로 동일한 네이밍 규칙을 따릅니다.
- **의미있는 약어만 사용**: 약어를 사용해야 하는 경우, 팀 내에서 잘 알려진 약어만 사용합니다.

---

### 🔙 Navigation
- [가이드 목록으로 돌아가기](guide.md)
