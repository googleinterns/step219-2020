package com.google.sps.src;

public class Place {
  private String string;
  private double lat;
  private double lng;

  public Place(String str) {
    this.string = str;
  }

  public Place(double lat, double lng) {
    this.lat = lat;
    this.lng = lng;
  }

  public Place(double lat, double lng, String str) {
    this.lat = lat;
    this.lng = lng;
    this.string = str;
  }

  public double getLat() {
    return lat;
  }

  public double getLng() {
    return lng;
  }

  public String getString() {
    return string;
  }
}
