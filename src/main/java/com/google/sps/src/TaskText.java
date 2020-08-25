package com.google.sps.src;

public class TaskText{
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

  public void setComment(String comment) {
      this.comment = comment;
  }

  public void setTitle(String title) {
      this.title = title;
  }

  public String getComment() {
      return comment;
  }
  
  public String getTitle() {
      return title;
  }
}