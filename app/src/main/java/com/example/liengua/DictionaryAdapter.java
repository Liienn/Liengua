package com.example.liengua;

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
        if (showSpanish) appendTranslation(translation, "ES", entry.getTranslationSpanish());
        if (showDutch) appendTranslation(translation, "NL", entry.getTranslationDutch());
        if (showRussian) appendTranslation(translation, "RU", entry.getTranslationRussian());

        holder.translationTextView.setText(translation.toString());
        holder.sentenceTextView.setText(entry.getSentence());
    }

    private void appendTranslation(StringBuilder translation, String langPrefix, String text) {
        if (text != null && !text.isEmpty()) {
            translation.append(langPrefix).append(": ").append(text).append("\n");
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

