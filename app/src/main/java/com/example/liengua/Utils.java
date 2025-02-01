package com.example.liengua;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

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
    public static void setDrawableAlpha(Context context, Drawable drawable, int alphaPercent) {
        if (drawable != null) {
            drawable = DrawableCompat.wrap(drawable);
            int alpha = (int) (255 * (alphaPercent / 100.0));
            drawable.setAlpha(alpha);
        }
    }

    public static void setDrawableWithAlpha(Context context, TextView textView, int drawableResId, int alphaPercent) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableResId);
        if (drawable != null) {
            setDrawableAlpha(context, drawable, alphaPercent);
            textView.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
        }
    }

    public static void setDrawableWithAlpha(Context context, Button button, int drawableResId, int alphaPercent) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableResId);
        if (drawable != null) {
            drawable = DrawableCompat.wrap(drawable);
            setDrawableAlpha(context, drawable, alphaPercent);
            button.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
        }
    }
}
