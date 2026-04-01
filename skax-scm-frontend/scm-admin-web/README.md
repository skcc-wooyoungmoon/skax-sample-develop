# Frontend 개발환경 표준 및 개발 가이드

## 개요

### 목적
본 문서는 프론트엔드 개발 시 일관된 코드 품질과 개발 생산성을 확보하기 위한 표준을 정의합니다.

### 적용 범위
- 개발 환경 구성
- 프로젝트 구조
- 코드 네이밍/컨벤션
- 컴포넌트 작성 방식
- API 통신 및 에러 처리
- 상태 관리
- 테스트/품질 관리

### 목차
적용 범위 항목과 동일한 순서로 본문을 구성합니다.

1. [개발 환경 구성](#1-개발-환경-구성)
    - 1.1 [주요 기술](#주요-기술)
    - 1.2 [필수 설치](#필수-설치)
    - 1.3 [권장 VSCode Extension](#권장-vscode-extension)
    - 1.4 [로컬 개발 실행 순서](#로컬-개발-실행-순서-처음-세팅--서버-기동)
    - 1.5 [백엔드 샘플 연동 실행 절차](#백엔드-샘플-연동-실행-절차-인증-포함)
2. [프로젝트 구조](#2-프로젝트-구조)
    - 2.1 [디렉터리 트리 (현재 소스 기준)](#디렉터리-트리-현재-소스-기준)
    - 2.2 [구조 원칙](#구조-원칙)
    - 2.3 [주요 파일 역할](#주요-파일-역할)
    - 2.4 [샘플 화면 동작](#샘플-화면-동작)
    - 2.5 [데이터 확인 (PostgreSQL 등)](#데이터-확인-postgresql-등)
3. [코드 네이밍 및 컨벤션](#3-코드-네이밍-및-컨벤션)
    - 3.1 [파일 네이밍](#파일-네이밍)
    - 3.2 [약어 사용 지양](#약어-사용-지양)
    - 3.3 [예약어](#예약어)
    - 3.4 [Functions (함수)](#functions-함수)
    - 3.5 [Variables (변수)](#variables-변수)
    - 3.6 [Boolean variables (참/거짓)](#boolean-variables-참거짓)
    - 3.7 [import 순서](#import-순서-위에서-아래로)
    - 3.8 [주석 스타일](#주석-스타일)
4. [컴포넌트 작성 방식](#4-컴포넌트-작성-방식)
    - 4.1 [자주 사용하는 용어 정리](#자주-사용하는-용어-정리)
    - 4.2 [작성 순서 (파일 안에서 위→아래)](#작성-순서-파일-안에서-위아래)
    - 4.3 ["use client" (Next.js App Router)](#use-client-nextjs-app-router)
    - 4.4 [기본 예시](#기본-예시-한-줄씩)
    - 4.5 [이벤트와 Props](#이벤트와-props)
    - 4.6 [렌더링 규칙](#렌더링-규칙)
    - 4.7 [체크리스트 (컴포넌트 추가 후)](#체크리스트-컴포넌트-추가-후)
5. [API 통신 및 에러 처리](#5-api-통신-및-에러-처리)
    - 5.1 [원칙](#원칙)
    - 5.2 [백엔드 응답 규격 (참고)](#백엔드-응답-규격-참고)
    - 5.3 [axios client 예시](#axios-client-예시)
    - 5.4 [service 예시](#service-예시)
    - 5.5 [샘플·인증 API 경로 (현재 연동)](#샘플인증-api-경로-현재-연동)
    - 5.6 [에러 분류](#에러-분류)
    - 5.7 [에러 처리 원칙](#에러-처리-원칙)
6. [상태 관리](#6-상태-관리)
    - 6.1 [원칙](#원칙-1)
    - 6.2 [현재 샘플](#현재-샘플)
    - 6.3 [store 예시 (Zustand 도입 시)](#store-예시-zustand-도입-시)
7. [테스트 및 품질 관리](#7-테스트-및-품질-관리)
    - 7.1 [기본 원칙](#기본-원칙)
    - 7.2 [권장 테스트 범위](#권장-테스트-범위)
    - 7.3 [Git 커밋 메시지 규칙](#git-커밋-메시지-규칙)
    - 7.4 [보안/운영 유의사항](#보안운영-유의사항)
    - 7.5 [팀 배포용 최소 템플릿 체크리스트](#팀-배포용-최소-템플릿-체크리스트-참고)

---

## 빠른 시작 (요약)

1. 모노레포 기준 `skax-scm-sample/skax-scm-frontend/scm-admin-web`(또는 이 README가 있는 폴더)로 이동
2. `.env.local` 생성 후 `NEXT_PUBLIC_API_BASE_URL` 설정(백엔드 `server.port`와 일치)
3. `npm install`
4. `npm run dev`
5. DB/Redis 기동: `cd ..\..\ && docker compose up -d postgres redis` (=`skax-scm-sample` 루트 기준)
6. 백엔드 실행: `cd ..\..\skax-scm-backend\common\scm-common-service && .\gradlew.bat bootRun --args="--spring.profiles.active=local"` 후 로그인/CRUD 확인
7. PR 전 `npm run lint`, `npm run type-check`, `npm run build`

---

## 1. 개발 환경 구성

<a id="주요-기술"></a>
### 1.1 주요 기술

#### 기술 스택

> 버전은 `package.json`에 명시된 값을 기준으로 표기했습니다. `^`는 호환 범위 내 업데이트를 허용한다는 뜻입니다.

| 구분 | 기술 | 버전(`package.json` 기준) |
|------|------|-------------------|
| 런타임 | Node.js | **20.x LTS 이상** 권장 |
| Framework | Next.js (App Router) | **^15.2.0** |
| UI | React / React DOM | **^19.0.0** / **^19.0.0** |
| Language | TypeScript | **^5.8.3** |
| HTTP | Axios | **^1.9.0** |
| 품질 | ESLint / eslint-config-next | **^9.24.0** / **^15.2.0** (Next와 맞춤) |
| 스타일 | CSS Modules + `globals.css` | Next·React에 내장 |

- **빌드 출력**: `next.config.ts`에 `output: 'export'`가 설정되어 있어, `npm run build` 시 **정적 내보내기**로 `out/` 폴더가 생성됩니다. (개발은 `npm run dev`, 정적 미리보기는 `npx serve out` 등)

- **State Management: Zustand** — 현재 샘플에는 **미포함**. 도입 시 팀에서 **5.x 대** 등 최신 안정판을 선택하면 됩니다. (샘플 인증은 `localStorage` + `tokenStorage` 패턴)

#### 선택 기술
- UI 스타일: CSS Modules / Tailwind 중 프로젝트 표준에 맞춰 선택
- 서버 상태 캐싱: React Query(TanStack Query) 도입 가능

<a id="필수-설치"></a>
### 1.2 필수 설치

| 도구 | 용도 |
|------|------|
| **Node.js** (LTS, 권장 **20.x 이상**) | JavaScript 런타임 · `npm` 포함 |
| **npm** | 패키지 설치·스크립트 실행 (Node 설치 시 함께 설치됨) |
| **Git** | 소스 버전 관리·clone |
| **VS Code** (또는 동등 IDE) | 편집·디버그·ESLint/Prettier 연동 |

#### Windows (PowerShell) — `winget` 예시

관리자 권한이 필요할 수 있습니다. `winget`이 없으면 [앱 설치 관리자(WinGet)](https://learn.microsoft.com/ko-kr/windows/package-manager/winget/) 또는 각 공식 사이트에서 설치합니다.

```powershell
# Node.js LTS (npm 포함)
winget install OpenJS.NodeJS.LTS

# Git
winget install Git.Git

# Visual Studio Code
winget install Microsoft.VisualStudioCode
```

설치 후 **새 터미널**을 연 뒤 버전 확인:

```powershell
node -v    # 예: v20.x.x / v22.x.x (Next 15는 18.18+, 19.8+, 20+ 지원)
npm -v     # 예: 10.x.x
git --version
code --version
```

> **수동 설치**: [Node.js 공식 LTS](https://nodejs.org/) · [Git for Windows](https://git-scm.com/download/win) · [VS Code](https://code.visualstudio.com/)

#### macOS — Homebrew 예시

```bash
brew install node git
brew install --cask visual-studio-code
```

```bash
node -v && npm -v && git --version
```

<a id="권장-vscode-extension"></a>
### 1.3 권장 VSCode Extension
- ESLint
- Prettier - Code formatter
- GitLens
- Tailwind CSS IntelliSense (Tailwind 사용 시)

<a id="로컬-개발-실행-순서-처음-세팅--서버-기동"></a>
### 1.4 로컬 개발 실행 순서 (처음 세팅 ~ 서버 기동)

아래는 **한 번도 실행해 본 적 없을 때** 위에서 아래 순서대로 진행하면 됩니다.

#### 사전 확인

터미널(PowerShell 등)에서 다음이 출력되면 다음 단계로 진행합니다.

```powershell
node -v
npm -v
```

#### 1단계: 프로젝트 루트로 이동

저장소를 clone(또는 압축 해제)했다면, **이 README가 있는 폴더**로 이동합니다.

```powershell
cd skax-scm-frontend\scm-admin-web
```

(경로는 실제 clone 위치에 맞게 조정하세요.)

#### 2단계: 환경 변수 파일 만들기 (`.env.local`)

1. `scm-admin-web` 폴더 안에 **`.env.local`** 이라는 이름의 새 파일을 만듭니다.  
   - VS Code: 탐색기에서 우클릭 → 새 파일 → `.env.local`  
   - 이미 있다면 내용만 확인·수정합니다.
2. 아래 내용을 **그대로 복사해 붙여넣고 저장**합니다.

```env
# 게이트웨이 경유(권장): docker compose로 gateway + backend 띄운 경우
NEXT_PUBLIC_API_BASE_URL=http://localhost:8081

# common만 직접 띄운 경우(Gradle bootRun 등, 기본 포트 8082)
# NEXT_PUBLIC_API_BASE_URL=http://localhost:8082

NEXT_PUBLIC_APP_ENV=local
```

| 변수 | 설명 |
|------|------|
| `NEXT_PUBLIC_API_BASE_URL` | 브라우저가 호출할 API **호스트+포트**. **`http://localhost:8081`** = `scm-api-gateway`(로드밸런싱·라우팅). **`http://localhost:8082`** = `scm-common-service` 직접(게이트웨이 없이 로컬 개발할 때). |
| `NEXT_PUBLIC_APP_ENV` | 구분용 라벨(선택). `local` / `dev` 등 팀 규칙에 맞게 사용 |

> `docker-compose.yml`의 `scm-admin-web` 서비스도 `NEXT_PUBLIC_API_BASE_URL=http://localhost:8081`로 게이트웨이를 가리킵니다.  
> `src/services/http/client.ts`는 env 미설정 시 **8081(게이트웨이)** 을 기본값으로 씁니다. common만 직접 띄우면 `.env.local`에서 **8082**로 바꿉니다.  
> `.env.local`은 **Git에 커밋하지 않습니다** (개인 PC·로컬 전용).

#### 3단계: npm 패키지 설치 (의존성)

같은 폴더(`scm-admin-web`)에서 실행합니다.

```bash
npm install
```

- 처음에는 시간이 조금 걸릴 수 있습니다.  
- 오류가 나면 `node -v`가 **20 이상 권장**인지 확인하세요.

#### 4단계: 개발 서버 실행

```bash
npm run dev
```

- 터미널에 `localhost:3000` (또는 Next가 안내하는 URL)이 보이면 성공입니다.
- 브라우저에서 **http://localhost:3000** 으로 접속합니다.

> 샘플 화면을 쓰려면 **백엔드(`scm-common-service`)가 먼저 떠 있고**, 위 2단계 URL이 백엔드와 일치해야 합니다. 백엔드 기동 방법은 아래 **「백엔드 샘플 연동」**을 참고하세요.

#### 5단계: 품질·빌드 검증 (기능 개발 후·PR 전 권장)

개발 서버는 **끄고(Ctrl+C)** 같은 폴더에서 순서대로 실행해 봅니다.

```bash
npm run lint
npm run type-check
npm run build
```

| 명령 | 하는 일 |
|------|---------|
| `npm run lint` | ESLint로 문법·규칙 검사 |
| `npm run type-check` | TypeScript 타입 오류 검사 (`tsc --noEmit`) |
| `npm run build` | 정적 내보내기(`output: 'export'`)로 `out/` 생성·검증 |

모두 **에러 없이 끝나면** 로컬 기준으로는 통과로 보면 됩니다. 정적 산출물은 `out/` 디렉터리를 참고합니다.

---

<a id="백엔드-샘플-연동-실행-절차-인증-포함"></a>
### 1.5 백엔드 샘플 연동 실행 절차 (인증 포함)

프론트는 **위「로컬 개발 실행 순서」1~4단계**까지 끝낸 뒤, 아래를 이어서 하면 됩니다.

1. **인프라·백엔드**: 모노레포 루트 `skax-scm-sample`에서 `docker compose up -d postgres redis`(또는 백엔드 README 절차)로 DB·Redis를 띄운 뒤, `skax-scm-backend/common/scm-common-service`에서 `scm-common-service`를 실행합니다. (예: `.\gradlew.bat bootRun --args="--spring.profiles.active=local"`)
2. **프론트**는 이미 `npm run dev`로 떠 있다면 그대로 두고, 브라우저에서 `http://localhost:3000` 을 엽니다.
3. 첫 화면 인증 패널에서 `email` / `password` / `username`을 입력하고 **회원가입**을 누릅니다.
4. 같은 계정으로 **로그인**을 누르면 accessToken이 저장되고, 이후 API 요청에 Bearer 토큰이 붙습니다.
5. **MyBatis 샘플** 패널에서 `sample_mybatis_item` 연동 CRUD(조회·등록·수정·삭제)를 시험합니다.

> 참고: 백엔드는 기본적으로 `http://localhost:3000` CORS를 허용하도록 설정되어 있습니다.

---

## 2. 프로젝트 구조

<a id="디렉터리-트리-현재-소스-기준"></a>
### 2.1 디렉터리 트리 (현재 소스 기준)

아래 트리에서 `#` 뒤는 **이 폴더/파일이 무엇인지**를 적은 주석입니다.  
(백엔드에 비유하면 `src/main/java` 아래 패키지처럼, 프론트는 **화면·API·타입**을 폴더로 나눕니다.)

파일명과 `#` 사이는 **고정 폭(모노스페이스 기준)으로 맞춰** 한눈에 읽기 쉽게 두었습니다.  
`node_modules/`, `.next/` 는 `npm install` / 빌드 시 생기므로 트리에는 생략합니다. 로컬 전용 **`.env.local`** 은 Git에 올리지 않으며, 보통 트리에 표시하지 않습니다.

```text
scm-admin-web/                                    # 프로젝트 루트 (npm·설정 파일이 모이는 곳)
│
├─ src/                                           # 애플리케이션 소스 본체 (업무 코드의 대부분)
│  │
│  ├─ app/                                        # Next.js App Router: URL과 연결되는 페이지·레이아웃
│  │  ├─ globals.css                              # 사이트 전역 CSS (기본 글꼴·body 스타일 등)
│  │  ├─ layout.tsx                               # 루트 레이아웃: html/body·공통 감싸기·metadata
│  │  └─ page.tsx                                 # 경로 "/" 의 화면: 컴포넌트 조합만 두는 것을 권장
│  │
│  ├─ components/                                 # 재사용 UI (도메인·기능별 하위 폴더로 분리)
│  │  └─ sample/                                  # 백엔드 샘플 API 연동 데모용 화면
│  │     ├─ SampleAuthGateway.tsx                 # 회원가입·로그인·로그아웃, 인증 후 CRUD 영역 표시
│  │     ├─ SampleAuthGateway.module.css          # AuthGateway 전용 스타일 (CSS Modules)
│  │     ├─ SampleCrudDashboard.tsx               # MyBatis 샘플 API: 목록·등록·수정·삭제 UI
│  │     └─ SampleCrudDashboard.module.css        # CRUD 대시보드 전용 스타일 (CSS Modules)
│  │
│  ├─ services/                                   # API·외부 통신 (화면에서 직접 axios 호출 지양)
│  │  ├─ auth/                                    # 인증 도메인
│  │  │  ├─ authService.ts                        # signup / authenticate 등 HTTP API 함수
│  │  │  └─ tokenStorage.ts                       # accessToken localStorage 저장·조회·삭제
│  │  ├─ http/                                    # HTTP 공통
│  │  │  └─ client.ts                             # Axios 인스턴스, Bearer·401 등 인터셉터
│  │  └─ sample/                                  # 샘플 도메인 API
│  │     └─ sampleService.ts                      # /api/v1/common/sample/mybatis-items CRUD 호출
│  │
│  └─ types/                                      # TS 타입 정의 (DTO·요청/응답 형태)
│     ├─ common/                                  # 여러 도메인에서 공통으로 쓰는 타입
│     │  └─ api.ts                                # ApiResponse<T> 등 백엔드 공통 응답 형태
│     └─ sample.ts                                # 샘플 아이템 필드(id, name, description, 날짜 등)
│
├─ .env.example                                   # 환경변수 키 샘플 (.env.local 작성 시 참고, 커밋 O)
├─ .eslintrc.json                                 # ESLint 규칙 (next/core-web-vitals, next/typescript)
├─ .gitignore                                     # Git 제외 목록 (node_modules, .next, .env.local 등)
├─ Dockerfile                                     # 컨테이너 이미지 빌드 정의 (배포 파이프라인용)
├─ next-env.d.ts                                  # Next가 생성·참조하는 TS 선언 (수정 거의 없음)
├─ next.config.ts                                 # Next 빌드/번들/이미지 등 런타임 설정
├─ nginx.conf                                     # Nginx 리버스 프록시·정적 서빙 예시 (운영 배포 참고)
├─ package.json                                   # 스크립트(dev, build, lint)·dependencies 선언
├─ package-lock.json                              # 설치된 정확한 버전 고정 (팀 재현성 위해 커밋 권장)
├─ tsconfig.json                                  # TS 컴파일 옵션, 경로 별칭 @/* → src/*
└─ README.md                                      # 개발 표준·실행 순서·구조 설명 (이 문서)
```

#### 확장 시 자주 추가하는 폴더 (현재 레포에는 없을 수 있음)

| 폴더 | 용도 (비유) |
|------|-------------|
| `public/` | 정적 파일 (이미지, favicon) — Spring의 `static` |
| `src/components/common/` | 여러 화면에서 재사용하는 버튼·모달 등 |
| `src/hooks/` | 여러 컴포넌트에서 쓰는 로직 묶음 (`use...`) |
| `src/utils/` | 날짜 포맷 등 순수 함수 (UI 없음) |
| `src/stores/` | Zustand 등 전역 상태 |

<a id="구조-원칙"></a>
### 2.2 구조 원칙
- 화면(`app`)에서 직접 API를 호출하지 않습니다.
- API 호출은 `services`에 기능별로 작성합니다.
- 전역 상태는 `stores`(도입 시), 로컬 상태는 컴포넌트 내부에서 관리합니다.
- 공통 타입은 `types`로 통합합니다.
- 확장 시 권장: `components/common`(공통 UI), `hooks`, `utils`, `public`

<a id="주요-파일-역할"></a>
### 2.3 주요 파일 역할
| 경로 | 역할 |
|------|------|
| `src/app/page.tsx` | 인증 게이트 진입 |
| `src/components/sample/SampleAuthGateway.tsx` | 회원가입/로그인/로그아웃 |
| `src/components/sample/SampleCrudDashboard.tsx` | MyBatis 샘플 CRUD UI |
| `src/services/http/client.ts` | Axios + 인터셉터(토큰, 에러) |
| `src/services/auth/*` | 인증 API·토큰 저장 |
| `src/services/sample/sampleService.ts` | 샘플 도메인 API |
| `src/types/common/api.ts` | `ApiResponse<T>` 등 공통 타입 |
| `Dockerfile` | 이미지 빌드·컨테이너 배포 시 참고 |
| `nginx.conf` | 정적 파일·API 프록시 등 웹 서버 구성 예시 |
| `package-lock.json` | 실제 설치 버전 고정 (동일 환경 재현) |

<a id="샘플-화면-동작"></a>
### 2.4 샘플 화면 동작
- **등록/수정**: 폼 상태에 따라 생성 또는 업데이트
- **초기화**: 입력 폼·편집 상태만 초기화 (재조회 아님)
- **새로고침**: 서버 재조회(API 재호출)
- **삭제**: 삭제 후 목록 재조회

<a id="데이터-확인-postgresql-등"></a>
### 2.5 데이터 확인 (PostgreSQL 등)

백엔드(`scm-common-service`)는 기본적으로 **PostgreSQL**(`docker-compose`의 `postgres` 등)을 사용합니다.

- **SQL 클라이언트(DBeaver 등)**  
  - 호스트 `localhost`, 포트 `5432`, DB `scm`, 사용자 `postgres`, 비밀번호는 `docker-compose`의 `postgres` 서비스 설정과 동일(샘플은 보통 `changeit`)  
  - 샘플 테이블: **`sample_mybatis_item`**, 사용자: **`users`** 등
- **백엔드 README**  
  - `skax-scm-backend/common/scm-common-service/README.md`의 DB 준비·`schema.sql` 안내 참고

프론트 샘플 API는 **`/api/v1/common/sample/mybatis-items`** 만 호출합니다.

---

## 3. 코드 네이밍 및 컨벤션

이 절은 **이름 짓기·쓰기 습관**을 맞추기 위한 규칙입니다.  
필요한 항목만 빠르게 적용하려면 **표와 Good/Bad** 예시를 우선 참고하면 됩니다. 세부 문법은 [Google TypeScript Style Guide](https://google.github.io/styleguide/tsguide.html) 를 기본으로 합니다.

### 목적별 안내

| 하고 싶은 일 | 이 절에서 볼 곳 |
|--------------|-----------------|
| 새 파일 이름을 정하고 싶다 | 아래 **파일 네이밍** |
| 변수·함수 이름을 정하고 싶다 | **약어 사용 지양** → **Functions** → **Variables** → **Boolean** |
| 버튼 눌렀을 때 처리 함수 이름 | **이벤트 핸들러** |
| 주석을 어떻게 달지 | 맨 아래 **주석 스타일** |

---

### Code convention 기준

코드 작성 스타일의 기본 룰은 **[Google TypeScript Style Guide](https://google.github.io/styleguide/tsguide.html)** 를 따릅니다.  
(링크가 안 열리면 검색: `Google TypeScript Style Guide`)

---

<a id="파일-네이밍"></a>
### 3.1 파일 네이밍

| 종류 | 규칙 | 예시 | 비고 |
|------|------|------|------|
| 화면·UI 컴포넌트 | **PascalCase** + `.tsx` | `UserTable.tsx`, `SampleAuthGateway.tsx` | 파일명 ≈ 컴포넌트 이름 |
| 커스텀 훅 | **`use` + camelCase** + `.ts` | `useUserList.ts`, `useAuth.ts` | React에서만 쓰는 “로직 묶음” |
| API·도메인 호출 | **camelCase** + `Service.ts` | `userService.ts`, `sampleService.ts` | 서버와 통신하는 코드 |
| 전역 상태(store) | **camelCase** + `Store.ts` | `authStore.ts` | Zustand 등 도입 시 |
| 유틸·헬퍼 | **kebab-case** + `.ts` | `date-helpers.ts` | 여러 곳에서 쓰는 짧은 함수 모음 |
| 전역 CSS | **kebab-case** 또는 프로젝트 관례 | `globals.css` | Next 기본 파일명은 그대로 |

---

<a id="약어-사용-지양"></a>
### 3.2 약어 사용 지양

**원칙:** 누구나 이름만 보고 의미를 알 수 있게 씁니다.  
**예외:** `DNS`, `URL`, `API`, `Id` 처럼 업계에서 통하는 약어는 사용해도 됩니다.

**Good — 이렇게 쓰면 좋습니다**

```typescript
const errorCount = 0;           // 무엇의 개수인지 분명함
const dnsConnectionIndex = 0; // DNS는 일반적으로 통용되는 약어
const referrerUrl = '';       // URL도 마찬가지
const customerId = '';        // Id는 흔히 씀
```

**Bad — 피합니다**

```typescript
const n = 0;              // 의미 없음
const nErr = 0;         // 애매한 약어
const nCompCons = 0;    // 추측 불가
const wgcConnections = []; // wgc가 무엇인지 알 수 없음
const pcReader = null;  // PC가 너무 많은 뜻으로 쓰임
const cstmrId = '';     // 단어를 잘라 쓰지 않음
const kSecondsPerDay = 86400; // 헝가리안 표기(k 접두) 지양
```

---

<a id="예약어"></a>
### 3.3 예약어

언어가 이미 쓰는 단어(**예약어**)는 변수 이름·함수 이름으로 **쓸 수 없습니다**.  
에디터가 빨간 줄을 그으면 먼저 예약어인지 확인해 보세요.

**Bad (이름으로 쓰면 안 됨 — 아래 단어들은 “예약어”라서 변수/함수 이름에 사용 불가)**

예: `class`, `enum`, `extends`, `super`, `const`, `export`, `import`, `return`, `function` …

```typescript
// 아래처럼 쓰면 에디터·컴파일 단계에서 오류가 납니다.
// let class = 1;
// const import = 'x';
```

---

<a id="functions-함수"></a>
### 3.4 Functions (함수)

**원칙:** 함수 이름은 **동사**로 시작하고, 낱단어 이어 붙일 때는 **camelCase** (첫 글자만 소문자, 이후 붙는 단어는 대문자 시작)입니다.

**Good**

```typescript
const getDisplayName = (): string => {
  return '홍길동';
};
```

**Bad** (이름이 동사로 시작하지 않음)

```typescript
const displayName = (): string => {
  return '홍길동';
};
```

---

### 이벤트 핸들러

버튼 클릭·입력 등 **사용자 동작에 반응하는 함수**는 **`handle` + 동사**로 통일합니다.

```typescript
const handleClick = (message: string) => {
  window.alert(`${message} 클릭됨`);
};
```

---

<a id="variables-변수"></a>
### 3.5 Variables (변수)

**1) `var`는 쓰지 않습니다.** **`let`** 과 **`const`** 만 사용합니다.

- **`const`**: 값을 다시 넣지 않을 때 (기본으로 이것부터 고려)
- **`let`**: 나중에 값이 바뀌어야 할 때만

**Good**

```typescript
let draftTitle = '제목 입력 중'; // 나중에 수정될 수 있음
const apiBaseUrl = 'http://localhost:8081'; // 예: 게이트웨이(common 직결이면 8082)
```

**Bad**

```typescript
var draftTitle = '제목'; // var는 재선언 등으로 실수가 많아 사용 금지
```

**2) 변수 이름은 명사(사물·개념 이름)** 로 시작합니다. 동사로 시작하면 “함수인가?”와 헷갈립니다.

**Good**

```typescript
const personName = '이름';
```

**Bad**

```typescript
const setPersonName = '이름'; // set으로 시작 → 보통 함수처럼 보임
```

---

<a id="boolean-variables-참거짓"></a>
### 3.6 Boolean variables (참/거짓)

참/거짓 값은 **`is`**, **`has`**, 필요하면 **`can`** 을 앞에 붙입니다.

**Good**

```typescript
const isUserLoggedIn = false;
const hasUnreadNotification = true;
```

**Bad**

```typescript
const loginUser = false; // “로그인한 사용자 객체”처럼 읽힐 수 있음
```

---

### 한눈에 보는 네이밍 요약 (React 프로젝트)

```typescript
// Good 요약
const getUserList = async () => { /* ... */ };
const handleSave = () => { /* ... */ };
const isModalOpen = false;
const MAX_RETRY_COUNT = 3; // 진짜 불변 상수는 대문자+밑줄(SCREAMING_SNAKE_CASE)
```

```typescript
// Bad 요약
const userList = async () => { /* ... */ }; // 동사로 시작하지 않음
const click = () => { /* ... */ };          // 무엇을 클릭하는지 불명확
const modalOpen = false;                    // is 접두 없음
```

---

### React·TypeScript 프로젝트 공통 원칙

- 화면 코드는 **함수 컴포넌트 + Hooks** (`useState`, `useEffect` 등)를 사용합니다.
- TypeScript **`strict`** 모드를 끄지 않습니다 (설정은 `tsconfig.json`).
- 한 파일·한 함수가 너무 많은 일을 하지 않게 **나눕니다**.
- 쓰지 않는 변수·import는 **삭제**합니다 (경고가 나오면 정리).

<a id="import-순서-위에서-아래로"></a>
### 3.7 import 순서 (위에서 아래로)

1. 외부 라이브러리 (`react`, `axios` 등)  
2. 내부 절대 경로 (`@/components/...`, `@/services/...`)  
3. 상대 경로 (`./같은폴더`)  
4. 스타일·이미지 (`./파일.module.css`)

---

<a id="주석-스타일"></a>
### 3.8 주석 스타일

#### TODO (나중에 할 일)

```typescript
// TODO: 배포 전 임시 코드 제거
// TODO: [2025-Q2] 레거시 API 경로 통일
```

#### 함수·훅 설명 (JSDoc)

다른 사람이 **매개변수·반환값**을 바로 알 수 있게 적습니다.

```typescript
/**
 * 두 숫자의 평균을 반환합니다.
 * @param x 첫 번째 숫자
 * @param y 두 번째 숫자
 * @returns 평균값
 */
const getAverage = (x: number, y: number): number => (x + y) / 2;
```

#### 파일 맨 위 (선택이지만 권장)

**`.tsx` (화면·컴포넌트)**

```tsx
/**
 * 사용자 카드 UI
 * @author [git-아이디 또는 이름]
 */
```

**`.ts` (서비스·유틸)**

```typescript
/**
 * 사용자 API 호출 모음
 * @author [git-아이디 또는 이름]
 */
```

---

## 4. 컴포넌트 작성 방식

**컴포넌트**는 화면의 한 덩어리(버튼, 카드, 폼, 목록 등)를 **이름 붙은 단위**로 나눈 코드입니다.  
같은 UI를 여러 곳에서 재사용하거나, 파일을 열었을 때 **역할이 한눈에** 들어오게 만드는 것이 목표입니다.

<a id="자주-사용하는-용어-정리"></a>
### 4.1 자주 사용하는 용어 정리

| 용어 | 뜻 (이 프로젝트 기준) |
|------|------------------------|
| **컴포넌트** | `.tsx` 파일로 만든 UI 조각. 함수 하나가 화면 한 덩어리를 `return` 한다. |
| **Props** | 부모가 자식에게 넘기는 **입력값** (문자열, 숫자, 콜백 함수 등). 읽기 전용으로 쓰는 것이 일반적이다. |
| **state** | 화면 안에서 **바뀔 수 있는 값** (입력란, 열림/닫힘, 로딩 여부 등). `useState` 등으로 관리한다. |
| **JSX** | `return (` 아래에 쓰는, HTML과 비슷한 모양의 마크업. 실제로는 JavaScript로 변환된다. |
| **Hook** | `use`로 시작하는 함수 (`useState`, `useEffect` …). 컴포넌트 안에서만 호출한다. |

### 목적별 안내

| 하고 싶은 일 | 볼 곳 |
|--------------|--------|
| 새 화면 조각을 파일 하나로 만들고 싶다 | **작성 순서** · **기본 예시 (한 줄씩)** |
| 버튼 클릭·입력에 반응하고 싶다 | **이벤트와 Props** · 3절 **이벤트 핸들러** |
| 목록을 화면에 그리고 싶다 | **목록 렌더링과 key** |
| `useState`를 쓰는 파일 | 파일 맨 위에 **`"use client"`** (아래 참고) |

---

<a id="작성-순서-파일-안에서-위아래"></a>
### 4.2 작성 순서 (파일 안에서 위→아래)

| 순서 | 할 일 | 왜 이렇게 두나 |
|------|--------|----------------|
| 1 | **import** | 쓸 라이브러리·다른 컴포넌트·스타일을 먼저 가져온다. |
| 2 | **Props 타입** (`interface` …) | 이 컴포넌트가 **받는 입력**의 이름과 타입을 문서처럼 적어 둔다. |
| 3 | **컴포넌트 함수** 선언 | `const 이름 = (props) => { … }` 형태가 흔하다. |
| 4 | **state / Hook** | 화면이 바뀌어야 하면 `useState`, API 후처리면 `useEffect` 등. |
| 5 | **이벤트 핸들러** (`handle…`) | 클릭·제출 등 사용자 동작을 처리하는 함수. |
| 6 | **return ( JSX )** | 실제로 브라우저에 그릴 태그 구조. |

---

<a id="use-client-nextjs-app-router"></a>
### 4.3 `"use client"` (Next.js App Router)

- **서버 전용**으로 두어도 되는 단순 정적 UI는 생략할 수 있다.
- **`useState`**, **`useEffect`**, **브라우저 API**(`window`, `localStorage`), **이벤트 핸들러**를 쓰는 파일은 파일 **최상단 첫 줄**에 다음을 둔다.

```tsx
"use client";
```

이 프로젝트의 `SampleAuthGateway.tsx`, `SampleCrudDashboard.tsx`가 해당 패턴이다.

---

<a id="기본-예시-한-줄씩"></a>
### 4.4 기본 예시 (한 줄씩)

```tsx
"use client"; // 브라우저 상호작용·Hook 사용 시 필요 (Next App Router)

import type { FC } from "react"; // FC = 함수형 컴포넌트 타입 (생략하고 써도 됨)

// 이 카드가 부모에게서 받을 입력(Props)의 모양
interface UserCardProps {
  name: string; // 표시할 이름
  onClick: () => void; // 클릭 시 실행할 콜백 (인자 없음)
}

// UserCard: 화면에 이름 버튼 하나를 그리는 컴포넌트
const UserCard: FC<UserCardProps> = ({ name, onClick }) => {
  return <button onClick={onClick}>{name}</button>;
};

export default UserCard; // 다른 파일에서 import 해서 사용
```

**부모에서 쓰는 예:**

```tsx
// @/ 는 src/ 를 가리킴. UserCard.tsx 가 있는 경로로 맞춤
import UserCard from "@/components/user/UserCard";

// …
<UserCard name="홍길동" onClick={() => handleUserClick()} />
```

- `name`, `onClick`은 **Props 이름과 정확히 맞출** 것 (대소문자 포함).

---

<a id="이벤트와-props"></a>
### 4.5 이벤트와 Props

- **Props 이름**은 `interface`에 적은 것과 동일해야 한다.
- 버튼의 기본 동작은 `onClick`, 폼은 `onSubmit` 등 **HTML에서 쓰는 이름**을 그대로 쓰는 경우가 많다.
- 자식이 부모에게 “알려줘야” 할 때는 Props로 **함수**를 넘긴다 (`onSave`, `onClose` 등).

---

<a id="렌더링-규칙"></a>
### 4.6 렌더링 규칙

#### 1) 목록을 그릴 때는 `key` 필수

같은 종류의 항목을 `map`으로 여러 개 그릴 때, React가 **어떤 항목이 어떤 항목인지** 구분하려면 안정적인 `key`가 필요하다. 보통 **데이터의 고유 id**를 쓴다.

```tsx
{users.map((user) => (
  <UserCard
    key={user.id}
    name={user.name}
    onClick={() => handleUserClick(user.id)}
  />
))}
```

- **인덱스(0, 1, 2…)** 를 `key`로 쓰는 것은 순서가 바뀌는 목록에서는 피한다.

#### 2) 조건부 표시

보이거나 숨기거나 할 때는 조건을 **짧고 읽기 쉽게** 쓴다.

```tsx
{isLoading ? <p>불러오는 중…</p> : null}
{errorMessage ? <p role="alert">{errorMessage}</p> : null}
```

#### 3) JSX가 너무 깊어지면 컴포넌트로 쪼갠다

한 `return` 안에 `div`가 수십 줄 이어지면, **섹션별로 새 `.tsx` 파일**을 만들어 이름을 붙이는 것이 유지보수에 유리하다.

---

### 이 저장소에서 참고할 샘플

| 파일 | 설명 |
|------|------|
| `src/components/sample/SampleAuthGateway.tsx` | 로그인·회원가입 UI, 인증 후 하위 화면 전환 |
| `src/components/sample/SampleCrudDashboard.tsx` | 폼·목록·버튼, `useState` / `useCallback` / `useEffect` 조합 예시 |
| `src/app/page.tsx` | 루트 URL에서 어떤 컴포넌트를 붙이는지 (조합만 두는 패턴) |

---

<a id="체크리스트-컴포넌트-추가-후"></a>
### 4.7 체크리스트 (컴포넌트 추가 후)

- [ ] 파일명이 **PascalCase**이고 확장자가 **`.tsx`** 인가
- [ ] Props에 **`interface`** (또는 `type`)로 입력 타입을 적었는가
- [ ] Hook / 클릭 처리가 있으면 **`"use client"`** 가 파일 첫 줄에 있는가
- [ ] 목록에 **`key`** 를 넣었는가
- [ ] API 호출은 **컴포넌트가 아니라 `services`** 에 두었는가 (5절)

---

## 5. API 통신 및 에러 처리

<a id="원칙"></a>
### 5.1 원칙
- 공통 인스턴스(`src/services/http/client.ts`) 사용
- 인터셉터에서 토큰(`Bearer`)/공통 헤더/에러 처리
- UI 레이어에서 axios 직접 호출 금지

<a id="백엔드-응답-규격-참고"></a>
### 5.2 백엔드 응답 규격 (참고)
```ts
interface ApiResponse<T> {
  success: boolean;
  data: T;
  error?: { code: number; message: string };
}
```

<a id="axios-client-예시"></a>
### 5.3 axios client 예시
```ts
import axios from "axios";

export const http = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_BASE_URL,
  timeout: 10000,
});

http.interceptors.request.use((config) => {
  // token 주입 등
  return config;
});
```

<a id="service-예시"></a>
### 5.4 service 예시
```ts
import { http } from "@/services/http/client";
import type { User } from "@/types/user";

export const userService = {
  getUsers: async (): Promise<User[]> => {
    const res = await http.get("/api/users");
    return res.data?.data ?? [];
  },
};
```

<a id="샘플인증-api-경로-현재-연동"></a>
### 5.5 샘플·인증 API 경로 (현재 연동)

`src/services` 기준 실제 호출 경로는 다음과 같습니다.

- **샘플(MyBatis)**: `GET/POST /api/v1/common/sample/mybatis-items`, `PUT/DELETE /api/v1/common/sample/mybatis-items/{id}` (`sampleService.ts`)
- **인증**: `POST /api/v1/common/users/signup`, `POST /api/v1/common/users/authenticate` (`authService.ts`)

<a id="에러-분류"></a>
### 5.6 에러 분류
- 네트워크 에러 (timeout, DNS, 5xx)
- 인증/인가 에러 (401, 403)
- 비즈니스 에러 (검증 실패, 도메인 룰 위반)

<a id="에러-처리-원칙"></a>
### 5.7 에러 처리 원칙
- 사용자 메시지와 개발 로그 메시지 분리
- 공통 에러 핸들러(인터셉터)로 중복 처리 최소화
- 인증 만료(401) 시 재로그인 유도 또는 로그인 화면으로 복귀

---

## 6. 상태 관리

<a id="원칙-1"></a>
### 6.1 원칙
- 전역으로 필요한 상태만 store에 저장
- 페이지 한정 상태는 로컬 state 우선
- store는 도메인 단위 분리

<a id="현재-샘플"></a>
### 6.2 현재 샘플
- 인증 토큰: `localStorage` + `tokenStorage.ts` (Zustand 도입 전 패턴 예시)

<a id="store-예시-zustand-도입-시"></a>
### 6.3 store 예시 (Zustand 도입 시)
```ts
import { create } from "zustand";

interface AuthState {
  token: string | null;
  setToken: (token: string | null) => void;
}

export const useAuthStore = create<AuthState>((set) => ({
  token: null,
  setToken: (token) => set({ token }),
}));
```

---

## 7. 테스트 및 품질 관리

<a id="기본-원칙"></a>
### 7.1 기본 원칙
- `lint`, `type-check`, `build`는 PR 전 필수
- 핵심 화면 smoke test 수행
- API 주요 시나리오 성공/실패 케이스 점검

<a id="권장-테스트-범위"></a>
### 7.2 권장 테스트 범위
- 인증(로그인/로그아웃/만료)
- 메뉴 권한별 화면 분기
- 주요 CRUD 플로우

<a id="git-커밋-메시지-규칙"></a>
### 7.3 Git 커밋 메시지 규칙

형식:
```bash
<type>(<scope>): <message>
```

예시:
```bash
feat(admin): 사용자 목록 필터 기능 추가
fix(auth): 토큰 만료 처리 오류 수정
docs(readme): 프론트 개발 가이드 상세화
```

권장 type:
- `feat`, `fix`, `docs`, `refactor`, `test`, `chore`

<a id="보안운영-유의사항"></a>
### 7.4 보안/운영 유의사항
- 민감정보(토큰/개인정보) 콘솔 출력 금지
- 비밀값은 `.env.local`로 관리하고 커밋 금지
- 외부 라이브러리 업데이트 시 취약점 점검 병행
- 운영 환경에서 source map 정책 점검
- 운영 반영 전 CORS 허용 도메인을 환경별로 분리

<a id="팀-배포용-최소-템플릿-체크리스트-참고"></a>
### 7.5 팀 배포용 최소 템플릿 체크리스트 (참고)

**필수**
- [ ] `src/services/http/client.ts`
- [ ] `src/services/auth/tokenStorage.ts` (또는 동등한 토큰 저장 전략)
- [ ] `src/types/common/api.ts`
- [ ] `src/services/<domain>/<domain>Service.ts`
- [ ] `.env.example`, `README.md`

**선택**
- [ ] `src/components/common`, `src/hooks`, `src/utils`
- [ ] Zustand / React Query
- [ ] Husky + lint-staged, E2E(Playwright 등)
