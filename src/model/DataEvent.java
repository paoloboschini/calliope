package model;

import java.util.Date;

/**
 * 
 * An interface specification for data events that exposes only the functionality needed in the view-layer of the program. 
 * 
 * @author Marcus Ihlar
 *
 */
public interface DataEvent extends Comparable<DataEvent> {
	
	/**
	 * 
	 * @return A text string representing the title of the event.
	 */
	public String getTitle();
	
	
	/**
	 * 
	 * @return A string representation of the events start time.
	 */
	public String getStartTime();
	
	/**
	 * 
	 * @return A string representation of the events end time.
	 */
	public String getEndTime();
	
	/**
	 * 
	 * @return A date which contains year month and day of the event.
	 */
	public Date getDate();
	
	public Date getStart();
	public Date getEnd();
	
}
