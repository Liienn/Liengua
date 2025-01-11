package com.example.liengua;

public class Translation {
    private final String russian;
    private final String spanish;
    private final String dutch;
    private final String english;

    public Translation(String english, String spanish, String russian, String dutch) {
        this.english = english;
        this.spanish = spanish;
        this.russian = russian;
        this.dutch = dutch;
    }

    // Getters
    public String getSentence() {
        return english;
    }

    public String getTranslationSpanish() {
        return spanish;
    }

    public String getTranslationRussian() {
        return russian;
    }

    public String getTranslationEnglish() {
        return english;
    }

    public String getTranslationDutch() {
        return dutch;
    }
}
