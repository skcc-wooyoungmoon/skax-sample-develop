package com.skax.scm.gateway.config;

import com.skax.scm.gateway.util.IpAddressMatcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.support.ipresolver.RemoteAddressResolver;
import org.springframework.cloud.gateway.support.ipresolver.XForwardedRemoteAddressResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Slf4j
public class ApiKeyAuthFilter extends AbstractGatewayFilterFactory<ApiKeyAuthFilter.Config> {

    @Value("${app.interface.key.auth.enabled:true}")
    private boolean apiKeyAuthEnabled;

    @Value("${app.interface.key.auth.header:X-API-KEY}")
    private String apiKeyHeader;

    @Value("${app.interface.key.auth.value:your_super_secret_api_key}")
    private String validApiKey;

    @Autowired
    private IpAddressMatcher ipAddressMatcher;

    public static class Config {
        private String header = "X-API-KEY";
        private String apiKey;

        public String getHeader() {
            return header;
        }

        public void setHeader(String header) {
            this.header = header;
        }

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }
    }

    public ApiKeyAuthFilter() {
        super(Config.class);
    }

    /**
     * API Key 인증 실패 시 처리할 메소드
     */
    private Mono<Void> returnUnauthorized(ServerWebExchange exchange, String reason) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().set("X-API-Key-Status", "INVALID");
        log.warn("API Key authentication failed: {}", reason);
        return response.setComplete();
    }

    /**
     * 로그 출력 메소드
     */
    private void print(String status, String msg, ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        RemoteAddressResolver resolver = XForwardedRemoteAddressResolver.maxTrustedIndex(1);
        String ip = resolver.resolve(exchange).getAddress().getHostAddress();
        String method = request.getMethod().toString();
        String path = request.getPath().toString();
        String apiKey = request.getHeaders().get(apiKeyHeader) == null ? ""
                : request.getHeaders().get(apiKeyHeader).get(0);

        // API Key는 로그에서 마스킹 처리
        String maskedApiKey = apiKey.length() > 8
                ? apiKey.substring(0, 4) + "****" + apiKey.substring(apiKey.length() - 4)
                : "****";

        log.info("[{}][{}][{}][{} {}][{}]", status, ip, maskedApiKey, method, path, msg);
    }

    private void print(ServerWebExchange exchange) {
        this.print("SUCC", "", exchange);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            try {
                // API Key 인증이 비활성화된 경우 통과
                if (!apiKeyAuthEnabled) {
                    this.print(exchange);
                    return chain.filter(exchange);
                }

                ServerHttpRequest request = exchange.getRequest();

                // Interface Allow IP 체크 (필수)
                RemoteAddressResolver resolver = XForwardedRemoteAddressResolver.maxTrustedIndex(1);
                String sourceIp = resolver.resolve(exchange).getAddress().getHostAddress();
                boolean matchYn = ipAddressMatcher.matchesInterface(sourceIp);

                if (!matchYn) {
                    // Interface Allow IP가 아닐 경우 오류 리턴
                    this.print("UNAUTH", "NOT INTERFACE ALLOW IP", exchange);
                    return returnUnauthorized(exchange, "Source IP not in interface allow list");
                }

                // 설정에서 API Key 헤더명과 값 가져오기
                String headerName = config.getHeader() != null ? config.getHeader() : apiKeyHeader;
                String expectedApiKey = config.getApiKey() != null ? config.getApiKey() : validApiKey;

                List<String> apiKeyHeaderValues = request.getHeaders().get(headerName);

                String apiKey = null;
                if (apiKeyHeaderValues != null && !apiKeyHeaderValues.isEmpty()) {
                    apiKey = apiKeyHeaderValues.get(0);
                }

                // API Key가 없는 경우
                if (!StringUtils.hasText(apiKey)) {
                    this.print("UNAUTH", "API KEY MISSING", exchange);
                    return returnUnauthorized(exchange, "API Key is required");
                }

                // API Key 검증
                if (!expectedApiKey.equals(apiKey)) {
                    this.print("UNAUTH", "INVALID API KEY", exchange);
                    return returnUnauthorized(exchange, "Invalid API Key");
                }

                // API Key가 유효한 경우, 요청에 인증 정보 추가
                ServerHttpRequest mutatedRequest = request
                        .mutate()
                        .header("X-API-Key-Authenticated", "true")
                        .header("X-API-Key-Source", "A-SYSTEM")
                        .build();

                exchange = exchange.mutate().request(mutatedRequest).build();

            } catch (Exception e) {
                this.print("ERR", "API KEY AUTHENTICATION EXCEPTION: " + e.getMessage(), exchange);
                return returnUnauthorized(exchange, "Authentication error");
            }

            // 인증 통과 후 다음 필터로 전달
            this.print(exchange);
            return chain.filter(exchange);
        };
    }
}