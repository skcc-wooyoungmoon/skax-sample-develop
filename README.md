# SKAX Sample Architecture

[![Frontend Admin Web](https://github.com/<your-id>/skax-sample-develop/actions/workflows/frontend-admin-web.yaml/badge.svg)](https://github.com/<your-id>/skax-sample-develop/actions/workflows/frontend-admin-web.yaml)
[![Backend API Gateway](https://github.com/<your-id>/skax-sample-develop/actions/workflows/backend-api-gateway.yaml/badge.svg)](https://github.com/<your-id>/skax-sample-develop/actions/workflows/backend-api-gateway.yaml)
[![Backend Common Service](https://github.com/<your-id>/skax-sample-develop/actions/workflows/backend-common-common.yaml/badge.svg)](https://github.com/<your-id>/skax-sample-develop/actions/workflows/backend-common-common.yaml)

개인 학습/포트폴리오 용도로 운영하는 **Public GitHub 샘플 레포지토리**입니다.
MSA 기반 구조(Frontend, Backend, AI Agent, DevOps)를 실험하고, 문서화-배포-운영 흐름을 함께 관리합니다.

## Demo & Deployment

- Public Repository: `https://github.com/<your-id>/skax-sample-develop`
- Live Demo: `https://<your-domain>`
- API Endpoint: `https://<your-api-domain>`

> 위 링크는 개인 환경에 맞게 교체해서 사용하세요.

## Project Overview

이 프로젝트는 아래 목표를 중심으로 구성되어 있습니다.

- 도메인 단위 분리와 느슨한 결합을 고려한 MSA 구조 연습
- Docker/Helm 기반의 배포 가능한 개발 표준 정리
- AI Agent/MCP 연동을 고려한 확장 가능한 백엔드 구조 실험
- 문서 중심 협업(아키텍처/코딩 규칙/운영 가이드) 워크플로우 유지

## Architecture Docs

핵심 설계 문서는 아래에서 확인할 수 있습니다.

- [아키텍처 정의서](./docs/100-msa.md)
  - 시스템을 Frontend / Backend / AI Agent / DevOps 영역으로 분리해 설명합니다.
- [Repository 구성도](./docs/110-repo.md)
  - 전체 저장소 구조와 각 영역의 역할을 정의합니다.

## Repository Structure

```text
skax-sample-develop/
|- skax-scm-frontend/      # 관리자/사용자 웹
|- skax-scm-backend/       # 게이트웨이, 도메인 서비스, 공통 서비스
|- skax-scm-agent/         # AI Agent 관련 영역
|- skax-scm-devops/        # Helm chart, CI/CD, 운영 자산
`- docs/                   # 아키텍처/개발/운영 가이드
```

## Quick Start

### 1) Clone

```bash
git clone https://github.com/<your-id>/skax-sample-develop.git
cd skax-sample-develop
```

### 2) Local Run (Docker Compose)

```bash
docker compose up -d
```

### 3) Stop

```bash
docker compose down
```

> 서비스별 상세 실행 방법은 각 하위 프로젝트의 `README.md`를 참고하세요.

## Tech Stack

- Frontend: Next.js, TypeScript
- Backend: Java, Gradle, Spring 기반 서비스 구조
- Infra/DevOps: Docker, Helm, CI/CD
- Documentation: Markdown

## Documentation Index

프로젝트 정책/개발 규칙 문서는 `docs/` 하위에 정리되어 있습니다.

- [CI/CD Workflow 설정 가이드](./docs/ci-cd-workflow-guide.md) ← **GitHub Actions 시작 여기서**
- 설치/개발 환경 가이드
- 코딩/네이밍/예외 처리 규칙
- API URI/HTTP/파일 처리 가이드
- 로그/캐시/컨텍스트 스토리지 가이드

## Roadmap

- [ ] Public demo URL 연결 및 환경 변수 템플릿 정리
- [ ] 모듈별 실행 스크립트 표준화
- [ ] GitHub Actions 기반 CI 파이프라인 샘플 추가
- [ ] 관측성(로그/메트릭) 샘플 대시보드 문서화

## Contributing

개인 샘플 레포지토리이지만, 이슈/개선 제안은 언제든 환영합니다.

1. Issue 생성
2. Branch 생성
3. Commit 및 Pull Request 등록

## License

현재 라이선스 정책은 미정입니다. 공개 배포 전 `LICENSE` 파일을 추가하세요.
