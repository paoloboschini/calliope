package model;

import java.util.Date;
import java.util.NavigableSet;
import java.util.SortedSet;

import controller.EventDataListener;

public interface DataModel {
	
	public NavigableSet<DataEvent> getEventsInRange(Date start, Date end);
	public void registerEventDataListener(EventDataListener edl);
}
