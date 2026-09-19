package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.myapplication.AddEditNoteActivity;
import com.example.myapplication.R;

public class NoteDetailFragment extends DialogFragment {

    private static final String ARG_NOTE_ID = "arg_note_id";

    public interface NoteActionListener {
        void onNoteChanged();
    }

    private NoteActionListener listener;
    private DatabaseHelper databaseHelper;
    private String userEmail;
    private Note note;

    public static NoteDetailFragment newInstance(int noteId) {
        NoteDetailFragment fragment = new NoteDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_NOTE_ID, noteId);
        fragment.setArguments(args);
        return fragment;
    }

    public void setNoteActionListener(NoteActionListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_note_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        databaseHelper = new DatabaseHelper(requireContext());
        userEmail = SessionManager.getInstance(requireContext()).getLoggedInUser();
        int noteId = getArguments().getInt(ARG_NOTE_ID);
        note = databaseHelper.getNote(noteId, userEmail);

        if (note == null) {
            dismiss();
            return;
        }

        final TextView textViewTitle = view.findViewById(R.id.textView_detailTitle);
        TextView textViewDate = view.findViewById(R.id.textView_detailDate);
        TextView textViewTags = view.findViewById(R.id.textView_detailTags);
        TextView textViewContent = view.findViewById(R.id.textView_detailContent);
        final ImageView imageViewFavorite = view.findViewById(R.id.imageView_detailFavorite);
        ImageButton buttonEdit = view.findViewById(R.id.button_detailEdit);
        ImageButton buttonEmail = view.findViewById(R.id.button_detailEmail);
        ImageButton buttonDelete = view.findViewById(R.id.button_detailDelete);

        textViewTitle.setText(note.getTitle());
        textViewDate.setText(DateUtils.formatForDisplay(note.getCreatedDate()));
        textViewContent.setText(note.getContent());

        String normalizedTags = TagUtils.normalizeTagsString(note.getTags());
        if (!normalizedTags.isEmpty()) {
            textViewTags.setText("Tags: " + normalizedTags);
        } else {
            textViewTags.setText("");
        }

        updateFavoriteIcon(imageViewFavorite);

        imageViewFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewFavorite.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.favorite_scale));
                note.setFavorite(!note.isFavorite());
                databaseHelper.setFavorite(note.getId(), userEmail, note.isFavorite());
                updateFavoriteIcon(imageViewFavorite);
                notifyChanged();
            }
        });

        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddEditNoteActivity.class);
                intent.putExtra(AddEditNoteActivity.EXTRA_NOTE_ID, note.getId());
                startActivity(intent);
                notifyChanged();
                dismiss();
            }
        });

        buttonEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendByEmail();
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDelete();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog() != null ? getDialog().getWindow() : null;
        if (window != null) {
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            int width = (int) (metrics.widthPixels * 0.92);
            window.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT);
        }
    }

    private void updateFavoriteIcon(ImageView imageView) {
        imageView.setImageResource(note.isFavorite() ? R.drawable.ic_favorite_filled : R.drawable.ic_favorite_border);
    }

    private void sendByEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setType("message/rfc822");
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, note.getTitle());
        emailIntent.putExtra(Intent.EXTRA_TEXT, note.getContent());
        if (emailIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivity(emailIntent);
        } else {
            Toast.makeText(requireContext(), "No email app found on this device", Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmDelete() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Note")
                .setMessage("Are you sure you want to delete this note?")
                .setPositiveButton("Delete", new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(android.content.DialogInterface dialog, int which) {
                        databaseHelper.deleteNote(note.getId(), userEmail);
                        notifyChanged();
                        dismiss();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void notifyChanged() {
        if (listener != null) {
            listener.onNoteChanged();
        }
    }
}
