package com.naver.news.nelo.service;

import com.naver.news.nelo.data.innerNelo;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.ParallelFlux;
import reactor.core.scheduler.Schedulers;

import lombok.extern.slf4j.Slf4j;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.util.*;

@Slf4j
@Service
@AllArgsConstructor
public class NeloService {
    private final FileService files;

    public Flux<innerNelo> analyze() throws IOException {
        String uuid = UUID.randomUUID().toString();
        return Flux.fromIterable(files.createPaths())
                   .publishOn(Schedulers.elastic())
                   .flatMap(files::readFile)
                   .flatMap(files::convert)
                   .doOnNext(list -> files.pipePush(list, uuid))
                   .flatMap(list -> files.ranking(uuid));
    }
}
