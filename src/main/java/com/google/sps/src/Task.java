package com.google.sps.src;


public class Task {
  private DateTime dateTime;
  private Time time;
  private Date date;
  private TaskText taskText;
  private Place place;
  private long number;

  public Task(DateTime dateTime, TaskText taskText, Place place, long number) {
    this.dateTime = dateTime;
    this.taskText = taskText;
    this.place = place;
    this.number = number;
    this.date = new Date(dateTime.getDateAsString());
    this.time = new Time(dateTime.getTimeAsString());
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

  public void setTime(String time) {
    this.dateTime.setTime(time);
    this.time = new Time(this.dateTime.getTimeAsString());
  }

  public void setDate(String date) {
    this.dateTime.setDate(date);
    this.date = new Date(this.dateTime.getDateAsString());
  }

  public TaskText getTaskText() {
    return taskText;
  }

  public Place getPlace() {
    return place;
  }
}