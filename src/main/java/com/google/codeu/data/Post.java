package com.google.codeu.data;

import java.util.ArrayList;
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
	private String coverImageUrl;
	private String title;
	private String content;
	private LocationMarker marker;
	private List<ChartDataRow> chartData;
	private List<Message> messages;


	// Should be used when creating post for the first time.
	public Post(
			String creator,
			long timestamp,
			String coverImageUrl,
			String title,
			String content,
			LocationMarker marker,
			List<ChartDataRow> chartData) {
		this(UUID.randomUUID(),
					creator,
					timestamp,
					coverImageUrl,
					title,
					content,
					marker,
					chartData,
					new ArrayList<>());
	}

	// Should be used by post-feed when creating post snippet for feed.
	public Post(
			UUID postID,
			String creator,
			long timestamp,
			String coverImageUrl,
			String title,
			String content) {
		this(postID,
					creator,
					timestamp,
					coverImageUrl,
					title,
					content,
					null,
					null,
					null);
	}

	public Post(
			UUID postID,
			String creator,
			long timestamp,
			String coverImageUrl,
			String title,
			String content,
			LocationMarker marker,
			List<ChartDataRow> chartData,
			List<Message> messages) {
		this.postID = postID;
		this.creator = creator;
		this.timestamp = timestamp;
		this.coverImageUrl = coverImageUrl;
		this.title = title;
		this.content = content;
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
		return coverImageUrl;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
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
