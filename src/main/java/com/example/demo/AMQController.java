package com.example.demo;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
@RequiredArgsConstructor
public class AMQController {

    private final AMQProducerService producerService;

    @PostMapping("/produce")
    public ResponseEntity<String> produce(
            @RequestParam(defaultValue = "MyQueue") String queue,
            @RequestParam(defaultValue = "100") int count) {
        Random random = new Random();
        int suffissoNumerico = random.nextInt(3); //unused for no
        String nomeCodaCompleto = queue; // + suffissoNumerico;
        producerService.sendMessages(nomeCodaCompleto, count);
        return ResponseEntity.ok("✅ Sent " + count + " messages to " + queue);
    }
}
