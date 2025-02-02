package com.example.liengua;

import java.util.ArrayList;
import java.util.List;

public class Collection {
    private String name;
    private String description;
    private final boolean isTopLevel;
    private final List<DictionaryEntry> entries;

    // Constructor for top-level collections with description
    public Collection(String name, String description) {
        this.name = name;
        this.description = description;
        this.isTopLevel = true;
        this.entries = new ArrayList<>();
    }

    // Constructor for top-level collections without description (default description)
    public Collection(String name) {
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

    public boolean isTopLevel() {
        return isTopLevel;
    }

    public void addEntry(DictionaryEntry entry) {
        entries.add(entry);
    }

    public List<DictionaryEntry> getEntries() {
        return entries;
    }
}