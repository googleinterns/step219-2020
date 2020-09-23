package com.google.sps.src;

public class Task {

  private final long datastoreId;
  // private final long userDatastoreId;
  private final String userDatastoreId;
  private DateTime dateTime;
  private Place place;
  private String title;
  private String comment;
  private Boolean isDone;

  public Task(
      DateTime dateTime,
      String title,
      String comment,
      Place place,
      long datastoreId,
      Boolean isDone,
      String userDatastoreId) {
    this.dateTime = dateTime;
    this.title = title;
    this.comment = comment;
    this.place = place;
    this.datastoreId = datastoreId;
    this.isDone = isDone;
    this.userDatastoreId = userDatastoreId;
  }

  public void setField(String fieldName, String data) throws RuntimeException {
    if (fieldName.equals("task_placeData")) {
      place = new Place(data);
    } else if (fieldName.equals("task_timeData")) {
      this.dateTime = new DateTime(data);
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

  public void setDate(String date) {
    this.dateTime.setDate(date);
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public DateTime getTime() {
    return dateTime;
  }

  public void setTime(String time) {
    this.dateTime.setTime(time);
  }

  public Place getPlace() {
    return place;
  }

  public void setPlace(Place place) {
    this.place = place;
  }

  public Boolean getIsDone() {
    return isDone;
  }

  public void setIsDone(Boolean isDone) {
    this.isDone = isDone;
  }
}
