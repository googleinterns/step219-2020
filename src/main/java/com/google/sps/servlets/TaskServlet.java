package com.google.sps.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import com.google.gson.Gson;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.sps.src.Task;
import com.google.sps.src.TaskText;
import com.google.sps.src.Time;

@WebServlet("/send-task")
public class TaskServlet extends HttpServlet {
  
  ArrayList<Task> tasks;
  
  @Override
  public void init() {
    tasks = new ArrayList<Task>();
    Query query = new Query("task");
    PreparedQuery results = DatastoreServiceFactory.getDatastoreService().prepare(query);
    for (Entity entity : results.asIterable()) {
      String text = (String)entity.getProperty("text");
      String date = (String)entity.getProperty("date");
      String comment = (String)entity.getProperty("comment");
      Task task = new Task(new Time(date), new TaskText(text, comment));
      tasks.add(task);
    }
  }

  private Task getTask(HttpServletRequest request) {
      return new Task(new Time(request.getParameter("task-date")), 
                          new TaskText(request.getParameter("task-text"), request.getParameter("task-comment")));
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Task task = getTask(request);
    tasks.add(task);
    Entity taskEntity = new Entity("task");
    taskEntity.setProperty("text", task.getTaskText().getTitle());
    taskEntity.setProperty("date", task.getTime().getDate());
    taskEntity.setProperty("comment", task.getTaskText().getComment());
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(taskEntity);
    response.sendRedirect("/index.html");
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Gson gson = new Gson();
    response.getWriter().println(gson.toJson(tasks));
  }
}