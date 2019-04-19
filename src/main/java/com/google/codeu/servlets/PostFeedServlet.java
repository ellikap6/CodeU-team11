package com.google.codeu.servlets;

import java.io.IOException;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.codeu.data.Datastore;

@WebServlet("/post-feed")
public class PostFeedServlet extends HttpServlet {

 private Datastore datastore;

 @Override
 public void init() {
      datastore = new Datastore();
 }

 @Override
 public void doGet (HttpServletRequest request, HttpServletResponse response)
      throws IOException {
       System.out.println("Post feed doGet is called!");

 }

 @Override
 public void doPost (HttpServletRequest request, HttpServletResponse response) 
      throws IOException {

  }

}