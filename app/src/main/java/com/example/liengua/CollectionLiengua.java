package com.example.liengua;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CollectionLiengua implements Serializable {
    private String name;
    private String description;
    private List<DictionaryEntry> entries;

    // Constructor for top-level collections with description
    public CollectionLiengua(String name, String description) {
        this.name = name;
        this.description = description;
        this.entries = new ArrayList<>();
    }

    // Constructor for top-level collections without description (default description)
    public CollectionLiengua(String name) {
        this(name, "No description");
    }

    public String getName() {
        return name;
    }

    public void setName(String nameText) {
        name = nameText;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String descriptionText) {
        description = descriptionText;
    }

    public void addEntry(DictionaryEntry entry) {
        entries.add(entry);
        if (entry.getCollectionList() == null || entry.getCollectionList().isEmpty() || !entry.getCollectionList().contains(this.getName())) {
            entry.addToCollection(this.getName());
        }
    }

    public List<DictionaryEntry> getEntries() {
        return entries;
    }

    public void removeEntry(DictionaryEntry entry) {
        entries.remove(entry);
        entry.removeFromCollection(this.getName());
    }

    public void setEntries(List<DictionaryEntry> entries) {
        this.entries = entries;
    }

    public boolean containsEntry(DictionaryEntry entry) {
        List<String> listOfEntries = new ArrayList<>();
        for(DictionaryEntry e: getEntries()) {
            listOfEntries.add(e.getSentence());
        }
        return listOfEntries.contains(entry.getSentence());
    }
}