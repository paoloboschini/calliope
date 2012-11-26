package view;

import model.DataEvent;

/**
 * This class specifies the behavior of a
 * graphical representation of a day. 
 * 
 * @author Paolo Boschini
 * @author Marcus Ihlar
 *
 */
@SuppressWarnings("serial")
public abstract class DayCard extends CalendarComponent {
	/**
	 * Adds a swing event to be displayed in the current day-card. The day card is responsible for setting the right position of the
	 * event depending on start time.
	 * 
	 * @param ec An event to be added to the specific card.
	 */
	public abstract void addEvent(EventComponent ec);
	/**
	 * Adds a data event to be displayed in the current day-card. The day card is responsible for setting the right position of the
	 * event depending on start time.
	 * 
	 * @param ec An event to be added to the specific card.
	 */
	public abstract void addEvent(DataEvent ec);
	/**
	 * Removes an event from the day card.
	 * @param ec The event to be removed
	 */
	public abstract void removeEvent(EventComponent ec);
	/**
	 * This method is called whenever an event changed in some way. If the time of the event is changed the current day card is
	 * responsible for correct positioning. 
	 * @param ec The event that is updated
	 */
	public abstract void updateEvent(EventComponent ec);
	/**
	 * Removes all events from the day card.
	 */
	public abstract void clearEvents();
}