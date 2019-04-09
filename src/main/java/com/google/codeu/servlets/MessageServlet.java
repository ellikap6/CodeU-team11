/*
 * Copyright 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.codeu.servlets;

import java.util.logging.Logger;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ServingUrlOptions;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.codeu.data.Datastore;
import com.google.codeu.data.Message;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.Translate.TranslateOption;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.Document.Type;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;
import java.io.IOException;
import java.lang.Exception;



/** Handles fetching and saving {@link Message} instances. */
@WebServlet("/messages")
public class MessageServlet extends HttpServlet {

  private static final Logger log = Logger.getLogger(MessageServlet.class.getName());
  private Datastore datastore;


  private float getSentimentScore(String text) throws IOException {
    Document doc = Document.newBuilder()
            .setContent(text).setType(Type.PLAIN_TEXT).build();

    LanguageServiceClient languageService = LanguageServiceClient.create();
    Sentiment sentiment = languageService.analyzeSentiment(doc).getDocumentSentiment();
    languageService.close();

    return sentiment.getScore();
  }

  private void translateMessages(List<Message> messages, String targetLanguageCode) {
	  Translate translate = TranslateOptions.getDefaultInstance().getService();

	  for(Message message : messages) {
	    String originalText = message.getText();

	    Translation translation =
	        translate.translate(originalText, TranslateOption.targetLanguage(targetLanguageCode));
	    String translatedText = translation.getTranslatedText();

	    message.setText(translatedText);
	  }
	}



  @Override
  public void init() {
    datastore = new Datastore();
  }

  /**
   * Responds with a JSON representation of {@link Message} data for a specific user. Responds with
   * an empty array if the user is not provided.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    response.setContentType("application/json");

    String user = request.getParameter("user");

  if (user == null || user.equals("")) {
      // Request is invalid, return empty array
      response.getWriter().println("[]");
      return;
    }

    List<Message> messages = datastore.getMessages(user);

    String targetLanguageCode = request.getParameter("language");

    if(targetLanguageCode != null) {
      translateMessages(messages, targetLanguageCode);
    }
    Gson gson = new Gson();
    String json = gson.toJson(messages);

    response.getWriter().println(json);
  }

  /** Stores a new {@link Message}. */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    UserService userService = UserServiceFactory.getUserService();
    if (!userService.isUserLoggedIn()) {
      response.sendRedirect("/index.html");
      return;
    }

    String user = userService.getCurrentUser().getEmail();

    // basicWithImages allows a, b, blockquote, br, cite, code, dd, dl, dt, em, i, li, ol,
    // p, pre, q, small, span, strike, strong, sub, sup, u, ul, and image tags
    String text  = Jsoup.clean(request.getParameter("text"), Whitelist.basicWithImages());
    String recipient = request.getParameter("recipient");

    float sentimentScore = getSentimentScore(text);

    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
    List<BlobKey> blobKeys = blobs.get("image");

    Message message = new Message(user, text, sentimentScore, recipient);

    if(blobKeys != null && !blobKeys.isEmpty()) {
      log.info("Block Keys size = " + blobKeys.size());
      try {
        BlobKey blobKey = blobKeys.get(0);
        ImagesService imagesService = ImagesServiceFactory.getImagesService();
        ServingUrlOptions options = ServingUrlOptions.Builder.withBlobKey(blobKey);
        String imageUrl = imagesService.getServingUrl(options);
        message.setImageUrl(imageUrl);
      } catch (Exception e) {
        System.out.println("Error we will have to solve eventually.");
      }
    }

    datastore.storeMessage(message);

    response.sendRedirect("/user-page.html?user=" + recipient);
  }
}
