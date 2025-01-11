package com.example.liengua;

public class DictionaryEntry {
    private String english;
    private String spanish;
    private String russian;
    private String dutch;

    // Getters and setters
    public String getSentence() {
        return english;
    }

    public void setSentence(String english) {
        this.english = english;
    }

    public String getTranslationSpanish() {
        return spanish;
    }

    public void setTranslationSpanish(String translationSpanish) {
        this.spanish = translationSpanish;
    }

    public String getTranslationRussian() {
        return russian;
    }

    public void setTranslationRussian(String translationRussian) {
        this.russian = translationRussian;
    }

    public String getTranslationDutch() {
        return dutch;
    }

    public void setTranslationDutch(String translationDutch) {
        this.dutch = translationDutch;
    }
}
