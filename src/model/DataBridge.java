package model;

import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import org.jdom.Element;

import controller.AddEventEdit;
import controller.ChangeEventEdit;
import controller.DateConverter;
import controller.EventHandler;
import controller.Main;
import controller.RemoveEventEdit;

public class DataBridge implements EventHandler {
	private TreeMap<DataEvent, Element> bridge;
	private InternalDataModel model;
	private XMLHandler database;
	
	public DataBridge(InternalDataModel model, XMLHandler database){
		this.model = model;
		this.database = database;
		bridge = new TreeMap<DataEvent, Element>();
		createDataEvents(database.getElements());
		model.setData(bridge.navigableKeySet());
	}

	private void createDataEvents(List<Element> xmlElements) {
		for (Element e : xmlElements){
			bridge.put(createEventFromElement(e), e);
		}
	}

	private DataEvent createEventFromElement(Element e) {
		DataEvent tmp = null;
		tmp = new DefaultDataEvent(e.getChildText("title"), 
										DateConverter.getInstance().stringToDate(e.getChildText("start")), 
										DateConverter.getInstance().stringToDate(e.getChildText("end")));
		return tmp;
	}
	
	public DataEvent createDataEvent(String title, String start, String end){
		DataEvent tmp = null;
		tmp = new DefaultDataEvent(title, DateConverter.getInstance().stringToDate(start), DateConverter.getInstance().stringToDate(end));
		bridge.put(tmp, database.storeEvent(createXMLElement(tmp)));
		Main.getInstance().getUndoManager().addEdit(new AddEventEdit(tmp));
		return tmp;
	}
	
	private Element createXMLElement(DataEvent de){
		Element e = new Element("event");
		e.addContent(new Element("title").setText(de.getTitle()));
		e.addContent(new Element("start").setText(de.getStartTime()));
		e.addContent(new Element("end").setText(de.getEndTime()));
		return e;
	}
	
	public DataEvent changeEventTitle(DataEvent event, String title){
		return updateEvent(event, title, event.getStart(), event.getEnd());
	}
	public DataEvent changeEventTime(DataEvent event, String start, String end){
		return updateEvent(event, event.getTitle(), DateConverter.getInstance().stringToDate(start), DateConverter.getInstance().stringToDate(end));
	}

	@Override
	public void removeEvent(DataEvent de) {
		database.removeElement(bridge.get(de));
		bridge.remove(de);
		Main.getInstance().getUndoManager().addEdit(new RemoveEventEdit(de));
		model.contentChanged();
	}

	@Override
	public DataEvent changeEventTime(DataEvent event, Date start, Date end) {
		return updateEvent(event, event.getTitle(), start, end);
	}
	
	private DataEvent updateEvent(DataEvent event, String title, Date start, Date end){
		DataEvent nEvent = new DefaultDataEvent(title, start, end);
		Element nElem = bridge.get(event);
		database.removeElement(nElem);
		bridge.remove(event);
		nElem.getChild("title").setText(title);
		nElem.getChild("start").setText(DateConverter.getInstance().dateToString(start));
		nElem.getChild("end").setText(DateConverter.getInstance().dateToString(end));
		bridge.put(nEvent, database.storeEvent(nElem));
		Main.getInstance().getUndoManager().addEdit(new ChangeEventEdit(nEvent, event));
		model.contentChanged();
		return nEvent;
	}

	@Override
	public void addEventU(DataEvent event) {
		bridge.put(event, database.storeEvent(createXMLElement(event)));
		model.contentChanged();
	}

	@Override
	public void removeEventU(DataEvent event) {
		database.removeElement(bridge.get(event));
		bridge.remove(event);
		model.contentChanged();
	}
}
