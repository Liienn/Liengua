package com.example.liengua;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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

    private EditText searchInput;
    private RecyclerView recyclerView;
    private DictionaryAdapter dictionaryAdapter;
    private List<DictionaryEntry> entryList = new ArrayList<>();
    private final List<DictionaryEntry> filteredDictionaryEntries = new ArrayList<>();
    private CheckBox spanishCheckBox, dutchCheckBox, russianCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchInput = findViewById(R.id.search_input);
        recyclerView = findViewById(R.id.dictionary_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        spanishCheckBox = findViewById(R.id.spanish_checkbox);
        dutchCheckBox = findViewById(R.id.dutch_checkbox);
        russianCheckBox = findViewById(R.id.russian_checkbox);

        setupCheckBoxListeners();

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterList(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        fetchDataFromGitHub();
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
            boolean matchFound =
                    // Check the sentence in English
                    entry.getSentence().toLowerCase().contains(query.toLowerCase()) ||
                            // Check the main translations
                            (spanishCheckBox.isChecked() && entry.getTranslationSpanish().toLowerCase().contains(query.toLowerCase())) ||
                            (dutchCheckBox.isChecked() && entry.getTranslationDutch().toLowerCase().contains(query.toLowerCase())) ||
                            (russianCheckBox.isChecked() && entry.getTranslationRussian().toLowerCase().contains(query.toLowerCase()));

            // Check alternatives in each language if they exist
            if (entry.getAlternatives() != null) {
                if (entry.getAlternatives().containsKey("dutch") && dutchCheckBox.isChecked()) {
                    for (String alternative : Objects.requireNonNull(entry.getAlternatives().get("dutch"))) {
                        if (alternative.toLowerCase().contains(query.toLowerCase())) {
                            matchFound = true;
                            break; // Stop checking further once a match is found
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

            // If a match is found, add the entry to the filtered list
            if (matchFound) {
                filteredDictionaryEntries.add(entry);
            }

        }

        // Sort the entries alphabetically based on the English sentence
        Collections.sort(filteredDictionaryEntries, new Comparator<DictionaryEntry>() {
            @Override
            public int compare(DictionaryEntry entry1, DictionaryEntry entry2) {
                return entry1.getSentence().compareToIgnoreCase(entry2.getSentence());
            }
        });

        // Notify the adapter to refresh the list
        if (dictionaryAdapter != null) {
            dictionaryAdapter.notifyDataSetChanged();
        }
    }

}
