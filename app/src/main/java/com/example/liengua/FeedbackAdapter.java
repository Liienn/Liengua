package com.example.liengua;

import static com.example.liengua.FeedbackActivity.gatheredFeedback;
import static com.google.android.material.internal.ViewUtils.hideKeyboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Debug;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.collection.ArrayMap;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder> {
    private final Context context;
    private final List<String> feedbackItems = new ArrayList<>();

    private boolean showSpanish = true;
    private boolean showDutch = true;
    private boolean showRussian = true;
    private final DictionaryEntry entry;

    public FeedbackAdapter(Context context, DictionaryEntry entry) {
        this.context = context;
        this.entry = entry;
        prepareFeedbackItems(entry);
    }

    public DictionaryEntry getEntry() {
        return this.entry;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void prepareFeedbackItems(DictionaryEntry entry) {
        feedbackItems.clear(); // Clear existing list
        // Add main translations (if checked)
        if (showRussian && entry.getTranslationRussian() != null) {
            feedbackItems.add(entry.getTranslationRussian());
            List<String> russianAlternatives = entry.getAlternatives().get("russian");
            if (russianAlternatives != null) {
                feedbackItems.addAll(russianAlternatives);
            }
        }
        if (showDutch && entry.getTranslationDutch() != null) {
            feedbackItems.add(entry.getTranslationDutch());
            List<String> dutchAlternatives = entry.getAlternatives().get("dutch");
            if (dutchAlternatives != null) {
                feedbackItems.addAll(dutchAlternatives);
            }
        }
        if (showSpanish && entry.getTranslationSpanish() != null) {
            feedbackItems.add(entry.getTranslationSpanish());
            List<String> spanishAlternatives = entry.getAlternatives().get("spanish");
            if (spanishAlternatives != null) {
                feedbackItems.addAll(spanishAlternatives);
            }
        }
        notifyDataSetChanged();
    }



    @NonNull
    @Override
    public FeedbackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feedback_translation_item, parent, false);
        return new FeedbackAdapter.FeedbackViewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull FeedbackViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String phrase = feedbackItems.get(position);
        holder.originalPhrase.setText(phrase);
        // Set the phrase as a tag for easy retrieval
        holder.feedbackInput.setTag(phrase);
        holder.suggestDelete.setTag(phrase);
// Set listener for feedback text input
        holder.feedbackInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String key = (String) holder.feedbackInput.getTag();
                updateFeedback(key, s.toString(), null, holder);
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        // Set listener for suggestDelete checkbox
        holder.suggestDelete.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String key = (String) holder.suggestDelete.getTag();
            updateFeedback(key, null, isChecked, holder);
        });
    }
    @SuppressLint("LongLogTag")
    private void updateFeedback(String originalPhrase, String feedback, Boolean suggestDelete, FeedbackViewHolder holder) {
        FeedbackItem item = findFeedbackItem(originalPhrase);
        if (item == null) {
            item = new FeedbackItem(originalPhrase);
            gatheredFeedback.add(item);
        }
        item.setStatusChange(suggestDelete,feedback);
        if (suggestDelete != null && feedback != null) {
            item.setSuggestDelete(suggestDelete);
            item.setFeedback(feedback);
            item.setIsFeedbackProvided(true);
        } else if (suggestDelete != null) {
            item.setSuggestDelete(suggestDelete);
            item.setIsFeedbackProvided(true);
        } else if (feedback != null) {
            item.setFeedback(feedback);
            item.setIsFeedbackProvided(true);
        } else if (item.getFeedbackProvided()) {
            item.setIsFeedbackProvided(false);
        }

        if(item.getStatusChange()) {
            updateItemBackground(holder);
        }

        Log.d("FEEDBACK PRINT phrase", item.getOriginalPhrase());
        Log.d("FEEDBACK PRINT id", String.valueOf(item.getId()));
        Log.d("FEEDBACK PRINT feedback", item.getFeedback());
        Log.d("FEEDBACK PRINT feedback provided", String.valueOf(item.getFeedbackProvided()));
        Log.d("FEEDBACK PRINT status change", String.valueOf(item.getStatusChange()));

    }

    private FeedbackItem findFeedbackItem(String originalPhrase) {
        for (FeedbackItem item : gatheredFeedback) {
            if (item.getOriginalPhrase().equals(originalPhrase)) {
                return item;
            }
        }
        return null;
    }
    @SuppressLint({"NotifyDataSetChanged", "UseCompatLoadingForDrawables", "LongLogTag"})
    private void updateItemBackground(FeedbackViewHolder holder) {
        if(holder.originalPhrase.getText() != null) {
            FeedbackItem item = findFeedbackItem((String) holder.originalPhrase.getText());
            Log.d("FEEDBACK: original phrase clicked: ", (String) holder.originalPhrase.getText());
            assert item != null;
            holder.suggestDelete.setChecked(item.isSuggestDelete());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                if(item.getFeedback().isEmpty() && holder.feedbackInput.getText().isEmpty()) {
                    holder.feedbackInput.setText(item.getFeedback());
                }
            }
            if (holder.suggestDelete.isChecked()) {
                Log.d("FEEDBACK: original phrase: ", "Suggested deletion");
                holder.mainFeedbackItemLayout.setBackgroundColor(context.getResources().getColor(R.color.suggested_delete));
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                if (!holder.feedbackInput.getText().isEmpty()) {
                    Log.d("FEEDBACK: original phrase: ", item.getFeedback());
                    holder.mainFeedbackItemLayout.setBackgroundColor(context.getResources().getColor(R.color.feedback_received));
                } else {
                    Log.d("FEEDBACK: original phrase: ", "No feedback");
                    holder.mainFeedbackItemLayout.setBackground(context.getDrawable(R.drawable.border));
                }
            } else {
                Log.d("FEEDBACK: original phrase: ", "No feedback");
                holder.mainFeedbackItemLayout.setBackground(context.getDrawable(R.drawable.border));
            }
        }
        Log.d("FEEDBACK LIST: ", gatheredFeedback.toString());
    }

    @Override
    public int getItemCount() {
        return feedbackItems.size();
    }

    public void setLanguagesToShow(boolean showSpanish, boolean showDutch, boolean showRussian) {
        this.showSpanish = showSpanish;
        this.showDutch = showDutch;
        this.showRussian = showRussian;
        this.prepareFeedbackItems(this.entry);
    }

    public class FeedbackViewHolder extends RecyclerView.ViewHolder {
        LinearLayout mainFeedbackItemLayout;
        LinearLayout expandableLayout, headerLayout;
        TextView originalPhrase;
        CheckBox suggestDelete;
        EditText feedbackInput;
        ImageView expandArrowImage;
        final boolean[] isExpanded = {false};
        @SuppressLint({"RestrictedApi", "NotifyDataSetChanged"})
        public FeedbackViewHolder(@NonNull View itemView) {
            super(itemView);
            originalPhrase = itemView.findViewById(R.id.originalPhrase);
            expandableLayout = itemView.findViewById(R.id.expandableLayout);
            mainFeedbackItemLayout = itemView.findViewById(R.id.feedback_translation_item_layout);
            suggestDelete = itemView.findViewById(R.id.suggestDelete);
            feedbackInput = itemView.findViewById(R.id.feedback);
            expandArrowImage = itemView.findViewById(R.id.expandArrow);
            headerLayout = itemView.findViewById(R.id.headerLayout);
            mainFeedbackItemLayout.setOnClickListener(v -> {
                isExpanded[0] = !isExpanded[0];

                expandableLayout.setVisibility(isExpanded[0] ? View.VISIBLE : View.GONE);
                expandArrowImage.setRotation(isExpanded[0] ? 180f : 0f);

                if (!isExpanded[0]) {
                    hideKeyboard(v);
                    notifyDataSetChanged();
                }
            });
        }
    }
}
