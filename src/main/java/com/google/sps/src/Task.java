package com.google.sps.src;

public class Task {
  private Time time;
  private TaskText taskText;
  private Place place;
  private long number;

  public Task(Time time, TaskText taskText, Place place, long number) {
    this.time = time;
    this.taskText = taskText;
    this.place = place;
    this.number = number;
  }

  public long getNumber() {
    return number;
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