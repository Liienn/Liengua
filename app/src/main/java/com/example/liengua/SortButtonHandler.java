package com.example.liengua;

import android.annotation.SuppressLint;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

    public class SortButtonHandler {
        private final List<DictionaryEntry> list;
        private final DictionaryAdapter dictionaryAdapter;
    
        public SortButtonHandler(List<DictionaryEntry> list, DictionaryAdapter dictionaryAdapter) {
            this.list = list;
            this.dictionaryAdapter = dictionaryAdapter;
        }
    
        public void setupSortButton(final ImageButton sortButton, final ImageButton refreshButton, final ImageButton tuneButton) {
            sortButton.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onClick(View v) {
                    if (list != null && !list.isEmpty()) {
                        Log.d("SortButtonHandler", "Sort button clicked, sorting list...");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            Collections.sort(list, Comparator.comparing(DictionaryEntry::getSentence, String::compareToIgnoreCase));
                        } else {
                            Collections.sort(list, new Comparator<DictionaryEntry>() {
                                @Override
                                public int compare(DictionaryEntry entry1, DictionaryEntry entry2) {
                                    return entry1.getSentence().compareToIgnoreCase(entry2.getSentence());
                                }
                            });
                        }
                        dictionaryAdapter.notifyDataSetChanged();
                        if(refreshButton != null && tuneButton != null) {
                            refreshButton.setVisibility(View.VISIBLE);
                            tuneButton.setVisibility(View.GONE);
                        }
                        Log.d("SortButtonHandler", "List sorted and adapter notified.");
                    } else {
                        Log.d("SortButtonHandler", "Favorites list is null or empty.");
                    }
                }
            });
        }
    }
