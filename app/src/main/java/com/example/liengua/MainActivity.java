package com.example.liengua;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.Toast;
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
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DictionaryAdapter dictionaryAdapter;
    private List<DictionaryEntry> entryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize RecyclerView and set layout manager
        recyclerView = findViewById(R.id.dictionary_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set up checkboxes for language selection
        CheckBox spanishCheckBox = findViewById(R.id.spanish_checkbox);
        CheckBox dutchCheckBox = findViewById(R.id.dutch_checkbox);
        CheckBox russianCheckBox = findViewById(R.id.russian_checkbox);

        // Listen for changes in the checkbox selection
        spanishCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> updateLanguages());
        dutchCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> updateLanguages());
        russianCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> updateLanguages());

        // Fetch data from GitHub
        fetchDataFromGitHub();
    }

    private void updateLanguages() {
        // Get the selected languages from the checkboxes
        boolean showSpanish = ((CheckBox) findViewById(R.id.spanish_checkbox)).isChecked();
        boolean showDutch = ((CheckBox) findViewById(R.id.dutch_checkbox)).isChecked();
        boolean showRussian = ((CheckBox) findViewById(R.id.russian_checkbox)).isChecked();

        // Update the adapter with the selected languages
        if (dictionaryAdapter != null) {
            dictionaryAdapter.setLanguagesToShow(showSpanish, showDutch, showRussian);
            dictionaryAdapter.notifyDataSetChanged();
        }
    }

    private void fetchDataFromGitHub() {
        OkHttpClient client = new OkHttpClient();

        // GitHub URL to the raw JSON file
        String url = "https://raw.githubusercontent.com/Liienn/Liengua/main/translations/dictionary_data.json"; // Replace with your actual GitHub URL

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonData = response.body().string();

                    // Parse the JSON data using Gson
                    Gson gson = new Gson();
                    Type type = new TypeToken<List<DictionaryEntry>>() {}.getType();
                    entryList = gson.fromJson(jsonData, type);

                    // Run on the main thread to update the UI
                    runOnUiThread(() -> {
                        // Set the adapter with the fetched data
                        dictionaryAdapter = new DictionaryAdapter(entryList);
                        recyclerView.setAdapter(dictionaryAdapter);
                    });
                }
            }
        });
    }
}
