아래처럼 보면 됩니다. **퍼블릭 GitHub 기준 “무료로 시작하기 쉬운 순서”**로는 보통 **Cloudflare Pages/Workers → Vercel → Netlify → Render → Railway → Fly.io**를 추천합니다. [pages.cloudflare](https://pages.cloudflare.com)

## 워크로드별 추천

| 서비스 | 잘 맞는 워크로드 | GitHub Actions 연동 | 비고 |
|---|---|---|---|
| Cloudflare Pages / Workers | 정적 프론트, 엣지 API, 가벼운 BFF | 좋음 | Pages 무료 플랜은 빌드/배포가 포함되고, Workers Free도 제공됩니다.  [pages.cloudflare](https://pages.cloudflare.com) |
| Vercel | Next.js, 프론트엔드, 프리뷰 배포 | 좋음 | Next.js와 가장 궁합이 좋고, public 프로젝트의 빠른 시작에 적합합니다.  [gist.github](https://gist.github.com/ky28059/1c9af929a9030105da8cf00006b50484) |
| Netlify | 정적 사이트, 폼, Functions | 좋음 | 무료 플랜에 CI/CD와 Functions가 포함됩니다.  [netlify](https://www.netlify.com/blog/introducing-netlify-free-plan/) |
| Render | 웹서비스, 백엔드, Postgres | 좋음 | 무료 웹서비스를 제공하지만, 무료 인스턴스는 production 용도가 아닙니다.  [render](https://render.com/docs/free) |
| Railway | 백엔드, Docker, 빠른 프로토타이핑 | 좋음 | 현재 가격 정책은 수시로 바뀌며, 무료보다는 크레딧/체험 성격이 강합니다.  [railway](https://railway.com/pricing) |
| Fly.io | 컨테이너, 멀티리전, 저지연 서비스 | 좋음 | 가격은 사용량 기반이라 컨테이너/엣지 성격에 맞지만, 무료 범위는 변동이 큽니다.  [azhida.github](https://azhida.github.io/docs.fly.io/about/pricing) |

## 선택 우선순위

### 1) 프론트만 있으면
**Cloudflare Pages**를 1순위로 두는 게 좋습니다. 무료 플랜에서 정적 사이트와 대규모 트래픽에 유리하고, Workers를 붙이면 엣지 API까지 한 플랫폼에서 처리할 수 있습니다. [developers.cloudflare](https://developers.cloudflare.com/workers/platform/pricing/)

### 2) Next.js면
**Vercel**이 1순위입니다. GitHub 연동과 프리뷰 배포가 편하고, Next.js 배포 경험이 가장 매끄러운 편입니다. [gist.github](https://gist.github.com/ky28059/1c9af929a9030105da8cf00006b50484)

### 3) 정적 사이트 + 간단한 서버리스 함수면
**Netlify**가 편합니다. 무료 플랜에 CI/CD, Functions, 충분한 기본 리소스가 들어 있어 “빠르게 올려서 테스트”하기 좋습니다. [netlify](https://www.netlify.com/blog/introducing-netlify-free-plan/)

### 4) 백엔드 API나 작은 웹서비스면
**Render**가 무난합니다. Git push 기반 배포가 쉽고, 무료 웹서비스/DB를 써서 프로토타입을 돌리기 좋지만, 무료 인스턴스는 슬립 정책과 production 비권장 조건을 감안해야 합니다. [render](https://render.com/docs/free)

### 5) Docker 이미지로 바로 굴리고 싶으면
**Fly.io**를 고려하세요. 컨테이너 배포와 멀티리전에 강하지만, 무료 범위가 자주 바뀌고 요금 예측은 상대적으로 어려운 편입니다. [azhida.github](https://azhida.github.io/docs.fly.io/about/pricing)

### 6) “일단 빠르게 배포”가 목표면
**Railway**도 좋지만, 무료 정책이 안정적이지 않아서 장기 무료 운영보다는 단기 검증용으로 보는 편이 안전합니다. [youtube](https://www.youtube.com/watch?v=_dZXZSmmw2g)

## GitHub Actions 연동 관점

GitHub Actions는 워크플로 파일을 `.github/workflows`에 두고 `push`, `pull_request` 같은 이벤트로 테스트/배포를 자동화하는 구조입니다. [youtube](https://www.youtube.com/watch?v=ylEy4eLdhFs)
퍼블릭 저장소에서는 보통 **CI는 GitHub Actions**, **배포는 각 호스팅 플랫폼의 deploy action 또는 CLI**로 나누는 방식이 가장 관리하기 쉽습니다. [dev](https://dev.to/tobidelly/step-by-step-guide-to-deploying-a-project-to-vercel-using-github-actions-for-free-l61)

실무적으로는 이런 패턴이 좋습니다.

- **프론트**: `npm ci` → test/build → Cloudflare Pages 또는 Vercel deploy. [gist.github](https://gist.github.com/ky28059/1c9af929a9030105da8cf00006b50484)
- **백엔드**: test → Docker build → Render/Railway/Fly.io deploy. [railway](https://railway.com/pricing)
- **엣지 API**: test → Workers deploy. [developers.cloudflare](https://developers.cloudflare.com/workers/platform/pricing/)

## 추천 조합

- **가장 무난한 무료 시작**: Cloudflare Pages + GitHub Actions. [pages.cloudflare](https://pages.cloudflare.com)
- **Next.js 중심**: Vercel + GitHub Actions. [dev](https://dev.to/tobidelly/step-by-step-guide-to-deploying-a-project-to-vercel-using-github-actions-for-free-l61)
- **정적 + 함수**: Netlify + GitHub Actions. [netlify](https://www.netlify.com/blog/introducing-netlify-free-plan/)
- **Spring Boot / API 서버**: Render 먼저 검토. [render](https://render.com/docs/free)
- **Docker 배포 필수**: Fly.io, 그다음 Railway. [azhida.github](https://azhida.github.io/docs.fly.io/about/pricing)

## 한 줄 결론

**퍼블릭 GitHub에서 무료 시작 기준으로는 Cloudflare Pages가 가장 범용적이고, Next.js는 Vercel, 백엔드는 Render, 컨테이너는 Fly.io 순으로 보는 게 좋습니다.** [gist.github](https://gist.github.com/ky28059/1c9af929a9030105da8cf00006b50484)

