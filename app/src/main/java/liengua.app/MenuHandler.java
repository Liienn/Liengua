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
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View menuView = inflater.inflate(R.layout.menu_layout, null);

        popupWindow = new PopupWindow(menuView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                true);

        Button favorites = menuView.findViewById(R.id.menu_favorites);
        Button collections = menuView.findViewById(R.id.menu_collections);
        Button exercises = menuView.findViewById(R.id.menu_exercises);
        Button about = menuView.findViewById(R.id.menu_about);

        com.example.liengua.Utils.setDrawableWithAlpha(context, favorites, R.drawable.stars_24px, 80);
        com.example.liengua.Utils.setDrawableWithAlpha(context, collections, R.drawable.bookmark_24px, 80);
        com.example.liengua.Utils.setDrawableWithAlpha(context, exercises, R.drawable.sports_gymnastics_24px, 80);
        com.example.liengua.Utils.setDrawableWithAlpha(context, about, R.drawable.info_24px, 80);

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

            about.setOnClickListener(v -> {
                // Start AboutActivity
                Intent intent = new Intent(context, AboutActivity.class);
                context.startActivity(intent);
                popupWindow.dismiss();
            });

        popupWindow.showAsDropDown(anchorView);
    }
}