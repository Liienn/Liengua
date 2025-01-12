package com.example.liengua;

import java.util.List;
import java.util.Map;

public class DictionaryEntry {
    private final String english;
    private final String spanish;
    private final String russian;
    private final String dutch;
    private Map<String, List<String>> alternatives;

    // Getters and setters
    public Map<String, List<String>> getAlternatives() {
        return alternatives;
    }
    public DictionaryEntry(String english, String spanish, String russian, String dutch) {
        this.english = english;
        this.spanish = spanish;
        this.russian = russian;
        this.dutch = dutch;
    }
    public void setAlternatives(Map<String, List<String>> alternatives) {
        this.alternatives = alternatives;
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
