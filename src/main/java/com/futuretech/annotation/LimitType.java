package com.futuretech.annotation;


/**
 * 限流类型
 */
public enum LimitType {
    /**
     * 默认策略全局限流
     */
    DEFAULT,

    /**
     * 根据请求者IP进行限流
     */
    IP,

    /**
     * 根据用户ID进行限流
     */
    USER,

    /**
     * 根据请求参数进行限流
     */
    PARAM,

    /**
     * 根据请求路径进行限流
     */
    PATH
}
