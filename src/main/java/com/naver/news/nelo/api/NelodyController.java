package com.naver.news.nelo.api;

import com.naver.news.nelo.service.NeloService;
import com.naver.news.nelo.data.innerNelo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.thymeleaf.spring5.context.webflux.IReactiveDataDriverContextVariable;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;

import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.ParallelFlux;
import lombok.AllArgsConstructor;
import java.io.IOException;


@Controller
@AllArgsConstructor
public class NelodyController {
    private final NeloService nelo;

    @GetMapping(value = "/a")
    public String test2(final Model model) throws IOException {
        ParallelFlux<innerNelo> publisher = nelo.analyzeParallel();
        IReactiveDataDriverContextVariable reactiveDataDrivenMode = new ReactiveDataDriverContextVariable(publisher);
        model.addAttribute("innerNelo", reactiveDataDrivenMode);
        return "index";
    }
}
