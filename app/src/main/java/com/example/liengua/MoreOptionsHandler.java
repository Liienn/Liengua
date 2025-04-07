package com.example.liengua;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

public class MoreOptionsHandler {

    private final Context context;
    private PopupWindow popupWindow;

    private final DictionaryEntry entry;

    public MoreOptionsHandler(Context context, DictionaryEntry entry) {
        this.context = context;
        this.entry = entry;
    }

    public void showMoreOptions(View anchorView) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View moreOptionsView = inflater.inflate(R.layout.entry_more_options_layout, null);

        popupWindow = new PopupWindow(moreOptionsView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                true);

        Button giveFeedback = moreOptionsView.findViewById(R.id.options_give_feedback);
        Button flagRequestReview = moreOptionsView.findViewById(R.id.options_request_review);
        //Button flagReviewed = moreOptionsView.findViewById(R.id.options_reviewed);

        giveFeedback.setOnClickListener(v -> {
            Intent intent = new Intent(context, FeedbackActivity.class);
            intent.putExtra("entry", entry);
            context.startActivity(intent);
            popupWindow.dismiss();
        });

        flagRequestReview.setOnClickListener(v -> {
            Intent intent = new Intent(context, CollectionsActivity.class);
            context.startActivity(intent);
            popupWindow.dismiss();
        });


        popupWindow.showAsDropDown(anchorView);
    }
}
