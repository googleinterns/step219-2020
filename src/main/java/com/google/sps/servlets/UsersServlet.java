package com.example.appengine.users;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

// With @WebServlet annotation the webapp/WEB-INF/web.xml is no longer required.
@WebServlet(
    name = "UserAPI",
    description = "UserAPI: Login / Logout with UserService",
    urlPatterns = "/userapi"
)
public class UsersServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();

    //String thisUrl = req.getRequestURI();
    String main_page = "https://8080-17f5303d-2dea-4c50-b733-2cb7b78be97f.europe-west4.cloudshell.dev/index.html";
    String login_page = "https://8080-17f5303d-2dea-4c50-b733-2cb7b78be97f.europe-west4.cloudshell.dev/main-page.html";
    Gson gson = new Gson();
    String user_id = "none";
    if (request.getUserPrincipal() != null) {
        user_id = userService.getCurrentUser().getUserId();
        doStoreUserInfo(user_id, userService.getCurrentUser().getEmail());
        System.out.println("user loged in");
        //System.out.println(userService.getCurrentUser().getName());
        System.out.println(userService.getCurrentUser().getUserId());
    } else {
        user_id = "<p>Please <a href=\"" + userService.createLoginURL(login_page) + "\">sign in</a>.</p>";
        System.out.println("user not loged in");
        //userService.createLoginURL(login_page);
    }
    response.getWriter().println(gson.toJson(user_id));
    
  }

  private void doStoreUserInfo(String user_id, String email) {
    Key key = KeyFactory.createKey("user", user_id);
    System.out.println("Key key = KeyFactory");
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    try {
      Entity entity = datastore.get(key);
      System.out.println("User " + user_id +"with email" + email+ " exists");
    } catch (Exception e) {
      Entity entity = new Entity("user", user_id);
      entity.setProperty("email", email);
      entity.setProperty("user_id", user_id);
      entity.setProperty("key_name", entity.getKey().getName());
      System.out.println("User " + user_id +"with email" + email+" NOT exists");
      System.out.println("User key " + entity.getKey().getName());
      datastore.put(entity);
    }
  }
}