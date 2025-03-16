package com.futuretech.generator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserIdGenerator {
    private static final Map<String, String> CHANNEL_CODE = new HashMap<>();
    static {
        CHANNEL_CODE.put("APP", "01");
        CHANNEL_CODE.put("WEB", "02");
        CHANNEL_CODE.put("MINI_PROGRAM", "03");
    }

    @Autowired
    private RedisTemplate redisTemplate;

    public String generateUserId(String channel) {
        StringBuilder userId = new StringBuilder("13");
        userId.append(CHANNEL_CODE.getOrDefault(channel, "00"));
        userId.append(DateUtil.format(new Date(), "yyyyMMdd"));

        //生成递增序列
        String dateKey = DateUtil.format(new Date(), "yyyyMMdd");
        Long increment = redisTemplate.opsForValue().increment("user_seq:" + dateKey, 1);
        userId.append(String.format("%04d", increment % 10000));

        return userId.toString();
    }

}

class DateUtil {
    public static String format(Date date, String formatter) {
        //
        SimpleDateFormat sdf = new SimpleDateFormat(formatter);
        String formattedDate = sdf.format(date);
        return formattedDate;
    }
}
