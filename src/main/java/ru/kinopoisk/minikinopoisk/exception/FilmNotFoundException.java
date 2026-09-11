package ru.kinopoisk.minikinopoisk.exception;

public class FilmNotFoundException extends RuntimeException {

    public FilmNotFoundException(Long id) {
        super("Фильм с id " + id + " не найден");
    }
}