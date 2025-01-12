package com.example.liengua;

import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DictionaryAdapter extends RecyclerView.Adapter<DictionaryAdapter.DictionaryViewHolder> {

    private final List<DictionaryEntry> dictionaryEntryList;
    private boolean showSpanish = true;
    private boolean showDutch = true;
    private boolean showRussian = true;
    private boolean isTextSizeSet = false;

    public DictionaryAdapter(List<DictionaryEntry> dictionaryEntryList) {
        this.dictionaryEntryList = dictionaryEntryList;
    }

    // Method to update which languages to show
    public void setLanguagesToShow(boolean showSpanish, boolean showDutch, boolean showRussian) {
        this.showSpanish = showSpanish;
        this.showDutch = showDutch;
        this.showRussian = showRussian;
    }

    @NonNull
    @Override
    public DictionaryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.translation_item, parent, false);
        DictionaryViewHolder holder = new DictionaryViewHolder(view);

        if (!isTextSizeSet) {
            float defaultTextSize = holder.sentenceTextView.getTextSize();
            float newTextSize = defaultTextSize * 1.2f;  // Increase size by 20%
            holder.sentenceTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, newTextSize);
            holder.sentenceTextView.setTypeface(null, Typeface.BOLD);  // Make English sentence bold
            isTextSizeSet = true;  // Mark that the text size is set
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(DictionaryViewHolder holder, int position) {
        DictionaryEntry entry = dictionaryEntryList.get(position);

        // Build the translation string for the selected languages
        StringBuilder translation = new StringBuilder();

        if (showSpanish && entry.getTranslationSpanish() != null && !entry.getTranslationSpanish().isEmpty()) {
            translation.append("ES: ").append(entry.getTranslationSpanish()).append("\n");
        }
        if (showDutch && entry.getTranslationDutch() != null && !entry.getTranslationDutch().isEmpty()) {
            translation.append("NL: ").append(entry.getTranslationDutch()).append("\n");
        }
        if (showRussian && entry.getTranslationRussian() != null && !entry.getTranslationRussian().isEmpty()) {
            translation.append("RU: ").append(entry.getTranslationRussian()).append("\n");
        }

        // Set the translation text
        holder.translationTextView.setText(translation.toString());

        // Set the sentence text (English sentence is always visible)
        holder.sentenceTextView.setText(entry.getSentence());
    }

    @Override
    public int getItemCount() {
        return dictionaryEntryList.size();
    }

    public static class DictionaryViewHolder extends RecyclerView.ViewHolder {
        TextView sentenceTextView;
        TextView translationTextView;

        public DictionaryViewHolder(View itemView) {
            super(itemView);
            sentenceTextView = itemView.findViewById(R.id.englishSentence);
            translationTextView = itemView.findViewById(R.id.translation);
        }
    }
}
