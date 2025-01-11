package com.example.liengua;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class DictionaryAdapter extends RecyclerView.Adapter<DictionaryAdapter.DictionaryViewHolder> {

    private List<Translation> translationList;

    public DictionaryAdapter(List<Translation> translationList) {
        this.translationList = translationList;
    }

    @Override
    public DictionaryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.translation_item, parent, false);
        return new DictionaryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DictionaryViewHolder holder, int position) {
        Translation translation = translationList.get(position);
        holder.sentenceTextView.setText(translation.getSentence());
        holder.translationTextView.setText(translation.getTranslationEnglish());  // Example: Display English translation
    }

    @Override
    public int getItemCount() {
        return translationList.size();
    }

    public static class DictionaryViewHolder extends RecyclerView.ViewHolder {
        TextView sentenceTextView;
        TextView translationTextView;

        public DictionaryViewHolder(View itemView) {
            super(itemView);
            sentenceTextView = itemView.findViewById(R.id.sentence);
            translationTextView = itemView.findViewById(R.id.translation);
        }
    }
}
