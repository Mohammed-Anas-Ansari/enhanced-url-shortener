package com.eus.redis.service;

import com.eus.exception.ServiceUnavailableException;
import com.eus.redis.enums.RedisEntry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.eus.redis.enums.RedisEntry.GENERATED_KEY_QUEUE;

@Service
@Slf4j
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
        String firstPop = redisTemplate.opsForList().leftPop(String.valueOf(key));
        if (firstPop == null) {
            for (int i = 0; i < 3; i++) {
                log.error("Retry count for non null key: {}", i + 1);
                String nextPop = redisTemplate.opsForList().leftPop(String.valueOf(key));
                if (nextPop != null) {
                    return nextPop;
                }
            }
            log.error("Got 3 subsequent null keys. Redis queue size is {}", getQueue(GENERATED_KEY_QUEUE).size());
            throw new ServiceUnavailableException("Please try again later!");
        }
        return firstPop;
    }

    public List<String> getQueue(RedisEntry key) {
        return redisTemplate.opsForList().range(String.valueOf(key), 0, -1);
    }
}
