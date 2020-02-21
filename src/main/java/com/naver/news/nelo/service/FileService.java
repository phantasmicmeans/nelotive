package com.naver.news.nelo.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.naver.news.nelo.domain.Nelo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FileService {
    private final ObjectMapper mapper;
    public FileService(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public Map<String, Integer> map = new HashMap<>();
    private final Path rootLocation = Paths.get("/Users/phantasmicmeans/Downloads/log_2020 0221_184517_981.json");

    public List<Path> createPaths() throws IOException {
        return Files.walk(rootLocation, 1)
                    .filter(path -> !path.equals(rootLocation))
                    .collect(Collectors.toList());
    }


    public byte[] readFile(Path path) {
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            log.error("IOException : " + e.getMessage());
            throw new RuntimeException();
        }
    }

    public Flux<Nelo> convert(byte[] file) {
        List<Nelo> nelos = null;
        try {
            nelos = mapper.readValue(file, new TypeReference<List<Nelo>>() {});
        } catch (IOException e) {
            log.error("IOException : " + e.getMessage());
            throw new RuntimeException(); // TODO Error 정의
        }
        return Flux.fromIterable(nelos);
    }

    // 테스트용 임시 메소드
    // TODO redis sorted set
    public synchronized void push(Nelo nelo) {
        String key = nelo.getBody();
        if (map.containsKey(key)) {
            map.compute(key, (k, v) -> v = v + 1);
        } else {
            map.compute(key, (k, v) -> v = 1);
        }
    }
}
