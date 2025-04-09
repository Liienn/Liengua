package com.example.liengua;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DictionaryEntry implements Serializable {
    private final Integer id;
    private final String english;
    private final String spanish;
    private final String russian;
    private final String dutch;
    private Map<String, List<String>> alternatives;
    final private List<String> tags;
    final private List<String> otherKeyWords;
    public boolean isFavorite, isInCollection;
    private List<String> collectionList;

    // Getters and setters
    public DictionaryEntry(String english, String spanish, String russian, String dutch) {
        this.id = 0;
        this.english = english;
        this.spanish = spanish;
        this.russian = russian;
        this.dutch = dutch;
        this.alternatives = alternatives != null ? alternatives : new HashMap<>();
        this.tags = new ArrayList<>();
        this.otherKeyWords = new ArrayList<>();
        this.isFavorite = false;
        this.isInCollection = false;
        this.collectionList = new ArrayList<>();
    }

    // Getters
    public int getId() { return this.id; }
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

    public List<String> getTags() { return this.tags; }
    public List<String> getOtherKeyWords() { return this.otherKeyWords; }

    public List<String> getCollectionList() {return collectionList; }

    public void addToCollection(String collectionName) {
        if (this.collectionList == null) {
            this.collectionList = new ArrayList<>();
        }
        this.collectionList.add(collectionName);
        this.isInCollection = !getCollectionList().isEmpty();
    }

    public void removeFromCollection(String collectionName) {
            if (this.collectionList != null) {
                this.collectionList.remove(collectionName);
            } else {
                this.collectionList = new ArrayList<>();
            }
        this.isInCollection = !getCollectionList().isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DictionaryEntry that = (DictionaryEntry) o;
        return Objects.equals(getSentence(), that.getSentence());
    }

}
