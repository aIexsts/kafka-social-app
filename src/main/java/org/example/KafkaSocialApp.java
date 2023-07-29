package org.example;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableKafka
@EnableKafkaStreams
@EnableScheduling
@Slf4j
public class KafkaSocialApp {
    public static void main(String[] args) {
        SpringApplication.run(KafkaSocialApp.class, args);
    }
}


