package org.example.model;

import java.time.Instant;

public record Post(
        String id,
        Instant createdAt,
        String text,
        String userId
){

}
