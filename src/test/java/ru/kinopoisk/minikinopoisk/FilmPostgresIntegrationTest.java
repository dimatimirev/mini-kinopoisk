package ru.kinopoisk.minikinopoisk;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
@Transactional
class FilmPostgresIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("kinopoisk_test")
                    .withUsername("test")
                    .withPassword("test");

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");

        registry.add(
                "spring.jpa.hibernate.ddl-auto",
                () -> "none"
        );

        registry.add(
                "spring.flyway.enabled",
                () -> "true"
        );

        registry.add(
                "spring.flyway.locations",
                () -> "classpath:db/migration"
        );
    }

    @Autowired
    private FilmRepository filmRepository;

    @Test
    void postgresContainer_shouldStartAndRunFlywayMigrations() {
        assertThat(postgres.isRunning()).isTrue();

        assertThat(filmRepository.count()).isEqualTo(15);
    }

    @Test
    void repository_shouldCreateAndFindFilmInPostgres() {
        Film film = new Film(
                null,
                "Testcontainers Film",
                2025,
                8.9
        );

        Film savedFilm = filmRepository.save(film);

        assertThat(savedFilm.getId()).isNotNull();

        Film foundFilm = filmRepository.findById(savedFilm.getId())
                .orElseThrow();

        assertThat(foundFilm.getTitle()).isEqualTo("Testcontainers Film");
        assertThat(foundFilm.getYear()).isEqualTo(2025);
        assertThat(foundFilm.getRating()).isEqualTo(8.9);
    }

    @Test
    void repository_shouldUpdateFilmInPostgres() {
        Film film = new Film(
                null,
                "Film Before Update",
                2020,
                7.0
        );

        Film savedFilm = filmRepository.save(film);

        savedFilm.setTitle("Film After Update");
        savedFilm.setYear(2021);
        savedFilm.setRating(8.5);

        filmRepository.save(savedFilm);

        Film updatedFilm = filmRepository.findById(savedFilm.getId())
                .orElseThrow();

        assertThat(updatedFilm.getTitle()).isEqualTo("Film After Update");
        assertThat(updatedFilm.getYear()).isEqualTo(2021);
        assertThat(updatedFilm.getRating()).isEqualTo(8.5);
    }

    @Test
    void repository_shouldDeleteFilmFromPostgres() {
        Film film = new Film(
                null,
                "Film For Delete",
                2019,
                6.5
        );

        Film savedFilm = filmRepository.save(film);

        Long filmId = savedFilm.getId();

        assertThat(filmRepository.findById(filmId)).isPresent();

        filmRepository.deleteById(filmId);

        assertThat(filmRepository.findById(filmId)).isEmpty();
    }
}