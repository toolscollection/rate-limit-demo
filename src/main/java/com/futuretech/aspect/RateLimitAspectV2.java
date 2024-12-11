package com.futuretech.aspect;

import com.futuretech.annotation.LimitType;
import com.futuretech.annotation.RateLimit;
import com.futuretech.exception.RateLimitException;
import com.futuretech.util.NetworkUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitAspectV2 {

    private final RedisTemplate<String, Object> redisTemplate;

    @Around("@annotation(rateLimit)")
    public Object limit(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {

        String userId = getUserId();
        String userKey = "rateLimit:userid:" + userId;
        int count = rateLimit.count();
        int time = rateLimit.period();
        LimitType limitType = rateLimit.type();

        String limitKey = getCombinedKey(rateLimit, joinPoint);
        if (!checkRateLimit(limitKey, count, time)) {
            throw new RateLimitException("请求过于频繁，请稍后重试");
        }
        return joinPoint.proceed();
    }

    private boolean checkRateLimit(String key, int limit, int timeWindow) {

        Long increment = redisTemplate.opsForValue().increment(key, 1);
        if (increment == 1) {
            //可以访问，将请求次数放入缓存
            redisTemplate.opsForValue().set(key, Integer.valueOf(increment+""), timeWindow, TimeUnit.SECONDS);
        }
        log.info("限流统计：{}，当前请求次数：{}", key, increment);
        return increment <= limit;
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

    private String getUserId() {
        // 这里应该根据你的用户认证机制来获取用户ID，比如SecurityUtils.getUserId();
        // 示例中返回模拟值，实际应用中需要修改
        return "user123";
    }

    private String getIpAddress() {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(
                RequestContextHolder.getRequestAttributes())).getRequest();
        String clientIp = NetworkUtils.getClientIp(request);
        return clientIp;
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
