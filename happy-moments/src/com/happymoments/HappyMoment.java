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

	public void setText(String text) {
		this.text = text;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public void setCreatedDate(long millis) {
		this.createdDate = new Date(millis);
	}
	
	public String toString() {
		return String.format("text=%s date=%s", text, createdDate);
	}

	public String getText() {
		return text;
	}
	
	public Date getCreatedDate() {
		return createdDate;
	}

}
