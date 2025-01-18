package com.example.liengua;

import androidx.recyclerview.widget.RecyclerView;
import java.util.Collections;
import java.util.List;
import android.annotation.SuppressLint;
import android.view.View;
import android.widget.Button;

public class RandomizeButtonHandler {

    private final List<?> itemList;
    private final RecyclerView.Adapter<?> adapter;

    public RandomizeButtonHandler(List<?> itemList, RecyclerView.Adapter<?> adapter) {
        this.itemList = itemList;
        this.adapter = adapter;
    }

    public void setupRandomizeButton(Button randomizeButton) {
        randomizeButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View v) {
                if (itemList != null) {
                    Collections.shuffle(itemList);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }
}