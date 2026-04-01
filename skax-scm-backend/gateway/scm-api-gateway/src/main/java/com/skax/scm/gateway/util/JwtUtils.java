package com.skax.scm.gateway.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

import java.util.Map;

/**
 * JwtUtils.java
 * : 작성필요
 *
 * @author Lee Ki Jung(jellyfishlove@sk.com)
 * @version 1.0.0
 * @since 2021-10-19, 최초 작성
 */

@Component
@Slf4j
public class JwtUtils {
    @Value("${app.jwt.secret}")
    private String jwtSecret;

    public String validateJwtToken(String authToken) {
        String msg = "";
        try {
            Jwts.parser().setSigningKey(jwtSecret.getBytes(java.nio.charset.StandardCharsets.UTF_8))
                    .parseClaimsJws(authToken);
        } catch (SignatureException e) {
            msg = "INVALID JWT SIGNATURE : " + e.getMessage();
        } catch (MalformedJwtException e) {
            msg = "INVALID JWT TOKEN : " + e.getMessage();
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
            throw e;
        } catch (UnsupportedJwtException e) {
            msg = "JWT TOKEN IS UNSUPPORTED : " + e.getMessage();
        } catch (IllegalArgumentException e) {
            msg = "JWT CLAIMS STRING IS EMPTY : " + e.getMessage();
        }

        return msg;
    }

    public Map<String, Object> getClaims(String token) {
        try {
            return Jwts.parser().setSigningKey(jwtSecret.getBytes(java.nio.charset.StandardCharsets.UTF_8))
                    .parseClaimsJws(token).getBody();
        } catch (SignatureException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return null;
    }
}