package com.futuretech.controller;

import com.futuretech.annotation.LimitType;
import com.futuretech.annotation.RateLimit;
import com.futuretech.generator.UserIdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/test")
public class TestController {

    private static final Logger log = LoggerFactory.getLogger(TestController.class);
    @Autowired
    private UserIdGenerator userIdGenerator;

    @GetMapping("/limit1")
    @RateLimit(count = 3, period = 5, timeUnit = TimeUnit.SECONDS, description = "默认限流")
    public String testDefaultLimit() {
        return "Success - Default Limit";
    }

    @GetMapping("/limit2")
    @RateLimit(count = 5, period = 10, type = LimitType.IP, description = "IP限流")
    public String testIpLimit() {
        return "Success - IP Limit";
    }

    @GetMapping("/limit3")
    @RateLimit(count = 2, period = 5, type = LimitType.USER, description = "用户限流")
    public String testUserLimit() {
        return "Success - User Limit";
    }

    @GetMapping("/limit4")
    @RateLimit(count = 3, period = 5, type = LimitType.PARAM, description = "参数限流")
    public String testParamLimit(@RequestParam String param) {
        return "Success - Param Limit: " + param;
    }

    @GetMapping("/limit5")
    @RateLimit(count = 4, period = 5, type = LimitType.PATH, description = "路径限流")
    public String testPathLimit() {
        return "Success - Path Limit";
    }

    @GetMapping("/test")
    public void say(String channel, Long count) {
        for (int i = 0; i < count; i++) {
            String s = userIdGenerator.generateUserId(channel);
            log.info("第{}次生成的userId为{}", i, s);
        }

    }
}
