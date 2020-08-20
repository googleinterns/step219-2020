package com.google.sps.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Calendar; 


@WebServlet("/time")
public class TimeServlet extends HttpServlet {
  private Calendar calendar;

  @Override
  public void init() {
    calendar = Calendar.getInstance();
  }

  public void setTime(int year, int month, int date, int hour, int minute) {
    calendar.set(Calendar.YEAR, year);
    calendar.set(Calendar.MONTH, month);
    calendar.set(Calendar.DATE, date);
    calendar.set(Calendar.HOUR, hour);
    calendar.set(Calendar.MINUTE, minute);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    setTime(Integer.valueOf(request.getParameter("year")), 
              Integer.valueOf(request.getParameter("month")), 
              Integer.valueOf(request.getParameter("date")),
              Integer.valueOf(request.getParameter("hour")),
              Integer.valueOf(request.getParameter("minute")));
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html;");
    response.getWriter().println(calendar.getTime());
  }
}