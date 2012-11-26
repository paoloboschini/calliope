package model;

import java.util.Date;

public class DelimiterEvent implements DataEvent {
	
	private Date date;
	private boolean start;
	
	public DelimiterEvent(Date d, boolean start){
		date = d;
		this.start = start;
	}
	
	@Override
	public int compareTo(DataEvent other) {
		// TODO Auto-generated method stub
		return date.compareTo(other.getStart());
	}

	@Override
	public String getTitle() {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String getStartTime() {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String getEndTime() {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public Date getDate() {
		// TODO Auto-generated method stub
		return date;
	}

	@Override
	public Date getStart() {
		// TODO Auto-generated method stub
		return date;
	}

	@Override
	public Date getEnd() {
		// TODO Auto-generated method stub
		return date;
	}
	
	public DelimiterEvent setDate(Date d){
		date = d;
		return this;
	}
	
	
	public boolean equals(Object other){
		return other instanceof DelimiterEvent && ((DataEvent)other).compareTo(this) == 0;
	}

}
