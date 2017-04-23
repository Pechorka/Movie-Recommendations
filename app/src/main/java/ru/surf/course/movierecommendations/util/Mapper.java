package ru.surf.course.movierecommendations.util;


public interface Mapper<T, V> {
    V map(T key);
}
