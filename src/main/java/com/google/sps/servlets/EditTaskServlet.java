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
  ArrayList<Key> keys;

  
  @Override
  public void init() {
    keys = new ArrayList<Key>();
    Query query = new Query("task");
    PreparedQuery results = DatastoreServiceFactory.getDatastoreService().prepare(query);
    for (Entity entity : results.asIterable()) {
      keys.add(entity.getKey());
    }
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    int id = Integer.parseInt(request.getParameter("id"));
    System.out.println(id);
    String title = request.getParameter("task-text");
    System.out.println(title);
    String date = request.getParameter("task-date");
    System.out.println(date);
    String place = request.getParameter("task-place");
    System.out.println(place);
    String comment = request.getParameter("task-comment");
    System.out.println(comment);
    try {
    Entity taskEntity = datastore.get(keys.get(id));

    if (!title.equals("")) {
        taskEntity.setProperty("text",title);
    }
    if (!date.equals("")) {
        taskEntity.setProperty("date", date);
    }
    if (!place.equals("")) {
        taskEntity.setProperty("place", place);
    }
    if (!comment.equals("")) {
        taskEntity.setProperty("comment", comment);
    }
    datastore.put(taskEntity);

    System.out.println("data edited");
    }
     catch(Exception e)
    { 
        System.out.println("exeption");
        return;
    }
    response.sendRedirect("/index.html");
  }
}