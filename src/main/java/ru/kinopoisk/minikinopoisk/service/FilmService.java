package ru.kinopoisk.minikinopoisk;

import org.springframework.stereotype.Service;
import ru.kinopoisk.minikinopoisk.exception.FilmNotFoundException;

import java.util.List;

@Service
public class FilmService {

    private final FilmRepository filmRepository;

    public FilmService(FilmRepository filmRepository) {
        this.filmRepository = filmRepository;
    }

    public List<Film> getAllFilms() {
        return filmRepository.findAll();
    }

    public Film getFilmById(Long id) {
        return filmRepository.findById(id)
                .orElseThrow(() -> new FilmNotFoundException(id));
    }

    public Film addFilm(Film film) {
        return filmRepository.save(film);
    }

    public Film updateFilm(Long id, Film film) {
        Film existingFilm = getFilmById(id);

        existingFilm.setTitle(film.getTitle());
        existingFilm.setYear(film.getYear());
        existingFilm.setRating(film.getRating());

        return filmRepository.save(existingFilm);
    }

    public void deleteFilm(Long id) {
        Film film = getFilmById(id);
        filmRepository.delete(film);
    }
}