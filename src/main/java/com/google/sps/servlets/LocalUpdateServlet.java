package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

@WebServlet("/update-local-task-list")
public class LocalUpdateServlet extends HttpServlet {

  /**
   * An ArrayList which contains all task of the user. All views for tasks in the UI are connected
   * with this array
   */
  private ArrayList<Task> tasks;

  /** Initialize list of tasks taken from Datastore */
  @Override
  public void init() {
    tasks = new ArrayList<>();

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    Query query = new Query("task");
    query.addSort("dateTime", SortDirection.ASCENDING);
    PreparedQuery results = datastore.prepare(query);
    for (Entity entity : results.asIterable()) {
      String text = (String) entity.getProperty("text");
      Date dateTime = (Date) entity.getProperty("dateTime");
      String place = (String) entity.getProperty("place");
      String comment = (String) entity.getProperty("comment");
      String state = (String) entity.getProperty("isDone");

      Task task =
          new Task(
              new DateTime(dateTime), text, comment, new Place(place), entity.getKey().getId(),
              Boolean.valueOf(state));
      System.out.println(task);
      tasks.add(task);
    }
  }

  private Entity buildTaskEntityFromRequest(HttpServletRequest request) {
    Entity taskEntity = new Entity("task");
    taskEntity.setProperty("text", request.getParameter("task-date"));
    taskEntity.setProperty("date", request.getParameter("task-text"));
    taskEntity.setProperty("comment", request.getParameter("task-comment"));
    taskEntity.setProperty("place", request.getParameter("task-place"));
    String dateString = request.getParameter("task-date") + " " + request.getParameter("task-time");

    Date calendarDate = new Date();
    try {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
      calendarDate = sdf.parse(dateString);
    } catch (Exception e) {
      System.out.println("Parsing date goes wrong" + e);
    }
    taskEntity.setProperty("dateTime", calendarDate);

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
              new DateTime(
                  request.getParameter("task-date") + " " + request.getParameter("task-time")),
              request.getParameter("task-text"),
              request.getParameter("task-comment"),
              new Place(request.getParameter("task-place")),
              taskEntity.getKey().getId(), false);

      tasks.add(task);
      System.out.println("The id of the task is " + taskEntity.getKey().getId());
      response.getWriter().println(new GsonBuilder()
          .setDateFormat("yyyy-MM-dd HH:mm").create().toJson(task));
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
          task.setTime(request.getParameter("time"));
          task.setTitle(request.getParameter("title"));
          task.setDate(request.getParameter("date"));
          task.setIsDone(request.getParameter("isDone"));

          Place place =
              Boolean.parseBoolean(request.getParameter("are_coordinates_changed"))
                  ? new Place(
                  Double.parseDouble(request.getParameter("lat")),
                  Double.parseDouble(request.getParameter("lng")),
                  request.getParameter("place"))
                  : new Place(request.getParameter("place"));
          task.setPlace(place);
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
    Gson gson = new GsonBuilder()
        .setDateFormat("yyyy-MM-dd HH:mm").create();
    response.getWriter().println(gson.toJson(tasks));
  }
}
