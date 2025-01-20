package com.example.liengua;

import android.annotation.SuppressLint;
import android.content.Context;
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
        Button option1 = menuView.findViewById(R.id.menu_option_1);
        Button option2 = menuView.findViewById(R.id.menu_option_2);
        Button option3 = menuView.findViewById(R.id.menu_option_3);

        option1.setOnClickListener(v -> {
            // Handle option 1 click
            popupWindow.dismiss();
        });

        option2.setOnClickListener(v -> {
            // Handle option 2 click
            popupWindow.dismiss();
        });

        option3.setOnClickListener(v -> {
            // Handle option 3 click
            popupWindow.dismiss();
        });

        // Show the popup window
        popupWindow.showAsDropDown(anchorView);
    }
}