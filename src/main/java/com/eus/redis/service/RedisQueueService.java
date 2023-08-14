package com.eus.redis.service;

import com.eus.redis.enums.RedisEntry;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RedisQueueService {

    private final RedisTemplate<String, String> redisTemplate;

    public void addValueToQueue(RedisEntry key, String value) {
        redisTemplate.opsForList().rightPush(String.valueOf(key), value);
    }

    public void addValuesToQueueInBatch(RedisEntry key, List<String> values) {
        redisTemplate.opsForList().rightPushAll(String.valueOf(key), values);
    }

    public String readFromQueueHead(RedisEntry key) {
        return redisTemplate.opsForList().index(String.valueOf(key), 0);
    }

    public String popFromQueueHead(RedisEntry key) {
        return redisTemplate.opsForList().leftPop(String.valueOf(key));
    }

    public List<String> getQueue(RedisEntry key) {
        return redisTemplate.opsForList().range(String.valueOf(key), 0, -1);
    }
}
