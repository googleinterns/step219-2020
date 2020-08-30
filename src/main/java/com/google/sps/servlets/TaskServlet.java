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
import com.google.sps.src.Place;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Key;

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
      String date = (String)entity.getProperty("date");
      String place = (String)entity.getProperty("place");
      String comment = (String)entity.getProperty("comment");
      Task task = new Task(new Time(date), 
        new TaskText(text, comment), 
        new Place(place), 
        entity.getKey().getId());
        tasks.add(task);
    }
  }

  private Task getTask(HttpServletRequest request, long id) {
      return new Task(new Time(request.getParameter("task-date")), 
                          new TaskText(request.getParameter("task-text"),
                          request.getParameter("task-comment")),
                          new Place(request.getParameter("task-place")),
                          id);
  }

  private void doAddTask(HttpServletRequest request, HttpServletResponse response) {
    Entity taskEntity = new Entity("task");
    taskEntity.setProperty("text", request.getParameter("task-date"));
    taskEntity.setProperty("date", request.getParameter("task-text"));
    taskEntity.setProperty("comment", request.getParameter("task-comment"));
    taskEntity.setProperty("place", request.getParameter("task-place"));
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(taskEntity);

    Task task = getTask(request, taskEntity.getKey().getId());
    tasks.add(task);

    System.out.println("The id of the task is " + taskEntity.getKey().getId());
  }

  private void doEditTask(HttpServletRequest request, HttpServletResponse response) {
    String fieldName = request.getParameter("field");
    long number = Long.parseLong(request.getParameter("number"));
    String newFieldData = request.getParameter("new_data");
    for (Task task : tasks) {
      if (task.getNumber() == number) {
        if (fieldName.equals("task_placeData")) {
          task.setPlace(new Place(newFieldData));
        } else if (fieldName.equals("task_timeData")) {
          task.setTime(new Time(newFieldData));
        }
        else if (fieldName.equals("task_titleData")) {
          task.getTaskText().setTitle(newFieldData);
        }
        else if (fieldName.equals("task_commentData")) {
          task.getTaskText().setComment(newFieldData);
        }
        break;
      }
    }
  }

  private void doNotifyTask(HttpServletRequest request, HttpServletResponse response) {
    long number = Long.parseLong(request.getParameter("number"));
    tasks.removeIf(element -> (element.getNumber() == number));
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    System.out.println(request.getParameter("type"));
    String type = request.getParameter("type");
    if (type.equals("add")) {
      doAddTask(request, response);
    } else if (type.equals("notify")) {
      doNotifyTask(request, response);
    } else if (type.equals("edit")) {
      doEditTask(request, response);
    }
    response.sendRedirect("/index.html");
  }


  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Gson gson = new Gson();
    response.getWriter().println(gson.toJson(tasks));
  }
}