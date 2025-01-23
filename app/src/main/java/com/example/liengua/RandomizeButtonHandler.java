package com.example.liengua;

import androidx.recyclerview.widget.RecyclerView;
import java.util.Collections;
import java.util.List;
import android.annotation.SuppressLint;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class RandomizeButtonHandler {

    private final List<DictionaryEntry> entryList;
    private final List<DictionaryEntry> filteredDictionaryEntries;
    private final RecyclerView.Adapter<?> adapter;

    public RandomizeButtonHandler(List<DictionaryEntry> entryList, List<DictionaryEntry> filteredDictionaryEntries, RecyclerView.Adapter<?> adapter) {
        this.entryList = entryList;
        this.filteredDictionaryEntries = filteredDictionaryEntries;
        this.adapter = adapter;
    }
    public RandomizeButtonHandler(List<DictionaryEntry> entryList, RecyclerView.Adapter<?> adapter) {
        this.entryList = entryList;
        this.adapter = adapter;
        this.filteredDictionaryEntries = null;
    }

    public void setupRandomizeButton(ImageButton randomizeButton, final ImageButton refreshButton) {
        randomizeButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View v) {
                if (entryList != null) {
                    Collections.shuffle(entryList);
                    if(filteredDictionaryEntries != null) {
                        updateFilteredEntries();
                    }
                    adapter.notifyDataSetChanged();
                    if(refreshButton != null) {
                        refreshButton.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    private void updateFilteredEntries() {
        assert filteredDictionaryEntries != null;
        filteredDictionaryEntries.clear();
        filteredDictionaryEntries.addAll(entryList);
    }
}