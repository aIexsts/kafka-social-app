package org.example;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.model.Comment;
import org.example.model.Like;
import org.example.model.Post;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Consumer {
    @KafkaListener(topics = {"posts"}, groupId = "spring-boot-kafka")
    public void consumePosts(ConsumerRecord<String, Post> record) {
        System.out.println("[POSTS] " + record.value() + " with key " + record.key());
    }

    @KafkaListener(topics = {"likes"}, groupId = "spring-boot-kafka")
    public void consumeLikes(ConsumerRecord<String, Like> record) {
        System.out.println("[LIKES] " + record.value() + " with key " + record.key());
    }

    @KafkaListener(topics = {"comments"}, groupId = "spring-boot-kafka")
    public void consumeComments(ConsumerRecord<String, Comment> record) {
        System.out.println("[COMMENTS] " + record.value() + " with key " + record.key());
    }
}
