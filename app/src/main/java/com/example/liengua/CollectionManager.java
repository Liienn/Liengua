package com.example.liengua;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CollectionManager {

    private static final String PREFS_NAME = "collections_prefs";
    private static final String COLLECTIONS_KEY = "collections";

    public static void saveCollection(Context context, Collection collection) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        List<Collection> collections = getCollections(context);
        collections.add(collection);

        Gson gson = new Gson();
        String json = gson.toJson(collections);
        editor.putString(COLLECTIONS_KEY, json);
        editor.apply();
    }

    public static List<Collection> getCollections(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = sharedPreferences.getString(COLLECTIONS_KEY, null);

        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<Collection>>() {}.getType();
            return gson.fromJson(json, type);
        } else {
            return new ArrayList<>();
        }
    }

    public static void createCollection(Context context, List<DictionaryEntry> dictionaryEntryList, int i) {
        showCreateCollectionDialog(context, dictionaryEntryList.get(i));
    }

    public static void deleteCollection(Context context, Collection collection) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        List<Collection> collections = getCollections(context);
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

            Collection collection = new Collection(name, description);
            collection.addEntry(entry); // Add the entry to the collection

            saveCollection(context, collection);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}