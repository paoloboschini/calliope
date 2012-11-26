package controller;

import java.util.Date;

import model.DataEvent;
/**
 * Interface that provides methods that are called when changes in the GUI should be reflected in the model.
 * 
 * @author Marcus Ihlar
 *
 */
public interface EventHandler {
	/**
	 * 
	 * @param event
	 * @param start
	 * @param end
	 */
	public DataEvent changeEventTime(DataEvent event, String start, String end);
	/**
	 * 
	 * @param event
	 * @param start
	 * @param end
	 */
	public DataEvent changeEventTime(DataEvent event, Date start, Date end);
	/**
	 * 
	 * @param event
	 * @param title
	 */
	public DataEvent changeEventTitle(DataEvent event, String title);
	/**
	 * 
	 * @param event
	 */
	public void removeEvent(DataEvent event);
	/**
	 * 
	 * @param title
	 * @param start  
	 * @param end
	 * @return
	 */
	public DataEvent createDataEvent(String title, String start, String end);
	/**
	 * 
	 * @param event
	 */
	public void addEventU(DataEvent event);
	/**
	 * 
	 * @param event
	 */
	public void removeEventU(DataEvent event);
}
