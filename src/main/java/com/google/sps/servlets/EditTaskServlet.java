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
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.sps.src.Task;
import com.google.sps.src.TaskText;
import com.google.sps.src.Time;
import com.google.sps.src.Place;
import java.lang.Exception;

@WebServlet("/edit-task")
public class EditTaskServlet extends HttpServlet {
  ArrayList<Task> tasks;
  ArrayList<Key> keys;

  
  @Override
  public void init() {
    tasks = new ArrayList<Task>();
    keys = new ArrayList<Key>();
    Query query = new Query("task");
    PreparedQuery results = DatastoreServiceFactory.getDatastoreService().prepare(query);
    for (Entity entity : results.asIterable()) {
      String text = (String)entity.getProperty("text");
      String date = (String)entity.getProperty("date");
      String place = (String)entity.getProperty("place");
      String comment = (String)entity.getProperty("comment");
      Task task = new Task(new Time(date), new TaskText(text, comment), new Place(place));
      tasks.add(task);
      keys.add(entity.getKey());
    }
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    int id = Integer.parseInt(request.getParameter("id"));
    try {
    Entity taskEntity = datastore.get(keys.get(id));
    

    String title = request.getParameter("task-text");
    if (!title.equals("")) {
        taskEntity.setProperty("text",title);
    }
    String date = request.getParameter("task-date");
    if (!date.equals("")) {
        taskEntity.setProperty("date", date);
    }
    String place = request.getParameter("task-place");
    if (!place.equals("")) {
        taskEntity.setProperty("place", place);
    }
    String comment = request.getParameter("task-comment");
    if (!comment.equals("")) {
        taskEntity.setProperty("comment", comment);
    }
    datastore.put(taskEntity);
    }
     catch(Exception e)
    { return;
    }
    response.sendRedirect("/index.html");
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Gson gson = new Gson();
    Query query = new Query("task");
    PreparedQuery results = DatastoreServiceFactory.getDatastoreService().prepare(query);
    tasks.clear();
    for (Entity entity : results.asIterable()) {
      String text = (String)entity.getProperty("text");
      String date = (String)entity.getProperty("date");
      String place = (String)entity.getProperty("place");
      String comment = (String)entity.getProperty("comment");
      Task task = new Task(new Time(date), new TaskText(text, comment), new Place(place));
      tasks.add(task);
    }
    response.getWriter().println(gson.toJson(tasks));
  }
}