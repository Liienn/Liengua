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
import java.util.List;

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
        filteredDictionaryEntries.clear();

        for (DictionaryEntry entry : entryList) {
            boolean matchFound =
                            (entry.getSentence().toLowerCase().contains(query.toLowerCase())) ||
                            (spanishCheckBox.isChecked() && entry.getTranslationSpanish().toLowerCase().contains(query.toLowerCase())) ||
                            (dutchCheckBox.isChecked() && entry.getTranslationDutch().toLowerCase().contains(query.toLowerCase())) ||
                            (russianCheckBox.isChecked() && entry.getTranslationRussian().toLowerCase().contains(query.toLowerCase()));

            if (matchFound) {
                filteredDictionaryEntries.add(entry);
            }
        }

        if (dictionaryAdapter != null) {
            dictionaryAdapter.notifyDataSetChanged();
        }
    }
}
