package com.google.sps.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Key;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/remove-task")
public class RemoveTaskServlet extends HttpServlet {

  private void doEditTask(HttpServletRequest request, HttpServletResponse response, long number) {
    Key key = KeyFactory.createKey("task", number);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    try {
      Entity entity = datastore.get(key);
      Map<String, String> viewToProperty = new HashMap<>();
      viewToProperty.put("task_titleData", "text");
      viewToProperty.put("task_timeData", "time");
      viewToProperty.put("task_placeData", "place");
      viewToProperty.put("task_commentData", "comment");

      String newFieldData = request.getParameter("new_data");
      entity.setProperty(viewToProperty.get(request.getParameter("field")), newFieldData);
      datastore.put(entity);
    } catch (Throwable e) {
      System.out.println("Key doesn't exists");
    }
  }

  private void doDeleteTask(HttpServletRequest request, HttpServletResponse response, long number) {
    Key key = KeyFactory.createKey("task", number);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.delete(key);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    long number = Long.parseLong(request.getParameter("number"));

    System.out.println("LOG: doPost /remove-task request");
    System.out.println("LOG: number of the item is " + number);
    System.out.println("LOG: type of the request is " + request.getParameter("type"));

    if (request.getParameter("type").equals("edit")) {
      doEditTask(request, response, number);
    } else if (request.getParameter("type").equals("delete")) {
      doDeleteTask(request, response, number);
    }
    response.sendRedirect("/index.html");
  }
}