package com.example.liengua;

import static androidx.core.app.ActivityCompat.recreate;
import static com.example.liengua.FavoritesActivity.initializeFavorites;
import static com.example.liengua.FavoritesActivity.loadFavorites;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class DictionaryAdapter extends RecyclerView.Adapter<DictionaryAdapter.DictionaryViewHolder> {

    private final Context context;
    private static List<DictionaryEntry> dictionaryEntryList;
    private static Toast toast;
    private boolean showSpanish = true;
    private boolean showDutch = true;
    private boolean showRussian = true;
    private static List<DictionaryEntry> favoritesList;
    boolean showMoveButtonsForEntry = false;

    public DictionaryAdapter(Context context, List<DictionaryEntry> dictionaryEntryList, RecyclerView recyclerView, ImageButton scrollToTopButton, ImageButton scrollToBottomButton) {
        this.context = context;
        DictionaryAdapter.dictionaryEntryList = dictionaryEntryList;
        favoritesList = loadFavorites(context);

        scrollToTopButton.setOnClickListener(v -> recyclerView.smoothScrollToPosition(0));
        scrollToBottomButton.setOnClickListener(v -> recyclerView.smoothScrollToPosition(dictionaryEntryList.size() - 1));

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (!showMoveButtonsForEntry) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (!recyclerView.canScrollVertically(-1)) {
                        // User is at the top of the list
                        scrollToTopButton.setVisibility(View.GONE);
                        scrollToBottomButton.setVisibility(View.GONE);
                    } else if (dy > 0) {
                        // User is scrolling down
                        scrollToTopButton.setVisibility(View.GONE);
                        scrollToBottomButton.setVisibility(View.VISIBLE);
                    }

                    if (!recyclerView.canScrollVertically(1)) {
                        // User is at the bottom of the list
                        scrollToTopButton.setVisibility(View.GONE);
                        scrollToBottomButton.setVisibility(View.GONE);
                    } else if (dy < 0) {
                        // User is scrolling up
                        scrollToBottomButton.setVisibility(View.GONE);
                        scrollToTopButton.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    public void setLanguagesToShow(boolean showSpanish, boolean showDutch, boolean showRussian) {
        this.showSpanish = showSpanish;
        this.showDutch = showDutch;
        this.showRussian = showRussian;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setShowMoveButtons(boolean showMoveButtons) {
        this.showMoveButtonsForEntry = showMoveButtons;
        notifyDataSetChanged();
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

        // Check if the entry is in the favorites list and update the favorite icon
        if (isFavorite(entry, favoritesList)) {
            Log.d("Favorites","I FOUND this entry in the list!");
            entry.isFavorite = true;
            holder.favoriteButton.setImageResource(R.drawable.stars_filled_24px);
        } else {
            entry.isFavorite = false;
            holder.favoriteButton.setImageResource(R.drawable.stars_24px);
        }

        holder.favoriteButton.setOnClickListener(v -> {
            if (entry.isFavorite) {
                // Show confirmation dialog before removing favorite
                new AlertDialog.Builder(context)
                        .setTitle("Remove Favorite")
                        .setMessage(Html.fromHtml("Are you sure you want to remove<br><br><div style='text-align:center;'><b>'" + entry.getSentence() + "'</b></div>from favorites?"))
                        .setPositiveButton("Yes", (dialog, which) -> {
                            Log.d("Favorites","I wish to REMOVE this entry");
                            FavoritesActivity.removeFavorite(entry, context, favoritesList);
                            holder.favoriteButton.setImageResource(R.drawable.stars_24px);
                            Log.d("Favorites", entry + " " + entry.isFavorite);
                            Log.d("Favorites", favoritesList.toString());
                            notifyItemChanged(position);
                        })
                        .setNegativeButton("No", null)
                        .show();
            } else {
                Log.d("Favorites","I wish to ADD this entry");
                FavoritesActivity.addFavorite(entry, context, favoritesList);
                holder.favoriteButton.setImageResource(R.drawable.stars_filled_24px);
            }
            Log.d("Favorites", entry + " " + entry.isFavorite);
            Log.d("Favorites",favoritesList.toString());
            notifyItemChanged(position);
        });

        // Set the bookmark button state (example: change icon if bookmarked)
        if (CollectionManager.getCollections() != null && !CollectionManager.getCollections().isEmpty()) {
            holder.bookmarkButton.setImageResource(R.drawable.bookmark_filled_24px);
        } else {
            holder.bookmarkButton.setImageResource(R.drawable.bookmark_24px);
        }

        holder.bookmarkButton.setOnClickListener(v -> {
            // Show a dialog to select collections to add/remove the bookmark
            showBookmarkDialog(entry);
        });

        if (showMoveButtonsForEntry) {
            holder.moveButtonsLayout.setVisibility(View.VISIBLE);
        } else {
            holder.moveButtonsLayout.setVisibility(View.GONE);
        }

        // Set click listeners for move up and move down buttons
        holder.moveUpButton.setOnClickListener(v -> {
            if (position > 0) {
                Collections.swap(dictionaryEntryList, position, position - 1);
                notifyItemMoved(position, position - 1);
                FavoritesActivity.saveFavorites(context,dictionaryEntryList);
            }
        });

        holder.moveDownButton.setOnClickListener(v -> {
            if (position < dictionaryEntryList.size() - 1) {
                Collections.swap(dictionaryEntryList, position, position + 1);
                notifyItemMoved(position, position + 1);
                FavoritesActivity.saveFavorites(context,dictionaryEntryList);
            }
        });
    }
    public void restartActivity() {
        recreate();
    }

    private void recreate() {

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

    private boolean isFavorite(DictionaryEntry entry, List<DictionaryEntry> list) {
        favoritesList = list;
        for (DictionaryEntry favorite : favoritesList) {
            if (favorite.getSentence().equals(entry.getSentence())) {
                return true;
            }
        }
        return false;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateFavoritesList(List<DictionaryEntry> newFavoritesList) {
        Log.d("Favorites","Updating favorites list to: " + newFavoritesList);
        favoritesList = newFavoritesList;
        notifyDataSetChanged();
    }

    private void showBookmarkDialog(DictionaryEntry entry) {
        // Get the predefined collections
        List<Collection> predefinedCollections = CollectionManager.getCollections();
        String[] collectionNames = new String[predefinedCollections.size()];
        boolean[] checkedItems = new boolean[predefinedCollections.size()];

        // Populate the collection names and checked items
        for (int i = 0; i < predefinedCollections.size(); i++) {
            Collection collection = predefinedCollections.get(i);
            collectionNames[i] = collection.getName();
            checkedItems[i] = entry.getCollectionManager().containsCollection(collection);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Collections coming soon!")
                .setMultiChoiceItems(collectionNames, checkedItems, (dialog, which, isChecked) -> {
                    Collection collection = predefinedCollections.get(which);
                    if (isChecked) {
                        entry.getCollectionManager().addCollection(collection);
                    } else {
                        entry.getCollectionManager().removeCollection(collection);
                    }
                })
                .setPositiveButton("Save", (dialog, which) -> notifyItemChanged(dictionaryEntryList.indexOf(entry)))
                .setNegativeButton("Cancel", null)
                .show();
    }

    public static class DictionaryViewHolder extends RecyclerView.ViewHolder {

        LinearLayout moveButtonsLayout;
        TextView sentenceTextView;
        TextView dutchTranslationTextView;
        TextView spanishTranslationTextView;
        TextView russianTranslationTextView;
        ImageButton favoriteButton;
        ImageButton bookmarkButton;
        ImageButton moveUpButton;
        ImageButton moveDownButton;

        public DictionaryViewHolder(View itemView) {
            super(itemView);
            sentenceTextView = itemView.findViewById(R.id.englishSentence);
            spanishTranslationTextView = itemView.findViewById(R.id.spanishTranslation);
            dutchTranslationTextView = itemView.findViewById(R.id.dutchTranslation);
            russianTranslationTextView = itemView.findViewById(R.id.russianTranslation);
            favoriteButton = itemView.findViewById(R.id.favorite_button);
            bookmarkButton = itemView.findViewById(R.id.bookmark_button);
            moveButtonsLayout = itemView.findViewById(R.id.move_buttons_layout);
            moveUpButton = itemView.findViewById(R.id.move_up_button);
            moveDownButton = itemView.findViewById(R.id.move_down_button);
        }
    }
}
