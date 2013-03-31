package com.happymoments;

import java.util.Date;

public class HappyMoment {

	private String id;
	private String text;
	private String filename;
	private String color;
	private Date createdDate;

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getFilename() {
		return filename;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getColor() {
		return color;
	}

	public void setCreatedDate(long millis) {
		this.createdDate = new Date(millis);
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public String toString() {
		return String.format("id=%s text=%s date=%s", id, text, createdDate);
	}

}
