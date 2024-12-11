package com.futuretech.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {
    /**
     * 限流类型
     */
    LimitType type() default LimitType.DEFAULT;

    /**
     * 限流key前缀
     */
    String prefix() default "rate_limit:";

    /**
     * 限流时间窗口，默认1秒
     */
    int period() default 1;

    /**
     * 时间单位，默认秒
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 限流次数，默认100次
     */
    int count() default 100;

    /**
     * 限流描述
     */
    String description() default "";
}
