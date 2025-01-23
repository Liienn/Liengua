package com.example.liengua;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

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

}