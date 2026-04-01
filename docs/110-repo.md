# 📂 SCM Repository Structure

> 앞서 정의된 마이크로서비스 아키텍처(`100-msa.md`)를 기반으로 구성된 전체 프로젝트 소스 코드 및 레포지토리(Repository) 디렉토리 구조도입니다.
> 💡 **참고:** 모든 레포지토리는 사내시스템 **'전사표준저장소'**인 GitHub의 `skax-internal` 조직(Organization) 하위에 구성됩니다.

```text
GitHub skax-internal/
├── ─────────────── [ 🖥️ skax-scm-frontend ]
│   ├── scm-admin-web                 # 관리자 및 분석가용 시스템 관리/설정 Web
│   ├── scm-user-web                  # 일반 사용자용 업무 처리 및 조회 Web
│   └── scm-ui-components             # [Shared] 재사용 가능한 공통 UI 컴포넌트 라이브러리
│
├── ─────────────── [ ⚙️ skax-scm-backend ]
│   ├── Business/
│   │   ├── scm-demand-service        # [Domain] 중장기/단기 수요 관리 서비스
│   │   ├── scm-resource-service      # [Domain] 리소스(인력) 공급계획 및 관리
│   │   ├── scm-operation-service     # [Domain] 인력 배치 최적화 및 운영 서비스
│   │   └── scm-performance-service   # [Domain] 프로젝트 역량/인력 평가 서비스
│   ├── Common/
│   │   ├── scm-account-service          # [Shared] OKTA 연동 로그인, 토큰 발급 및 사용 권한 관리
│   │   └── scm-common-service        # [Shared] 시스템 전체 통합 설정, 공통 코드 관리
│   ├── Batch/
│   │   └── scm-eai-service           # [Batch] EAI I/F 데이터 가공 및 SCM Schema 이관
│   └── Gateway/
│       └── scm-gateway               # [Gateway] 트래픽 단일 진입점, API 라우팅, 토큰(JWT) 보안 검증
│
├── ─────────────── [ 🤖 skax-scm-agent ]
│   ├── Agent/
│   │   ├── scm-demand-agent          # [Agent] 트렌드 기반 수요 예측 분석 및 추론
│   │   └── scm-operation-agent       # [Agent] 제약 사항 고려 최적 인력 투입 제안
│   └── MCP/
│       ├── scm-demand-mcp            # [MCP] 수요 데이터 컨텍스트 연동 서버
│       ├── scm-resource-mcp          # [MCP] 인력 상태 및 역량 정보 제공 서버
│       └── scm-operation-mcp         # [MCP] 시뮬레이션 및 최적화 엔진 제어 도구
│
└── ─────────────── [ ☁️ skax-scm-devops ]
    ├── scm-logging                   # [DevOps] 통합 로깅 데이터 파이프라인 수집 (OpenSearch, fluentbit)
    ├── scm-monitoring                # [DevOps] 애플리케이션 및 시스템 메트릭 대시보드 (Prometheus, Grafana)
    └── scm-cicd
        └── scm-helm-charts           # [DevOps] 전체 앱 공통 Helm Chart 템플릿 보관소
```

## 👥 4대 코어 Repository 담당 조직/사용자

전체 MSA 아키텍처 환경은 4개의 거대한 상위 프로젝트 단위로 분류되며, 각 영역별로 권한을 가진 주담당 조직 및 기여자가 명확히 분리됩니다.

| 권한 영역 (상위 Repository) | 주 담당 조직 (Ownership) | 주요 기여자 및 사용자 | 책무 및 권한 |
| :--- | :--- | :--- | :--- |
| **`🖥️ skax-scm-frontend`** | **Frontend 개발 파트** | FE 개발자, UI/UX 디자이너 | 통합/개별 UI 애플리케이션 개발, 공통 화면 컴포넌트 정책 수립 |
| **`⚙️ skax-scm-backend`** | **Backend 개발 파트** | BE 개발자, 도메인/솔루션 아키텍트 | 핵심 비즈니스 로직(Domain) 구현 및 공통 API(Shared) 서비스 운영 |
| **`🤖 skax-scm-agent`** | **AI / Data 분석 파트** | AI 엔지니어 | 최적화 모델 학습, Agent/MCP 파이프라인 연계 및 데이터 모델링 관리 |
| **`☁️ skax-scm-devops`** | **DevOps 파트** | 플랫폼 아키텍트, DevOps 엔지니어 | 애플리케이션 관측성(로깅/모니터링) 구성 및 CI/CD 배포 파이프라인 통제 (※ 클라우드 인프라 자원 구성은 **Cloud 부문**에서 전담) |

## 📌 Repository 운영 및 배포 정책 가이드

* **멀티 리포지토리(Polyrepo) 기반 분리**
  * 각 서비스 폴더는 Git 상에서 **독립된 개별 Repository** 단위로 생성 및 운영됨을 원칙으로 합니다.
  * 각각 고유한 CI(지속적 통합) 파이프라인(GitHub Actions 등)을 가집니다.

* **통합 배포(DevOps) 파이프라인 활용**
  * 도메인 소스 코드가 수정되더라도 해당 저장소에는 직접 배포를 지시하는 명령이 포함되지 않습니다. 
  * 컨테이너 이미지가 빌드/등록되면 최종 배포 상태는 `scm-cicd` 레포지토리(또는 별도로 구성될 배포 선언 저장소)를 통해 실제 배포 도구 파이프라인을 거쳐 실환경에 반영되도록 구성합니다.

* **구조 변경 참고(특이사항)**
  * [100-msa.md]에서 명시된 바와 같이 요구사항이나 AWS 인프라 환경 구성에 따라 **Frontend 통합 (단일 애플리케이션)**, **Agent Gateway 구성 추가** 등 리포지토리 디렉토리 아키텍처가 동적으로 통합되거나 추가 분리될 수 있습니다.
