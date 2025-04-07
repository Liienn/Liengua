package com.example.liengua;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FeedbackActivity extends AppCompatActivity {
    private CheckBox spanishCheckBox, dutchCheckBox, russianCheckBox;
    private RecyclerView feedbackRecyclerView;
    public static FeedbackAdapter feedbackAdapter;
    public final static List<FeedbackItem> gatheredFeedback = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("FeedbackActivity", "onCreate called");
        setContentView(R.layout.feedback_screen);
        DictionaryEntry entry = (DictionaryEntry) getIntent().getSerializableExtra("entry");
        // Initialize the RecyclerView
        feedbackRecyclerView = findViewById(R.id.phrasesRecyclerView);
        feedbackRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Initialize the adapter with the entry object
        feedbackAdapter = new FeedbackAdapter(this, entry);
        // Set the adapter to the RecyclerView
        feedbackRecyclerView.setAdapter(feedbackAdapter);
        spanishCheckBox = findViewById(R.id.spanish_checkbox);
        dutchCheckBox = findViewById(R.id.dutch_checkbox);
        russianCheckBox = findViewById(R.id.russian_checkbox);
        ImageView infoIcon = findViewById(R.id.info_icon1);
        infoIcon.setOnClickListener(v -> showInfoDialog());
        setupCheckBoxListeners();
    }
    private void setupCheckBoxListeners() {
        spanishCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> updateLanguages());
        dutchCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> updateLanguages());
        russianCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> updateLanguages());
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateLanguages() {
        if (feedbackAdapter != null) {
            feedbackAdapter.setLanguagesToShow(
                    spanishCheckBox.isChecked(),
                    dutchCheckBox.isChecked(),
                    russianCheckBox.isChecked()
            );
            feedbackAdapter.notifyDataSetChanged();
        }
    }

    private void showInfoDialog() {
        SpannableString spannableString = new SpannableString(
                "These are the phrases you can send feedback for. \n" +
                        "Select checkboxes above to filter on language. \n" +
                        "You can send feedback for multiple phrases at a time."
        );
        AlertDialog.Builder builder = new AlertDialog.Builder(FeedbackActivity.this);
        builder.setTitle("Information");
        builder.setMessage(spannableString);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
