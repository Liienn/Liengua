package com.example.liengua;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.view.LayoutInflater;

public class MenuHandler {

    private final Context context;
    private PopupWindow popupWindow;

    public MenuHandler(Context context) {
        this.context = context;
    }

    public void showMenu(View anchorView) {
        // Inflate the menu layout
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View menuView = inflater.inflate(R.layout.menu_layout, null);

        // Create the popup window
        popupWindow = new PopupWindow(menuView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                true);

        // Set up menu options
        Button favorites = menuView.findViewById(R.id.menu_favorites);
        Button collections = menuView.findViewById(R.id.menu_collections);
        Button exercises = menuView.findViewById(R.id.menu_exercises);

        favorites.setOnClickListener(v -> {
            // Start FavoritesActivity
            Intent intent = new Intent(context, FavoritesActivity.class);
            context.startActivity(intent);
            popupWindow.dismiss();
        });

        collections.setOnClickListener(v -> {
            // Start CollectionsActivity
            Intent intent = new Intent(context, CollectionsActivity.class);
            context.startActivity(intent);
            popupWindow.dismiss();
        });

        exercises.setOnClickListener(v -> {
            // Start ExercisesActivity
            Intent intent = new Intent(context, ExercisesActivity.class);
            context.startActivity(intent);
            popupWindow.dismiss();
        });

        // Show the popup window
        popupWindow.showAsDropDown(anchorView);
    }
}