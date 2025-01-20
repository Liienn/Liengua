package com.example.liengua;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "FavoritesPrefs";
    public static final String FAVORITES_KEY = "favorites";
    @SuppressLint("StaticFieldLeak")
    private static DictionaryAdapter dictionaryAdapter;
    private static List<DictionaryEntry> favoritesList;
    private static final Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorites_layout);

        initializeFavorites(this);

        RecyclerView favoritesListView = findViewById(R.id.favorites_list);
        ImageButton homeButton = findViewById(R.id.home_button);
        ImageButton menuButton = findViewById(R.id.menu_button);
        ImageButton clearFavoritesButton = findViewById(R.id.clear_favorites_button);

        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(FavoritesActivity.this, MainActivity.class);
            startActivity(intent);
        });

        menuButton.setOnClickListener(this::showMenu);

        clearFavoritesButton.setOnClickListener(v -> clearFavorites(this));

        // Set up the RecyclerView with the favorites
        dictionaryAdapter = new DictionaryAdapter(this ,favoritesList);
        favoritesListView.setLayoutManager(new LinearLayoutManager(this));
        favoritesListView.setAdapter(dictionaryAdapter);
        dictionaryAdapter.updateFavoritesList(loadFavorites(this));
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onResume() {
        super.onResume();
        // Reload favorites list and notify adapter
        favoritesList = loadFavorites(this);
        dictionaryAdapter.updateFavoritesList(favoritesList);
        dictionaryAdapter.notifyDataSetChanged();
    }
    private void showMenu(View anchorView) {
        MenuHandler menuHandler = new MenuHandler(this);
        menuHandler.showMenu(anchorView);
    }

    static List<DictionaryEntry> loadFavorites(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = sharedPreferences.getString(FAVORITES_KEY, null);
        if (json != null) {
            Type type = new TypeToken<List<DictionaryEntry>>() {}.getType();
            return gson.fromJson(json, type);
        } else {
            return new ArrayList<>();
        }
    }

    private static void saveFavorites(Context context, List<DictionaryEntry> list) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String json = gson.toJson(list);
        editor.putString(FAVORITES_KEY, json);
        Log.d("FavoritesActivity", "Saved favorites: " + json);
        editor.apply();
        if(dictionaryAdapter != null) {
            dictionaryAdapter.updateFavoritesList(list);
        }
    }

    static void initializeFavorites(Context context) {
        if (favoritesList == null) {
            favoritesList = loadFavorites(context);
        }
        if (FavoritesActivity.favoritesList == null) {
            FavoritesActivity.favoritesList = loadFavorites(context);
        }
    }

    // Example method to add a favorite (you can call this method when a user adds a favorite)
    @SuppressLint("NotifyDataSetChanged")
    static void addFavorite(DictionaryEntry favorite, Context context, List<DictionaryEntry> list) {
        if (list != null && !containsEntry(list, favorite)) {
            list.add(favorite);
            favorite.isFavorite = true;
            Log.d("FavoritesActivity", "Added favorite: " + favorite);
            initializeFavorites(context);
            if(dictionaryAdapter != null) {
                dictionaryAdapter.notifyDataSetChanged();
            }
            saveFavorites(context, list);
        }
    }

    private static boolean containsEntry(List<DictionaryEntry> list, DictionaryEntry entry) {
        for (DictionaryEntry e : list) {
            if (e.getSentence().equals(entry.getSentence())) {
                return true;
            }
        }
        return false;
    }

    // Example method to remove a favorite (you can call this method when a user removes a favorite)
    @SuppressLint("NotifyDataSetChanged")
    static void removeFavorite(DictionaryEntry favorite, Context context, List<DictionaryEntry> list) {
        if (list != null && containsEntry(list, favorite)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                list.removeIf(entry -> entry.getSentence().equals(favorite.getSentence()));
            }
            favorite.isFavorite = false;
            Log.d("FavoritesActivity", "Removed favorite: " + favorite);
            dictionaryAdapter.notifyDataSetChanged();
            saveFavorites(context, list);
        }
    }

    // Method to clear all favorites
    @SuppressLint("NotifyDataSetChanged")
    public static void clearFavorites(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        favoritesList.clear();
        if (dictionaryAdapter != null) {
            dictionaryAdapter.notifyDataSetChanged();
        }
        Log.d("FavoritesActivity", "Cleared all favorites");
    }
}