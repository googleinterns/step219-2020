package com.google.sps.src;

public class Task {

  private final TaskText taskText;
  private final long number;
  private Time time;
  private Place place;

  public Task(Time time, TaskText taskText, Place place, long number) {
    this.time = time;
    this.taskText = taskText;
    this.place = place;
    this.number = number;
  }

  public void setField(String fieldName, String data) throws RuntimeException {
    if (fieldName.equals("task_placeData")) {
      place = new Place(data);
    } else if (fieldName.equals("task_timeData")) {
      time = new Time(data);
    } else if (fieldName.equals("task_titleData")) {
      taskText.setTitle(data);
    } else if (fieldName.equals("task_commentData")) {
      taskText.setComment(data);
    } else {
      throw new RuntimeException("Wrong field name");
    }
  }

  public long getNumber() {
    return number;
  }

  public void setComment(String message) {
    taskText.setComment(message);
  }

  public TaskText getTaskText() {
    return taskText;
  }

  public Time getTime() {
    return time;
  }

  public void setTime(Time time) {
    this.time = time;
  }

  public Place getPlace() {
    return place;
  }

  public void setPlace(Place place) {
    this.place = place;
  }
}
