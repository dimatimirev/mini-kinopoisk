package ru.kinopoisk.minikinopoisk;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FilmRepository extends JpaRepository<Film, Long> {
    // Если нужны кастомные запросы — можно добавить здесь
}