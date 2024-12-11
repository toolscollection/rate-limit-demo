package com.futuretech.aspect;

import com.futuretech.annotation.RateLimit;
import com.futuretech.exception.RateLimitException;
import com.futuretech.util.NetworkUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
/*@Aspect
@Component*/
@RequiredArgsConstructor
public class RateLimitAspect {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisScript<Long> limitScript;

    @Before("@annotation(rateLimit)")
    public void doBefore(JoinPoint point, RateLimit rateLimit) throws Throwable {
        String key = getCombinedKey(rateLimit, point);

        List<String> keys = Collections.singletonList(key);

        Long count = redisTemplate.execute(limitScript, keys,
                rateLimit.count(),
                rateLimit.period(),
                rateLimit.timeUnit().toSeconds(rateLimit.period()));

        if (count == null || count.intValue() > rateLimit.count()) {
            throw new RateLimitException("访问过于频繁，请稍候再试");
        }

        log.info("限流统计：{}，当前请求次数：{}", key, count);
    }

    private String getCombinedKey(RateLimit rateLimit, JoinPoint point) {
        StringBuilder key = new StringBuilder(rateLimit.prefix());

        switch (rateLimit.type()) {
            case IP:
                key.append(getIpAddress());
                break;
            case USER:
                key.append(getUserId());
                break;
            case PARAM:
                key.append(getMethodParams(point));
                break;
            case PATH:
                key.append(getRequestPath());
                break;
            default:
                key.append(getMethodName(point));
        }

        return key.toString();
    }

    private String getIpAddress() {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(
                RequestContextHolder.getRequestAttributes())).getRequest();
        String clientIp = NetworkUtils.getClientIp(request);
        return clientIp;
    }

    private String getUserId() {
        // 这里应该根据你的用户认证机制来获取用户ID
        // 示例中返回模拟值，实际应用中需要修改
        return "user123";
    }

    private String getMethodParams(JoinPoint point) {
        StringBuilder params = new StringBuilder();
        Object[] args = point.getArgs();
        for (Object arg : args) {
            if (arg != null) {
                params.append(arg.toString());
            }
        }
        return params.toString();
    }

    private String getRequestPath() {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(
                RequestContextHolder.getRequestAttributes())).getRequest();
        return request.getRequestURI();
    }

    private String getMethodName(JoinPoint point) {
        Method method = ((MethodSignature) point.getSignature()).getMethod();
        return method.getDeclaringClass().getName() + ":" + method.getName();
    }
}
