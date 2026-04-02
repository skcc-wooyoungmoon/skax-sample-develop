# Helm Chart 수동 배포 가이드

> **참고**: 현재 CI/CD는 GitOps 방식으로 구성되어 있으나, 테스트나 트러블슈팅을 위한 수동 설치 가이드입니다.

## 📦 scm-common-service 배포

### 1. 해당 차트 디렉토리로 이동
```bash
cd skax-scm-devops/scm-cicd/helm-charts/scm-backend/common/scm-common-service
```

### 2. Helm 배포 (Install / Upgrade) 실행
```bash
helm upgrade --install scm-common-service ./ \
  -n scm-backend \
  --create-namespace \
  -f ./values-dev.yaml
```

## 📦 scm-api-gateway 배포

### 1. 해당 차트 디렉토리로 이동
```bash
cd skax-scm-devops/scm-cicd/helm-charts/scm-backend/gateway/scm-api-gateway
```

### 2. Helm 배포 (Install / Upgrade) 실행
```bash
helm upgrade --install scm-api-gateway ./ \
  -n scm-backend \
  --create-namespace \
  -f ./values-dev.yaml
```

## 📦 scm-admin-web 배포

### 1. 해당 차트 디렉토리로 이동
```bash
cd skax-scm-devops/scm-cicd/helm-charts/scm-frontend/scm-admin-web
```

### 2. Helm 배포 (Install / Upgrade) 실행
```bash
helm upgrade --install scm-admin-web ./ \
  -n scm-frontend \
  --create-namespace \
  -f ./values-dev.yaml
```