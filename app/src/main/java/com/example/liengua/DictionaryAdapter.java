package com.example.liengua;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.WindowDecorActionBar;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

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

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull DictionaryViewHolder holder, int position) {
        DictionaryEntry entry = dictionaryEntryList.get(position);

        StringBuilder translation = new StringBuilder();
        holder.spanishTranslationTextView.setOnClickListener(null); // Remove previous listeners

        if (showSpanish && entry.getTranslationSpanish() != null && !entry.getTranslationSpanish().isEmpty()) {
            setTranslationTextViewText(holder.spanishTranslationTextView,"ES: " + entry.getTranslationSpanish());
            holder.spanishTranslationTextView.setOnClickListener(v -> showAlternatives("spanish", holder.spanishTranslationTextView, holder));
        } else {
            holder.spanishTranslationTextView.setVisibility(View.GONE);
        }

        if (showDutch && entry.getTranslationDutch() != null && !entry.getTranslationDutch().isEmpty()) {
            setTranslationTextViewText(holder.dutchTranslationTextView,"NL: " + entry.getTranslationDutch());
            holder.dutchTranslationTextView.setOnClickListener(v -> showAlternatives("dutch", holder.dutchTranslationTextView, holder));
        } else {
            holder.dutchTranslationTextView.setVisibility(View.GONE);
        }

        if (showRussian && entry.getTranslationRussian() != null && !entry.getTranslationRussian().isEmpty()) {
            setTranslationTextViewText(holder.russianTranslationTextView,"RU: " + entry.getTranslationRussian());
            holder.russianTranslationTextView.setOnClickListener(v -> showAlternatives("russian", holder.russianTranslationTextView, holder));
        } else {
            holder.russianTranslationTextView.setVisibility(View.GONE);
        }

        // Set the translation text
        holder.sentenceTextView.setText(entry.getSentence());
    }
    private void setTranslationTextViewText(TextView translationTextView, String translation) {
        translationTextView.setText(translation);
        translationTextView.setVisibility(View.VISIBLE);
        translationTextView.setMovementMethod(LinkMovementMethod.getInstance());
    }
    private void showAlternatives(String language, TextView translationTextView, DictionaryViewHolder holder) {
        DictionaryEntry entry = dictionaryEntryList.get(holder.getAdapterPosition());
        List<String> languageAlternatives = entry.getAlternatives().get(language);

        if (languageAlternatives != null && !languageAlternatives.isEmpty()) {
            CharSequence[] altArray = languageAlternatives.toArray(new CharSequence[0]);

            new AlertDialog.Builder(translationTextView.getContext())
                    .setTitle(entry.getSentence())
                    .setItems(altArray, (dialog, which) -> {
                        // Optional: Handle click on alternative
                    })
                    .setPositiveButton("Close", null)
                    .show();
        } else {
            Toast.makeText(translationTextView.getContext(), "No " + language + " alternatives available.", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public int getItemCount() {
        return dictionaryEntryList.size();
    }

    public static class DictionaryViewHolder extends RecyclerView.ViewHolder {

        TextView sentenceTextView;
        TextView dutchTranslationTextView;
        TextView spanishTranslationTextView;
        TextView russianTranslationTextView;

        public DictionaryViewHolder(View itemView) {
            super(itemView);
            sentenceTextView = itemView.findViewById(R.id.englishSentence);
            spanishTranslationTextView = itemView.findViewById(R.id.spanishTranslation);
            dutchTranslationTextView = itemView.findViewById(R.id.dutchTranslation);
            russianTranslationTextView = itemView.findViewById(R.id.russianTranslation);
        }
    }
}
