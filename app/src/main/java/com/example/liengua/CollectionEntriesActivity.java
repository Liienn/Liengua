package com.example.liengua;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CollectionEntriesActivity extends AppCompatActivity {
    private RecyclerView entriesRecyclerView;
    private static List<DictionaryEntry> entries;
    private DictionaryAdapter adapter;
    private CheckBox spanishCheckBox, dutchCheckBox, russianCheckBox;
    private static TextView emptyEntriesMessage;
    private TextView discriptionTextView;
    private ImageButton randomizeButton, sortButton, refreshButton, tuneButton, scrollToTopButton, scrollToBottomButton, backButton, removeEntryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collection_entries_layout);

        randomizeButton = findViewById(R.id.randomize_button);
        sortButton = findViewById(R.id.sort_button);
        refreshButton = findViewById(R.id.refresh_button);
        tuneButton = findViewById(R.id.tune_collection_button);
        scrollToTopButton = findViewById(R.id.scroll_to_top_button);
        scrollToBottomButton = findViewById(R.id.scroll_to_bottom_button);
        backButton = findViewById(R.id.collection_entries_back_button);
        spanishCheckBox = findViewById(R.id.spanish_checkbox);
        dutchCheckBox = findViewById(R.id.dutch_checkbox);
        russianCheckBox = findViewById(R.id.russian_checkbox);
        TextView collectionNameTextView = findViewById(R.id.collection_name_text_view);
        discriptionTextView = findViewById(R.id.collection_description_text_view);
        emptyEntriesMessage = findViewById(R.id.empty_collections_message);
        entriesRecyclerView = findViewById(R.id.entries_list);

        entriesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Get the collection from the intent
        Intent intent = getIntent();
        CollectionLiengua collection = (CollectionLiengua) intent.getSerializableExtra("collection");

        if (collection != null) {
            collectionNameTextView.setText(collection.getName());
            entries = collection.getEntries();
            Log.d(TAG, "Collection name: " + collection.getName());
            Log.d(TAG, "Number of entries: " + entries.size());

            // Set up the adapter
            adapter = new DictionaryAdapter(this, entries,entriesRecyclerView,scrollToTopButton,scrollToBottomButton, collection);
            entriesRecyclerView.setAdapter(adapter);
        } else {
            Log.e(TAG, "Collection is null");
        }

        updateEmptyMessageVisibility();
        if(!emptyEntriesMessage.isShown()) {
            setupCheckBoxListeners();
            tuneButton.setVisibility(View.VISIBLE);
            RefreshButtonHandler refreshButtonHandler = new RefreshButtonHandler(CollectionEntriesActivity.this);
            refreshButtonHandler.setupRefreshButton(refreshButton);
            RandomizeButtonHandler randomizeButtonHandler = new RandomizeButtonHandler(entries, adapter);
            randomizeButtonHandler.setupRandomizeButton(randomizeButton, refreshButton, tuneButton);
            SortButtonHandler sortButtonHandler = new SortButtonHandler(entries, adapter);
            sortButtonHandler.setupSortButton(sortButton, refreshButton, tuneButton);
            TuneButtonHandler tuneButtonHandler = new TuneButtonHandler(entries, adapter, CollectionEntriesActivity.this);
            tuneButtonHandler.setTuneButton(tuneButton, adapter, randomizeButton, sortButton, scrollToTopButton, scrollToBottomButton, refreshButton);

        } else {
            spanishCheckBox.setVisibility(View.GONE);
            dutchCheckBox.setVisibility(View.GONE);
            russianCheckBox.setVisibility(View.GONE);
            randomizeButton.setVisibility(View.GONE);
            sortButton.setVisibility(View.GONE);
            refreshButton.setVisibility(View.GONE);
            tuneButton.setVisibility(View.GONE);
        }

        backButton.setOnClickListener(v -> {
            Intent backIntent = new Intent(CollectionEntriesActivity.this, CollectionsActivity.class);
            startActivity(backIntent);
            finish();
        });

    }

    static void updateEmptyMessageVisibility() {
        if (entries.isEmpty()) {
            emptyEntriesMessage.setVisibility(View.VISIBLE);
        } else {
            emptyEntriesMessage.setVisibility(View.GONE);
        }
    }
    private void setupCheckBoxListeners() {
        spanishCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> updateLanguages());
        dutchCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> updateLanguages());
        russianCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> updateLanguages());
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateLanguages() {
        if (adapter != null) {
            adapter.setLanguagesToShow(
                    spanishCheckBox.isChecked(),
                    dutchCheckBox.isChecked(),
                    russianCheckBox.isChecked()
            );
            adapter.notifyDataSetChanged();
        }
    }
}
