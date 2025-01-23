package com.example.liengua;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class FavoritesActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "FavoritesPrefs";
    public static final String FAVORITES_KEY = "favorites";
    @SuppressLint("StaticFieldLeak")
    private static DictionaryAdapter dictionaryAdapter;
    private static List<DictionaryEntry> favoritesList;
    private TextView emptyFavoritesMessage;
    private CheckBox spanishCheckBox, dutchCheckBox, russianCheckBox;
    private ImageButton randomizeButton, sortButton, refreshButton;
    private ImageView infoIcon;
    private static final Gson gson = new Gson();

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("FavoritesActivity", "onCreate called");
        setContentView(R.layout.favorites_layout);
        favoritesList = loadFavorites(this);
        List<DictionaryEntry> originalFavoritesList = favoritesList;
        randomizeButton = findViewById(R.id.randomize_button);
        sortButton = findViewById(R.id.sort_button);
        refreshButton = findViewById(R.id.refresh_button);
        spanishCheckBox = findViewById(R.id.spanish_checkbox);
        dutchCheckBox = findViewById(R.id.dutch_checkbox);
        russianCheckBox = findViewById(R.id.russian_checkbox);
        infoIcon = findViewById(R.id.info_icon1);
        infoIcon.setVisibility(View.GONE);
        RecyclerView favoritesListView = findViewById(R.id.favorites_list);
        ImageButton homeButton = findViewById(R.id.home_button);
        ImageButton menuButton = findViewById(R.id.menu_button);
        ImageButton clearFavoritesButton = findViewById(R.id.clear_favorites_button);
        emptyFavoritesMessage = findViewById(R.id.empty_favorites_message);
        ImageButton tuneButton = findViewById(R.id.tune_favorites_button);

        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(FavoritesActivity.this, MainActivity.class);
            startActivity(intent);
        });

        menuButton.setOnClickListener(this::showMenu);

        clearFavoritesButton.setOnClickListener(v -> clearFavorites(this));
        tuneButton.setOnClickListener(v -> {
            boolean showMoveButtons = !dictionaryAdapter.showMoveButtonsForEntry;
            dictionaryAdapter.setShowMoveButtons(showMoveButtons);
            clearFavoritesButton.setVisibility(showMoveButtons ? View.VISIBLE : View.GONE);
        });
        dictionaryAdapter = new DictionaryAdapter(this ,favoritesList);
        favoritesListView.setLayoutManager(new LinearLayoutManager(this));
        favoritesListView.setAdapter(dictionaryAdapter);
        dictionaryAdapter.updateFavoritesList(favoritesList);
        dictionaryAdapter.notifyDataSetChanged();
        updateEmptyMessageVisibility();
        
        if(!emptyFavoritesMessage.isShown()) {
            setupCheckBoxListeners();
            clearFavoritesButton.setVisibility(View.GONE);
            tuneButton.setVisibility(View.VISIBLE);
            refreshButton.setVisibility(View.GONE);
            RefreshButtonHandler refreshButtonHandler = new RefreshButtonHandler(FavoritesActivity.this);
            refreshButtonHandler.setupRefreshButton(refreshButton);
            RandomizeButtonHandler randomizeButtonHandler = new RandomizeButtonHandler(favoritesList, dictionaryAdapter);
            randomizeButtonHandler.setupRandomizeButton(randomizeButton, refreshButton);
            SortButtonHandler sortButtonHandler = new SortButtonHandler(favoritesList, dictionaryAdapter);
            sortButtonHandler.setupSortButton(sortButton, refreshButton);
        } else {
            spanishCheckBox.setVisibility(View.GONE);
            dutchCheckBox.setVisibility(View.GONE);
            russianCheckBox.setVisibility(View.GONE);
            randomizeButton.setVisibility(View.GONE);
            sortButton.setVisibility(View.GONE);
            refreshButton.setVisibility(View.GONE);
            clearFavoritesButton.setVisibility(View.GONE);
            tuneButton.setVisibility(View.GONE);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onResume() {
        super.onResume();
        Log.d("FavoritesActivity", "onResume called");
        // Reload favorites list and notify adapter
        favoritesList = loadFavorites(this);
        Log.d("Favorites", "FavoritesList = " + favoritesList);
        dictionaryAdapter.updateFavoritesList(favoritesList);
        dictionaryAdapter.notifyDataSetChanged();
        updateEmptyMessageVisibility();
    }

    private void setupCheckBoxListeners() {
        spanishCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> updateLanguages());
        dutchCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> updateLanguages());
        russianCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> updateLanguages());
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateLanguages() {
        if (dictionaryAdapter != null) {
            dictionaryAdapter.setLanguagesToShow(
                    spanishCheckBox.isChecked(),
                    dutchCheckBox.isChecked(),
                    russianCheckBox.isChecked()
            );
            dictionaryAdapter.notifyDataSetChanged();
        }
    }

    private void updateEmptyMessageVisibility() {
        if (favoritesList.isEmpty()) {
            emptyFavoritesMessage.setVisibility(View.VISIBLE);
        } else {
            emptyFavoritesMessage.setVisibility(View.GONE);
        }
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

    @SuppressLint("NotifyDataSetChanged")
    static void saveFavorites(Context context, List<DictionaryEntry> list) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String json = gson.toJson(list);
        editor.putString(FAVORITES_KEY, json);
        Log.d("FavoritesActivity", "Saved favorites: " + json);
        editor.apply();
        if(dictionaryAdapter != null) {
            dictionaryAdapter.updateFavoritesList(list);
            dictionaryAdapter.notifyDataSetChanged();
        }
    }

    static void initializeFavorites(Context context) {
        if (favoritesList == null) {
            favoritesList = loadFavorites(context);
        }
        if (FavoritesActivity.favoritesList == null) {
            FavoritesActivity.favoritesList = loadFavorites(context);
        }
        if (dictionaryAdapter == null) {
            dictionaryAdapter = new DictionaryAdapter(context, Collections.emptyList());
        }
    }

    // Example method to add a favorite (you can call this method when a user adds a favorite)
    @SuppressLint("NotifyDataSetChanged")
    static void addFavorite(DictionaryEntry favorite, Context context, List<DictionaryEntry> list) {
        if (list != null && !containsEntry(list, favorite)) {
            list.add(favorite);
            favorite.isFavorite = true;
            Log.d("FavoritesActivity", "Added favorite: " + favorite);
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
            saveFavorites(context, list);
        }
    }

    // Method to clear all favorites
    @SuppressLint("NotifyDataSetChanged")
    public static void clearFavorites(Context context) {
        new AlertDialog.Builder(context)
                        .setTitle(Html.fromHtml("Remove <b>ALL</b> Favorites"))
                        .setMessage(Html.fromHtml("Are you sure you want to delete <b>ALL</b> favorites?"))
                        .setPositiveButton("Yes", (dialog, which) -> {
                            SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.clear();
                            editor.apply();
                            favoritesList.clear();
                            if (dictionaryAdapter != null) {
                                dictionaryAdapter.notifyDataSetChanged();
                            }
                            Log.d("FavoritesActivity", "Cleared all favorites");
                        })
                        .setNegativeButton("No", null)
                        .show();
        
    }
}