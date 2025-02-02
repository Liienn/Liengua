package com.example.liengua;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CollectionManager {

    private static final String PREFS_NAME = "collections_prefs";
    private static final String COLLECTIONS_KEY = "collections";

    public static void saveCollection(Context context, List<CollectionLiengua> collections) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(collections);
        editor.putString(COLLECTIONS_KEY, json);
        editor.apply();
    }

    public static void saveCollection(Context context, CollectionLiengua collection,  List<DictionaryEntry> entries) {
        List<CollectionLiengua> collections = getCollections(context);
        boolean collectionExists = false;

        for (int i = 0; i < collections.size(); i++) {
            CollectionLiengua col = collections.get(i);
            if (col.getName().equals(collection.getName())) {
                col.setEntries(entries);
                collections.set(i, collection);
                collectionExists = true;
                break;
            }
        }

        if (!collectionExists) {
            collections.add(collection);
        }

        saveCollection(context, collections);
    }


    public static void saveCollection(Context context, CollectionLiengua collection) {
        List<CollectionLiengua> collections = getCollections(context);
        boolean collectionExists = false;

        for (int i = 0; i < collections.size(); i++) {
            CollectionLiengua col = collections.get(i);
            if (col.getName().equals(collection.getName())) {
                collections.set(i, collection);
                collectionExists = true;
                break;
            }
        }

        if (!collectionExists) {
            collections.add(collection);
        }

        saveCollection(context, collections);
    }

    public static List<CollectionLiengua> getCollections(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = sharedPreferences.getString(COLLECTIONS_KEY, null);

        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<CollectionLiengua>>() {}.getType();
            return gson.fromJson(json, type);
        } else {
            return new ArrayList<>();
        }
    }

    public static void createCollection(Context context, List<DictionaryEntry> dictionaryEntryList, int i) {
        if (i < 0 || i >= dictionaryEntryList.size()) {
            Toast.makeText(context, "Invalid entry index", Toast.LENGTH_SHORT).show();
            return;
        }
        showCreateCollectionDialog(context, dictionaryEntryList.get(i));
    }

    public static void deleteCollection(Context context, CollectionLiengua collection) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        List<CollectionLiengua> collections = getCollections(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            collections.removeIf(c -> c.getName().equals(collection.getName()) && c.getDescription().equals(collection.getDescription()));
        }

        Gson gson = new Gson();
        String json = gson.toJson(collections);
        editor.putString(COLLECTIONS_KEY, json);
        editor.apply();
    }
    public static void showCreateCollectionDialog(Context context, DictionaryEntry entry) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_create_collection, null);
        builder.setView(dialogView);

        EditText nameEditText = dialogView.findViewById(R.id.name_edit_text);
        EditText descriptionEditText = dialogView.findViewById(R.id.description_edit_text);

        builder.setPositiveButton("Create", (dialog, which) -> {
            String name = nameEditText.getText().toString();
            String description = descriptionEditText.getText().toString();
            boolean collectionExists = false;
            for (CollectionLiengua col : getCollections(context)) {
                if (col.getName().equals(name)) {
                    collectionExists = true;
                    col.setDescription(description); // Update the description if the collection already exists
                    col.addEntry(entry); // Add the entry to the existing collection
                    saveCollection(context, getCollections(context)); // Save the updated collections
                    Toast.makeText(context, "Collection updated", Toast.LENGTH_SHORT).show();
                    break;
                }
            }
            if (!collectionExists) {
                CollectionLiengua collection = new CollectionLiengua(name, description);
                collection.addEntry(entry);
                saveCollection(context, collection);
                Toast.makeText(context, "Collection created", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void addEntryToCollection(Context context, DictionaryEntry entry, CollectionLiengua collection) {
        List<CollectionLiengua> collections = getCollections(context);
        for (CollectionLiengua col : collections) {
            if (col.getName().equals(collection.getName())) {
                col.addEntry(entry);
                break;
            }
        }
        saveCollection(context, collections);
    }

    public static void removeEntryFromCollection(Context context, DictionaryEntry entry, CollectionLiengua collection) {
        List<CollectionLiengua> collections = getCollections(context);
        for (CollectionLiengua col : collections) {
            if (col.getName().equals(collection.getName())) {
                col.getEntries().remove(entry);
                break;
            }
        }
        saveCollection(context, collections);
    }
}