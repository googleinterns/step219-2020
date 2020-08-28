/* this is a part of Darya's implementation of a project, that we desided to delete and use Egor's implementation

But it would be great , if you would be able to review that code

*/








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
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.sps.src.Task;
import com.google.sps.src.TaskText;
import com.google.sps.src.Time;
import com.google.sps.src.Place;

@WebServlet("/load-task")
public class TaskMainServlet extends HttpServlet {
  
  ArrayList<Task> tasks;
  
  @Override
  public void init() {
    tasks = new ArrayList<Task>();
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
      Task task = new Task(new Time(date), new TaskText(text, comment), new Place(place), 0); //0 is fictional value
      tasks.add(task);
    }
    response.getWriter().println(gson.toJson(tasks));
  }
}