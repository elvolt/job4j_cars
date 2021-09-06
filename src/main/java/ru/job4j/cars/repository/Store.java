package ru.job4j.cars.repository;

import ru.job4j.cars.model.Post;

import java.util.Collection;

public interface Store {
    Collection<Post> getAllPosts();

    Collection<Post> getPostsForLastDay();

    Collection<Post> getPostsByMark(String mark);
}