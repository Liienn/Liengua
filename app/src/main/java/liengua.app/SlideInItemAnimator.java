package com.example.liengua;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.view.View;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

public class SlideInItemAnimator extends DefaultItemAnimator {

    @Override
    public boolean animateMove(RecyclerView.ViewHolder holder, int fromX, int fromY, int toX, int toY) {
        final View view = holder.itemView;
        final ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY", fromY - toY, 0);
        animator.setDuration(300);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                dispatchMoveFinished(holder);
            }
        });
        animator.start();
        return true;
    }

    @Override
    public void onMoveFinished(RecyclerView.ViewHolder item) {
        super.onMoveFinished(item);
        item.itemView.setTranslationY(0);
    }
}