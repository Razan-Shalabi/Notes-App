package com.example.myapplication;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.SessionManager;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.Locale;
import java.util.TreeSet;

public class AllNotesFragment extends Fragment implements NoteDetailFragment.NoteActionListener {

    private DatabaseHelper databaseHelper;
    private String userEmail;

    private EditText editTextSearch;
    private ChipGroup chipGroupTags;
    private ListView listViewNotes;
    private TextView textViewEmpty;

    private ArrayList<Note> allNotes = new ArrayList<>();
    private ArrayList<Note> displayedNotes = new ArrayList<>();
    private NoteAdapter adapter;

    private String selectedTag = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_all_notes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        databaseHelper = new DatabaseHelper(requireContext());
        userEmail = SessionManager.getInstance(requireContext()).getLoggedInUser();

        editTextSearch = view.findViewById(R.id.editText_search);
        chipGroupTags = view.findViewById(R.id.chipGroup_tags);
        listViewNotes = view.findViewById(R.id.listView_notes);
        textViewEmpty = view.findViewById(R.id.textView_emptyAllNotes);

        adapter = new NoteAdapter(getActivity(), displayedNotes, new NoteAdapter.OnFavoriteClickListener() {
            @Override
            public void onFavoriteClick(Note note) {
                databaseHelper.setFavorite(note.getId(), note.getUserEmail(), !note.isFavorite());
                loadNotes();
            }
        });
        listViewNotes.setAdapter(adapter);

        listViewNotes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Note note = displayedNotes.get(position);
                NoteDetailFragment detailFragment = NoteDetailFragment.newInstance(note.getId());
                detailFragment.setNoteActionListener(AllNotesFragment.this);
                detailFragment.show(getParentFragmentManager(), "note_detail");
            }
        });

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilters();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadNotes();
    }

    private void loadNotes() {
        allNotes = databaseHelper.getAllNotes(userEmail);
        buildTagChips();
        applyFilters();
    }

    private void buildTagChips() {
        TreeSet<String> distinctTags = new TreeSet<>();
        for (Note note : allNotes) {
            if (note.getTags() == null) continue;
            for (String tag : note.getTags().split(",")) {
                String normalized = TagUtils.normalizeTag(tag);
                if (!normalized.isEmpty()) {
                    distinctTags.add(normalized);
                }
            }
        }

        chipGroupTags.removeAllViews();

        if (distinctTags.isEmpty()) {
            chipGroupTags.setVisibility(View.GONE);
            selectedTag = null;
            return;
        }
        chipGroupTags.setVisibility(View.VISIBLE);

        Chip allChip = new Chip(requireContext());
        allChip.setText("All Tags");
        allChip.setCheckable(true);
        allChip.setChecked(selectedTag == null);
        allChip.setOnClickListener(v -> {
            selectedTag = null;
            applyFilters();
        });
        chipGroupTags.addView(allChip);

        for (String tag : distinctTags) {
            Chip chip = new Chip(requireContext());
            chip.setText(tag);
            chip.setCheckable(true);
            chip.setChecked(tag.equals(selectedTag));
            chip.setOnClickListener(v -> {
                selectedTag = tag;
                applyFilters();
            });
            chipGroupTags.addView(chip);
        }
    }

    private void applyFilters() {
        String query = editTextSearch.getText().toString().trim().toLowerCase(Locale.getDefault());

        displayedNotes.clear();
        for (Note note : allNotes) {
            boolean matchesQuery = query.isEmpty()
                    || note.getTitle().toLowerCase(Locale.getDefault()).contains(query)
                    || note.getContent().toLowerCase(Locale.getDefault()).contains(query);

            boolean matchesTag = selectedTag == null || noteHasTag(note, selectedTag);

            if (matchesQuery && matchesTag) {
                displayedNotes.add(note);
            }
        }
        adapter.notifyDataSetChanged();
        textViewEmpty.setVisibility(displayedNotes.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private boolean noteHasTag(Note note, String tag) {
        if (note.getTags() == null) return false;
        for (String noteTag : note.getTags().split(",")) {
            if (TagUtils.normalizeTag(noteTag).equals(tag)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onNoteChanged() {
        loadNotes();
    }
}
