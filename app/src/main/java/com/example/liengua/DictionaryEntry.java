package com.example.liengua;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DictionaryEntry {
    private final String english;
    private final String spanish;
    private final String russian;
    private final String dutch;
    private Map<String, List<String>> alternatives;

    // Getters and setters
    public DictionaryEntry(String english, String spanish, String russian, String dutch) {
        this.english = english;
        this.spanish = spanish;
        this.russian = russian;
        this.dutch = dutch;
        this.alternatives = alternatives != null ? alternatives : new HashMap<>();
        alternatives.put("dutch", new ArrayList<>());
        alternatives.put("spanish", new ArrayList<>());
        alternatives.put("russian", new ArrayList<>());
        if (entry.alternatives != null) {
            alternatives.putAll(entry.alternatives);
        }
    }

    // Parsing the JSON response
    Gson gson = new Gson();
    DictionaryEntry entry = gson.fromJson(jsonResponse, DictionaryEntry.class);


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
