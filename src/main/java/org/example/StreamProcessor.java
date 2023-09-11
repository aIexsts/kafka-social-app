package org.example;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.*;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.kstream.internals.WindowedSerializer;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.Stores;
import org.apache.kafka.streams.state.WindowStore;
import org.example.model.*;
import org.example.util.PostTimestampExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@Slf4j
@RequiredArgsConstructor
public class StreamProcessor {

    private final Serde<Post> postSerde;
    private final Serde<Like> likeSerde;
    private final Serde<Comment> commentSerde;
    private final Serde<PostCounts> postsCountsSerde;
    private final Serde<PostWithCounts> postsWithCountsSerde;

//    @Autowired
//    public void postsEnrichedWithTotalCounts(StreamsBuilder builder) {
//        KStream<String, Like> likesStream = builder.stream("likes", Consumed.with(Serdes.String(), likeSerde));
//        KStream<String, Comment> commentsStream = builder.stream("comments", Consumed.with(Serdes.String(), commentSerde));
//
//        // likes count per post
//        KTable<String, Long> likesCountsTable = likesStream.selectKey((k, v) -> v.postId())
//                .groupByKey()
//                .aggregate(
//                        () -> 0L,
//                        (aggKey, newValue, aggValue) -> aggValue + 1L,
//                        Materialized.<String, Long, KeyValueStore<Bytes, byte[]>>as("likes-counts-store").withValueSerde(Serdes.Long()).withRetention(Duration.ofHours(1)));
//
//        // comments count per post
//        KTable<String, Long> commentsCountsTable = commentsStream.selectKey((k, v) -> v.postId())
//                .groupByKey()
//                .aggregate(
//                        () -> 0L,
//                        (aggKey, newValue, aggValue) -> aggValue + 1L,
//                        Materialized.<String, Long, KeyValueStore<Bytes, byte[]>>as("comments-counts-store").withValueSerde(Serdes.Long()).withRetention(Duration.ofHours(1)));
//
//        KTable<String, PostCounts> postCountsJoined = likesCountsTable.join(commentsCountsTable, PostCounts::new);
//
//        // Enrich posts with count data:
//        KTable<String, Post> postsTable = builder.table("posts", Consumed.with(Serdes.String(), postSerde).withTimestampExtractor(new PostTimestampExtractor()));
//
//        // final table
//        KTable<String, PostWithCounts> postWithCountsTable = postCountsJoined.join(postsTable, (postCounts, post) -> new PostWithCounts(
//                post.id(),
//                post.createdAt(),
//                post.text(),
//                post.userId(),
//                postCounts.likesCount(),
//                postCounts.commentsCount()
//        ));
//
//        KStream<String, PostWithCounts> postWithCountsKStream = postWithCountsTable.toStream();
//        postWithCountsKStream.to("posts-with-counts", Produced.with(Serdes.String(), postsWithCountsSerde));
//
//        postWithCountsKStream.print(Printed.<String, PostWithCounts>toSysOut().withLabel("post-with-counts"));
//    }

    @Autowired
    public void trendingPosts(StreamsBuilder builder) {
        WindowedSerializer<String> windowedSerializer = new TimeWindowedSerializer<String>(new StringSerializer());
        Deserializer<Windowed<String>> windowedDeserializer = new TimeWindowedDeserializer<String>(new StringDeserializer());
        Serde<Windowed<String>> windowedSerde = Serdes.serdeFrom(windowedSerializer, windowedDeserializer);



        KStream<String, Like> likesStream = builder.stream("likes", Consumed.with(Serdes.String(), likeSerde));
//        KStream<String, Comment> commentsStream = builder.stream("comments", Consumed.with(Serdes.String(), commentSerde));


        TimeWindows tumblingWindow = TimeWindows.of(Duration.ofSeconds(60)).grace(Duration.ofSeconds(5));


        var store = Stores.persistentTimestampedWindowStore(
                "some-state-store",
                Duration.ofMinutes(5),
                Duration.ofMinutes(2),
                false);
        var materialized = Materialized
                .<String, Long>as(store)
                .withKeySerde(Serdes.String());


        // likes count per post
        KTable<Windowed<String>, Long> likesCountsMinuteTable = likesStream.selectKey((k, v) -> v.postId())
                .groupByKey()
                .windowedBy(tumblingWindow)
                .count(materialized)
                .suppress(Suppressed.untilWindowCloses(Suppressed.BufferConfig.unbounded().shutDownWhenFull()));


        KStream<Windowed<String>, Long> windowedLongKStream = likesCountsMinuteTable.toStream();

        windowedLongKStream.print(Printed.<Windowed<String>, Long>toSysOut().withLabel("post-with-counts-minuted"));

        windowedLongKStream.to("test", Produced.with(windowedSerde, Serdes.Long()));






        // comments count per post
//        KTable<Windowed<String>, Long> commentsCountsMinuteTable = commentsStream.selectKey((k, v) -> v.postId())
//                .groupByKey()
//                .windowedBy(tumblingWindow)
//                .count(Materialized.as("comments-windowed-counts"))
//                .suppress(Suppressed.untilWindowCloses(Suppressed.BufferConfig.unbounded().shutDownWhenFull()));

//        KTable<Windowed<String>, PostCounts> postCountsJoinedPerMinute = likesCountsMinuteTable.join(commentsCountsMinuteTable, PostCounts::new);
//        KStream<Windowed<String>, PostCounts> windowedPostCountsKStream = postCountsJoinedPerMinute.toStream();

//        windowedPostCountsKStream.to("posts-counts-minute-window");



//        postCountsJoinedPerMinute;
//
////        postCountsJoinedPerMinute.groupBy((k, v) -> k.key());
//
////        KTable<String, PostCounts> postCountsJoined = likesCountsTable.join(commentsCountsTable, PostCounts::new);
//
//        // Enrich posts with count data:
//        KTable<String, Post> postsTable = builder.table("posts", Consumed.with(Serdes.String(), postSerde).withTimestampExtractor(new PostTimestampExtractor()));
//
//        // final table
//        KTable<String, PostWithCounts> postWithCountsTable = postCountsJoinedPerMinute.join(postsTable, (postCounts, post) -> new PostWithCounts(
//                post.id(),
//                post.createdAt(),
//                post.text(),
//                post.userId(),
//                postCounts.likesCount(),
//                postCounts.commentsCount()
//        ));
//
//        KStream<String, PostWithCounts> postWithCountsKStream = postWithCountsTable.toStream();
//        postWithCountsKStream.to("posts-with-counts", Produced.with(Serdes.String(), postsWithCountsSerde));
//
//        postWithCountsKStream.print(Printed.<String, PostWithCounts>toSysOut().withLabel("post-with-counts"));
    }
}
