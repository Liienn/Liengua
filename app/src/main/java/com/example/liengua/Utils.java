package com.example.liengua;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;

import androidx.core.content.ContextCompat;

public class Utils {
    public static void insertDrawable(Context context, SpannableString spannableString, String text, int drawableResId) {
        int start = spannableString.toString().indexOf(text);
        int end = start + text.length();

        if (start != -1) {
            @SuppressLint("UseCompatLoadingForDrawables")
            Drawable drawable = ContextCompat.getDrawable(context, drawableResId);
            if (drawable != null) {
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
                spannableString.setSpan(imageSpan, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            }
        }
    }
}
