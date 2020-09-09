package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.gson.Gson;
import com.google.sps.src.Place;
import com.google.sps.src.Task;
import com.google.sps.src.Time;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/update-local-task-list")
public class LocalUpdateServlet extends HttpServlet {

  /**
   * An ArrayList which contains all task of the user. All views for tasks in the UI are connected
   * with this array
   */
  private ArrayList<Task> tasks;

  /**
   * Initialize list of tasks taken from Datastore
   */
  @Override
  public void init() {
    tasks = new ArrayList<>();

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    Query query = new Query("task");
    PreparedQuery results = datastore.prepare(query);
    for (Entity entity : results.asIterable()) {
      String text = (String) entity.getProperty("text");
      String date = (String) entity.getProperty("date");
      String place = (String) entity.getProperty("place");
      String comment = (String) entity.getProperty("comment");
      Task task =
          new Task(new Time(date), text, comment, new Place(place), entity.getKey().getId());
      tasks.add(task);
    }
  }

  private Entity buildTaskEntityFromRequest(HttpServletRequest request) {
    Entity taskEntity = new Entity("task");
    taskEntity.setProperty("text", request.getParameter("task-date"));
    taskEntity.setProperty("date", request.getParameter("task-text"));
    taskEntity.setProperty("comment", request.getParameter("task-comment"));
    taskEntity.setProperty("place", request.getParameter("task-place"));
    return taskEntity;
  }

  /** Adding task to a Datastore */
  private void doAddTask(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    try {
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      Entity taskEntity = buildTaskEntityFromRequest(request);
      datastore.put(taskEntity);

      Task task =
          new Task(
              new Time(request.getParameter("task-time")),
              request.getParameter("task-text"),
              request.getParameter("task-comment"),
              new Place(request.getParameter("task-place")),
              taskEntity.getKey().getId());
      tasks.add(task);
      System.out.println("The id of the task is " + taskEntity.getKey().getId());
      response.getWriter().println(new Gson().toJson(task));
    } catch (Exception e) {
      System.out.println("LOG: error " + e);
      response.sendError(HttpServletResponse.SC_BAD_REQUEST);
    }
  }

  /**
   * Edit one field of specified task
   *
   * @param request contains id, field which is need to be changed and new data for this field
   */
  private void doEditTask(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    try {
      String fieldName = request.getParameter("field");
      long number = Long.parseLong(request.getParameter("number"));
      String newFieldData = request.getParameter("new_data");
      for (Task task : tasks) {
        if (task.getDatastoreId() == number) {
          task.setField(fieldName, newFieldData);
          break;
        }
      }
    } catch (Exception e) {
      System.out.println("LOG: error! " + e);
      response.sendError(HttpServletResponse.SC_BAD_REQUEST);
    }
  }

  /** Delete task from Datastore */
  private void doDeleteTask(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    try {
      long number = Long.parseLong(request.getParameter("number"));
      tasks.removeIf(element -> (element.getDatastoreId() == number));
    } catch (Throwable e) {
      System.out.println("LOG: error " + e);
      response.sendError(HttpServletResponse.SC_BAD_REQUEST);
    }
  }

  /**
   * Parsing given request. This function works with updating tasks it calls function which add,
   * delete or edit them.
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    System.out.println("LOG: doPost() function");
    System.out.println("LOG: request type=" + request.getParameter("type"));

    String type = request.getParameter("type");
    if (type.equals("add")) {
      doAddTask(request, response);
    } else if (type.equals("delete")) {
      doDeleteTask(request, response);
    } else if (type.equals("edit")) {
      doEditTask(request, response);
    } else if (type.equals("change")) {
      doChangeTask(request, response);
    } else {
      System.out.println("There is no needed type of request");
      response.sendError(HttpServletResponse.SC_BAD_REQUEST);
    }
  }

  /**
   * Changing all fields of the task
   */
  private void doChangeTask(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    try {
      long number = Long.parseLong(request.getParameter("number"));
      for (Task task : tasks) {
        if (task.getDatastoreId() == number) {
          task.setComment(request.getParameter("comment"));
          task.setPlace(new Place(request.getParameter("place")));
          task.setTime(new Time(request.getParameter("time")));
          task.setTitle(request.getParameter("title"));
          return;
        }
      }
    } catch (Exception e) {
      System.out.println("LOG: error " + e);
      response.sendError(HttpServletResponse.SC_BAD_REQUEST);
    }
  }

  /**
   * Send all user tasks to javascript in json format
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Gson gson = new Gson();
    response.getWriter().println(gson.toJson(tasks));
  }
}
