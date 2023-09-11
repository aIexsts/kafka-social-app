package org.example.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

public class TopFivePosts implements Iterable<PostWithCounts> {
    private final Map<String, PostWithCounts> currentPosts = new HashMap<>();

    private final TreeSet<PostWithCounts> topFive = new TreeSet<>((o1, o2) -> {
        final Long o1Plays = o1.likesCount();
        final Long o2Plays = o2.likesCount();

        final int result = o2Plays.compareTo(o1Plays);
        if (result != 0) {
            return result;
        }
        final String post1Id = o1.id();
        final String post2Id = o2.id();
        return post1Id.compareTo(post2Id);
    });

    @Override
    public String toString() {
        return currentPosts.toString();
    }

    public void add(final PostWithCounts postWithCounts) {
        if (currentPosts.containsKey(postWithCounts.id())) {
            topFive.remove(currentPosts.remove(postWithCounts.id()));
        }
        topFive.add(postWithCounts);
        currentPosts.put(postWithCounts.id(), postWithCounts);
        if (topFive.size() > 5) {
            final PostWithCounts last = topFive.last();
            currentPosts.remove(last.id());
            topFive.remove(last);
        }
    }

    void remove(final PostWithCounts value) {
        topFive.remove(value);
        currentPosts.remove(value.id());
    }

    @Override
    public Iterator<PostWithCounts> iterator() {
        return topFive.iterator();
    }
}
