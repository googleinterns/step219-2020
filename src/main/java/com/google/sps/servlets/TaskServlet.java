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

@WebServlet("/task")
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
      String comment = (String)entity.getProperty("comment")
      Task task = new Task(new Time(date), new TaskText(text, comment));
      messages.add(task);
    }
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    messages.add(new UserComment(getUserMessage(request), getUserEmail()));
    
    Entity commentEntity = new Entity("comment");
    commentEntity.setProperty("message", getUserMessage(request));
    commentEntity.setProperty("email", getUserEmail());
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);
    response.sendRedirect("/index.html");
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html;");
    response.getWriter().println(calendar.getTime());
  }
}