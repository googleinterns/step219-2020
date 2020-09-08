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
import com.google.sps.src.DateTime;
import com.google.sps.src.Place;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Key;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;    

@WebServlet("/send-task")
public class TaskServlet extends HttpServlet {
  
  private ArrayList<Task> tasks;

  @Override
  public void init() {
    tasks = new ArrayList<Task>();

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    
    Query query = new Query("task");
    PreparedQuery results = datastore.prepare(query);
    for (Entity entity : results.asIterable()) {
      String text = (String)entity.getProperty("text");
      String dateTime = (String)entity.getProperty("dateTime");
      String place = (String)entity.getProperty("place");
      String comment = (String)entity.getProperty("comment");

      Task task = new Task(new DateTime(dateTime),
        new TaskText(text, comment), 
        new Place(place), 
        entity.getKey().getId());
        System.out.println(task);
        tasks.add(task);
    }
  }

  private Task getTask(HttpServletRequest request, long id) {
      return new Task(new DateTime(request.getParameter("task-date")+" "+request.getParameter("task-time")),
                        new TaskText(request.getParameter("task-text"),request.getParameter("task-comment")),
                        new Place(request.getParameter("task-place")),
                        id);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    System.out.println(request.getParameter("type"));
    String type = request.getParameter("type");
    if (type.equals("notify")) {
      long number = Long.parseLong(request.getParameter("number"));
      tasks.removeIf(element -> (element.getNumber() == number));
      response.sendRedirect("/index.html");
      return;
    } else if (type.equals("edit")) {
      String fieldName = request.getParameter("field");
      long number = Long.parseLong(request.getParameter("number"));
      String newFieldData = request.getParameter("new_data");
      for (Task task : tasks) {
        if (task.getNumber() == number) {
          if (fieldName.equals("task_placeData")) {
            task.setPlace(new Place(newFieldData));
          } else if (fieldName.equals("task_timeData")) {
            task.setTime(newFieldData);
          } else if (fieldName.equals("task_dateData")) {
            task.setDate(newFieldData);
          } else if (fieldName.equals("task_titleData")) {
            task.getTaskText().setTitle(newFieldData);
          }
          else if (fieldName.equals("task_commentData")) {
            task.getTaskText().setComment(newFieldData);
          }
          break;
        }
      }
      response.sendRedirect("/index.html");
      return;
    }

    Entity taskEntity = new Entity("task");
    taskEntity.setProperty("text", request.getParameter("task-text"));
    String dateString = request.getParameter("task-date")+" "+request.getParameter("task-time");
    taskEntity.setProperty("dateTime", dateString);
    taskEntity.setProperty("comment", request.getParameter("task-comment"));
    taskEntity.setProperty("place", request.getParameter("task-place"));
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(taskEntity);

    Task task = getTask(request, taskEntity.getKey().getId());
    tasks.add(task);
    
    System.out.println("The id of the task is " + taskEntity.getKey().getId());
    response.sendRedirect("/index.html");
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Gson gson = new Gson();
    System.out.println(tasks);
    response.getWriter().println(gson.toJson(tasks));
  }
}