package com.google.sps.src;

public class Task {

  private final long datastoreId;
  private Time time;
  private Place place;
  private String title;
  private String comment;

  public void setTitle(String title) {
    this.title = title;
  }

  public Task(Time time, String title, String comment, Place place, long datastoreId) {
    this.time = time;
    this.title = title;
    this.comment = comment;
    this.place = place;
    this.datastoreId = datastoreId;
  }

  public void setField(String fieldName, String data) throws RuntimeException {
    if (fieldName.equals("task_placeData")) {
      place = new Place(data);
    } else if (fieldName.equals("task_timeData")) {
      time = new Time(data);
    } else if (fieldName.equals("task_titleData")) {
      title = data;
    } else if (fieldName.equals("task_commentData")) {
      comment = data;
    } else {
      throw new RuntimeException("Wrong field name");
    }
  }

  public long getDatastoreId() {
    return datastoreId;
  }

  public void setComment(String message) {
    comment = message;
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
