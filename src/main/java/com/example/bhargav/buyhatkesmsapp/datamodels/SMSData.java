package com.example.bhargav.buyhatkesmsapp.datamodels;


import java.io.Serializable;

public class SMSData implements Serializable{

    private static final long serialVersionUID = 1L;
	private String number;
	private String body;
	private String id;

    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNumber() {
		return number;
	}
	
	public void setNumber(String number) {
		this.number = number;
	}
	
	public String getBody() {
		return body;
	}
	
	public void setBody(String body) {
		this.body = body;
	}
}
