package com.example.liengua;

import static com.example.liengua.CollectionManager.showCreateCollectionDialog;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

public class CollectionsActivity extends AppCompatActivity {
    private ListView collectionsListView;
    private Button addCollectionButton;
    private static List<CollectionLiengua> collections;
    private static TextView emptyCollectionsMessage;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collections_layout);

        addCollectionButton = findViewById(R.id.add_collection_button);
        ImageButton homeButton = findViewById(R.id.home_button);
        ImageButton menuButton = findViewById(R.id.menu_button);
        TextView pageTopTextView = findViewById(R.id.page_top_text_view);
        emptyCollectionsMessage = findViewById(R.id.empty_collections_message);
        pageTopTextView.setText("Collections");

        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(CollectionsActivity.this, MainActivity.class);
            startActivity(intent);
        });

        menuButton.setOnClickListener(this::showMenu);

        collectionsListView = findViewById(R.id.collections_list);

        // Retrieve collections from SharedPreferences
        collections = CollectionManager.getCollections(this);

        // Set up the adapter
        CollectionAdapter adapter = new CollectionAdapter(this, collections);
        collectionsListView.setAdapter(adapter);

        addCollectionButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View v) {
                showCreateCollectionDialog(CollectionsActivity.this, null);
            }
        });

        updateEmptyMessageVisibility();

    }
    static void updateEmptyMessageVisibility() {
        if (collections == null || collections.isEmpty()) {
            emptyCollectionsMessage.setVisibility(View.VISIBLE);
        } else {
            emptyCollectionsMessage.setVisibility(View.GONE);
        }
    }

    private void showMenu(View anchorView) {
        MenuHandler menuHandler = new MenuHandler(this);
        menuHandler.showMenu(anchorView);
    }
}