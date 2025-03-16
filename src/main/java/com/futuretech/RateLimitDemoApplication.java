package com.futuretech;

import com.futuretech.generator.DataGenerator;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class RateLimitDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(RateLimitDemoApplication.class, args);
    }

    /*@Bean
    public CommandLineRunner commandLineRunner(DataGenerator dataGenerator) {
        return args -> {
            // 生成1万用户，每个用户100个订单
            // 这样会生成：
            // - 1万用户数据
            // - 100万订单数据
            // - 约300-500万订单商品数据
            // - 100万支付数据
            dataGenerator.generateData(100000, 100);
        };
    }*/

}
