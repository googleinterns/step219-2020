package com.google.sps.src;

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
import java.util.ArrayList;
import java.io.PrintWriter;
import java.security.Principal;

// With @WebServlet annotation the webapp/WEB-INF/web.xml is no longer required.
public class Users {
  private String user_id;
  private String sign_button;

  public Users() {
    user_id = "none";
    sign_button = "";
  }

  public void checkLogInOfUser(Principal userPrincipal) {
    UserService userService = UserServiceFactory.getUserService();

    String login_page = "/main-page.html";

    if (userPrincipal != null) {
        user_id = userService.getCurrentUser().getUserId();
        doStoreUserInfo(user_id, userService.getCurrentUser().getEmail());
        sign_button = "<a href=\""
                  + userService.createLogoutURL(login_page)
                  + "\">sign out</a>.</p>";
        System.out.println("user loged in");
        System.out.println(userService.getCurrentUser().getUserId());
    } else {
        user_id = "none";
        sign_button = "<p>Please <a href=\"" + userService.createLoginURL(login_page) + "\">sign in</a>.</p>";
        System.out.println("user not loged in");
    }
  }

  public ArrayList<String> getUserIdAndButton(Principal userPrincipal){
    if (user_id == "none") {
      checkLogInOfUser(userPrincipal);
    }
    ArrayList<String> arrayResponse = new ArrayList<String>(2);
    arrayResponse.add(user_id);
    arrayResponse.add(sign_button);
    return arrayResponse;
  }

  public String getUserId(Principal userPrincipal){
    if (user_id == "none") {
      checkLogInOfUser(userPrincipal);
    }
    System.out.println("LOG: Returned user id from Users.java"+user_id);
    return user_id;
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