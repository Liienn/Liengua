package com.example.liengua;

import static com.example.liengua.Utils.insertDrawable;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "FavoritesPrefs";
    public static final String FAVORITES_KEY = "favorites";
    @SuppressLint("StaticFieldLeak")
    private static DictionaryAdapter dictionaryAdapter;
    private static List<DictionaryEntry> favoritesList;
    private TextView emptyFavoritesMessage;
    private CheckBox spanishCheckBox, dutchCheckBox, russianCheckBox;
    private ImageButton randomizeButton, sortButton, refreshButton, scrollToTopButton, scrollToBottomButton;
    private ImageView infoIcon;
    private static final Gson gson = new Gson();

    @SuppressLint({"NotifyDataSetChanged", "UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("FavoritesActivity", "onCreate called");
        setContentView(R.layout.favorites_layout);
        favoritesList = loadFavorites(this);
        ImageButton scrollToTopButton = findViewById(R.id.scroll_to_top_button);
        ImageButton scrollToBottomButton = findViewById(R.id.scroll_to_bottom_button);
        randomizeButton = findViewById(R.id.randomize_button);
        sortButton = findViewById(R.id.sort_button);
        refreshButton = findViewById(R.id.refresh_button);
        spanishCheckBox = findViewById(R.id.spanish_checkbox);
        dutchCheckBox = findViewById(R.id.dutch_checkbox);
        russianCheckBox = findViewById(R.id.russian_checkbox);
        infoIcon = findViewById(R.id.info_icon1);
        infoIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfoDialog();
            } });
        RecyclerView favoritesListView = findViewById(R.id.entries_list);
        ImageButton homeButton = findViewById(R.id.home_button);
        ImageButton menuButton = findViewById(R.id.menu_button);
        ImageButton clearFavoritesButton = findViewById(R.id.clear_favorites_button);
        emptyFavoritesMessage = findViewById(R.id.empty_favorites_message);
        ImageButton tuneButton = findViewById(R.id.tune_favorites_button);
        TextView pageTopTextView = findViewById(R.id.page_top_text_view);
        pageTopTextView.setText("Favorites");
        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(FavoritesActivity.this, MainActivity.class);
            startActivity(intent);
        });

        menuButton.setOnClickListener(this::showMenu);

        clearFavoritesButton.setOnClickListener(v -> clearFavorites(this));

        tuneButton.setBackground(getResources().getDrawable(R.drawable.border_solid));
        tuneButton.setOnClickListener(v -> {
            boolean showMoveButtons = !dictionaryAdapter.showMoveButtonsForEntry;
            dictionaryAdapter.setShowMoveButtons(showMoveButtons);
            clearFavoritesButton.setVisibility(showMoveButtons ? View.VISIBLE : View.GONE);
            randomizeButton.setVisibility(showMoveButtons? View.GONE: View.VISIBLE);
            sortButton.setVisibility(showMoveButtons? View.GONE: View.VISIBLE);
            scrollToTopButton.setVisibility(showMoveButtons? View.GONE: View.VISIBLE);
            scrollToBottomButton.setVisibility(showMoveButtons? View.GONE: View.VISIBLE);
            if(refreshButton.isShown()) {
                refreshButton.setVisibility(View.GONE);
            }
            if (showMoveButtons) {
                tuneButton.setBackgroundColor(getResources().getColor(R.color.peach_700));
                tuneButton.setBackground(getResources().getDrawable(R.drawable.border_solid_blue));

            } else {
                tuneButton.setBackground(getResources().getDrawable(R.drawable.border_solid));
            }
        });

        dictionaryAdapter = new DictionaryAdapter(this ,favoritesList, favoritesListView, scrollToTopButton, scrollToBottomButton);
        favoritesListView.setLayoutManager(new LinearLayoutManager(this));
        favoritesListView.setAdapter(dictionaryAdapter);
        favoritesListView.setItemAnimator(new SlideInItemAnimator());
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
            randomizeButtonHandler.setupRandomizeButton(randomizeButton, refreshButton, tuneButton);
            SortButtonHandler sortButtonHandler = new SortButtonHandler(favoritesList, dictionaryAdapter);
            sortButtonHandler.setupSortButton(sortButton, refreshButton, tuneButton);
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

    private void showInfoDialog() {
        // Create a SpannableString with the info text
        SpannableString spannableString = new SpannableString(
                "These are the phrases you have added to favorites. \n\n" +
                "'fav' : Click to remove a phrase from your favorites. \n\n" +
                "'tune' : Click to enable reordering phrases. \n" +
                "Click it again to disable reordering and to use the sort buttons again. \n" +
                "The order will be saved, even when you exit the com.example.liengua.app. \n\n" +
                "'refresh' : Click to refresh the list to your saved order. \n" +
                "(This button will be hidden when you are reordering phrases) \n\n" +
                "'home' : Click to return to the main screen."
        );

        // Insert drawables into the SpannableString
        insertDrawable(this, spannableString, "'tune'", R.drawable.tune_24px);
        insertDrawable(this, spannableString, "'fav'", R.drawable.stars_24px);
        insertDrawable(this, spannableString, "'home'", R.drawable.home_24px);
        insertDrawable(this, spannableString, "'refresh'", R.drawable.refresh_24px);

        // Create and show the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(FavoritesActivity.this);
        builder.setTitle("Information");
        builder.setMessage(spannableString);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
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