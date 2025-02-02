package com.example.liengua;

import static androidx.core.content.ContextCompat.getColor;
import static androidx.core.content.ContextCompat.getDrawable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.graphics.drawable.DrawableContainerCompat;

import java.util.List;

public class TuneButtonHandler {
    private final List<DictionaryEntry> list;
    private final DictionaryAdapter dictionaryAdapter;

    private final Context context;
    public TuneButtonHandler(List<DictionaryEntry> list, DictionaryAdapter dictionaryAdapter, Context context
    ) {
        this.list = list;
        this.dictionaryAdapter = dictionaryAdapter;
        this.context = context;
    }
    @SuppressLint("UseCompatLoadingForDrawables")
    public void setTuneButton(ImageButton tuneButton,
                              DictionaryAdapter adapter,
                              ImageButton randomizeButton,
                              ImageButton sortButton,
                              ImageButton scrollToTopButton,
                              ImageButton scrollToBottomButton,
                              ImageButton refreshButton) {
        tuneButton.setBackground(getDrawable(context, R.drawable.border_solid));
        tuneButton.setOnClickListener(v -> {
            boolean showMoveButtons = !adapter.showMoveButtonsForEntry;
            adapter.setShowMoveButtons(showMoveButtons);
            randomizeButton.setVisibility(showMoveButtons ? View.GONE : View.VISIBLE);
            sortButton.setVisibility(showMoveButtons ? View.GONE : View.VISIBLE);
            scrollToTopButton.setVisibility(showMoveButtons ? View.GONE : View.VISIBLE);
            scrollToBottomButton.setVisibility(showMoveButtons ? View.GONE : View.VISIBLE);
            if (refreshButton.isShown()) {
                refreshButton.setVisibility(View.GONE);
            }
            if (showMoveButtons) {
                tuneButton.setBackgroundColor(getColor(context, R.color.peach_700));
                tuneButton.setBackground(getDrawable(context, R.drawable.border_solid_blue));

            } else {
                tuneButton.setBackground(getDrawable(context, R.drawable.border_solid));
            }
        });
    }
}
