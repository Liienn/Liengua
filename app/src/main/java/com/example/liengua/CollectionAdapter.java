package com.example.liengua;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

public class CollectionAdapter extends BaseAdapter {
    private final Context context;
    private final List<CollectionLiengua> collections;

    public CollectionAdapter(Context context, List<CollectionLiengua> collections) {
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

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.collection_list_item, parent, false);
        }

        CollectionLiengua collection = collections.get(position);


        TextView nameTextView = convertView.findViewById(R.id.collection_name_text_view);
        TextView descriptionTextView = convertView.findViewById(R.id.collection_description_text_view);
        ImageButton deleteButton = convertView.findViewById(R.id.delete_collection_button);
        ImageButton editButton = convertView.findViewById(R.id.collection_edit_button);

        nameTextView.setText(collection.getName());
        descriptionTextView.setText(collection.getDescription());

        deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle(Html.fromHtml("Remove collection"))
                    .setMessage(Html.fromHtml("Are you sure you want to delete collection '" + collection.getName() +"'?"))
                    .setPositiveButton("Yes", (dialog, which) -> {
                        CollectionManager.deleteCollection(context, collection);
                        collections.remove(position);
                        notifyDataSetChanged();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        editButton.setOnClickListener(v -> {
            CollectionManager.showEditCollectionDialog(context, collection);
            notifyDataSetChanged();
        });

        convertView.setOnClickListener(v -> {
            Intent intent = new Intent(context, CollectionEntriesActivity.class);
            intent.putExtra("collection", collection);
            context.startActivity(intent);
        });

        return convertView;
    }
}
