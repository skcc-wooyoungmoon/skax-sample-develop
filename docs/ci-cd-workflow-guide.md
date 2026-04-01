# CI/CD Workflow 설정 가이드

GitHub Actions + GitHub Container Registry(GHCR) + GitOps 패턴 기반의 CI/CD 파이프라인 설정 순서를 설명합니다.

---

## 전체 흐름

```
코드 Push
  └─► [1. determine-environment]  브랜치 기준으로 dev/prod 환경 결정
        └─► [2. build]             Docker 빌드 → GHCR push
              └─► [3. deploy]      Helm values 파일 image tag 업데이트 → git commit/push (GitOps)
                    └─► (Optional) AKS에 Helm 배포
```

---

## Step 1. GitHub Repository 생성 및 기본 설정

1. [github.com](https://github.com) → 우측 상단 `+` → `New repository`
2. `Repository name` 입력 후 `Public` 선택 → `Create repository`
3. 로컬에서 원격 연결:

```bash
git remote add origin https://github.com/<your-id>/<repo-name>.git
git branch -M main
git push -u origin main
```

---

## Step 2. GitHub Actions 활성화 확인

- Repository 상단 `Actions` 탭 클릭
- 처음 접근 시 `I understand my workflows, go ahead and enable them` 버튼 클릭

---

## Step 3. GitHub Environments 생성

워크플로우는 `dev-env`, `prd-env` 두 환경을 사용합니다.

1. Repository → `Settings` → `Environments`
2. `New environment` 클릭

| 환경명 | 설명 |
|--------|------|
| `dev-env` | develop, feature/*, hotfix/* 브랜치용 개발 환경 |
| `prd-env` | main 브랜치 또는 workflow_dispatch(prod) 선택 시 운영 환경 |

3. `prd-env` 에는 `Required reviewers` (배포 승인자) 설정을 권장합니다.

---

## Step 4. GitHub Container Registry (GHCR) 패키지 권한 설정

GHCR은 `GITHUB_TOKEN`만으로 push/pull이 가능합니다. 단, **패키지 가시성**을 확인하세요.

1. Repository → `Settings` → `Actions` → `General`
2. `Workflow permissions` → **`Read and write permissions`** 선택 → `Save`

> 이 설정 덕분에 별도 PAT 없이 `secrets.GITHUB_TOKEN`으로 GHCR에 이미지를 push할 수 있습니다.

---

## Step 5. Environment Variables 설정 (AKS 배포 시만 필요)

Azure AKS에 실제 배포하려면 각 환경(dev-env / prd-env)에 아래 Variables를 추가합니다.

1. Repository → `Settings` → `Environments` → 환경 선택
2. `Environment variables` → `Add variable`

| Variable 이름 | 값 예시 | 설명 |
|---------------|---------|------|
| `AZURE_CLIENT_ID` | `xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx` | Azure Service Principal Client ID |
| `AZURE_TENANT_ID` | `xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx` | Azure Tenant ID |
| `AZURE_SUBSCRIPTION_ID` | `xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx` | Azure Subscription ID |

### Azure Service Principal 생성 방법

```bash
# Azure CLI 로그인
az login

# Service Principal 생성 (AKS 리소스 그룹에 Contributor 권한 부여)
az ad sp create-for-rbac \
  --name "github-actions-sp" \
  --role contributor \
  --scopes /subscriptions/<subscription-id>/resourceGroups/<resource-group> \
  --sdk-auth
```

> OIDC 방식(Federated Identity)을 권장합니다. [Azure 공식 문서](https://docs.microsoft.com/en-us/azure/developer/github/connect-from-azure) 참고

---

## Step 6. 워크플로우 파일 TODO 항목 수정

각 워크플로우 파일에서 `# TODO:` 주석이 달린 항목을 본인 환경에 맞게 수정합니다.

### 공통 수정 항목

```yaml
# AKS 배포 시 아래 값을 실제 리소스명으로 수정
DEV_AKS_RG: <dev-resource-group>        # 예: my-project-dev-rg
DEV_AKS_CLUSTER: <dev-aks-cluster>      # 예: my-project-dev-aks
PRD_AKS_RG: <prod-resource-group>       # 예: my-project-prod-rg
PRD_AKS_CLUSTER: <prod-aks-cluster>     # 예: my-project-prod-aks
```

### Frontend 추가 수정 항목

```yaml
# API 서버 URL (실제 배포 URL로 교체)
DEV_NEXT_PUBLIC_API_BASE_URL: https://api-dev.<your-domain>
PRD_NEXT_PUBLIC_API_BASE_URL: https://api.<your-domain>
```

---

## Step 7. AKS 배포 활성화 (선택)

GitOps 패턴(Helm values tag 업데이트)만 사용하는 경우 추가 설정 없이 워크플로우가 동작합니다.

ArgoCD 등 GitOps 툴과 연동하거나, 직접 AKS에 배포하려면 각 워크플로우 파일 하단의 **주석 처리된 AKS 배포 단계를 활성화**하세요:

```yaml
# 아래 주석을 해제하여 AKS 직접 배포 활성화
- name: Azure Login
  uses: azure/login@v2
  ...
- name: Set AKS Context
  uses: azure/aks-set-context@v3
  ...
- name: Helm Deploy
  run: helm upgrade --install ...
```

---

## Step 8. 워크플로우 트리거 확인

| 브랜치 | 결과 환경 |
|--------|-----------|
| `feature/*`, `hotfix/*`, `develop` push | `dev-env` 빌드 및 배포 |
| `main` push | `prd-env` 빌드 및 배포 |
| Actions 탭 → `workflow_dispatch` | 수동으로 dev/prod 선택 |

---

## 생성되는 이미지 태그 형식

```
ghcr.io/<owner>/<app-name>:dev-20260401.42-a1b2c3d
                              ^         ^  ^      ^
                              env  날짜  실행번호 커밋SHA
```

---

## 자주 발생하는 오류

| 오류 | 원인 | 해결 |
|------|------|------|
| `denied: permission_denied` (GHCR push) | Workflow permissions가 Read only | `Settings > Actions > General > Read and write` 로 변경 |
| `Environment 'prd-env' not found` | Environments 미생성 | Step 3 참고하여 환경 생성 |
| `No changes to commit` | Helm tag 동일 | 정상 동작 (이미 최신 tag) |
| `Push failed, exit 1` | 동시 push 충돌 | 자동 재시도 로직이 3회 처리, 그래도 실패 시 재실행 |
| `az: command not found` | AKS 배포 단계 활성화 후 Azure CLI 미설치 | `ubuntu-latest`는 기본 설치되어 있으므로 runner 환경 확인 |

---

## 참고 링크

- [GitHub Actions 공식 문서](https://docs.github.com/en/actions)
- [GitHub Container Registry](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-container-registry)
- [docker/build-push-action](https://github.com/docker/build-push-action)
- [Azure Login Action (OIDC)](https://github.com/Azure/login)
- [Azure AKS set context](https://github.com/Azure/aks-set-context)

