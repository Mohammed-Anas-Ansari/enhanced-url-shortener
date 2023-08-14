package com.eus.service;

import com.eus.entity.KeyGenerator;
import com.eus.redis.enums.RedisEntry;
import com.eus.redis.service.RedisQueueService;
import com.eus.repository.KeyGeneratorRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class KeyGeneratorService {

//    @Value("${application.key-gen.character-set}")
    private final String CHARACTER_SET;

//    @Value("${application.key-gen.base}")
    private final int BASE;

//    @Value("${application.key-gen.key-size}")
    private final int KEY_SIZE;

//    @Value("${application.key-gen.batch-size}")
    private final int BATCH_SIZE;

//    @Value("${application.key-gen.total-batches}")
    private final int TOTAL_BATCHES;

//    @Value("${application.redis-queue.threshold}")
    private final int QUEUE_THRESHOLD;

    private final KeyGeneratorRepository keyGeneratorRepository;

    private final RedisQueueService redisQueueService;

    public KeyGeneratorService(@Value("${application.key-gen.character-set}") String characterSet,
                               @Value("${application.key-gen.base}") int base,
                               @Value("${application.key-gen.key-size}") int keySize,
                               @Value("${application.key-gen.batch-size}") int batchSize,
                               @Value("${application.key-gen.total-batches}") int totalBatches,
                               @Value("${application.redis-queue.threshold}") int queueThreshold,
                               KeyGeneratorRepository keyGeneratorRepository,
                               RedisQueueService redisQueueService) {
        CHARACTER_SET = characterSet;
        BASE = base;
        KEY_SIZE = keySize;
        BATCH_SIZE = batchSize;
        TOTAL_BATCHES = totalBatches;
        QUEUE_THRESHOLD = queueThreshold;
        this.keyGeneratorRepository = keyGeneratorRepository;
        this.redisQueueService = redisQueueService;
    }

    public void generateKeyTest() {
        KeyGenerator keyGenerator = KeyGenerator.builder().keyPrefix("abcd").build();
        keyGeneratorRepository.save(keyGenerator);
    }

    public void generateKeyOnQueueBeingUnderThreshold() {
        List<String> queue = redisQueueService.getQueue(RedisEntry.GENERATED_KEY_QUEUE);
        if (queue.isEmpty() || queue.size() <= QUEUE_THRESHOLD) {
            generateKeyInBatch();
        }
    }

    public void generateKeyInBatch() {
        generateKeyInBatch(BATCH_SIZE, TOTAL_BATCHES);
    }

    public void generateKeyInBatch(final int batchSize, final int totalBatch) {
        long lastGeneratedNumber = fetchLastGeneratedNumber();
        for (int batchNumber = 1; batchNumber <= totalBatch; batchNumber++) {
            System.out.println("Batch " + batchNumber + ":");
            generateSequentialStrings(lastGeneratedNumber, batchSize);
            System.out.println();
        }
    }

    private Long fetchLastGeneratedNumber() {
        Optional<KeyGenerator> latestRecord = keyGeneratorRepository.findTopByOrderByIdDesc();
        return latestRecord.map(KeyGenerator::getId).orElse(1L);
    }

    @Transactional
    private void generateSequentialStrings(long startNumber, int batchSize) {
        for (int i = 0; i < batchSize; i++) {
            String generatedKey = generateSequentialString(startNumber + i);
            System.out.println(generatedKey);
            KeyGenerator saved = keyGeneratorRepository.save(KeyGenerator.builder().keyPrefix(generatedKey).build());
            List<String> generatedExtendedSequentialString = generateExtendedSequentialString(generatedKey);
            redisQueueService.addValuesToQueueInBatch(RedisEntry.GENERATED_KEY_QUEUE, generatedExtendedSequentialString);
            saved.setUsed(true);
            keyGeneratorRepository.save(saved);
        }
    }

    private String generateSequentialString(long number) {
        StringBuilder sb = new StringBuilder(KEY_SIZE);
        for (int i = 0; i < KEY_SIZE; i++) {
            int digitValue = (int) (number % BASE);
            sb.append(CHARACTER_SET.charAt(digitValue));
            number /= BASE;
        }
        return sb.reverse().toString();
    }

    private List<String> generateExtendedSequentialString(String key) {
        List<String> list = new ArrayList<>();
        int characterSetLength = CHARACTER_SET.length();
        for (int i = 0; i < characterSetLength; i++) {
            list.add(key + CHARACTER_SET.charAt(i));
        }
        return list;
    }
}
