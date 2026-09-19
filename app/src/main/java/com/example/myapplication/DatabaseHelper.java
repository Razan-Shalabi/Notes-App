package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "NotesAppDB";
    private static final int DATABASE_VERSION = 2;

    private static final String TABLE_USERS = "USERS";
    private static final String TABLE_NOTES = "NOTES";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USERS + "(" +
                "EMAIL TEXT PRIMARY KEY," +
                "FIRST_NAME TEXT," +
                "LAST_NAME TEXT," +
                "PASSWORD TEXT," +
                "SALT TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_NOTES + "(" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "USER_EMAIL TEXT," +
                "TITLE TEXT," +
                "CONTENT TEXT," +
                "CREATED_DATE TEXT," +
                "IS_FAVORITE INTEGER," +
                "TAGS TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        onCreate(db);
    }

    public boolean isEmailRegistered(String email) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT EMAIL FROM " + TABLE_USERS + " WHERE EMAIL=?", new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean registerUser(User user) {
        SQLiteDatabase db = getWritableDatabase();
        String salt = PasswordUtils.generateSalt();
        String hashedPassword = PasswordUtils.hashPassword(user.getPassword(), salt);
        ContentValues values = new ContentValues();
        values.put("EMAIL", user.getEmail());
        values.put("FIRST_NAME", user.getFirstName());
        values.put("LAST_NAME", user.getLastName());
        values.put("PASSWORD", hashedPassword);
        values.put("SALT", salt);
        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    public boolean validateLogin(String email, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT PASSWORD, SALT FROM " + TABLE_USERS + " WHERE EMAIL=?",
                new String[]{email});
        boolean valid = false;
        if (cursor.moveToFirst()) {
            String storedHash = cursor.getString(0);
            String salt = cursor.getString(1);
            valid = PasswordUtils.verifyPassword(password, salt, storedHash);
        }
        cursor.close();
        return valid;
    }

    public User getUser(String email) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE EMAIL=?", new String[]{email});
        User user = null;
        if (cursor.moveToFirst()) {
            user = new User(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3)
            );
            user.setSalt(cursor.getString(4));
        }
        cursor.close();
        return user;
    }

    public boolean updateUserProfile(String email, String firstName, String lastName) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("FIRST_NAME", firstName);
        values.put("LAST_NAME", lastName);
        int rows = db.update(TABLE_USERS, values, "EMAIL=?", new String[]{email});
        return rows > 0;
    }

    public boolean updateUserPassword(String email, String newPlainPassword) {
        SQLiteDatabase db = getWritableDatabase();
        String salt = PasswordUtils.generateSalt();
        String hashedPassword = PasswordUtils.hashPassword(newPlainPassword, salt);
        ContentValues values = new ContentValues();
        values.put("PASSWORD", hashedPassword);
        values.put("SALT", salt);
        int rows = db.update(TABLE_USERS, values, "EMAIL=?", new String[]{email});
        return rows > 0;
    }

    public long addNote(Note note) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("USER_EMAIL", note.getUserEmail());
        values.put("TITLE", note.getTitle());
        values.put("CONTENT", note.getContent());
        values.put("CREATED_DATE", note.getCreatedDate());
        values.put("IS_FAVORITE", note.isFavorite() ? 1 : 0);
        values.put("TAGS", note.getTags());
        return db.insert(TABLE_NOTES, null, values);
    }

    public boolean updateNote(Note note) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("TITLE", note.getTitle());
        values.put("CONTENT", note.getContent());
        values.put("TAGS", note.getTags());
        int rows = db.update(TABLE_NOTES, values, "ID=? AND USER_EMAIL=?",
                new String[]{String.valueOf(note.getId()), note.getUserEmail()});
        return rows > 0;
    }

    public boolean setFavorite(int noteId, String userEmail, boolean favorite) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("IS_FAVORITE", favorite ? 1 : 0);
        int rows = db.update(TABLE_NOTES, values, "ID=? AND USER_EMAIL=?",
                new String[]{String.valueOf(noteId), userEmail});
        return rows > 0;
    }

    public boolean deleteNote(int noteId, String userEmail) {
        SQLiteDatabase db = getWritableDatabase();
        int rows = db.delete(TABLE_NOTES, "ID=? AND USER_EMAIL=?",
                new String[]{String.valueOf(noteId), userEmail});
        return rows > 0;
    }

    public Note getNote(int noteId, String userEmail) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NOTES + " WHERE ID=? AND USER_EMAIL=?",
                new String[]{String.valueOf(noteId), userEmail});
        Note note = null;
        if (cursor.moveToFirst()) {
            note = cursorToNote(cursor);
        }
        cursor.close();
        return note;
    }

    public ArrayList<Note> getAllNotes(String userEmail) {
        ArrayList<Note> notes = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NOTES + " WHERE USER_EMAIL=? ORDER BY ID DESC",
                new String[]{userEmail});
        while (cursor.moveToNext()) {
            notes.add(cursorToNote(cursor));
        }
        cursor.close();
        return notes;
    }

    public ArrayList<Note> getFavoriteNotes(String userEmail) {
        ArrayList<Note> notes = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NOTES + " WHERE USER_EMAIL=? AND IS_FAVORITE=1 ORDER BY ID DESC",
                new String[]{userEmail});
        while (cursor.moveToNext()) {
            notes.add(cursorToNote(cursor));
        }
        cursor.close();
        return notes;
    }

    public ArrayList<Note> getSortedNotes(String userEmail, String sortBy) {
        ArrayList<Note> notes = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String orderBy = "CREATED_DATE DESC";
        if ("alpha".equals(sortBy)) {
            orderBy = "TITLE COLLATE NOCASE ASC";
        }
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NOTES + " WHERE USER_EMAIL=? ORDER BY " + orderBy,
                new String[]{userEmail});
        while (cursor.moveToNext()) {
            notes.add(cursorToNote(cursor));
        }
        cursor.close();
        return notes;
    }

    private Note cursorToNote(Cursor cursor) {
        return new Note(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4),
                cursor.getInt(5) == 1,
                cursor.getString(6)
        );
    }
}
