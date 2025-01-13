package com.example.liengua;

import android.content.Context;
import android.widget.Toast;

import java.util.List;
import java.util.Random;

public class RandomPhraseHelper { //TODO: create a button that shows a random phrase
 /* Put this in the activity_main.xml
        <Button
        android:id="@+id/random_phrase_button"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="@string/get_random_phrase"
        android:layout_marginTop="16dp"/>
 * */
    private final List<DictionaryEntry> dictionaryEntries;

    public RandomPhraseHelper(List<DictionaryEntry> dictionaryEntries) {
        this.dictionaryEntries = dictionaryEntries;
    }

    // Method to get a random phrase
    public void showRandomPhrase(Context context) {
        if (dictionaryEntries.isEmpty()) {
            Toast.makeText(context, "No phrases available", Toast.LENGTH_SHORT).show();
            return;
        }

        // Pick a random phrase from the list
        Random random = new Random();
        DictionaryEntry randomEntry = dictionaryEntries.get(random.nextInt(dictionaryEntries.size()));

        // Show the random phrase to the user
        String randomPhrase = randomEntry.getSentence();  // Or any other field you want
        Toast.makeText(context, "Random Phrase: " + randomPhrase, Toast.LENGTH_LONG).show();
    }
}
