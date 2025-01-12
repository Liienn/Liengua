package com.example.liengua;

public class DictionaryEntry {
    private String english;
    private String spanish;
    private String russian;
    private String dutch;

    // Constructor
    public DictionaryEntry(String english, String spanish, String russian, String dutch) {
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

    public String getTranslationDutch() {
        return dutch;
    }
}
