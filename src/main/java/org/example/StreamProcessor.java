package org.example;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.example.model.Comment;
import org.example.model.Like;
import org.example.model.Post;
import org.example.util.PostTimestampExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class StreamProcessor {

    private final Serde<Post> postSerde;
    private final Serde<Like> likeSerde;
    private final Serde<Comment> commentSerde;

    @Autowired
    public void process(StreamsBuilder builder) {
        KStream<String, Post> postsStream = builder.stream("posts", Consumed.with(Serdes.String(), postSerde).withTimestampExtractor(new PostTimestampExtractor()));
        KStream<String, Like> likesStream = builder.stream("likes", Consumed.with(Serdes.String(), likeSerde));
        KStream<String, Comment> commentsStream = builder.stream("comments", Consumed.with(Serdes.String(), commentSerde));

        // debug only
        postsStream.print(Printed.<String, Post>toSysOut().withLabel("posts"));
        likesStream.print(Printed.<String, Like>toSysOut().withLabel("likes"));
        commentsStream.print(Printed.<String, Comment>toSysOut().withLabel("comments"));
    }
}
