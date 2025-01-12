package com.example.liengua;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class DictionaryAdapter extends RecyclerView.Adapter<DictionaryAdapter.DictionaryViewHolder> {

    private final List<DictionaryEntry> dictionaryEntryList;
    private boolean showSpanish = true;
    private boolean showDutch = true;
    private boolean showRussian = true;

    public DictionaryAdapter(List<DictionaryEntry> dictionaryEntryList) {
        this.dictionaryEntryList = dictionaryEntryList;
    }

    public void setLanguagesToShow(boolean showSpanish, boolean showDutch, boolean showRussian) {
        this.showSpanish = showSpanish;
        this.showDutch = showDutch;
        this.showRussian = showRussian;
    }

    @NonNull
    @Override
    public DictionaryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.translation_item, parent, false);
        return new DictionaryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DictionaryViewHolder holder, int position) {
        DictionaryEntry entry = dictionaryEntryList.get(position);

        StringBuilder translation = new StringBuilder();
        holder.translationTextView.setOnClickListener(null); // Remove previous listeners

        // Append Spanish translation if enabled
        if (showSpanish && entry.getTranslationSpanish() != null && !entry.getTranslationSpanish().isEmpty()) {
            translation.append("ES: ").append(entry.getTranslationSpanish()).append("\n");
        }
        // Append Dutch translation if enabled
        if (showDutch && entry.getTranslationDutch() != null && !entry.getTranslationDutch().isEmpty()) {
            translation.append("NL: ").append(entry.getTranslationDutch()).append("\n");
        }
        // Append Russian translation if enabled
        if (showRussian && entry.getTranslationRussian() != null && !entry.getTranslationRussian().isEmpty()) {
            translation.append("RU: ").append(entry.getTranslationRussian()).append("\n");
        }

        // Set the translation text
        holder.translationTextView.setText(translation.toString());
        holder.sentenceTextView.setText(entry.getSentence());
    }

    private void showAlternatives(String language, Map<String, List<String>> alternatives, DictionaryViewHolder holder) {
        List<String> languageAlternatives = alternatives.get(language);
        if (languageAlternatives != null && !languageAlternatives.isEmpty()) {
            StringBuilder altText = new StringBuilder("Alternatives:\n");
            for (String alt : languageAlternatives) {
                altText.append(alt).append("\n");
            }

            // Show alternatives in a Toast (or consider using a Dialog for better UX)
            Toast.makeText(holder.translationTextView.getContext(), altText.toString(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(holder.translationTextView.getContext(), "No alternatives available.", Toast.LENGTH_SHORT).show();
        }
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
