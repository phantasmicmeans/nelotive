package com.naver.news.nelo;

import com.naver.news.nelo.domain.Nelo;
import com.naver.news.nelo.service.FileService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.util.*;

@Slf4j
@Service
public class NeloService {
    private final FileService files;
    public NeloService(FileService files) {
        this.files = files;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    static class innerNelo implements Comparable<innerNelo> {
        private String body;
        private int count;
        @Override
        public int compareTo(innerNelo o) {
            if (this.count < o.getCount()) {
                return 1;
            } else if (this.count > o.getCount()) {
                return -1;
            }
            return 0;
        }
    }

    public Flux<Nelo> analyze() throws IOException {
        return Flux.fromIterable(files.createPaths())
                   .publishOn(Schedulers.elastic())
                   .map(files::readFile)
                   .flatMap(files::convert)
                   .doOnNext(files::push);
    }

    public List<innerNelo> result() throws IOException {
        Flux<Nelo> analyzed = analyze();
        analyzed.blockLast();

        int sum = 0;
        List<innerNelo> nelos = new ArrayList<>();

        Set<String> keys = files.map.keySet();
        Iterator<String> iterator = keys.iterator();

        while(iterator.hasNext()) {
            String key = iterator.next();
            int count = files.map.get(key);
            sum += count;
            if (count > 100) {
                nelos.add(new innerNelo(key, count));
            }
        }
        Collections.sort(nelos);
        nelos.forEach(ele -> System.out.println("WARNING : " + ele.getBody() + " , count : " + ele.getCount()));
        System.out.println("SUM : " + sum);
        return nelos;
    }
}
