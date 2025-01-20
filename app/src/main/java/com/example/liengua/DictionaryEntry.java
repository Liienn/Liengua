package com.example.liengua;

import android.content.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DictionaryEntry {
    private final String english;
    private final String spanish;
    private final String russian;
    private final String dutch;
    private Map<String, List<String>> alternatives;
    public boolean isFavorite;
    private CollectionManager collectionManager;

    // Getters and setters
    public DictionaryEntry(String english, String spanish, String russian, String dutch) {
        this.english = english;
        this.spanish = spanish;
        this.russian = russian;
        this.dutch = dutch;
        this.alternatives = alternatives != null ? alternatives : new HashMap<>();
        this.isFavorite = false;
        this.collectionManager = new CollectionManager();
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

    public Map<String, List<String>> getAlternatives() {
        return alternatives;
    }

    public CollectionManager getCollectionManager() {
        return collectionManager;
    }


}
