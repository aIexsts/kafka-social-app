package org.example;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.stereotype.Component;

@Component
public class SetupTopics {
    @Bean
    NewTopic postsTopic() {
        return TopicBuilder.name("posts").partitions(4).replicas(1).build();
    }
    @Bean
    NewTopic postLikesTopic() {
        return TopicBuilder.name("post-likes").partitions(4).replicas(1).build();
    }
    @Bean
    NewTopic postCommentsTopic() {
        return TopicBuilder.name("post-comments").partitions(4).replicas(1).build();
    }
}
