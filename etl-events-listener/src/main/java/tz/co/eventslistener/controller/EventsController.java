package tz.co.eventslistener.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class EventsController {

    @Value("${kafka.event.topic-name}")
    private String topicName;

    private KafkaTemplate<String, String> kafkaTemplate;

    public EventsController(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping("/event")
    public ResponseEntity<String> eventsHandler(@RequestBody String event){
        log.info("Received event from channel: {}", event);
        kafkaTemplate.send(topicName, event);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
