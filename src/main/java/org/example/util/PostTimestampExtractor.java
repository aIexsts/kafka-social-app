package org.example.util;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.processor.TimestampExtractor;
import org.example.model.Post;

import java.time.format.DateTimeParseException;

public class PostTimestampExtractor implements TimestampExtractor {
    @Override
    public long extract(ConsumerRecord<Object, Object> record, long partitionTime) {
        try {
            Post event = (Post) record.value();
            if (event != null && event.createdAt() != null) {
                return event.createdAt().toEpochMilli();
            }
        } catch (DateTimeParseException ignore) {}

        return partitionTime;
    }
}
