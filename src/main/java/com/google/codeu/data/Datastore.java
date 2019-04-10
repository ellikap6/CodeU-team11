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

  public List<Post> getAllPosts() {
    Query query = new Query("Post")
			    .addSort("timestamp", SortDirection.DESCENDING);
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

  public Post getPost(UUID postID) {
    Query query = new Query("Post")
                  .setFilter(new Query.FilterPredicate(
                                  "postID",
                                  FilterOperator.EQUAL,
                                  postID.toString()));
    PreparedQuery results = datastore.prepare(query);

    Entity entity = results.asSingleEntity();
	  if(entity == null) {
	   return null;
	  }

    return buildPost(entity, true);
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
      List<Message> messages = getMessages(postID);

      return new Post(
        postID,
        creator,
        timestamp,
        coverImageUrl,
        title,
        content,
        marker,
        chartData,
        messages);
    }

    return new Post(
      postID,
      creator,
      timestamp,
      coverImageUrl,
      title,
      content);
  }

  public void storePost(Post post) {
    Entity postEntity = new Entity("ChartData", post.getId().toString());
    postEntity.setProperty("creator", post.getCreator());
    postEntity.setProperty("timestamp", post.getTimestamp());
    postEntity.setProperty("coverImageUrl", post.getCoverImageUrl());
    postEntity.setProperty("title", post.getTitle());
    postEntity.setProperty("content", post.getContent());
    for (ChartDataRow row : post.getChartDataRows()) {
      storeChartDataRow(row);
    }
    storeLocationMarker(post.getLocationMarker());

    // Messages are not stored up front. They will be stored on the post page
    // with storeMessage() and they will have to postID to link back to this
    // post.
    datastore.put(postEntity);
  }

  private List<ChartDataRow> getChartDataRows(UUID postID) {
    Query query = new Query("ChartData")
                  .setFilter(new Query.FilterPredicate(
                                  "postID",
                                  FilterOperator.EQUAL,
                                  postID.toString()));
    PreparedQuery results = datastore.prepare(query);

    List<ChartDataRow> chartDataRows = new ArrayList<>();
    for (Entity entity : results.asIterable()) {
			try {
        UUID postID = UUID.fromString((String) entity.getProperty("postID"));
        long date = (long) entity.getProperty("date");
        int value = (int) entity.getProperty("value");

				ChartDataRow chartDataRow = new ChartDataRow(date, value, postID);
				chartDataRows.add(chartDataRow);
			} catch (Exception e) {
				System.err.println("Error reading message.");
				System.err.println(entity.toString());
				e.printStackTrace();
			}
		}
    return chartDataRows;
  }

  private void storeChartDataRow(ChartDataRow chartDataRow) {
    Entity chartDataRowEntity = new Entity("ChartData");
    chartDataRowEntity.setProperty("date", chartDataRow.getDate());
    chartDataRowEntity.setProperty("value", chartDataRow.getValue());
    chartDataRowEntity.setProperty("postID", chartDataRow.getPostID().toString());
    datastore.put(chartDataRowEntity);
  }

  private List<Message> getMessages(UUID postID) {
	  Query query = new Query("Message")
		            .setFilter(new Query.FilterPredicate("postID", FilterOperator.EQUAL, postID.toString()))
		            .addSort("timestamp", SortDirection.DESCENDING);
	  PreparedQuery results = datastore.prepare(query);

    return fillMessageList(results);
  }

  private List<Message> fillMessageList(PreparedQuery results) {
		List<Message> messages = new ArrayList<>();

		for (Entity entity : results.asIterable()) {
			try {
				String idString = entity.getKey().getName();
				UUID id = UUID.fromString(idString);
				String user = (String) entity.getProperty("user");
				String text = (String) entity.getProperty("text");
        UUID postID = UUID.fromString((String) entity.getProperty("postID"));
				long timestamp = (long) entity.getProperty("timestamp");
				float sentimentScore = entity.getProperty("sentimentScore") == null? (float) 0.0
						: ((Double) entity.getProperty("sentimentScore")).floatValue();

        String imageUrl = (String) entity.getProperty("imageUrl");
				Message message = new Message(id, user, text, timestamp, sentimentScore, postID, imageUrl);
				messages.add(message);
			} catch (Exception e) {
				System.err.println("Error reading message.");
				System.err.println(entity.toString());
				e.printStackTrace();
			}
		}
		return messages;
  }

  public void storeMessage(Message message) {
    Entity messageEntity = new Entity("Message", message.getId().toString());
    messageEntity.setProperty("user", message.getUser());
    messageEntity.setProperty("text", message.getText());
    messageEntity.setProperty("timestamp", message.getTimestamp());
    messageEntity.setProperty("postID", message.getPostID().toString());
    messageEntity.setProperty("sentimentScore", message.getSentimentScore());

    if(message.getImageUrl() != null) {
      messageEntity.setProperty("imageUrl", message.getImageUrl());
    }
    datastore.put(messageEntity);
  }

  private LocationMarker getLocationMarker(UUID postID) {
    Query query = new Query("LocationMarker")
                  .setFilter(new Query.FilterPredicate(
                                  "postID",
                                  FilterOperator.EQUAL,
                                  postID.toString()));
    PreparedQuery results = datastore.prepare(query);

    Entity entity = results.asSingleEntity();
    if(entity == null) {
     return null;
    }

    double lat = (double) entity.getProperty("lat");
    double lng = (double) entity.getProperty("lng");
    String content = (String) entity.getProperty("content");
    UUID postID = UUID.fromString((String) entity.getProperty("postID"));
    LocationMarker marker = new LocationMarker(lat, lng, content, postID);
    return marker;
  }

  private void storeLocationMarker(LocationMarker marker) {
    Entity markerEntity = new Entity("LocationMarker");
    markerEntity.setProperty("lat", marker.getLat());
    markerEntity.setProperty("lng", marker.getLng());
    markerEntity.setProperty("content", marker.getContent());
    markerEntity.setProperty("postID",marker.getPostID().toString());
    datastore.put(markerEntity);
  }
}
