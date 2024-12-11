package com.futuretech.util;

import javax.servlet.http.HttpServletRequest;


public class NetworkUtils {
    public static String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Real-IP");
        if (ip == null || ip.isEmpty()) {
            ip = request.getHeader("X-Forwarded-For");
            if (ip != null && ip.contains(",")) {
                ip = ip.split(",")[0];  // 获取第一个 IP
            }
        }
        return (ip != null && !ip.isEmpty()) ? ip : request.getRemoteAddr();
    }
}

