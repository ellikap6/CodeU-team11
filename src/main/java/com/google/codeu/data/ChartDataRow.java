package com.google.codeu.data;

import java.util.UUID;

/**
 * This is an object for a single chart row and its data. We are using our
 * charts to chart temperature, so the value the data row stores is a
 * temperature.
 *
 * @author Elli Kaplan
 *
 */
public class ChartDataRow {

	private UUID postID;
	private long date;
	private int value;

	public ChartDataRow(UUID postID, long date, int value) {
		this.postID = postID;
		this.date = date;
		this.value = value;
	}

	public UUID getPostID() {
		return postID;
	}

	public long getDate() {
		return date;
	}

	public int getValue() {
		return value;
	}
}
