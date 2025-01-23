package com.example.liengua;

import static com.example.liengua.Utils.insertDrawable;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    private EditText searchInput, contactMessageEditText;
    private DictionaryAdapter dictionaryAdapter;
    private List<DictionaryEntry> entryList = new ArrayList<>();
    private final List<DictionaryEntry> filteredDictionaryEntries = new ArrayList<>();
    private ImageButton randomizeButton, sortButton, refreshButton, scrollToTopButton, scrollToBottomButton;
    private CheckBox spanishCheckBox, dutchCheckBox, russianCheckBox;
    private RecyclerView recyclerView;
    private ImageView arrow1, arrow2;
    private List<DictionaryEntry> originalEntryList;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        originalEntryList = entryList;
        // Initialize BottomSheetBehavior
        LinearLayout bottomSheet = findViewById(R.id.bottom_sheet);
        assert bottomSheet != null;
        BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        fetchDataFromGitHub();
        // Initialize views
        ImageButton menuButton = findViewById(R.id.menu_button);
        MenuHandler menuHandler = new MenuHandler(this);
        menuButton.setOnClickListener(menuHandler::showMenu);

        searchInput = findViewById(R.id.search_input);
        recyclerView = findViewById(R.id.entries_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        scrollToBottomButton = findViewById(R.id.scroll_to_bottom_button);
        scrollToTopButton = findViewById(R.id.scroll_to_top_button);

        spanishCheckBox = findViewById(R.id.spanish_checkbox);
        dutchCheckBox = findViewById(R.id.dutch_checkbox);
        russianCheckBox = findViewById(R.id.russian_checkbox);

        refreshButton = findViewById(R.id.refresh_button);

        ImageButton sendButton = findViewById(R.id.sendButton);
        contactMessageEditText = findViewById(R.id.contactMessage);
        LinearLayout swipeIconLayout = findViewById(R.id.swipe_icon_layout);
        final View rootView = findViewById(android.R.id.content);
        // Ensure itemList is initialized

        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                rootView.getWindowVisibleDisplayFrame(r);
                int screenHeight = rootView.getRootView().getHeight();
                int keypadHeight = screenHeight - r.bottom;

                // Check if the keyboard is visible
                if (keypadHeight > screenHeight * 0.15) { // Assume 15% of screen height as keyboard threshold
                    swipeIconLayout.setVisibility(View.GONE);
                    if(randomizeButton != null) {
                        randomizeButton.setVisibility(View.GONE);
                        sortButton.setVisibility(View.GONE);
                        refreshButton.setVisibility(View.GONE);
                    }
                } else {
                    swipeIconLayout.setVisibility(View.VISIBLE);
                    if(randomizeButton != null) {
                        randomizeButton.setVisibility(View.VISIBLE);
                        sortButton.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        // Use ViewTreeObserver to get the height of the bottom sheet after layout
        bottomSheet.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                // Remove the listener to prevent repeated calls
                bottomSheet.getViewTreeObserver().removeOnPreDrawListener(this);

                // Get the height of the bottom sheet
                int bottomSheetHeight = (int) (bottomSheet.getHeight() * 0.5);

                // Set the paddingBottom of the RecyclerView dynamically based on bottom sheet height
                RecyclerView recyclerView = findViewById(R.id.entries_list);
                recyclerView.setPadding(
                        recyclerView.getPaddingLeft(),  // Keep current left padding
                        recyclerView.getPaddingTop(),   // Keep current top padding
                        recyclerView.getPaddingRight(), // Keep current right padding
                        bottomSheetHeight               // Set dynamic bottom padding
                );

                return true;
            }
        });


        swipeIconLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }
        });

        // Setup info icon click listener
        ImageView infoIcon = findViewById(R.id.info_icon1);
        infoIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfoDialog();
        } });

        // Setup checkbox listeners
        setupCheckBoxListeners();

        // Search input listener
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterList(s.toString(), false);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        arrow1 = findViewById(R.id.swipe_icon1);
        arrow2 = findViewById(R.id.swipe_icon);
        // Set the bottom sheet to be swiped up and down
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED); // Default collapsed state
        bottomSheetBehavior.setHideable(false); // Optional: prevent hiding the sheet entirely
        // Get the screen height in pixels
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;
// Calculate 2% of the screen height
        int peekHeight = (int) (screenHeight * 0.09);
