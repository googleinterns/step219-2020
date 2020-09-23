package com.google.sps.src;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTime {

  private Date calendarDate;

  /**
   * To variables below are needed to be used in javascript
   */
  public DateTime() {
    calendarDate = new Date();
  }

  public DateTime(String dateStr) {
    try {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
      calendarDate = sdf.parse(dateStr);
    } catch (Exception e) {
      System.out.println("ERROR: " + e);
    }
  }

  public DateTime(Date date) {
    calendarDate = date;
  }

  public Date getTime() {
    return calendarDate;
  }

  public void setTime(String time) {
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    String strDate = dateFormat.format(calendarDate.getTime());
    try {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
      calendarDate = sdf.parse(strDate + " " + time);
    } catch (Exception e) {
      System.out.println("ERROR: " + e);
    }
  }

  public void setDate(String newDate) {
    DateFormat dateFormat = new SimpleDateFormat("HH:mm");
    String strDate = dateFormat.format(calendarDate.getTime());
    try {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
      calendarDate = sdf.parse(newDate + " " + strDate);
    } catch (Exception e) {
      System.out.println("ERROR: " + e);
    }
  }

  public String getTimeDateAsString() {
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    return dateFormat.format(calendarDate.getTime());
  }

  public String getDateAsString() {
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    return dateFormat.format(calendarDate.getTime());
  }

  public String getTimeAsString() {
    DateFormat dateFormat = new SimpleDateFormat("HH:mm");
    return dateFormat.format(calendarDate.getTime());
  }
}
