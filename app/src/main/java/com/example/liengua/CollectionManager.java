package com.example.liengua;

import java.util.Collections;
import java.util.List;

public class CollectionManager {

    private static final List<Collection> collections = Collections.emptyList();

    public CollectionManager() {
        collections.add(new Collection("Default Collection 1")); // Add default collection
        collections.add(new Collection("Default Collection 2"));
        collections.add(new Collection("Default Collection 3"));
        collections.add(new Collection("Default Collection 4"));
    }

    public static List<Collection> getCollections() {
        return collections;
    }

    public void addCollection(Collection collection) {
        collections.add(collection);
    }

    public void removeCollection(Collection collection) {
        collections.remove(collection);
    }

    public boolean containsCollection(Collection collection) {
        return collections.contains(collection);
    }

    public Collection findCollectionByName(String name) {
        for (Collection collection : collections) {
            if (collection.getName().equals(name)) {
                return collection;
            }
        }
        return null;
    }
}