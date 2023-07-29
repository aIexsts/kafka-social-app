package org.example.model;

import java.time.Instant;

public record PostWithCounts(
        String id,
        Instant createdAt,
        String text,
        String userId,
        Long likesCount,
        Long commentsCount
) {
}
