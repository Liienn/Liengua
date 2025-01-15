package com.example.liengua;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
    private CheckBox spanishCheckBox, dutchCheckBox, russianCheckBox;
    private ImageView arrow1, arrow2;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Initialize BottomSheetBehavior
        LinearLayout bottomSheet = findViewById(R.id.bottom_sheet);
        if (bottomSheet == null) {
            Log.e("BottomSheet", "Bottom sheet view is null");
        } else {
            Log.d("BottomSheet", "Bottom sheet view found");
        }
        assert bottomSheet != null;
        BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        // Initialize views
        fetchDataFromGitHub();
        searchInput = findViewById(R.id.search_input);
        RecyclerView recyclerView = findViewById(R.id.dictionary_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        spanishCheckBox = findViewById(R.id.spanish_checkbox);
        dutchCheckBox = findViewById(R.id.dutch_checkbox);
        russianCheckBox = findViewById(R.id.russian_checkbox);
        ImageButton sendButton = findViewById(R.id.sendButton);
        contactMessageEditText = findViewById(R.id.contactMessage);
        LinearLayout swipeIconLayout = findViewById(R.id.swipe_icon_layout);
        final View rootView = findViewById(android.R.id.content);
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
                } else {
                    swipeIconLayout.setVisibility(View.VISIBLE);
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
                RecyclerView recyclerView = findViewById(R.id.dictionary_list);
                recyclerView.setPadding(
                        recyclerView.getPaddingLeft(),  // Keep current left padding
                        recyclerView.getPaddingTop(),   // Keep current top padding
                        recyclerView.getPaddingRight(), // Keep current right padding
                        bottomSheetHeight               // Set dynamic bottom padding
                );

                return true;
            }
        });

        // Setup checkbox listeners
        setupCheckBoxListeners();


        // Search input listener
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterList(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        arrow1 = findViewById(R.id.swipe_icon3);
        arrow2 = findViewById(R.id.swipe_icon);
        // Set the bottom sheet to be swiped up and down
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED); // Default collapsed state
        bottomSheetBehavior.setHideable(false); // Optional: prevent hiding the sheet entirely
        // Get the screen height in pixels
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;
// Calculate 2% of the screen height
        int peekHeight = (int) (screenHeight * 0.1);
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
        spanishCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> filterList(searchInput.getText().toString()));
        dutchCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> filterList(searchInput.getText().toString()));
        russianCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> filterList(searchInput.getText().toString()));
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

                    runOnUiThread(() -> {
                        dictionaryAdapter = new DictionaryAdapter(filteredDictionaryEntries);
                        RecyclerView recyclerView = findViewById(R.id.dictionary_list);
                        recyclerView.setAdapter(dictionaryAdapter);
                        filterList(""); // Initialize the filter list
                    });
                }
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void filterList(String query) {
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Collections.sort(filteredDictionaryEntries, Comparator.comparing(DictionaryEntry::getSentence, String::compareToIgnoreCase));
        }
        dictionaryAdapter.notifyDataSetChanged();
    }
}
