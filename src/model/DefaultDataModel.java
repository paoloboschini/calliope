package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NavigableSet;

import controller.EventDataListener;


public class DefaultDataModel implements InternalDataModel {
	
	private List<EventDataListener> listeners;
	private NavigableSet<DataEvent> data;
	private DelimiterEvent startMark, endMark;
	
	
	public DefaultDataModel(){
		listeners = new ArrayList<EventDataListener>();
		startMark = new DelimiterEvent(null, true);
		endMark = new DelimiterEvent(null, true);
	}
	
	@Override
	public NavigableSet<DataEvent> getEventsInRange(Date start, Date end) {
		startMark.setDate(start);
		endMark.setDate(end);
		return data.subSet(startMark, true, endMark, true);
	}


	@Override
	public void registerEventDataListener(EventDataListener edl) {
		listeners.add(edl);
		
	}

	@Override
	public void setData(NavigableSet<DataEvent> data) {
		this.data = data;
	}

	@Override
	public void contentChanged() {
		for (EventDataListener edl : listeners){
			edl.contentChanged();
		}
	}
}
