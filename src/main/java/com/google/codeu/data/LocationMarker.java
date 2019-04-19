package com.google.codeu.data;

import java.util.UUID;

/**
 * LocationMarker is very similar to UserMarker but it also includes the
 * postID which is the post it belongs to.
 * 
 * @author Elli Kaplan
 *
 */
public class LocationMarker {

	private UUID postID;
	private double lat;
	private double lng;
	private String content;

	/**
	 * Constructor of LocationMarker
	 * @param postID note: this is not its own id, it’s the id of the post
	 * @param lat
	 * @param lng
	 * @param content
	 */
	public LocationMarker(UUID postID, double lat, double lng, String content) {
		this.postID = postID;
		this.lat = lat;
		this.lng = lng;
		this.content = content;
	}

	public UUID getPostID() {
		return postID;
	}
	
	public double getLat() {
		return lat;
	}

	public double getLng() {
		return lng;
	}

	public String getContent() {
		return content;
	}
}
