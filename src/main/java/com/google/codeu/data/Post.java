package com.google.codeu.data;

import java.util.List;
import java.util.UUID;

/**
 * This class creates a Post object that is used for our User's posts and to
 * populate the webpage.
 * 
 * @author Elli Kaplan
 *
 */
public class Post {

	private UUID postID;
	private String creator;
	private long timestamp;
	private String coverInamgeUrl;
	private LocationMarker marker;
	private List<ChartDataRow> chartData;
	private List<Message> messages;

	public Post(String creator, String coverImageUrl, LocationMarker marker, List<ChartDataRow> chartData,
			List<Message> messages) {
		this(UUID.randomUUID(), creator, System.currentTimeMillis(), coverImageUrl, marker, chartData, messages);
	}

	public Post(UUID postID, String creator, long timestamp, String coverImageUrl, LocationMarker marker,
			List<ChartDataRow> chartData, List<Message> messages) {
		this.postID = postID;
		this.creator = creator;
		this.timestamp = timestamp;
		this.coverInamgeUrl = coverImageUrl;
		this.marker = marker;
		this.chartData = chartData;
		this.messages = messages;

	}

	/**
	 * @return the postID
	 */
	public UUID getPostID() {
		return postID;
	}

	/**
	 * @return the creator
	 */
	public String getCreator() {
		return creator;
	}

	/**
	 * @return the timestamp
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * @return the coverInamgeUrl
	 */
	public String getCoverInamgeUrl() {
		return coverInamgeUrl;
	}

	/**
	 * @return the marker
	 */
	public LocationMarker getMarker() {
		return marker;
	}

	/**
	 * @return the chartData
	 */
	public List<ChartDataRow> getChartData() {
		return chartData;
	}

	/**
	 * @return the messages
	 */
	public List<Message> getMessages() {
		return messages;
	}

	/**
	 * @param mesages the messages to set
	 */
	public void setMessages(List<Message> mesages) {
		this.messages = mesages;
	}

}
