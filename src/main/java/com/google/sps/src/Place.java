package com.google.sps.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class Place {
  private String string;
  private double lat;
  private double lng;

  public Place(String str) {
      this.string = str;
  }
  public Place(double lat, double lng) {
      this.lat = lat;
      this.lng  lng;
  }
  public Place(double lat, double lng, String str) {
      this(lat, lng);
      this(str);
  }
}