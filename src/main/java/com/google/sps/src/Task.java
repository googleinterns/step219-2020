package com.google.sps.src;

public class Task {
  private Time time;
  private TaskText taskText;
  private Place place;

  public Task(Time time, TaskText taskText, Place place) {
    this.time = time;
    this.taskText = taskText;
    this.place = place;
  }

  public Task(Time time, TaskText taskText)
  {
    this.time = time;
    this.taskText = taskText;
  }

  public void setComment(String message) {
    taskText.setComment(message);
  }

  public void setPlace(Place place) {
    this.place = place;
  }

  public void setTime(Time time) {
    this.time = time;
  }

  public TaskText getTaskText() {
    return taskText;
  }

  public Time getTime() {
    return time;
  }

  public Place getPlace() {
    return place;
  }
}