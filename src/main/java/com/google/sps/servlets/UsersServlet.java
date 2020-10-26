package com.google.sps.servlets;

import com.google.gson.Gson;
import com.google.sps.src.Users;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(
    name = "UserAPI",
    description = "UserAPI: Login / Logout with UserService",
    urlPatterns = "/userapi")
public class UsersServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Gson gson = new Gson();
    PrintWriter out = response.getWriter();
    Users users = new Users();
    String user_id = users.getUserId(request.getUserPrincipal());
    ArrayList<String> userIdAndButton = users.getUserIdAndButton(request.getUserPrincipal());
    ArrayList<String> JSONRequest = new ArrayList<String>(2);
    JSONRequest.add(gson.toJson(userIdAndButton.get(0)));
    JSONRequest.add(gson.toJson(userIdAndButton.get(1)));
    out.println(JSONRequest);
    // response.getWriter().println(new Gson().toJson(user_id));
  }
}
