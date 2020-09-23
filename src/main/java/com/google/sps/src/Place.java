package com.google.sps.src;

public class Place {

  private String name;
  private double lat = 90;
  private double lng = 90;

  public Place(String str) {
    this.name = str;
  }

  public Place(double lat, double lng) {
    this.lat = lat;
    this.lng = lng;
  }

  public Place(double lat, double lng, String str) {
    this.lat = lat;
    this.lng = lng;
    this.name = str;
  }

  public double getLat() {
    return lat;
  }

  public double getLng() {
    return lng;
  }

  public String getName() {
    return name;
  }
}
