package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.SessionManager;

import java.util.ArrayList;

public class FavoritesFragment extends Fragment implements NoteDetailFragment.NoteActionListener {

    private DatabaseHelper databaseHelper;
    private String userEmail;

    private ListView listViewFavorites;
    private TextView textViewEmpty;

    private ArrayList<Note> favoriteNotes = new ArrayList<>();
    private NoteAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        databaseHelper = new DatabaseHelper(requireContext());
        userEmail = SessionManager.getInstance(requireContext()).getLoggedInUser();

        listViewFavorites = view.findViewById(R.id.listView_favorites);
        textViewEmpty = view.findViewById(R.id.textView_emptyFavorites);

        adapter = new NoteAdapter(getActivity(), favoriteNotes, new NoteAdapter.OnFavoriteClickListener() {
            @Override
            public void onFavoriteClick(Note note) {
                databaseHelper.setFavorite(note.getId(), note.getUserEmail(), !note.isFavorite());
                loadFavorites();
            }
        });
        listViewFavorites.setAdapter(adapter);

        listViewFavorites.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Note note = favoriteNotes.get(position);
                NoteDetailFragment detailFragment = NoteDetailFragment.newInstance(note.getId());
                detailFragment.setNoteActionListener(FavoritesFragment.this);
                detailFragment.show(getParentFragmentManager(), "note_detail");
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFavorites();
    }

    private void loadFavorites() {
        favoriteNotes.clear();
        favoriteNotes.addAll(databaseHelper.getFavoriteNotes(userEmail));
        adapter.notifyDataSetChanged();
        textViewEmpty.setVisibility(favoriteNotes.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onNoteChanged() {
        loadFavorites();
    }
}
