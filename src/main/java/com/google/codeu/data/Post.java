package com.google.codeu.data;

import java.util.List;
import java.util.UUID;

/**
 * This class creates a Post object that is used for our User's posts and to
 * populate the web page.
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
	private List<Message> mesages;

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
		this.mesages = messages;

	}

	/**
	 * @return the postID
	 */
	public UUID getPostID() {
		return postID;
	}

	/**
	 * @param postID the postID to set
	 */
	public void setPostID(UUID postID) {
		this.postID = postID;
	}

	/**
	 * @return the creator
	 */
	public String getCreator() {
		return creator;
	}

	/**
	 * @param creator the creator to set
	 */
	public void setCreator(String creator) {
		this.creator = creator;
	}

	/**
	 * @return the timestamp
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * @return the coverInamgeUrl
	 */
	public String getCoverInamgeUrl() {
		return coverInamgeUrl;
	}

	/**
	 * @param coverInamgeUrl the coverInamgeUrl to set
	 */
	public void setCoverInamgeUrl(String coverInamgeUrl) {
		this.coverInamgeUrl = coverInamgeUrl;
	}

	/**
	 * @return the marker
	 */
	public LocationMarker getMarker() {
		return marker;
	}

	/**
	 * @param marker the marker to set
	 */
	public void setMarker(LocationMarker marker) {
		this.marker = marker;
	}

	/**
	 * @return the chartData
	 */
	public List<ChartDataRow> getChartData() {
		return chartData;
	}

	/**
	 * @param chartData the chartData to set
	 */
	public void setChartData(List<ChartDataRow> chartData) {
		this.chartData = chartData;
	}

	/**
	 * @return the mesages
	 */
	public List<Message> getMesages() {
		return mesages;
	}

	/**
	 * @param mesages the mesages to set
	 */
	public void setMesages(List<Message> mesages) {
		this.mesages = mesages;
	}

}
