package com.example.myapplication;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.myapplication.R;

import java.util.List;

public class NoteAdapter extends ArrayAdapter<Note> {

    public interface OnFavoriteClickListener {
        void onFavoriteClick(Note note);
    }

    private Activity activity;
    private List<Note> notes;
    private OnFavoriteClickListener favoriteClickListener;

    public NoteAdapter(Activity activity, List<Note> notes, OnFavoriteClickListener favoriteClickListener) {
        super(activity, R.layout.list_item_note, notes);
        this.activity = activity;
        this.notes = notes;
        this.favoriteClickListener = favoriteClickListener;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(activity).inflate(R.layout.list_item_note, parent, false);
        }

        final Note note = notes.get(position);

        TextView textViewTitle = convertView.findViewById(R.id.textView_noteTitle);
        TextView textViewDate = convertView.findViewById(R.id.textView_noteDate);
        TextView textViewTags = convertView.findViewById(R.id.textView_noteTags);
        final ImageView imageViewFavorite = convertView.findViewById(R.id.imageView_favorite);

        textViewTitle.setText(note.getTitle());
        textViewDate.setText(DateUtils.formatForDisplay(note.getCreatedDate()));

        String normalizedTags = TagUtils.normalizeTagsString(note.getTags());
        if (!normalizedTags.isEmpty()) {
            textViewTags.setVisibility(View.VISIBLE);
            textViewTags.setText(normalizedTags);
        } else {
            textViewTags.setVisibility(View.GONE);
        }

        imageViewFavorite.setImageResource(note.isFavorite() ? R.drawable.ic_favorite_filled : R.drawable.ic_favorite_border);

        imageViewFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(activity, R.anim.favorite_scale);
                imageViewFavorite.startAnimation(animation);
                if (favoriteClickListener != null) {
                    favoriteClickListener.onFavoriteClick(note);
                }
            }
        });

        return convertView;
    }
}
