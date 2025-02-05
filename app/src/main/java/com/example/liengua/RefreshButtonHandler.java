package com.example.liengua;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class RefreshButtonHandler {
    private final AppCompatActivity activity;

    public RefreshButtonHandler(AppCompatActivity activity) {
        this.activity = activity;
    }

    public void setupRefreshButton(ImageButton refreshButton) {
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.recreate();
            }
        });
    }

    public void setupRefreshButton(ImageButton refreshButton, DictionaryAdapter adapter) {
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View v) {
                activity.recreate();
                adapter.notifyDataSetChanged();
            }
        });
    }

}