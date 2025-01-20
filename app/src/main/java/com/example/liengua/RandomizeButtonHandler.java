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

    public void setupRandomizeButton(ImageButton randomizeButton) {
        randomizeButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View v) {
                if (entryList != null) {
                    Collections.shuffle(entryList);
                    updateFilteredEntries();
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void updateFilteredEntries() {
        filteredDictionaryEntries.clear();
        filteredDictionaryEntries.addAll(entryList);
    }
}