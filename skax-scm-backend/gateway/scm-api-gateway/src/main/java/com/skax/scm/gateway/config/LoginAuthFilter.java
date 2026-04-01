package com.skax.scm.gateway.config;

import com.skax.scm.gateway.api.dto.TokenStatus;
import com.skax.scm.gateway.util.IpAddressMatcher;
import com.skax.scm.gateway.util.JwtUtils;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.support.ipresolver.RemoteAddressResolver;
import org.springframework.cloud.gateway.support.ipresolver.XForwardedRemoteAddressResolver;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class LoginAuthFilter extends AbstractGatewayFilterFactory<LoginAuthFilter.Config> {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private IpAddressMatcher ipAddressMatcher;

    public static class Config {
    }

    public LoginAuthFilter() {
        super(Config.class);
    }

    /*
     * 인증 실패 시 처리할 메소드
     */
    private Mono<Void> returnUnAuthorized(ServerWebExchange exchange, TokenStatus tokenStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().set("token-status", tokenStatus.getCode());
        return response.setComplete();
    }

    private void print(String status, String msg, ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        RemoteAddressResolver resolver = XForwardedRemoteAddressResolver.maxTrustedIndex(1);
        String ip = resolver.resolve(exchange).getAddress().getHostAddress();
        String method = request.getMethod().toString();
        String path = request.getPath().toString();
        log.info("[{}][{}][{} {}][{}]", status, ip, method, path, msg);
    }

    private void print(ServerWebExchange exchange) {
        this.print("SUCC", "", exchange);
    }

    @Override
    @SuppressWarnings("unchecked")
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            try {
                ServerHttpRequest request = exchange.getRequest();
                List<String> token = request.getHeaders().get(HttpHeaders.AUTHORIZATION);

                String tokenString = null;
                if (token != null) {
                    tokenString = token.get(0);
                }
                /*
                 * 인증 토큰이 있는가? Bearer로 시작
                 */
                if (!StringUtils.hasText(tokenString) || !tokenString.startsWith("Bearer ")) {
                    /*
                     * 토큰이 없을 경우, white list ip 에 포함되는지 확인
                     */
                    RemoteAddressResolver resolver = XForwardedRemoteAddressResolver.maxTrustedIndex(1);
                    String sourceIp = resolver.resolve(exchange).getAddress().getHostAddress();
                    boolean matchYn = ipAddressMatcher.matches(sourceIp);

                    if (!matchYn) {
                        /*
                         * white list ip가 아닐 경우 오류 리턴
                         */
                        this.print("UNAUTH", "NOT WHITE LIST IP", exchange);
                        return returnUnAuthorized(exchange, TokenStatus.TOKEN_NOTOK);
                    } else {
                        /*
                         * WHITE_LIST_IP의 API Role
                         */
                        String whitelistRoleStr = "WHITELIST_IP";

                        ServerHttpRequest mutatedRequest = request
                                .mutate()
                                .header("uid", "UNKNOWN")
                                .header("role", whitelistRoleStr)
                                .build();

                        exchange = exchange.mutate().request(mutatedRequest).build();
                    }
                } else {
                    /*
                     * 토큰이 있을 경우, 인증 토큰 Text 추출
                     */
                    final String jwt = tokenString.substring(7);
                    /*
                     * 인증 토큰이 있는가 ?
                     */
                    if (jwt == null) {
                        this.print("UNAUTH", "JWT IS NULL", exchange);
                        return returnUnAuthorized(exchange, TokenStatus.TOKEN_NOTOK);
                    }

                    /*
                     * 인증 토큰이 있다면 유효성 확인, Redis 필요시 추가
                     */
                    try {
                        String msg = jwtUtils.validateJwtToken(jwt);
                        if (!"".equals(msg)) {
                            this.print("UNAUTH", msg, exchange);
                            return returnUnAuthorized(exchange, TokenStatus.TOKEN_NOTOK);
                        }
                    } catch (ExpiredJwtException e) {
                        this.print("UNAUTH", "ACCESS TOKEN EXPIRED", exchange);
                        return returnUnAuthorized(exchange, TokenStatus.ACCESS_TOKEN_EXPIRED);
                    }

                    Map<String, Object> claims = jwtUtils.getClaims(jwt);
                    String userid = (String) claims.get("uid");
                    List<String> roles = (List<String>) claims.get("role");

                    // ROLE이 null이면 빈 문자열로 처리
                    String rolesString = (roles != null && !roles.isEmpty())
                            ? String.join("|", roles)
                            : "";

                    String username = (String) claims.get("username");
                    String email = (String) claims.get("email");

                    /*
                     * mutate()를 사용하여 Header값에 계정정보 입력
                     */
                    ServerHttpRequest mutatedRequest = exchange.getRequest()
                            .mutate()
                            .header("uid", userid)
                            .header("role", rolesString)
                            .header("username", username)
                            .header("email", email)
                            .build();

                    exchange = exchange.mutate().request(mutatedRequest).build();
                }
            } catch (NullPointerException e) {
                this.print("ERR", "NULL POINTER EXCEPTION", exchange);
                return returnUnAuthorized(exchange, TokenStatus.TOKEN_NOTOK);
            }
            /*
             * 여기까지 왔다면 인증 통과. 다음 필터로 전달
             */
            this.print(exchange);
            return chain.filter(exchange);
        };
    }
}