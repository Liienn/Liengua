package com.example.liengua;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FeedbackActivity extends AppCompatActivity {
    private static CheckBox spanishCheckBox;
    private static CheckBox dutchCheckBox;
    private static CheckBox russianCheckBox;

    private Button sendFeedbackButton, cancelButton;
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
        sendFeedbackButton = findViewById(R.id.send_feedback_button);
        cancelButton = findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(view -> {
            finish();
        });
        String device = Build.MANUFACTURER.concat(" ").concat(Build.MODEL);
        sendFeedbackButton.setOnClickListener(view -> {
            for (FeedbackItem item: gatheredFeedback
                 ) {
                if (item.getFeedbackProvided() && entry != null) {
                    FeedbackClient.sendFeedback(entry.getId(), item.getOriginalPhrase(), item.isSuggestDelete(), item.getFeedback(), device);
                    Toast.makeText(this, "Feedback sent for <" + item.getOriginalPhrase() + ">", Toast.LENGTH_SHORT).show();
                } else if(entry == null) {
                    FeedbackClient.sendFeedback(0, item.getOriginalPhrase(), item.isSuggestDelete(), item.getFeedback(), device);
                    Toast.makeText(this, "Feedback sent for <" + item.getOriginalPhrase() + ">", Toast.LENGTH_SHORT).show();
                }
            }
        });
        ImageView infoIcon = findViewById(R.id.info_icon1);
        infoIcon.setOnClickListener(v -> showInfoDialog());
        setupCheckBoxListeners();
        russianCheckBox.setChecked(false);
        spanishCheckBox.setChecked(false);
        dutchCheckBox.setChecked(false);
        updateLanguage();
    }
    private void setupCheckBoxListeners() {
        spanishCheckBox.setOnClickListener(v -> selectLanguage("spanish"));
        dutchCheckBox.setOnClickListener(v -> selectLanguage("dutch"));
        russianCheckBox.setOnClickListener(v -> selectLanguage("russian"));
    }

    private void selectLanguage(String target) {
        spanishCheckBox.setOnCheckedChangeListener(null);
        dutchCheckBox.setOnCheckedChangeListener(null);
        russianCheckBox.setOnCheckedChangeListener(null);
        spanishCheckBox.setChecked(target.equals("spanish"));
        dutchCheckBox.setChecked(target.equals("dutch"));
        russianCheckBox.setChecked(target.equals("russian"));
        setupCheckBoxListeners();
        updateLanguage();
    }

    public static void disableAllCheckboxes() {
        if(russianCheckBox.isEnabled()) {
            spanishCheckBox.setEnabled(false);
            dutchCheckBox.setEnabled(false);
            russianCheckBox.setEnabled(false);
        }
    }

    public static void enableAllCheckboxes() {
        if(!russianCheckBox.isEnabled()) {
            spanishCheckBox.setEnabled(true);
            dutchCheckBox.setEnabled(true);
            russianCheckBox.setEnabled(true);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateLanguage(){
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
                        "You can send feedback for multiple phrases at a time, but not for multiple languages."
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
