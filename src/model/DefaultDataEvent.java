package model;

import java.util.Date;

import controller.DateConverter;

/**
 * 
 * 
 * @author Marcus Ihlar
 *
 */
public class DefaultDataEvent implements DataEvent {
	
	private String title;
	private Date start, end;
	
	public DefaultDataEvent(String title, Date start, Date end){
		this.title = title;
		this.end = end;
		this.start = start;
	}
	
	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public String getStartTime() {
		return DateConverter.getInstance().dateToString(start);
	}

	@Override
	public String getEndTime() {
		return DateConverter.getInstance().dateToString(end);
	}

	@Override
	public Date getDate() {
		return start;
	}

	@Override
	public int compareTo(DataEvent other) {
		if(other instanceof DelimiterEvent)
			return getStart().compareTo(other.getStart());
		int r = (r = this.getStart().compareTo(other.getStart())) == 0 ? ((r = this.getEnd().compareTo(other.getEnd())) == 0 ? this.getTitle().compareTo(other.getTitle()) : r) : r;  
//		int r = this.getStart().compareTo(other.getStart());
//		if (r == 0){
//			r = this.getEnd().compareTo(other.getEnd());
//			if (r == 0)
//				r = this.getTitle().compareTo(other.getTitle());
//		}
		return r;
	}
	
	@Override
	public int hashCode(){
		return getStart().hashCode() + getEnd().hashCode() + getTitle().hashCode();
	}
	
	@Override
	public boolean equals(Object other){
		return other instanceof DataEvent && ((DataEvent)other).compareTo(this) == 0;
	}

	@Override
	public Date getStart() {
		return start;
	}

	@Override
	public Date getEnd() {
		return end;
	}
	
	public String toString(){
		return getTitle()+" start: "+getStartTime()+" end:"+getEndTime();
	}

}
