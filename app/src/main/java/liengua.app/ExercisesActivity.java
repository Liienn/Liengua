package com.example.liengua;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ExercisesActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exercises_layout);
        TextView pageTopTextView = findViewById(R.id.page_top_text_view);
        pageTopTextView.setText("Exercises");

        ImageButton homeButton = findViewById(R.id.home_button);
        ImageButton menuButton = findViewById(R.id.menu_button);

        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(ExercisesActivity.this, MainActivity.class);
            startActivity(intent);
        });

        menuButton.setOnClickListener(this::showMenu);
    }

    private void showMenu(View anchorView) {
        MenuHandler menuHandler = new MenuHandler(this);
        menuHandler.showMenu(anchorView);
    }
}