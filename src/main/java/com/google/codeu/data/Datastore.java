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

package com.google.codeu.data;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.FetchOptions;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** Provides access to the data stored in Datastore. */
public class Datastore {

  private DatastoreService datastore;

  public Datastore() {
    datastore = DatastoreServiceFactory.getDatastoreService();
  }

  /** Returns the total number of messages for all users. */
  public int getTotalMessageCount(){
    Query query = new Query("Message");
    PreparedQuery results = datastore.prepare(query);
    return results.countEntities(FetchOptions.Builder.withLimit(1000));
  }

  /** Stores the Message in Datastore. */
  public void storeMessage(Message message) {
    Entity messageEntity = new Entity("Message", message.getId().toString());
    messageEntity.setProperty("user", message.getUser());
    messageEntity.setProperty("text", message.getText());
    messageEntity.setProperty("timestamp", message.getTimestamp());
    messageEntity.setProperty("sentimentScore", message.getSentimentScore());

    datastore.put(messageEntity);
  }

  /**
   * Gets messages posted by a specific user.
   *
   * @param String user's name
   * @return a list of messages posted by the user, or empty list if user has never posted a
   *     message. List is sorted by time descending.
   */
  public List<Message> getMessages(String user) {
	  Query query = new Query("Message")
		            .setFilter(new Query.FilterPredicate("user", FilterOperator.EQUAL, user))
		            .addSort("timestamp", SortDirection.DESCENDING);
	  PreparedQuery results = datastore.prepare(query);
	  
      return fillMessageList(results);
  }

  /**
   * Gets all messages posted.
   *
   * @return a list of all messages posted, or empty list if there are no posted
   *     messages. List is sorted by time descending.
   */
  public List<Message> getAllMessages(){
	  Query query = new Query("Message")
			    .addSort("timestamp", SortDirection.DESCENDING);
	  PreparedQuery results = datastore.prepare(query);

	  return fillMessageList(results);
	  
	 }
  
	/**
	 * Private helper method to create encapsulation for getMessages() and
	 * getAllMessages(). Takes a given query that specifies sorting and filtering.
	 * 
	 * @param results a PreparedQuery that specifies sorting and filtering of the messages
	 * @return List of messages from the posted messages, filtered and sorted
	 */
	private List<Message> fillMessageList(PreparedQuery results) {
		List<Message> messages = new ArrayList<>();

		for (Entity entity : results.asIterable()) {
			try {
				String idString = entity.getKey().getName();
				UUID id = UUID.fromString(idString);
				String user = (String) entity.getProperty("user");
				String text = (String) entity.getProperty("text");
				long timestamp = (long) entity.getProperty("timestamp");
				float sentimentScore = entity.getProperty("sentimentScore") == null? (float) 0.0
						: ((Double) entity.getProperty("sentimentScore")).floatValue();
				Message message = new Message(id, user, text, timestamp, sentimentScore);
				messages.add(message);
			} catch (Exception e) {
				System.err.println("Error reading message.");
				System.err.println(entity.toString());
				e.printStackTrace();
			}
		}
		return messages;


  }


}


