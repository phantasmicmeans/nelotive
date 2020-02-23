package com.naver.news.nelo.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.naver.news.nelo.data.Nelo;
import com.naver.news.nelo.data.innerNelo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

//TODO interface
@Slf4j
@Service
public class FileService {
    private final ObjectMapper mapper;
    private final ReactiveRedisTemplate<String, Object> reactiveRedis;
    private final RedisScript<Boolean> script;
    private final RedisTemplate<String, Object> redis;
    public FileService(ObjectMapper mapper, ReactiveRedisTemplate reactiveRedisTemplate, RedisTemplate<String, Object> redisTemplate, RedisScript<Boolean> stringRedisScript) {
        this.mapper = mapper;
        this.reactiveRedis = reactiveRedisTemplate;
        this.redis = redisTemplate;
        this.script = stringRedisScript;
    }


    public ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
    private final Path rootLocation = Paths.get("/Users/phantasmicmeans/Downloads/log_2020 0223_160324_454.json");

    public List<Path> createPaths() throws IOException {
        return Files.walk(rootLocation, 1)
                    .filter(path -> !path.equals(rootLocation))
                    .collect(Collectors.toList());
    }

    public Mono<byte[]> readFile(Path path) {
        try {
            return Mono.just(Files.readAllBytes(path));
        } catch (IOException e) {
            throw new RuntimeException(); // TODO Error 정의
        }
    }

    public Mono<List<Nelo>> convert(byte[] file) {
        try {
            return Mono.just(mapper.readValue(file, new TypeReference<List<Nelo>>() {}));
        } catch (IOException e) {
            throw new RuntimeException(); // TODO Error 정의
        }
    }

    /**
     * transaction 단위 - file 1EA
     * connection.zSetCommands().zIncrBy(key.getBytes(), 1, n.getBody().getBytes());
     */
    public void pipePush(List<Nelo> nelo, String key) {
        redis.execute((RedisCallback<Object>) connection -> {
            connection.openPipeline();
            for (Nelo n : nelo) {
                redis.opsForZSet().incrementScore(key, n.getBody(), 1);
            }
            return connection.closePipeline();
        });
    }

    /** for test
     */
    public void push(Nelo nelo) {
        String key = nelo.getBody();
        if (map.containsKey(key)) {
            map.compute(key, (k, v) -> v = v + 1);
        } else {
            map.compute(key, (k, v) -> v = 1);
        }
    }
}
