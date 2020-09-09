package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.Gson;
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

@WebServlet("/send-task")
public class TaskServlet extends HttpServlet {

  private ArrayList<Task> tasks;

  @Override
  public void init() {
    tasks = new ArrayList<Task>();

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    Query query = new Query("task");
    query.addSort("dateTime", SortDirection.ASCENDING);
    PreparedQuery results = datastore.prepare(query);
    for (Entity entity : results.asIterable()) {
      String text = (String) entity.getProperty("text");
      Date dateTime = (Date) entity.getProperty("dateTime");
      String place = (String) entity.getProperty("place");
      String comment = (String) entity.getProperty("comment");

      Task task =
          new Task(
              new DateTime(dateTime),
              text, comment,
              new Place(place),
              entity.getKey().getId());
      System.out.println(task);
      tasks.add(task);
    }
  }

  private Task getTask(HttpServletRequest request, long id) {
    return new Task(
        new DateTime(request.getParameter("task-date") + " " + request.getParameter("task-time")),
        request.getParameter("task-text"), request.getParameter("task-comment"),
        new Place(request.getParameter("task-place")),
        id);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    System.out.println(request.getParameter("type"));
    String type = request.getParameter("type");
    if (type.equals("delete")) {

    } else if (type.equals("edit")) {

    }

    Entity taskEntity = new Entity("task");
    taskEntity.setProperty("text", request.getParameter("task-text"));

    String dateString = request.getParameter("task-date") + " " + request.getParameter("task-time");
    Date calendarDate = new Date();
    try {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
      calendarDate = sdf.parse(dateString);
    } catch (Exception e) {
    }
    taskEntity.setProperty("dateTime", calendarDate);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(taskEntity);

    Task task = getTask(request, taskEntity.getKey().getId());
    tasks.add(task);

  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Gson gson = new Gson();
    System.out.println(tasks);
    response.getWriter().println(gson.toJson(tasks));
  }
}
