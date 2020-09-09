package com.google.sps.src;


public class Task {

  private final long datastoreId;
  private final DateTime dateTime;
  private Time time;
  private Date date;
  private Place place;
  private String title;
  private String comment;

  public Task(DateTime dateTime, String title, String comment, Place place, long datastoreId) {
    this.dateTime = dateTime;
    this.title = title;
    this.comment = comment;
    this.place = place;
    this.date = new Date(dateTime.getDateAsString());
    this.time = new Time(dateTime.getTimeAsString());
    this.datastoreId = datastoreId;
  }

  public void setField(String fieldName, String data) throws RuntimeException {
    if (fieldName.equals("task_placeData")) {
      place = new Place(data);
    } else if (fieldName.equals("task_timeData")) {
      time = new Time(data);
    } else if (fieldName.equals("task_dateData")) {
      setDate(data);
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

  public void setTitle(String title) {
    this.title = title;
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
}