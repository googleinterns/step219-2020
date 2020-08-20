package com.google.sps.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Calendar; 

public class CalendarExample1 {  
   public static void main(String[] args) {  
   Calendar calendar = Calendar.getInstance();  
   System.out.println("The current date is : " + calendar.getTime());  
   calendar.add(Calendar.DATE, -15);  
   System.out.println("15 days ago: " + calendar.getTime());  
   calendar.add(Calendar.MONTH, 4);  
   System.out.println("4 months later: " + calendar.getTime());  
   calendar.add(Calendar.YEAR, 2);  
   System.out.println("2 years later: " + calendar.getTime());  
   }  

@WebServlet("/time")
public class Time extends HttpServlet {
  private Calendar calendar;

  @Override
  public void init() {
    calendar = Calendar.getInstance());
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
    setTime(request.getParameter("year"), request.getParameter("month"), 
    request.getParameter("date"), request.getParameter("hour"), request.getParameter("minute"));
  }


  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html;");
    response.getWriter().println(calendar.getTime());
  }
}