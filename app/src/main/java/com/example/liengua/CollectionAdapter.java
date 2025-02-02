package com.example.liengua;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

public class CollectionAdapter extends BaseAdapter {
    private final Context context;
    private final List<Collection> collections;

    public CollectionAdapter(Context context, List<Collection> collections) {
        this.context = context;
        this.collections = collections;
    }

    @Override
    public int getCount() {
        return collections.size();
    }

    @Override
    public Object getItem(int position) {
        return collections.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.collection_list_item, parent, false);
        }

        Collection collection = collections.get(position);

        TextView nameTextView = convertView.findViewById(R.id.collection_name_text_view);
        TextView descriptionTextView = convertView.findViewById(R.id.collection_description_text_view);
        ImageButton deleteButton = convertView.findViewById(R.id.delete_collection_button);

        nameTextView.setText(collection.getName());
        descriptionTextView.setText(collection.getDescription());

        deleteButton.setOnClickListener(v -> {
            CollectionManager.deleteCollection(context, collection);
            collections.remove(position);
            notifyDataSetChanged();
        });

        return convertView;
    }
}
