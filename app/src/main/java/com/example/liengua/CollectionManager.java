package com.example.liengua;

import static androidx.core.app.ActivityCompat.recreate;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CollectionManager {

    private static final String PREFS_NAME = "collections_prefs";
    private static final String COLLECTIONS_KEY = "collections";

    private static final Handler handler = new Handler(Looper. getMainLooper());

    public static void saveCollection(Context context, List<CollectionLiengua> collections, DictionaryAdapter adapter) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(collections);
        editor.putString(COLLECTIONS_KEY, json);
        editor.apply();
        if(adapter != null) {
            handler.postDelayed(adapter::notifyDataSetChanged, 300);
        }
    }

    public static void saveCollection(Context context, CollectionLiengua collection,  List<DictionaryEntry> entries, DictionaryAdapter adapter) {
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
        if(adapter != null) {
            adapter.updateCollectionList(entries);
        }
        saveCollection(context, collections, adapter);
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

        saveCollection(context, collections, null);
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

    @SuppressLint("SetTextI18n")
    public static void showEditCollectionDialog(Context context, CollectionLiengua targetCollection) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_create_collection, null);
        builder.setView(dialogView);

        String previousName = targetCollection.getName();
        String previousDescription = targetCollection.getDescription();
        TextView titleTextView = dialogView.findViewById(R.id.collection_dialog_title);
        titleTextView.setText("Edit collection");
        EditText nameEditText = dialogView.findViewById(R.id.name_edit_text);
        nameEditText.setText(previousName);
        EditText descriptionEditText = dialogView.findViewById(R.id.description_edit_text);
        descriptionEditText.setText(previousDescription);

        builder.setPositiveButton("Edit", (dialog, which) -> {
            String name = nameEditText.getText().toString();
            String description = descriptionEditText.getText().toString();
            boolean editValid = false;
            for (CollectionLiengua col : getCollections(context)) {
                if (name.equals(previousName)) {
                    editValid = true;
                    break;
                } else if(col.getName().equals(name)) {
                    Toast.makeText(context, "This collection name already exists", Toast.LENGTH_SHORT).show();
                    break;
                } else {
                    editValid = true;
                }
            }
            if (editValid) {
                targetCollection.setName(name);
                if(!description.equals(previousDescription)) {
                    targetCollection.setDescription(description);
                }
                if(!name.equals(previousName)) {
                    for(CollectionLiengua col: CollectionManager.getCollections(context)) {
                        if(col.getName().equals(previousName)) {
                            deleteCollection(context, col);
                            break;
                        }
                    }
                }
                saveCollection(context, targetCollection);
                Toast.makeText(context, "Collection updated", Toast.LENGTH_SHORT).show();
                recreate((Activity) context);
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
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
                    if(entry != null) {
                        col.addEntry(entry);
                    }
                    saveCollection(context, getCollections(context),null); // Save the updated collections
                    Toast.makeText(context, "Collection updated", Toast.LENGTH_SHORT).show();
                    break;
                }
            }
            if (!collectionExists) {
                CollectionLiengua collection = new CollectionLiengua(name, description);
                if(entry != null) {
                    collection.addEntry(entry);
                }
                saveCollection(context, collection);
                Toast.makeText(context, "Collection created", Toast.LENGTH_SHORT).show();
                if(entry == null) {
                    recreate((Activity) context);
                }
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void addEntryToCollection(Context context, DictionaryEntry entry, CollectionLiengua collection) {
        List<CollectionLiengua> collections = getCollections(context);
        for (CollectionLiengua col : collections) {
            List<String> collEntries = new ArrayList<>();
            if(col.getName().equals(collection.getName())) {
                for (DictionaryEntry e : collection.getEntries()) {
                    collEntries.add(e.getSentence());
                }
                if (!collEntries.contains(entry.getSentence())) {
                    col.addEntry(entry);
                }
                break;
            }
        }
        saveCollection(context, collections, null);
    }

    public static void removeEntryFromCollection(Context context, DictionaryEntry entry, CollectionLiengua collection) {
        List<CollectionLiengua> collections = getCollections(context);
        for (CollectionLiengua col : collections) {
            if (col.getName().equals(collection.getName())) {
                col.removeEntry(entry);
                break;
            }
        }
        saveCollection(context, collections, null);
    }
}