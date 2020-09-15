package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.sps.src.DateTime;
import com.google.sps.src.Place;
import com.google.sps.src.Task;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

@WebServlet("/user-data")
public class UserDataServlet extends HttpServlet {

  /**
   * An ArrayList which contains all task of the user. All views for tasks in the UI are connected
   * with this array
   */
  private String user_id;
  private long user_key_id;

  /** Initialize list of tasks taken from Datastore */
  @Override
  public void init() {
    user_id = "";
    user_key_id = 500;
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    System.out.println("LOG: doPost() function");
    user_id = request.getParameter("user-id");
    System.out.println("LOG: user id=" + user_id);
    Key key = KeyFactory.createKey("user", user_id);
    System.out.println("Key key = KeyFactory");
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    try {
      Entity entity = datastore.get(key);
      user_key_id = entity.getKey().getId();
      System.out.println("User " + user_id+ " exists");
    } catch (Exception e) {
      Entity entity = new Entity("user", user_id);
      entity.setProperty("user_id", user_id);
      entity.setProperty("key_name", entity.getKey().getName());
      entity.setProperty("key_id", entity.getKey().getId());
      user_key_id = entity.getKey().getId();
      System.out.println("User " + user_id +" NOT exists");
      System.out.println("User key " + entity.getKey().getName());
      datastore.put(entity);
    }
    user_id = key.getName();
    response.getWriter().println(new Gson().toJson(user_key_id));
    System.out.println("Key_id "+ user_key_id);
    System.out.println("User_id "+ user_id);

/*

    Entity entity = new Entity("user", user_id);
    user_key_id = entity.getKey().getId();
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(entity);
    System.out.println("Key_id"+ user_key_id);*/
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Gson gson = new Gson();
    //response.getWriter().println(gson.toJson(user_key_id));
    response.getWriter().println(gson.toJson(user_id));
  }
}