package org.example.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.apache.kafka.common.serialization.Serde;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;

@Configuration
public class EventsSerdesConfig {

    private static final TypeFactory TYPE_FACTORY = TypeFactory.defaultInstance();

    @Bean
    public Serde<Post> postSerde(ObjectMapper objectMapper) {
        return new JsonSerde<>(TYPE_FACTORY.constructType(Post.class), objectMapper);
    }

    @Bean
    public Serde<Like> likeSerde(ObjectMapper objectMapper) {
        return new JsonSerde<>(TYPE_FACTORY.constructType(Like.class), objectMapper);
    }

    @Bean
    public Serde<Comment> commentSerde(ObjectMapper objectMapper) {
        return new JsonSerde<>(TYPE_FACTORY.constructType(Comment.class), objectMapper);
    }
}
