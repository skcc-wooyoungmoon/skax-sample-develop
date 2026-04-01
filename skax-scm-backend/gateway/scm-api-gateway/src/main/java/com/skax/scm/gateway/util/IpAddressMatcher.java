package com.skax.scm.gateway.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

/**
 * IP 주소 화이트리스트 매칭 유틸리티
 * LoginAuthFilter에서 사용
 */
@Component
@Slf4j
public class IpAddressMatcher {

    @Value("${app.web.ip.whitelist}")
    private String whitelistString;

    @Value("${app.interface.ip.allowList}")
    private String whitelistInterfaceString;

    /**
     * IP 주소가 화이트리스트에 포함되는지 확인
     */
    public boolean matches(String ipAddress) {
        if (ipAddress == null || ipAddress.trim().isEmpty()) {
            return false;
        }

        try {
            List<String> whitelist = Arrays.asList(whitelistString.split(","));

            for (String allowedRange : whitelist) {
                if (isIpInRange(ipAddress.trim(), allowedRange.trim())) {
                    log.debug("IP {} matches whitelist range {}", ipAddress, allowedRange);
                    return true;
                }
            }

            log.debug("IP {} does not match any whitelist range", ipAddress);
            return false;

        } catch (Exception e) {
            log.error("Error checking IP whitelist for {}: {}", ipAddress, e.getMessage());
            return false;
        }
    }

    /**
     * IP 주소가 인터페이스 허용 리스트에 포함되는지 확인
     */
    public boolean matchesInterface(String ipAddress) {
        if (ipAddress == null || ipAddress.trim().isEmpty()) {
            return false;
        }

        try {
            // IPv6 localhost를 IPv4 localhost로 변환
            String normalizedIp = normalizeIpAddress(ipAddress.trim());
            List<String> interfaceAllowList = Arrays.asList(whitelistInterfaceString.split(","));

            for (String allowedRange : interfaceAllowList) {
                if (isIpInRange(normalizedIp, allowedRange.trim())) {
                    log.debug("IP {} (normalized: {}) matches interface allow range {}", ipAddress, normalizedIp,
                            allowedRange);
                    return true;
                }
            }

            log.debug("IP {} (normalized: {}) does not match any interface allow range", ipAddress, normalizedIp);
            return false;

        } catch (Exception e) {
            log.error("Error checking IP interface allow list for {}: {}", ipAddress, e.getMessage());
            return false;
        }
    }

    /**
     * IP가 CIDR 범위에 포함되는지 확인
     */
    private boolean isIpInRange(String ip, String cidr) {
        try {
            if (!cidr.contains("/")) {
                // CIDR 표기가 아닌 경우 단일 IP로 처리
                return ip.equals(cidr);
            }

            String[] parts = cidr.split("/");
            String networkIp = parts[0];
            int prefixLength = Integer.parseInt(parts[1]);

            InetAddress targetAddr = InetAddress.getByName(ip);
            InetAddress networkAddr = InetAddress.getByName(networkIp);

            byte[] targetBytes = targetAddr.getAddress();
            byte[] networkBytes = networkAddr.getAddress();

            if (targetBytes.length != networkBytes.length) {
                return false;
            }

            int bytesToCheck = prefixLength / 8;
            int bitsToCheck = prefixLength % 8;

            // 바이트 단위 비교
            for (int i = 0; i < bytesToCheck; i++) {
                if (targetBytes[i] != networkBytes[i]) {
                    return false;
                }
            }

            // 나머지 비트 비교
            if (bitsToCheck > 0 && bytesToCheck < targetBytes.length) {
                int mask = 0xFF << (8 - bitsToCheck);
                return (targetBytes[bytesToCheck] & mask) == (networkBytes[bytesToCheck] & mask);
            }

            return true;

        } catch (UnknownHostException | NumberFormatException e) {
            log.error("Error parsing IP range {}: {}", cidr, e.getMessage());
            return false;
        }
    }

    /**
     * IP 주소 정규화 (IPv6 localhost를 IPv4 localhost로 변환)
     */
    private String normalizeIpAddress(String ipAddress) {
        // IPv6 localhost를 IPv4 localhost로 변환
        if ("0:0:0:0:0:0:0:1".equals(ipAddress) || "::1".equals(ipAddress)) {
            return "127.0.0.1";
        }
        return ipAddress;
    }
}