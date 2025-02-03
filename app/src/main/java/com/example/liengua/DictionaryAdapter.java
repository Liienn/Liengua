package com.example.liengua;

import static android.content.ContentValues.TAG;
import static com.example.liengua.FavoritesActivity.loadFavorites;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.style.IconMarginSpan;
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
    private final CollectionLiengua currentCollection;
    private final Handler handler = new Handler(Looper.getMainLooper());
    public DictionaryAdapter(Context context, List<DictionaryEntry> dictionaryEntryList, RecyclerView recyclerView, ImageButton scrollToTopButton, ImageButton scrollToBottomButton, CollectionLiengua currentCollection) {
        this.context = context;
        DictionaryAdapter.dictionaryEntryList = dictionaryEntryList;
        favoritesList = loadFavorites(context);
        this.currentCollection = currentCollection;

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
                    } else if (!recyclerView.canScrollVertically(1)) {
                        // User is at the bottom of the list
                        scrollToTopButton.setVisibility(View.GONE);
                        scrollToBottomButton.setVisibility(View.GONE);
                    } else if (dy > 0) {
                        // User is scrolling down
                        scrollToTopButton.setVisibility(View.GONE);
                        scrollToBottomButton.setVisibility(View.VISIBLE);
                    } else if (dy < 0) {
                        // User is scrolling up
                        scrollToBottomButton.setVisibility(View.GONE);
                        scrollToTopButton.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    public static int getFavoriteCount() {
        return favoritesList.size();
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

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                holder.favoriteButton.setAlpha(0.7F);
            }
        } else {
            entry.isFavorite = false;
            holder.favoriteButton.setImageResource(R.drawable.stars_24px);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                holder.favoriteButton.setAlpha(0.3F);
            }
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
        // Check if the entry is in any collection
        boolean isBookmarked = false;
        List<CollectionLiengua> collections = CollectionManager.getCollections(context);
        for (CollectionLiengua collection : collections) {
            if (collection.getEntries().contains(entry)) {
                isBookmarked = true;
                break;
            }
        }

        if (isBookmarked) {
            holder.bookmarkButton.setImageResource(R.drawable.bookmark_filled_24px);
        } else {
            holder.bookmarkButton.setImageResource(R.drawable.bookmark_24px);
        }

        holder.bookmarkButton.setOnClickListener(v -> {
            // Show a dialog to select collections to add/remove the bookmark
            showBookmarkDialog(entry);
        });

        holder.removeEntryButton.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Remove from collection")
                    .setMessage(Html.fromHtml("Are you sure you want to remove<br><br><div style='text-align:center;'><b>'" + entry.getSentence() + "'</b></div>from this collection?"))
                    .setPositiveButton("Yes", (dialog, which) -> {
                        Log.d("Collection", "I wish to REMOVE this entry");
                        currentCollection.getEntries().remove(entry);
                        notifyItemRemoved(position);
                        CollectionManager.saveCollection(context, currentCollection);
                        notifyDataSetChanged();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
        holder.removeEntryButton.setVisibility(showMoveButtonsForEntry && isCurrentActivityCollectionEntriesActivity() ? View.VISIBLE : View.GONE);
        holder.moveButtonsLayout.setVisibility(showMoveButtonsForEntry ? View.VISIBLE : View.GONE);

        // Set click listeners for move up and move down buttons
        holder.moveUpButton.setOnClickListener(v -> {
            if (position > 0) {
                Collections.swap(dictionaryEntryList, position, position - 1);
                notifyItemMoved(position, position - 1);
            }
            if(isCurrentActivityFavoritesActivity()) {
                FavoritesActivity.saveFavorites(context, dictionaryEntryList);
            } else if (isCurrentActivityCollectionEntriesActivity()) {
                CollectionManager.saveCollection(context, currentCollection, dictionaryEntryList);
            }
            handler.postDelayed(this::notifyDataSetChanged, 300);
        });

        holder.moveDownButton.setOnClickListener(v -> {
            if (position < dictionaryEntryList.size() - 1) {
                Collections.swap(dictionaryEntryList, position, position + 1);
                notifyItemMoved(position, position + 1);
            }
            if(isCurrentActivityFavoritesActivity()) {
                FavoritesActivity.saveFavorites(context,dictionaryEntryList);
            } else if (currentCollection != null && isCurrentActivityCollectionEntriesActivity()) {
                CollectionManager.saveCollection(context, currentCollection, dictionaryEntryList);
            }
            handler.postDelayed(this::notifyDataSetChanged, 300);

        });
        Log.d(TAG, "Binding entry at position " + position + ": " + entry.getSentence());
    }
    private boolean isCurrentActivityFavoritesActivity() {
        Context currentContext = context;
        while (currentContext instanceof ContextWrapper) {
            if (currentContext instanceof FavoritesActivity) {
                return true;
            }
            currentContext = ((ContextWrapper) currentContext).getBaseContext();
        }
        return false;
    }
    private boolean isCurrentActivityCollectionEntriesActivity() {
        Context currentContext = context;
        while (currentContext instanceof ContextWrapper) {
            if (currentContext instanceof CollectionEntriesActivity) {
                return true;
            }
            currentContext = ((ContextWrapper) currentContext).getBaseContext();
        }
        return false;
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
        Log.d(TAG, "Total number of entries: " + dictionaryEntryList.size());
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
        handler.postDelayed(this::notifyDataSetChanged, 300);
    }

    private void showBookmarkDialog(DictionaryEntry entry) {
        // Get the predefined collections
        List<CollectionLiengua> predefinedCollections = CollectionManager.getCollections(context);
        String[] collectionNames = new String[predefinedCollections.size()];
        boolean[] checkedItems = new boolean[predefinedCollections.size()];

        // Populate the collection names and checked items
        for (int i = 0; i < predefinedCollections.size(); i++) {
            CollectionLiengua collection = predefinedCollections.get(i);
            collectionNames[i] = collection.getName();
            checkedItems[i] = collection.getEntries().contains(entry);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Collections usable, but under construction! Prepare for bugs")
                .setMultiChoiceItems(collectionNames, checkedItems, (dialog, which, isChecked) -> {
                    CollectionLiengua collection = predefinedCollections.get(which);
                    if (isChecked) {
                        CollectionManager.addEntryToCollection(context, entry, collection);
                    } else {
                        CollectionManager.removeEntryFromCollection(context, entry, collection);
                    }
                })
                .setPositiveButton("Create new", (dialog, which) -> {
                    CollectionManager.createCollection(context, dictionaryEntryList, dictionaryEntryList.indexOf(entry));
                    notifyItemChanged(dictionaryEntryList.indexOf(entry));
                })
                .setNegativeButton("Close", null)
                .show();
    }

    public static class DictionaryViewHolder extends RecyclerView.ViewHolder {

        LinearLayout moveButtonsLayout;
        TextView sentenceTextView;
        TextView spanishTranslationTextView, russianTranslationTextView, dutchTranslationTextView;
        ImageButton favoriteButton;
        ImageButton bookmarkButton, removeEntryButton;
        ImageButton moveUpButton, moveDownButton;

        public DictionaryViewHolder(View itemView) {
            super(itemView);
            sentenceTextView = itemView.findViewById(R.id.englishSentence);
            spanishTranslationTextView = itemView.findViewById(R.id.spanishTranslation);
            dutchTranslationTextView = itemView.findViewById(R.id.dutchTranslation);
            russianTranslationTextView = itemView.findViewById(R.id.russianTranslation);
            favoriteButton = itemView.findViewById(R.id.favorite_button);
            bookmarkButton = itemView.findViewById(R.id.bookmark_button);
            removeEntryButton = itemView.findViewById(R.id.entry_delete_button);
            moveButtonsLayout = itemView.findViewById(R.id.move_buttons_layout);
            moveUpButton = itemView.findViewById(R.id.move_up_button);
            moveDownButton = itemView.findViewById(R.id.move_down_button);
        }
    }
}
