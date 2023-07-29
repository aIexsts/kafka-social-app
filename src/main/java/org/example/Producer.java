package org.example;

import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.Comment;
import org.example.model.Like;
import org.example.model.Post;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class Producer {
    private final KafkaTemplate<String, Post> postKafkaTemplate;
    private final KafkaTemplate<String, Like> likeKafkaTemplate;
    private final KafkaTemplate<String, Comment> commmentKafkaTemplate;
    @EventListener(ApplicationStartedEvent.class)
    public void appStartedListener() {
        System.out.println("[APP STARTED]");

        for (int postId = 1; postId <= 10000; postId++) {
            createPost("post_" + postId);
        }

        log.info("[PRODUCING 10000 POSTS]");
    }

    private void createPost(String postId) {
        Faker faker = Faker.instance();

        Post post = new Post(
                postId,
                Instant.now(),
                faker.lorem().sentence(faker.random().nextInt(8, 50)),
                UUID.randomUUID().toString()
        );

        postKafkaTemplate.send("posts", postId, post);
    }

    @Scheduled(cron = "*/1 * * * * *")
    public void generateLike() {
        Faker faker = Faker.instance();

        String postId = "post_" + faker.random().nextInt(1, 10000);
        String likeId =  UUID.randomUUID().toString();

        Like like = new Like(
                likeId,
                postId,
                UUID.randomUUID().toString()
        );

        likeKafkaTemplate.send("likes", likeId, like);
        log.info("[PRODUCING LIKE]");
    }

    @Scheduled(cron = "*/1 * * * * *")
    public void generateComment() {
        Faker faker = Faker.instance();

        String postId = "post_" + faker.random().nextInt(1, 10000);
        String commentId =  UUID.randomUUID().toString();

        Comment comment = new Comment(
                commentId,
                postId,
                UUID.randomUUID().toString(),
                faker.lorem().sentence(faker.random().nextInt(3, 30))
        );

        commmentKafkaTemplate.send("comments", commentId, comment);
        log.info("[PRODUCING COMMENT]");
    }
}
