package com.alura.literalura.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GutendexMappingTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void mapsGutendexSnakeCaseFields() throws Exception {
        String json = "{\"results\":[{\"id\":1342,\"title\":\"Pride and Prejudice\",\"authors\":[{\"name\":\"Austen, Jane\",\"birth_year\":1775,\"death_year\":1817}],\"languages\":[\"en\"],\"download_count\":12345}]}";

        GutenDexResponse response = objectMapper.readValue(json, GutenDexResponse.class);
        assertNotNull(response);
        assertEquals(1, response.getResults().size());

        BookDto book = response.getResults().get(0);
        assertEquals(1342L, book.getId());
        assertEquals(12345, book.getDownloadCount());
        assertEquals("en", book.getLanguages().get(0));
        assertEquals("Austen, Jane", book.getAuthors().get(0).getName());
        assertEquals(1775, book.getAuthors().get(0).getBirthYear());
        assertEquals(1817, book.getAuthors().get(0).getDeathYear());
    }
}
