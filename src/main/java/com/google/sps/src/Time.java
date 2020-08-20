package com.google.sps.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Calendar; 

public class Time {
  private Calendar calendar;

  public Time() {
    calendar = Calendar.getInstance();
  }

  public void setTime(int year, int month, int date, int hour, int minute) {
    calendar.set(Calendar.YEAR, year);
    calendar.set(Calendar.MONTH, month);
    calendar.set(Calendar.DATE, date);
    calendar.set(Calendar.HOUR, hour);
    calendar.set(Calendar.MINUTE, minute);
  }

  public DATE getTime(int year, int month, int date, int hour, int minute) {
    return calendar.getTime()
  }
}