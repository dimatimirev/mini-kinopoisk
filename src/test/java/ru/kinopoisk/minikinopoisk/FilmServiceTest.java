package ru.kinopoisk.minikinopoisk;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.kinopoisk.minikinopoisk.exception.FilmNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FilmServiceTest {

    @Mock
    private FilmRepository filmRepository;

    @InjectMocks
    private FilmService filmService;

    @Test
    void getAllFilms_returnsFilms() {

        List<Film> films = List.of(
                new Film(1L, "Интерстеллар", 2014, 8.7),
                new Film(2L, "Матрица", 1999, 8.7)
        );

        when(filmRepository.findAll()).thenReturn(films);

        List<Film> result = filmService.getAllFilms();

        assertEquals(2, result.size());
        assertEquals("Интерстеллар", result.get(0).getTitle());

        verify(filmRepository, times(1)).findAll();
    }

    @Test
    void getFilmById_returnsFilm() {

        Film film = new Film(
                1L,
                "Интерстеллар",
                2014,
                8.7
        );

        when(filmRepository.findById(1L))
                .thenReturn(Optional.of(film));

        Film result = filmService.getFilmById(1L);

        assertEquals(1L, result.getId());
        assertEquals("Интерстеллар", result.getTitle());
        assertEquals(2014, result.getYear());
        assertEquals(8.7, result.getRating());

        verify(filmRepository).findById(1L);
    }

    @Test
    void getFilmById_throwsExceptionWhenFilmNotFound() {

        when(filmRepository.findById(999L))
                .thenReturn(Optional.empty());

        FilmNotFoundException exception = assertThrows(
                FilmNotFoundException.class,
                () -> filmService.getFilmById(999L)
        );

        assertEquals(
                "Фильм с id 999 не найден",
                exception.getMessage()
        );

        verify(filmRepository).findById(999L);
    }

    @Test
    void addFilm_savesFilm() {

        Film film = new Film(
                null,
                "Начало",
                2010,
                8.8
        );

        Film savedFilm = new Film(
                1L,
                "Начало",
                2010,
                8.8
        );

        when(filmRepository.save(film))
                .thenReturn(savedFilm);

        Film result = filmService.addFilm(film);

        assertEquals(1L, result.getId());
        assertEquals("Начало", result.getTitle());

        verify(filmRepository).save(film);
    }

    @Test
    void updateFilm_updatesExistingFilm() {

        Film existingFilm = new Film(
                1L,
                "Старое название",
                2000,
                7.0
        );

        Film updatedFilm = new Film(
                null,
                "Новое название",
                2024,
                9.0
        );

        when(filmRepository.findById(1L))
                .thenReturn(Optional.of(existingFilm));

        when(filmRepository.save(existingFilm))
                .thenReturn(existingFilm);

        Film result = filmService.updateFilm(1L, updatedFilm);

        assertEquals("Новое название", result.getTitle());
        assertEquals(2024, result.getYear());
        assertEquals(9.0, result.getRating());

        verify(filmRepository).findById(1L);
        verify(filmRepository).save(existingFilm);
    }

    @Test
    void updateFilm_throwsExceptionWhenFilmNotFound() {

        when(filmRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                FilmNotFoundException.class,
                () -> filmService.updateFilm(
                        999L,
                        new Film(null, "Test", 2024, 8.0)
                )
        );

        verify(filmRepository).findById(999L);
        verify(filmRepository, never()).save(any());
    }

    @Test
    void deleteFilm_deletesExistingFilm() {

        Film film = new Film(
                1L,
                "Интерстеллар",
                2014,
                8.7
        );

        when(filmRepository.findById(1L))
                .thenReturn(Optional.of(film));

        filmService.deleteFilm(1L);

        verify(filmRepository).findById(1L);
        verify(filmRepository).delete(film);
    }

    @Test
    void deleteFilm_throwsExceptionWhenFilmNotFound() {

        when(filmRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                FilmNotFoundException.class,
                () -> filmService.deleteFilm(999L)
        );

        verify(filmRepository).findById(999L);
        verify(filmRepository, never()).delete(any());
    }
}
