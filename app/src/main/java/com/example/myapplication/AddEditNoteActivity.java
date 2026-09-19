package com.example.myapplication;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddEditNoteActivity extends AppCompatActivity {

    public static final String EXTRA_NOTE_ID = "extra_note_id";

    private EditText editTextTitle;
    private EditText editTextTags;
    private EditText editTextContent;
    private Button buttonSave;

    private DatabaseHelper databaseHelper;
    private String userEmail;
    private int noteId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_note);

        databaseHelper = new DatabaseHelper(this);
        userEmail = SessionManager.getInstance(this).getLoggedInUser();

        editTextTitle = findViewById(R.id.editText_noteTitle);
        editTextTags = findViewById(R.id.editText_noteTags);
        editTextContent = findViewById(R.id.editText_noteContent);
        buttonSave = findViewById(R.id.button_saveNote);

        noteId = getIntent().getIntExtra(EXTRA_NOTE_ID, -1);

        if (noteId != -1) {
            setTitle("Edit Note");
            Note note = databaseHelper.getNote(noteId, userEmail);
            if (note != null) {
                editTextTitle.setText(note.getTitle());
                editTextTags.setText(note.getTags());
                editTextContent.setText(note.getContent());
            }
        } else {
            setTitle("Add Note");
        }

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNote();
            }
        });
    }

    private void saveNote() {
        String title = editTextTitle.getText().toString().trim();
        String tags = TagUtils.normalizeTagsString(editTextTags.getText().toString());
        String content = editTextContent.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            editTextTitle.setError("Title is required");
            return;
        }

        if (TextUtils.isEmpty(content)) {
            editTextContent.setError("Content is required");
            return;
        }

        if (noteId == -1) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String createdDate = dateFormat.format(new Date());

            Note note = new Note(0, userEmail, title, content, createdDate, false, tags);
            databaseHelper.addNote(note);
            Toast.makeText(this, "Note added", Toast.LENGTH_SHORT).show();
        } else {
            Note note = databaseHelper.getNote(noteId, userEmail);
            if (note == null) {
                Toast.makeText(this, "This note no longer exists", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            note.setTitle(title);
            note.setContent(content);
            note.setTags(tags);
            databaseHelper.updateNote(note);
            Toast.makeText(this, "Note updated", Toast.LENGTH_SHORT).show();
        }

        finish();
    }
}
