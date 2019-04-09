package com.google.codeu.servlets;

import java.io.IOException;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.codeu.data.Datastore;
import com.google.codeu.data.Message;
import com.google.gson.Gson;

@WebServlet("/messagechart")
public class ChartServlet extends HttpServlet{

 private Datastore datastore;

 @Override
 public void init() {
  datastore = new Datastore();
 }

 @Override
 public void doGet(HttpServletRequest request, HttpServletResponse response)
   throws IOException {
     response.setContentType("application/json");
     // The following line should match however you manipulated getMessages() in Step 1
     List<Message> msgList = datastore.getAllMessages();
     Gson gson = new Gson();
     String json = gson.toJson(msgList);
     // System.out.println(json);
     response.getWriter().println(json);
 }
}
