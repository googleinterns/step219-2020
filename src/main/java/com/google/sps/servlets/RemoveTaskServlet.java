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
import java.util.HashMap; 
import java.util.Scanner;

@WebServlet("/remove-task")
public class RemoveTaskServlet extends HttpServlet {
 
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    long number = Long.parseLong(request.getParameter("number"));
    Key key = KeyFactory.createKey("task", number);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    System.out.println("LOG: doPost /remove-task request");
    System.out.println("LOG: number of the item is " + number);
    System.out.println("LOG: type of the request is " + request.getParameter("type"));

    if (request.getParameter("type").equals("edit")) {
      try {
        Entity entity = datastore.get(key);
        HashMap<String, String> viewToProperty = new HashMap();
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
      response.sendRedirect("/index.html");
    } else {
      datastore.delete(key);
      response.sendRedirect("/index.html");
    }
  }
}