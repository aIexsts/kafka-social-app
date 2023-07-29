package org.example.model;

public record Comment(
        String id,
        String postId,
        String userId,
        String text
){

}
