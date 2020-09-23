package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.sps.src.Users;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/update-server-task-list")
public class ServerUpdateServlet extends HttpServlet {

  private void doEditTask(HttpServletRequest request, HttpServletResponse response, long number)
      throws IOException {
    Users users = new Users();
    String user_id = users.getUserId(request.getUserPrincipal());
    //String user_id = request.getParameter("user-id");
    Key key = new KeyFactory.Builder("user", user_id)
        .addChild("task", number)
        .getKey();
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    try {
      Entity entity = datastore.get(key);
      Map<String, String> viewToProperty = new HashMap<>();
      viewToProperty.put("task_titleData", "text");
      viewToProperty.put("task_timeData", "time");
      viewToProperty.put("task_placeData", "place");
      viewToProperty.put("task_commentData", "comment");

      String newFieldData = request.getParameter("new_data");
      entity.setProperty(viewToProperty.get(request.getParameter("field")), newFieldData);
      datastore.put(entity);
    } catch (Exception e) {
      System.out.println("Key doesn't exists" + e);
      response.sendError(HttpServletResponse.SC_BAD_REQUEST);
    }
  }

  private void doDeleteTask(HttpServletRequest request, HttpServletResponse response, long number) {
    Users users = new Users();
    String user_id = users.getUserId(request.getUserPrincipal());
    //String user_id = request.getParameter("user-id");
    Key key = new KeyFactory.Builder("user", user_id)
        .addChild("task", number)
        .getKey();
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.delete(key);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    long number = Long.parseLong(request.getParameter("number"));

    String type = request.getParameter("type");
    if (type.equals("edit")) {
      doEditTask(request, response, number);
    } else if (type.equals("delete")) {
      doDeleteTask(request, response, number);
    } else if (type.equals("change")) {
      doChangeTask(request, response, number);
    } else {
      System.out.println("There is no needed type of request");
      response.sendError(HttpServletResponse.SC_BAD_REQUEST);
    }
  }

  private void doChangeTask(HttpServletRequest request, HttpServletResponse response, long number)
      throws IOException {
    Users users = new Users();
    String user_id = users.getUserId(request.getUserPrincipal());
    //String user_id = request.getParameter("user-id");
    Key key = new KeyFactory.Builder("user", user_id)
        .addChild("task", number)
        .getKey();
    //Key key = KeyFactory.createKey("task", number);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    try {
      Entity entity = datastore.get(key);
      entity.setProperty("text", request.getParameter("title"));
      entity.setProperty("comment", request.getParameter("comment"));
      entity.setProperty("place", request.getParameter("place"));
      entity.setProperty("time", request.getParameter("time"));
      entity.setProperty("date", request.getParameter("date"));
      entity.setProperty("isDone", request.getParameter("isDone"));

      if (Boolean.parseBoolean(request.getParameter("are_coordinates_changed"))) {
        entity.setProperty("lat", Double.parseDouble(request.getParameter("lat")));
        entity.setProperty("lng", Double.parseDouble(request.getParameter("lng")));
      }

      Date calendarDate = new SimpleDateFormat("yyyy-MM-dd HH:mm")
          .parse(request.getParameter("date") + " " + request.getParameter("time"));
      entity.setProperty("dateTime", calendarDate);
      datastore.put(entity);

    } catch (Exception e) {
      System.out.println("Request error: " + e);
      response.sendError(HttpServletResponse.SC_BAD_REQUEST);
    }
  }
}
