package com.google.sps.src;

import java.util.Calendar;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;  

public class DateTime {
  private Calendar calendar;

  public DateTime() {
    calendar = Calendar.getInstance();
  }

  public DateTime(int year, int month, int date, int hour, int minute) {
    calendar = Calendar.getInstance();
    calendar.set(Calendar.YEAR, year);
    calendar.set(Calendar.MONTH, month);
    calendar.set(Calendar.DATE, date);
    calendar.set(Calendar.HOUR, hour);
    calendar.set(Calendar.MINUTE, minute);
  }

  public DateTime(String dateStr) {
    calendar = Calendar.getInstance();
    try {
        Date date = new SimpleDateFormat("yyyy-mm-dd hh:mm").parse(dateStr);
        calendar.setTime(date);
    } catch (Exception e) {}
  }

  public DateTime(Date date) {
    calendar = Calendar.getInstance();
    calendar.setTime(date);
  }

  public Date getTime() {
    return calendar.getTime();
  }

  public void setTime(String time) {
    DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");  
    String strDate = dateFormat.format(calendar.getTime());
    try {
        Date date = new SimpleDateFormat("yyyy-mm-dd hh:mm").parse(strDate + " "+time);
        calendar.setTime(date);
    } catch (Exception e) {}
  }

  public void setDate(String newDate) {
    DateFormat dateFormat = new SimpleDateFormat("hh:mm");  
    String strDate = dateFormat.format(calendar.getTime());
    try {
        Date date = new SimpleDateFormat("yyyy-mm-dd hh:mm").parse(newDate +" "+ strDate);
        calendar.setTime(date);
    } catch (Exception e) {}
  }

  public String getTimeDateAsString() {
      DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm");  
      String strDate = dateFormat.format(calendar.getTime());  
    return strDate;
  }

  public String getDateAsString() {
      DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");  
      String strDate = dateFormat.format(calendar.getTime());  
    return strDate;
  }

  public String getTimeAsString() {
      DateFormat dateFormat = new SimpleDateFormat("hh:mm");  
      String strDate = dateFormat.format(calendar.getTime());  
    return strDate;
  }
}