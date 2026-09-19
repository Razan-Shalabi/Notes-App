package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.SessionManager;

import java.util.ArrayList;

public class SortedFragment extends Fragment implements NoteDetailFragment.NoteActionListener {

    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;
    private String userEmail;

    private RadioGroup radioGroupSortBy;
    private RadioButton radioSortByDate;
    private RadioButton radioSortByAlpha;
    private ListView listViewSorted;

    private ArrayList<Note> sortedNotes = new ArrayList<>();
    private NoteAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sorted, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        databaseHelper = new DatabaseHelper(requireContext());
        sessionManager = SessionManager.getInstance(requireContext());
        userEmail = sessionManager.getLoggedInUser();

        radioGroupSortBy = view.findViewById(R.id.radioGroup_sortBy);
        radioSortByDate = view.findViewById(R.id.radio_sortByDate);
        radioSortByAlpha = view.findViewById(R.id.radio_sortByAlpha);
        listViewSorted = view.findViewById(R.id.listView_sorted);

        adapter = new NoteAdapter(getActivity(), sortedNotes, new NoteAdapter.OnFavoriteClickListener() {
            @Override
            public void onFavoriteClick(Note note) {
                databaseHelper.setFavorite(note.getId(), note.getUserEmail(), !note.isFavorite());
                loadSortedNotes();
            }
        });
        listViewSorted.setAdapter(adapter);

        listViewSorted.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Note note = sortedNotes.get(position);
                NoteDetailFragment detailFragment = NoteDetailFragment.newInstance(note.getId());
                detailFragment.setNoteActionListener(SortedFragment.this);
                detailFragment.show(getParentFragmentManager(), "note_detail");
            }
        });

        String currentSort = sessionManager.getSortPreference();
        if ("alpha".equals(currentSort)) {
            radioSortByAlpha.setChecked(true);
        } else {
            radioSortByDate.setChecked(true);
        }

        radioGroupSortBy.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                String sortBy = checkedId == R.id.radio_sortByAlpha ? "alpha" : "date";
                sessionManager.setSortPreference(sortBy);
                loadSortedNotes();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadSortedNotes();
    }

    private void loadSortedNotes() {
        String sortBy = sessionManager.getSortPreference();
        sortedNotes.clear();
        sortedNotes.addAll(databaseHelper.getSortedNotes(userEmail, sortBy));
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onNoteChanged() {
        loadSortedNotes();
    }
}
