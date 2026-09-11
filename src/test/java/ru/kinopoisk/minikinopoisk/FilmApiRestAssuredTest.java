package ru.kinopoisk.minikinopoisk;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
class FilmApiRestAssuredTest {

    @LocalServerPort
    private int port;

    @Test
    void getAllFilms_shouldReturn200() {
        given()
                .port(port)
                .when()
                .get("/api/films")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON);
    }

    @Test
    void getFilmById_shouldReturn200() {
        Long filmId = createFilm();

        given()
                .port(port)
                .when()
                .get("/api/films/" + filmId)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(filmId.intValue()))
                .body("title", equalTo("Setup Film"))
                .body("year", equalTo(2000))
                .body("rating", equalTo(7.5f));
    }

    @Test
    void getFilmById_nonExisting_shouldReturn404() {
        given()
                .port(port)
                .when()
                .get("/api/films/999999")
                .then()
                .statusCode(404);
    }

    @Test
    void createFilm_shouldReturn201() {
        String requestBody = """
                {
                    "title": "REST Assured Film",
                    "year": 2024,
                    "rating": 8.5
                }
                """;

        given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/films")
                .then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .body("id", notNullValue())
                .body("title", equalTo("REST Assured Film"))
                .body("year", equalTo(2024))
                .body("rating", equalTo(8.5f));
    }

    @Test
    void createFilm_withInvalidYear_shouldReturn400() {
        String requestBody = """
                {
                    "title": "Invalid Film",
                    "year": 1800,
                    "rating": 8.5
                }
                """;

        given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/films")
                .then()
                .statusCode(400);
    }

    @Test
    void createFilm_withEmptyTitle_shouldReturn400() {
        String requestBody = """
                {
                    "title": "",
                    "year": 2024,
                    "rating": 8.5
                }
                """;

        given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/films")
                .then()
                .statusCode(400);
    }

    @Test
    void updateFilm_shouldReturn200() {
        Long filmId = createFilm();

        String requestBody = """
                {
                    "title": "Updated REST Assured Film",
                    "year": 2024,
                    "rating": 9.0
                }
                """;

        given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put("/api/films/" + filmId)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(filmId.intValue()))
                .body("title", equalTo("Updated REST Assured Film"))
                .body("year", equalTo(2024))
                .body("rating", equalTo(9.0f));
    }

    @Test
    void deleteFilm_shouldReturn204() {
        Long filmId = createFilm();

        given()
                .port(port)
                .when()
                .delete("/api/films/" + filmId)
                .then()
                .statusCode(204);
    }

    private Long createFilm() {
        String requestBody = """
                {
                    "title": "Setup Film",
                    "year": 2000,
                    "rating": 7.5
                }
                """;

        Integer filmId = given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/films")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        return filmId.longValue();
    }
}