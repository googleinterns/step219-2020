package com.google.sps.src;

public class TaskText {
    private String title;
    private String comment;

    public TaskText(String title) {
        this.title = title;
        this.comment = "";
    }

    public TaskText(String title, String comment) {
        this.title = title;
        this.comment = comment;
    }

    public TaskText() {
        this("New Task");
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
