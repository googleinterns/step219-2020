package com.google.sps.src;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.security.Principal;
import java.util.ArrayList;

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
    } else {
        user_id = "none";
      sign_button =
          "<p>Please <a href=\"" + userService.createLoginURL(login_page) + "\">sign in</a>.</p>";
      System.out.println("user not logged in");
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
    return user_id;
  }

  private void doStoreUserInfo(String user_id, String email) {
    Key key = KeyFactory.createKey("user", user_id);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    try {
      Entity entity = datastore.get(key);
    } catch (Exception e) {
      Entity entity = new Entity("user", user_id);
      entity.setProperty("email", email);
      entity.setProperty("user_id", user_id);
      entity.setProperty("key_name", entity.getKey().getName());
      datastore.put(entity);
    }
  }
}