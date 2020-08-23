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

@WebServlet("/remove-task")
public class TaskServlet extends HttpServlet {
 
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    long number = Long.parseLong(request.getParameter("number"));
    Key key = KeyFactory.createKey()
    response.sendRedirect("/index.html");
  }
}