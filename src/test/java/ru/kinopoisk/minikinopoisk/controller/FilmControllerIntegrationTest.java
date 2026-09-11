package ru.kinopoisk.minikinopoisk;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FilmControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getFilms_returnsOkWithJson() throws Exception {
        mockMvc.perform(get("/api/films"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getFilmById_whenFilmNotFound_returns404() throws Exception {
        mockMvc.perform(get("/api/films/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void addFilm_viaApi_returnsCreated() throws Exception {
        String payload = "{\"title\":\"Test Film\",\"year\":2024,\"rating\":9.5}";

        mockMvc.perform(post("/api/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Film"))
                .andExpect(jsonPath("$.year").value(2024))
                .andExpect(jsonPath("$.rating").value(9.5));
    }

    @Test
    void addFilm_withInvalidYear_returns400() throws Exception {
        String payload = "{\"title\":\"Invalid Film\",\"year\":1800,\"rating\":8.0}";

        mockMvc.perform(post("/api/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addFilm_withEmptyTitle_returns400() throws Exception {
        String payload = "{\"title\":\"\",\"year\":2024,\"rating\":8.0}";

        mockMvc.perform(post("/api/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateFilm_whenFilmNotFound_returns404() throws Exception {
        String payload = "{\"title\":\"Updated Film\",\"year\":2024,\"rating\":9.0}";

        mockMvc.perform(put("/api/films/999999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteFilm_whenFilmNotFound_returns404() throws Exception {
        mockMvc.perform(delete("/api/films/999999"))
                .andExpect(status().isNotFound());
    }

}