# API Test & Config

## 1. Hosts 설정 (hosts 변조)

로컬에서 개발 및 테스트를 진행하기 위해 운영체제의 `hosts` 파일에 다음 내용을 추가합니다.

```hosts
# AI SCM
20.249.146.239   admin.scm.local
20.249.146.239   api.scm.local
20.249.146.239   common.scm.local
```

## 2. Spring Actuator Health Check

서비스 헬스 체크를 위한 API 목록입니다.

| 서비스 (Service) | 환경 (Env) | cURL 명령어 (cURL Command) |
| :--- | :--- | :--- |
| **Common Service** | `로컬 (local)` | `curl -X GET http://localhost:8082/actuator/health` |
| **API Gateway** | `로컬 (local)` | `curl -X GET http://localhost:8081/actuator/health` |
| **API Gateway** | `개발 (dev)` | `curl -X GET http://api.scm.local/actuator/health` |
| **Common Service** | `개발 (deprecated)` | `curl -X GET http://common.scm.local/actuator/health` |

## 3. Swagger UI

API 명세서를 확인하고 테스트할 수 있는 Swagger UI 접속 경로 및 링크입니다.

| 서비스 (Service)| 환경 (Env) | Swagger UI 접속 링크 (Link) |
| :--- | :--- | :--- |
| **Common Service** | `로컬 (local)` | [http://localhost:8082/common/swagger-ui/index.html](http://localhost:8082/common/swagger-ui/index.html) |
| **Common Service** | `개발 (dev)` | [http://api.scm.local/common/swagger-ui/index.html](http://api.scm.local/common/swagger-ui/index.html) |
| **Common Service** | `개발 (deprecated)` | [http://common.scm.local/common/swagger-ui/index.html](http://common.scm.local/common/swagger-ui/index.html) |