// Set the peek height for the BottomSheetBehavior
        bottomSheetBehavior.setPeekHeight(peekHeight);
        // Add slide listener to the BottomSheetBehavior
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    // Handle the case when the sheet is fully expanded
                    Log.d("BottomSheet", "Expanded");
                    arrow1.setImageResource(android.R.drawable.arrow_down_float);
                    arrow2.setImageResource(android.R.drawable.arrow_down_float);
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    // Handle the case when the sheet is collapsed
                    Log.d("BottomSheet", "Collapsed");
                    arrow1.setImageResource(android.R.drawable.arrow_up_float);
                    arrow2.setImageResource(android.R.drawable.arrow_up_float);
                } else if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    // Handle the case when the user is dragging the sheet
                    Log.d("BottomSheet", "Dragging");
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // `slideOffset` ranges from 0 (collapsed) to 1 (expanded)
                // We want to change the icons smoothly around 90% of the expansion

                if (slideOffset > 0.9) {
                    // Sheet is 90% expanded or more: arrows pointing down
                    arrow1.setImageResource(android.R.drawable.arrow_down_float);
                    arrow2.setImageResource(android.R.drawable.arrow_down_float);
                } else if (slideOffset < 0.1) {
                    // Sheet is close to collapsed: arrows pointing up
                    arrow1.setImageResource(android.R.drawable.arrow_up_float);
                    arrow2.setImageResource(android.R.drawable.arrow_up_float);
                } else {
                    // Handle intermediate state, if needed (optional)
                    // You can adjust the icons based on the slideOffset
                    // For example, switch to a neutral icon or keep the existing arrow icons
                }
            }
        });

        // Send button functionality - send an email when clicked

        sendButton.setOnClickListener(v -> sendMessage());

        sortButton = findViewById(R.id.sort_button);
        sortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortListAlphabetically(entryList);
                refreshButton.setVisibility(View.VISIBLE);
            }
        });
        refreshButton.setVisibility(View.GONE);
    }

    private void showInfoDialog() {
        // Create a SpannableString with the info text
        SpannableString spannableString = new SpannableString(
                "Newly added phrases are marked with a '*'. The newer the phrase, the more '*' it has.\n\n" +
                        "You can filter the list by typing in the search bar and by checking the language checkboxes.\n(TIP: you can use the '*' in the filter to search for the newest additions)\n\n" +
                        "Click on a translation to see alternatives.\n\n" +
                        "Long press on a phrase to copy it to the clipboard.\n\n" +
                        "BUTTONS:\n\n" +
                        "menu : Click to access more options.\n\n" +
                        "'fav' : Click to add a phrase to your favorites.\n\n" +
                        "'coll' : Click to add a phrase to your collections.\n\n" +
                        "'Randomize' : Click to shuffle the list.\n\n" +
                        "'A-Z' : Click to sort the list alphabetically.\n\n" +
                        "message bar : Click or swipe to expand or collapse the message bar.\n\n" +
                        "'Send' : Click to send a message."
        );

        // Insert drawables into the SpannableString
        insertDrawable(this, spannableString, "menu", R.drawable.menu_24px);
        insertDrawable(this, spannableString, "'fav'", R.drawable.stars_24px);
        insertDrawable(this, spannableString, "'coll'", R.drawable.bookmark_24px);
        insertDrawable(this, spannableString, "'Randomize'", R.drawable.shuffle_24px);
        insertDrawable(this, spannableString, "'A-Z'", R.drawable.sort_by_alpha_24px);
        insertDrawable(this, spannableString,"message bar",R.drawable.mail_24px);
        insertDrawable(this, spannableString, "'Send'", R.drawable.send_24px);

        // Create and show the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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
    private void sendMessage() {
        String message = contactMessageEditText.getText().toString().trim();

        if (message.isEmpty()) {
            Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show();
            return;
        }

        // Set up the Intent to share the message
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, message);

        // Always show the chooser dialog
        Intent chooserIntent = Intent.createChooser(shareIntent, "Share via");
        startActivity(chooserIntent);
    }

    private void setupCheckBoxListeners() {
        spanishCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> filterList(searchInput.getText().toString(), false));
        dutchCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> filterList(searchInput.getText().toString(), false));
        russianCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> filterList(searchInput.getText().toString(), false));
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

    private void fetchDataFromGitHub() {
        OkHttpClient client = new OkHttpClient();
        String url = "https://raw.githubusercontent.com/Liienn/Liengua/main/translations/dictionary_data.json";

        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    String jsonData = response.body().string();
                    Gson gson = new Gson();
                    Type type = new TypeToken<List<DictionaryEntry>>() {}.getType();
                    entryList = gson.fromJson(jsonData, type);
                    originalEntryList = entryList;

                    runOnUiThread(() -> {
                        dictionaryAdapter = new DictionaryAdapter(MainActivity.this, filteredDictionaryEntries, recyclerView, scrollToTopButton, scrollToBottomButton);
                        RecyclerView recyclerView = findViewById(R.id.entries_list);
                        recyclerView.setAdapter(dictionaryAdapter);
                        filterList("", true); // Initialize the filter list

                        // Setup randomize button using RandomizeButtonHandler

                        randomizeButton = findViewById(R.id.randomize_button);

                        RefreshButtonHandler refreshButtonHandler = new RefreshButtonHandler(MainActivity.this);
                        refreshButtonHandler.setupRefreshButton(refreshButton);
                        RandomizeButtonHandler randomizeButtonHandler = new RandomizeButtonHandler(entryList, filteredDictionaryEntries, dictionaryAdapter);
                        randomizeButtonHandler.setupRandomizeButton(randomizeButton, refreshButton);

                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show());
                }
            }
        });

    }

    @SuppressLint("NotifyDataSetChanged")
    private void sortListAlphabetically(List<DictionaryEntry> list) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Collections.sort(list, Comparator.comparing(DictionaryEntry::getSentence, String::compareToIgnoreCase));
            filteredDictionaryEntries.clear();
            filteredDictionaryEntries.addAll(entryList);
            dictionaryAdapter.notifyDataSetChanged();
        }
    }
    
    @SuppressLint("NotifyDataSetChanged")
    private void filterList(String query, boolean sortAlphabetically) {
        updateLanguages();
        filteredDictionaryEntries.clear();

        for (DictionaryEntry entry : entryList) {
            boolean matchFound = entry.getSentence().toLowerCase().contains(query.toLowerCase()) ||
                    (spanishCheckBox.isChecked() && entry.getTranslationSpanish().toLowerCase().contains(query.toLowerCase())) ||
                    (dutchCheckBox.isChecked() && entry.getTranslationDutch().toLowerCase().contains(query.toLowerCase())) ||
                    (russianCheckBox.isChecked() && entry.getTranslationRussian().toLowerCase().contains(query.toLowerCase()));

            if (entry.getAlternatives() != null) {
                if (entry.getAlternatives().containsKey("dutch") && dutchCheckBox.isChecked()) {
                    for (String alternative : Objects.requireNonNull(entry.getAlternatives().get("dutch"))) {
                        if (alternative.toLowerCase().contains(query.toLowerCase())) {
                            matchFound = true;
                            break;
                        }
                    }
                }
                if (entry.getAlternatives().containsKey("spanish") && spanishCheckBox.isChecked()) {
                    for (String alternative : Objects.requireNonNull(entry.getAlternatives().get("spanish"))) {
                        if (alternative.toLowerCase().contains(query.toLowerCase())) {
                            matchFound = true;
                            break;
                        }
                    }
                }
                if (entry.getAlternatives().containsKey("russian") && russianCheckBox.isChecked()) {
                    for (String alternative : Objects.requireNonNull(entry.getAlternatives().get("russian"))) {
                        if (alternative.toLowerCase().contains(query.toLowerCase())) {
                            matchFound = true;
                            break;
                        }
                    }
                }
            }

            if (matchFound) {
                filteredDictionaryEntries.add(entry);
            }
        }
        
        if (sortAlphabetically) {
            sortListAlphabetically(filteredDictionaryEntries);
        }
        if(dictionaryAdapter != null) {
            dictionaryAdapter.notifyDataSetChanged();
        }
    }
}
