package com.mistareader;

import java.util.ArrayList;

public class Message {
	public long id;
	public int n;
	public String text;
	public String user;
	public long utime;
	public int vote;
	public ArrayList<Integer> repliedTo;
	public ArrayList<Reply> quote;
	public String quoteRepresentation;
	
	public String isUserStarter;
	public String timeText;
    public boolean isLoaded;
    public boolean isDeleted;
	
	public Message() {

		this.quoteRepresentation = "";
		this.isLoaded = false;
		this.isDeleted= true;
	} 
	

	public static class Reply{
		long id;
		public int n;
		
		public Reply(long id, int n) {
			this.id = id;
			this.n = n;
		}
	}	

}