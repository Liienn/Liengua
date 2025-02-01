package com.example.liengua;

import java.util.ArrayList;
import java.util.List;

public class Collection {

    private final String name;
    private final String description;
    private final List<Collection> subCollections;
    private final boolean isTopLevel;

    // Constructor for top-level collections with description
    public Collection(String name, String description) {
        this.name = name;
        this.description = description;
        this.subCollections = new ArrayList<>();
        this.isTopLevel = true;
    }

    // Constructor for top-level collections without description (default description)
    public Collection(String name) {
        this(name, "No description");
    }

    // Constructor for subcollections
    public Collection(String name, String description, boolean isTopLevel) {
        this.name = name;
        this.description = description;
        this.subCollections = new ArrayList<>();
        this.isTopLevel = isTopLevel;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<Collection> getSubCollections() {
        return subCollections;
    }

    public boolean isTopLevel() {
        return isTopLevel;
    }

    public void addSubCollection(Collection collection) {
        if (isTopLevel) {
            subCollections.add(collection);
        } else {
            throw new UnsupportedOperationException("Subcollections cannot have further subcollections.");
        }
    }

    public void removeSubCollection(Collection collection) {
        if (isTopLevel) {
            subCollections.remove(collection);
        } else {
            throw new UnsupportedOperationException("Subcollections cannot have further subcollections.");
        }
    }

    public boolean containsSubCollection(Collection collection) {
        return subCollections.contains(collection);
    }

    public Collection findSubCollectionByName(String name) {
        for (Collection collection : subCollections) {
            if (collection.getName().equals(name)) {
                return collection;
            }
        }
        return null;
    }
}