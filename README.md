# To-be  Architecture

> **SK AX 시스템**의 전체 마이크로서비스 아키텍처 구조 및 클라우드 인프라 운영 환경을 관리하기 위한 통합 레포지토리이자 가이드 문서 모음입니다.

---

## 📖 핵심 가이드 문서 (Architecture & Documentation)

SCM 시스템 구축 및 운영과 관련된 핵심 아키텍처 설계와 리포지토리 구성 정책을 확인할 수 있습니다.

### 1. [아키텍처 정의서](./docs/100-msa.md)
* 문서 파일: `docs/100-msa.md`
* 전체 시스템을 4대 영역(**Frontend, Backend, AI Agent, DevOps**)으로 식별하여 정리한 상위 MSA 아키텍처 다이어그램 및 기능 정의서입니다.
* Frontend(관리자/사용자 홈페이지), Backend(API Gateway, Business Domains, Common Services, Batch), AI Agent(Gateway, Agents, MCP Servers), DevOps(Logging, Monitoring, CI/CD) 레이어로 세분화한 아키텍처 통신 흐름과 각 마이크로서비스의 역할을 포괄합니다.

### 2. [전사표준저장소 Repository 구성도](./docs/110-repo.md)
* 문서 파일: `docs/110-repo.md`
* GitHub `skax-internal` 조직(Organization) 하위에 실질적으로 생성되고 배포되는 모든 Repository의 디렉토리 구조도를 명시합니다. 
* Frontend(FE 파트), Backend(BE 파트), AI Agent(AI/Data 파트), DevOps(DevOps 파트) 등 4대 코어 프로젝트 영역별 주 담당 조직(Ownership) 분류 및 역할(R&R) 정책을 포함합니다.

---

## 🎯 프로젝트 개요 및 운영 철학

본 SCM 시스템 환경은 확장성과 유연성을 최우선으로 다음과 같은 핵심 전략을 반영하여 구성됩니다:

* **도메인 단위 Polyrepo 격리**: 서비스 간 의존성 결합을 느슨하게 유지하며 도메인 단위로 자체 독립 생명주기(CI 파이프라인)를 소유합니다.
* **Declarative GitOps 체계**: 애플리케이션의 배포 환경과 워크로드 조작은 Git에 선언적(Helm Charts 등)으로 보관되며, CI/CD 통합 배포 파이프라인과 ArgoCD를 통해 단일 소스 오브 트루스(SSOT) 상태로 실 서비스에 동기화됩니다.
* **AI & NPO 연계 확장성**: 수요 관리, 리소스 최적화 등 코어 비즈니스 효율을 극대화하기 위하여, 데이터 기반 추론을 수행하는 AI 에이전트와 도메인 데이터를 연계하는 MCP(Model Context Protocol) 환경이 기본 구조로 채택되었습니다. 향후 도입될 **NPO(No People Operation)** 자동화 플랫폼과도 안정적으로 통신할 수 있는 기반을 제공합니다.
* **독립적인 플랫폼 파이프라인 운영**: DevOps/Infra 영역에서는 시스템의 구조적 인프라 자립성을 유지하기 위해 인프라 관리 자체를 클라우드 부문에 일임하며, 선제적인 애플리케이션 관측성(Logging/Monitoring) 및 CI/CD 자동화 운영 통제에만 집중합니다.

---

> 💡 **안내 사항**
> 인프라/배포 구성 및 소스코드 구성 시, 우선적으로 `docs/` 디렉토리 내의 최신 가이드 문서들을 정독하고 각 파트별 정책을 준수해 주시기 바랍니다.
