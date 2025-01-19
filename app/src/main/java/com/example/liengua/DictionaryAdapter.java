package com.example.liengua;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


public class DictionaryAdapter extends RecyclerView.Adapter<DictionaryAdapter.DictionaryViewHolder> {

    private static List<DictionaryEntry> dictionaryEntryList;
    private static Toast toast;
    private boolean showSpanish = true;
    private boolean showDutch = true;
    private boolean showRussian = true;

    public DictionaryAdapter(List<DictionaryEntry> dictionaryEntryList) {
        DictionaryAdapter.dictionaryEntryList = dictionaryEntryList;
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
    private void setupTranslationTextView(TextView textView,String label, String language, String translation, boolean showTranslation, DictionaryViewHolder holder) {

        if (showTranslation && translation != null && !translation.isEmpty()) {
            textView.setText(label + ": " + translation);
            textView.setVisibility(View.VISIBLE);
            textView.setOnClickListener(v -> showAlternatives(language.toLowerCase(), textView, holder));
            textView.setOnLongClickListener(v -> {
                String fullText = textView.getText().toString();
                String textToCopy = fullText;
                if (fullText.contains(": ")) {
                    String[] parts = fullText.split(": ", 2);
                    if (parts.length > 1) {
                        textToCopy = parts[1];
                    }
                }
                ClipboardManager clipboard = (ClipboardManager) v.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Copied Text", textToCopy);
                clipboard.setPrimaryClip(clip);
                if(toast != null) {
                    toast.cancel();
                }
                return true; // Indicates the event is consumed
            });
        } else {
            textView.setVisibility(View.GONE);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull DictionaryViewHolder holder, int position) {
        DictionaryEntry entry = dictionaryEntryList.get(position);

        setupTranslationTextView(holder.spanishTranslationTextView,"ES", "spanish", entry.getTranslationSpanish(), showSpanish, holder);
        setupTranslationTextView(holder.dutchTranslationTextView, "NL","dutch", entry.getTranslationDutch(), showDutch, holder);
        setupTranslationTextView(holder.russianTranslationTextView, "RU","russian", entry.getTranslationRussian(), showRussian, holder);

        // Set the translation text
        holder.sentenceTextView.setText(entry.getSentence());
    }
    private void setTranslationTextViewText(TextView translationTextView, String translation) {
        translationTextView.setText(translation);
        translationTextView.setVisibility(View.VISIBLE);
        translationTextView.setMovementMethod(LinkMovementMethod.getInstance());
    }
    private void copyAlternativeToClipboard(int which, String language, List<String> allAlternatives, TextView translationTextView) {
        String selectedAlternative = allAlternatives.get(which);
        ClipboardManager clipboard = (ClipboardManager) translationTextView.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(language + " alternative", selectedAlternative);
        clipboard.setPrimaryClip(clip);
        if(toast != null) {
            toast.cancel();
        }
    }
    private void showAlternatives(String language, TextView translationTextView, DictionaryViewHolder holder) {
        DictionaryEntry entry = dictionaryEntryList.get(holder.getAdapterPosition());
        List<String> languageAlternatives = entry.getAlternatives().get(language);

        if (languageAlternatives != null && !languageAlternatives.isEmpty()) {
            // Create a new list to include the original translation as the first item
            List<String> allAlternatives = new ArrayList<>();
            String originalTranslation = extractOriginalTranslation(translationTextView);
            if (originalTranslation != null) {
                allAlternatives.add(originalTranslation);
            }
            allAlternatives.addAll(languageAlternatives);

            // Convert the list to a CharSequence array for the dialog
            CharSequence[] altArray = allAlternatives.toArray(new CharSequence[0]);

           AlertDialog.Builder builder = new AlertDialog.Builder(translationTextView.getContext());
            builder.setTitle(entry.getSentence())
                    .setItems(altArray, (dialog, which) -> {
                        //copyAlternativeToClipboard(which, language, allAlternatives, translationTextView);
                        // Don't dismiss the dialog here, so it stays open
                    })
                    .setPositiveButton("Close", (dialog, which) -> dialog.dismiss()) // Dialog closes when 'Close' is clicked
                    .setCancelable(true); // Dialog closes if clicked outside the dialog

            AlertDialog dialog = builder.create();

            // Prevent closing dialog on item click, but keep other behavior intact
            dialog.setOnShowListener(dialogInterface -> {
                ListView listView = dialog.getListView();
                listView.setOnItemClickListener((parent, view, position, id) -> {
                    copyAlternativeToClipboard(position, language, allAlternatives, translationTextView);
                });
            });

            dialog.show();
        } else {
            if(toast != null) {
                toast.cancel();
            }
            toast = Toast.makeText(translationTextView.getContext(), "No " + language + " alternatives available for '" + entry.getSentence() + "'", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private String extractOriginalTranslation(TextView translationTextView) {
        String text = translationTextView.getText().toString();
        String[] parts = text.split(": ");
        if (parts.length > 1) {
            return parts[1];
        }
        return text;
    }



    @Override
    public int getItemCount() {
        return dictionaryEntryList.size();
    }

    public static List<DictionaryEntry> getDictionaryEntryList() {
        return dictionaryEntryList;
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
