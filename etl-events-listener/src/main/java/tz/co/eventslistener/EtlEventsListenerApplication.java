package tz.co.eventslistener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class EtlEventsListenerApplication {

    public static void main(String[] args) {
        SpringApplication.run(EtlEventsListenerApplication.class, args);
    }

}
