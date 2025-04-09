package com.example.liengua;

import static com.google.android.material.internal.ViewUtils.hideKeyboard;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
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
    private static LinearLayout addNewPhraseItemLayout;
    private static LinearLayout newPhraseExpendableLayout;
    private ImageView expandArrowImage;
    private TextView header;
    private EditText newPhraseEditText;
    private Button sendFeedbackButton, cancelButton;
    private static RecyclerView feedbackRecyclerView;
    private boolean isNewPhraseExpanded = false;
    public static FeedbackAdapter feedbackAdapter;
    public final static List<FeedbackItem> gatheredFeedback = new ArrayList<>();
    @SuppressLint({"RestrictedApi", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("FeedbackActivity", "onCreate called");
        setContentView(R.layout.feedback_screen);
        DictionaryEntry entry = (DictionaryEntry) getIntent().getSerializableExtra("entry");
        // Initialize the RecyclerView
        header = findViewById(R.id.targetPhrase_textView);
        if(entry != null) {
            header.setText("'" + entry.getSentence() + "'");
        }
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
        addNewPhraseItemLayout = findViewById(R.id.new_phrase_item_layout);
        newPhraseExpendableLayout = findViewById(R.id.new_phrase_expandableLayout);
        expandArrowImage = findViewById(R.id.expandArrowNewPhrase);
        newPhraseEditText = findViewById(R.id.new_phrase_input);
        addNewPhraseItemLayout.setOnClickListener(view -> {
            isNewPhraseExpanded = !isNewPhraseExpanded;
            newPhraseExpendableLayout.setVisibility(isNewPhraseExpanded ? View.VISIBLE : View.GONE);
            expandArrowImage.setRotation(isNewPhraseExpanded ? 180f : 0f);
            if(!isNewPhraseExpanded) {
                hideKeyboard(view);
                if(newPhraseEditText.getText() == null || newPhraseEditText.getText().toString().isEmpty()) {
                    enableFeedbackItems();
                }
            } else {
                disableFeedbackItems();
            }
        });
        cancelButton.setOnClickListener(view -> {
            finish();
        });
        String device = Build.MANUFACTURER.concat(" ").concat(Build.MODEL);
        sendFeedbackButton.setOnClickListener(view -> {
            if(!newPhraseEditText.getText().toString().isEmpty() && entry != null) {
                String targetLanguage = "";
                if(russianCheckBox.isChecked()) {
                    targetLanguage = "RU";
                } else if (dutchCheckBox.isChecked()) {
                    targetLanguage = "NL";
                } else if (spanishCheckBox.isChecked()) {
                    targetLanguage = "ES";
                }
                FeedbackClient.sendFeedback(entry.getId(), "NEW PHRASE: " + targetLanguage, false, newPhraseEditText.getText().toString(), device);
                Toast.makeText(this, "Suggested new phrase <" + newPhraseEditText.getText().toString() +">", Toast.LENGTH_SHORT).show();
            }
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
        disableAddNewPhrase();
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

    public static void disableAddNewPhrase() {
        if(addNewPhraseItemLayout != null) {
            addNewPhraseItemLayout.setVisibility(View.GONE);
        }
    }
    public static void enableAddNewPhrase() {
        if(addNewPhraseItemLayout != null) {
            addNewPhraseItemLayout.setVisibility(View.VISIBLE);
        }
    }

    public static void disableFeedbackItems() {
        if(feedbackRecyclerView != null) {
            feedbackRecyclerView.setVisibility(View.GONE);
        }
    }
    public static void enableFeedbackItems() {
        if(feedbackRecyclerView != null) {
            feedbackRecyclerView.setVisibility(View.VISIBLE);
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
