package org.example;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.example.model.*;
import org.example.util.PostTimestampExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;

import static java.lang.ProcessBuilder.Redirect.to;

@Component
@Slf4j
@RequiredArgsConstructor
public class StreamProcessor {

    private final Serde<Post> postSerde;
    private final Serde<Like> likeSerde;
    private final Serde<Comment> commentSerde;
    private final Serde<PostCounts> postsCountsSerde;
    private final Serde<PostWithCounts> postsWithCountsSerde;

    @Autowired
    public void process(StreamsBuilder builder) {
        KStream<String, Like> likesStream = builder.stream("likes", Consumed.with(Serdes.String(), likeSerde));
        KStream<String, Comment> commentsStream = builder.stream("comments", Consumed.with(Serdes.String(), commentSerde));

        // likes count per post
        KTable<String, Long> likesCountsTable = likesStream.selectKey((k, v) -> v.postId())
                .groupByKey()
                .aggregate(
                        () -> 0L,
                        (aggKey, newValue, aggValue) -> aggValue + 1L,
                        Materialized.<String, Long, KeyValueStore<Bytes, byte[]>>as("likes-counts-store").withValueSerde(Serdes.Long()).withRetention(Duration.ofHours(1)));

        // comments count per post
        KTable<String, Long> commentsCountsTable = commentsStream.selectKey((k, v) -> v.postId())
                .groupByKey()
                .aggregate(
                        () -> 0L,
                        (aggKey, newValue, aggValue) -> aggValue + 1L,
                        Materialized.<String, Long, KeyValueStore<Bytes, byte[]>>as("comments-counts-store").withValueSerde(Serdes.Long()).withRetention(Duration.ofHours(1)));

        KTable<String, PostCounts> postCountsJoined = likesCountsTable.join(commentsCountsTable, PostCounts::new);

        // Enrich posts with count data:
        KTable<String, Post> postsTable = builder.table("posts", Consumed.with(Serdes.String(), postSerde).withTimestampExtractor(new PostTimestampExtractor()));

        // final table
        KTable<String, PostWithCounts> postWithCountsTable = postCountsJoined.join(postsTable, (postCounts, post) -> new PostWithCounts(
                post.id(),
                post.createdAt(),
                post.text(),
                post.userId(),
                postCounts.likesCount(),
                postCounts.commentsCount()
        ));

        KStream<String, PostWithCounts> postWithCountsKStream = postWithCountsTable.toStream();
        postWithCountsKStream.to("posts-with-counts", Produced.with(Serdes.String(), postsWithCountsSerde));

        postWithCountsKStream.print(Printed.<String, PostWithCounts>toSysOut().withLabel("post-with-counts"));
    }
}
