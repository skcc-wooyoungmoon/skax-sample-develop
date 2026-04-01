package com.skax.scm.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Spring Security 설정 클래스
 * Spring Cloud Gateway에 최적화된 보안 설정
 * 
 * 주의사항:
 * - Spring Cloud Gateway는 자체 필터 체인을 사용
 * - 실제 인증은 커스텀 필터들(ApiKeyAuthFilter, LoginAuthFilter)에서 처리
 * - Spring Security는 기본적인 보안 헤더와 CORS 설정만 담당
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    /**
     * SecurityWebFilterChain 설정
     * Spring Cloud Gateway의 특성에 맞게 최소한의 보안 설정만 적용
     * 
     * 주요 역할:
     * 1. 기본 보안 헤더 설정
     * 2. CORS 설정 (필요시)
     * 3. 기본적인 보안 예외 처리
     * 
     * 실제 인증은 Gateway의 커스텀 필터에서 처리:
     * - ApiKeyAuthFilter: API Key 인증
     * - LoginAuthFilter: JWT 토큰 인증
     */
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                // CSRF 비활성화 (API Gateway에서는 일반적으로 불필요)
                .csrf(csrf -> csrf.disable())

                // HTTP Basic 인증 비활성화
                .httpBasic(httpBasic -> httpBasic.disable())

                // Form 로그인 비활성화
                .formLogin(formLogin -> formLogin.disable())

                // 로그아웃 비활성화
                .logout(logout -> logout.disable())

                // 인증 예외 처리 (Gateway 필터에서 처리하므로 최소한만 설정)
                .exceptionHandling(exceptionHandling -> exceptionHandling.authenticationEntryPoint((exchange, ex) -> {
                    // Gateway 필터에서 이미 처리하므로 여기서는 추가 처리하지 않음
                    return exchange.getResponse().setComplete();
                }))

                // 경로별 보안 규칙 설정
                .authorizeExchange(authorizeExchange -> authorizeExchange
                        // Actuator 엔드포인트는 인증 없이 접근 가능
                        .pathMatchers("/actuator/health/**").permitAll()
                        .pathMatchers("/actuator/info").permitAll()
                        .pathMatchers("/actuator/mappings").permitAll()
                        .pathMatchers("/actuator/env").permitAll()

                        // 모든 요청 허용 (실제 인증은 Gateway 필터에서 처리)
                        .anyExchange().permitAll());

        return http.build();
    }
}