package com.example.myapplication;

public class Note {

    private int id;
    private String userEmail;
    private String title;
    private String content;
    private String createdDate;
    private boolean favorite;
    private String tags;

    public Note() {

    }

    public Note(int id, String userEmail, String title, String content, String createdDate, boolean favorite, String tags) {
        this.id = id;
        this.userEmail = userEmail;
        this.title = title;
        this.content = content;
        this.createdDate = createdDate;
        this.favorite = favorite;
        this.tags = tags;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "Note{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", createdDate='" + createdDate + '\'' +
                ", favorite=" + favorite +
                '}';
    }
}
