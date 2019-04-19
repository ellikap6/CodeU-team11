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
    messageEntity.setProperty("postID", message.getRecipient());
    messageEntity.setProperty("sentimentScore", message.getSentimentScore());
    
    if(message.getImageUrl() != null) {
    	  messageEntity.setProperty("imageUrl", message.getImageUrl());
    	}
    
    datastore.put(messageEntity);
  }

  /**
   * Gets messages posted by a specific user.
   *
   * @param String user's name
   * @return a list of messages posted by the user, or empty list if user has never posted a
   *     message. List is sorted by time descending.
   */
  public List<Message> getMessages(String postID) {
	  Query query = new Query("Message")
		            .setFilter(new Query.FilterPredicate("postID", FilterOperator.EQUAL, postID))
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
                String postID = (String) entity.getProperty("postID");
				long timestamp = (long) entity.getProperty("timestamp");
				float sentimentScore = entity.getProperty("sentimentScore") == null? (float) 0.0
						: ((Double) entity.getProperty("sentimentScore")).floatValue();


				String imageUrl = (String) entity.getProperty("imageUrl");
				Message message = new Message(id, user, text, timestamp, sentimentScore,postID, imageUrl);

				messages.add(message);
			} catch (Exception e) {
				System.err.println("Error reading message.");
				System.err.println(entity.toString());
				e.printStackTrace();
			}
		}
		return messages;
  }

	/** Stores the User in Datastore. */
	 public void storeUser(User user) {
	  Entity userEntity = new Entity("User", user.getEmail());
	  userEntity.setProperty("email", user.getEmail());
	  userEntity.setProperty("aboutMe", user.getAboutMe());
	  datastore.put(userEntity);
	 }
	 
	 /**
	  * Returns the User owned by the email address, or
	  * null if no matching User was found.
	  */
	 public User getUser(String email) {
	 
	  Query query = new Query("User")
	    .setFilter(new Query.FilterPredicate("email", FilterOperator.EQUAL, email));
	  PreparedQuery results = datastore.prepare(query);
	  Entity userEntity = results.asSingleEntity();
	  if(userEntity == null) {
	   return null;
	  }
	  
	  String aboutMe = (String) userEntity.getProperty("aboutMe");
	  User user = new User(email, aboutMe);
	  
	  return user;
	 }
   
  
	public List<UserMarker> getMarkers() {
		List<UserMarker> markers = new ArrayList<>();

		Query query = new Query("UserMarker");
		PreparedQuery results = datastore.prepare(query);

		for (Entity entity : results.asIterable()) {
			try {
				double lat = (double) entity.getProperty("lat");
				double lng = (double) entity.getProperty("lng");
				String content = (String) entity.getProperty("content");

				UserMarker marker = new UserMarker(lat, lng, content);
				markers.add(marker);
			} catch (Exception e) {
				System.err.println("Error reading marker.");
				System.err.println(entity.toString());
				e.printStackTrace();
			}
		}
		return markers;
	}

	public void storeMarker(UserMarker marker) {
		Entity markerEntity = new Entity("UserMarker");
		markerEntity.setProperty("lat", marker.getLat());
		markerEntity.setProperty("lng", marker.getLng());
		markerEntity.setProperty("content", marker.getContent());
		datastore.put(markerEntity);
	}

	public List<Post> getAllPosts() {
		Query query = new Query("Post").addSort("timestamp", SortDirection.DESCENDING);
		PreparedQuery results = datastore.prepare(query);

		List<Post> posts = new ArrayList<>();

		for (Entity entity : results.asIterable()) {
			try {
				posts.add(buildPost(entity, false));
			} catch (Exception e) {
				System.err.println("Error reading message.");
				System.err.println(entity.toString());
				e.printStackTrace();
			}
		}
		return posts;
	}

	private Post buildPost(Entity entity, boolean isFullPost) {
		String idString = entity.getKey().getName();
		UUID postID = UUID.fromString(idString);
		String creator = (String) entity.getProperty("creator");
		String title = (String) entity.getProperty("title");
	    String content = (String) entity.getProperty("content");
		String coverImageUrl = (String) entity.getProperty("coverImageUrl");
		long timestamp = (long) entity.getProperty("timestamp");


		// When we are on index.html and need to show all posts we do not want to
		// get all this extra information yet because we do not use it on that page.
		// However when we are on post.html we want to get all the information.
		//
		// getAllPosts() uses isFullPost = false
		// getPost(postID) uses isFullPost = true
		if (isFullPost) {
			LocationMarker marker = getLocationMarker(postID);
			List<ChartDataRow> chartDataRows = getChartDataRows(postID);
			List<Message> messages = getMessages(postID.toString());

			return new Post(postID, creator, timestamp, coverImageUrl,title, content, marker, chartDataRows, messages);
		}

		return new Post(postID, creator, timestamp, coverImageUrl, title, content);
	}

	private List<ChartDataRow> getChartDataRows(UUID postID) {
		Query query = new Query("ChartData")
				.setFilter(new Query.FilterPredicate("postID", FilterOperator.EQUAL, postID.toString()));
		PreparedQuery results = datastore.prepare(query);

		List<ChartDataRow> chartDataRows = new ArrayList<>();
		for (Entity entity : results.asIterable()) {
			try {
				long date = (long) entity.getProperty("date");
				int value = (int) entity.getProperty("value");

				ChartDataRow chartDataRow = new ChartDataRow(postID, date, value);
				chartDataRows.add(chartDataRow);
			} catch (Exception e) {
				System.err.println("Error reading message.");
				System.err.println(entity.toString());
				e.printStackTrace();
			}
		}
		return chartDataRows;
	}

	private LocationMarker getLocationMarker(UUID postID) {
		Query query = new Query("LocationMarker")
				.setFilter(new Query.FilterPredicate("postID", FilterOperator.EQUAL, postID.toString()));
		PreparedQuery results = datastore.prepare(query);

		Entity entity = results.asSingleEntity();
		if (entity == null) {
			return null;
		}

		double lat = (double) entity.getProperty("lat");
		double lng = (double) entity.getProperty("lng");
		String content = (String) entity.getProperty("content");
		LocationMarker marker = new LocationMarker(postID, lat, lng, content);
		return marker;

	}

	private void storeLocationMarker(LocationMarker marker) {
		Entity markerEntity = new Entity("LocationMarker");
		markerEntity.setProperty("lat", marker.getLat());
		markerEntity.setProperty("lng", marker.getLng());
		markerEntity.setProperty("content", marker.getContent());
		markerEntity.setProperty("postID", marker.getPostID().toString());
		datastore.put(markerEntity);
	}

	private void storeChartDataRow(ChartDataRow chartDataRow) {
		Entity chartDataRowEntity = new Entity("ChartData");
		chartDataRowEntity.setProperty("date", chartDataRow.getDate());
		chartDataRowEntity.setProperty("value", chartDataRow.getValue());
		chartDataRowEntity.setProperty("postID", chartDataRow.getPostID().toString());
		datastore.put(chartDataRowEntity);
	}

	public void storePost(Post post) {
		Entity postEntity = new Entity("ChartData", post.getPostID().toString());
		postEntity.setProperty("creator", post.getCreator());
		postEntity.setProperty("timestamp", post.getTimestamp());
		postEntity.setProperty("coverImageUrl", post.getCoverInamgeUrl());
		// postEntity.setProperty("title", post.getTitle());
		// postEntity.setProperty("content", post.getContent());
		for (ChartDataRow row : post.getChartData()) {
			storeChartDataRow(row);
		}
		storeLocationMarker(post.getMarker());

		// Messages are not stored up front. They will be stored on the post page
		// with storeMessage() and they will have to postID to link back to this
		// post.
		datastore.put(postEntity);

	}
 

}
