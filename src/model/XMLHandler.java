package model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class XMLHandler {
	private String fileLocation;
	private Document doc;
	private List<Element> events;
	
	@SuppressWarnings("unchecked")
	public XMLHandler(String fileLocation){
		this.fileLocation = fileLocation;
	
		FileInputStream fis;
		try {
			fis = new FileInputStream(fileLocation);
			SAXBuilder builder = new SAXBuilder();
			doc = builder.build(fis);
			Element root = doc.getRootElement();
			events = root.getChildren("event"); 
			fis.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public Element storeEvent(Element el){
		events.add(el);
		writeXMLToDisk();
		return el;
	}
	
	public void removeElement(Element el){
		events.remove(el);
		writeXMLToDisk();
	}
	
	private void writeXMLToDisk(){
		FileOutputStream fileOutput;
		XMLOutputter output = new XMLOutputter(Format.getPrettyFormat());
		try {
			fileOutput = new FileOutputStream(fileLocation);
			output.output(doc, fileOutput);
			fileOutput.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected List<Element> getElements(){
		return events;
	}
}
