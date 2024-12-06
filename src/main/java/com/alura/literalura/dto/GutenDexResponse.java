package com.alura.literalura.dto;

import java.util.List;

public class GutenDexResponse {
    private List<BookDto> results;

    // Getters y Setters
    public List<BookDto> getResults() {
        return results;
    }

    public void setResults(List<BookDto> results) {
        this.results = results;
    }
}
