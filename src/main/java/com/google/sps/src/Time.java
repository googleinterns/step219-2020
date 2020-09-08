package com.google.sps.src;

import java.util.Calendar;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;  

public class Time {
  private String time;
  
  public Time(String time) {
    this.time = time;
  }
  
  public String getTime()  {
    return time;
  }
}

/*
public class Time {
  private Calendar calendar;

  public Time() {
    calendar = Calendar.getInstance();
  }

  public Time(int year, int month, int date, int hour, int minute) {
    calendar = Calendar.getInstance();
    calendar.set(Calendar.YEAR, year);
    calendar.set(Calendar.MONTH, month);
    calendar.set(Calendar.DATE, date);
    calendar.set(Calendar.HOUR, hour);
    calendar.set(Calendar.MINUTE, minute);
  }

  public Date getTime() {
    return calendar.getTime();
  }

  public String getTimeAsString() {
      DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");  
      String strDate = dateFormat.format(calendar.getTime());  
    return strDate;
  }
}*/