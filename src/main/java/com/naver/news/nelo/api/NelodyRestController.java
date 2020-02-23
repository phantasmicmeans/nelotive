package com.naver.news.nelo.api;

import com.naver.news.nelo.service.NeloService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.ParallelFlux;

import java.io.IOException;

// 테스트용
@RestController
@AllArgsConstructor
public class NelodyRestController {
    private final NeloService nelo;

    @GetMapping(value = "", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ParallelFlux<?> test() { // to rest controller
        try {
            return nelo.analyzeParallel();
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
