package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D.Float;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import model.DataEvent;

/**
 * DefaultDayCard represents a ui card wrapped in a scrollpane for
 * displaying ui events. It keeps tracks of the current selected ui event
 * and refresh the displayed ui events according to the current date.
 * 
 * @author Paolo Boschini
 * @author Marcus Ihlar
 *
 */
@SuppressWarnings("serial")
public class DefaultDayCard extends DayCard implements MouseMotionListener, MouseListener {
	
	@SuppressWarnings("unused")
	private float relativeWidth, relativeHeight, scalar;
	
	/**
	 * Specifies if an event is being created or not.
	 * This is used for avoiding creating redundant events
	 * when dragging the mouse.
	 */
	private boolean creatingEvent;
	
	/**
	 * Store the current start and stop position of the created event.  
	 */
	private int startCreatedTask, stopCreatedTask;

	/**
	 * The step size for 30 minutes in the grid.  
	 */
	private int stepSize;
	
	/**
	 * Store the position of the mouse pointer on the y axis
	 * for highlighting the current line on the grid.   
	 */
	private int mousePointer;

	/**
	 * Custom colors.
	 */
	private Color customDarkGray, customLightGray, customTransparentGray, fillGray;

	/**
	 * The current swing event component being created.
	 */
	private DefaultEventComponent eventComponent;

	/**
	 * List of all the swing events on the current day.
	 */
	private List<DefaultEventComponent> events;
	
	
	/**
	 * The current selected swing event gives visual feedback
	 * changing the color and gains the focus for renaming its title.
	 */
	private DefaultEventComponent currentSelectedEventComponent;
	
	/**
	 * The width of the first column in a day card for displaying the clocks.
	 */
	private int widthLabelClock;
	
	/**
	 * A new Day card.
	 */
	public DefaultDayCard(){

		// initialize fields
		creatingEvent = false;
		startCreatedTask = stopCreatedTask = 0;
		stepSize = 20;
		mousePointer = 20000;
		events = new ArrayList<DefaultEventComponent>();
		eventComponent = null;
		currentSelectedEventComponent = null;
		widthLabelClock = 50;
		customDarkGray = new Color(200,200,200);
		customLightGray = new Color(230,230,230);
		fillGray = new Color(240,240,240);
		customTransparentGray = new Color(0.8f,0.8f,0.8f,0.4f);

		// setup the day card
		addMouseListener(this);
		addMouseMotionListener(this);
	}
	
	/**
	 * A new Day card with relative extents.
	 */
	public DefaultDayCard(float width, float height){
		 this();
		 setExtent(width, height);
	}

	@Override
	protected void paintComponent(Graphics gg){
		
		Graphics2D g = (Graphics2D)gg;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	    g.setFont(new Font("Lucida Grande", Font.PLAIN, 11));
		
		/**
		 * Subtract widthLabelClock pixels for hiding the horizontal scroll bar,
		 * and set the height to 48 rows (30' per row) times the step size.
		 */
	    setPreferredSize(new Dimension(getParent().getWidth()-widthLabelClock, stepSize*48));
	    
		// draw the grid
	    for (int i = 0 ; i < 48 ; ++i){

	    	// fill morning and evening with darker color
	    	if (i < 16 || i > 35) {
	    		g.setColor(fillGray);
	    		g.fillRect(getX()+widthLabelClock, stepSize*i, getWidth(), 19);
			}

			// draw horizontal lines
	    	g.setColor(i%2 == 0 ? customDarkGray : customLightGray);
			g.drawLine(widthLabelClock, stepSize*i - 1, getWidth(), stepSize*i - 1);				

			// draw clock labels
			if (i % 2 == 0 && i != 0)
				g.drawString((i/2) < stepSize/2 ? "0"+(i/2)+":00" : (i/2)+":00",  stepSize/2, stepSize * i + 4);

			// draw pixel height
//			g.setColor(customDarkGray);
//			g.drawString(i*stepSize+"", stepSize/2+10, stepSize * i + 4);
		}

		// vertical line separator for the clock labels 
	    g.setColor(customDarkGray);
	    g.drawLine(widthLabelClock, 0, widthLabelClock, getHeight());
		
		// highlight the row there the mouse pointer is 
		g.setColor(customTransparentGray);
		g.fillRect(getX()+widthLabelClock, mousePointer, getWidth(), 19);
	}

	/** (non-Javadoc)
	 * @see view.DayCard#addEvent(model.DataEvent)
	 * 
	 * Convert a data event from the db to a swing event.
	 */
	@Override
	public void addEvent(DataEvent dataEvent) {
		
		Calendar startCal = Calendar.getInstance();
		startCal.setTime(dataEvent.getStart());
		int startHour = startCal.get(Calendar.HOUR_OF_DAY);
		int startMin = startCal.get(Calendar.MINUTE) == 30 ? 1 : 0;
		int startOnGrid = startHour*2*stepSize + (startMin*stepSize);
		
		Calendar stopCal = Calendar.getInstance();
		stopCal.setTime(dataEvent.getEnd());
		int stopHour = stopCal.get(Calendar.HOUR_OF_DAY);
		int stopMin = stopCal.get(Calendar.MINUTE) == 30 ? 1 : 0;
		int heightOnGrid = stopHour == 0 ? stepSize*48-startOnGrid : (stopHour-startHour)*2*stepSize + (stopMin-startMin)*stepSize;
		
		eventComponent = new DefaultEventComponent(	stepSize, dataEvent,
													widthLabelClock, startOnGrid, getWidth()-widthLabelClock, heightOnGrid,
													dataEvent.getTitle(), startHour, startMin*30, stopHour, stopMin*30,
													startOnGrid, startOnGrid+heightOnGrid); 

		
		// add the new swing event to this day view, and to the list of events in this view
		events.add((DefaultEventComponent)add(eventComponent));
		
		// select the swing event in this day view
		if (currentSelectedEventComponent != null && currentSelectedEventComponent.equals(eventComponent)) {
			eventComponent.setSelected(true);
		}
	}

	@Override
	public void removeEvent(EventComponent ec) {}

	@Override
	public void updateEvent(EventComponent ec) {}

	@Override
	public void clearEvents() {}

	@Override
	public void expand() {}

	@Override
	public void collapse() {}

	@Override
	public void setExtent(float width, float height) {
		relativeWidth = width;
		relativeHeight = height;
	}

	@Override
	public Float getExtent() { return null; }
	
	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}

	/**
	 * On mouse pressed setup for a new swing event,
	 * and de-select any selected events.
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		startCreatedTask = e.getY() / stepSize;
		currentSelectedEventComponent = null;
		for (DefaultEventComponent eve : events) eve.setSelected(false);
	}

	/**
	 * On mouse released create a new swing event.
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		if (!creatingEvent) return;
		creatingEvent = false;
		eventComponent.setStartHour(startCreatedTask/2);
		eventComponent.setStartMin(startCreatedTask%2 == 0 ? 0 : 30);
		eventComponent.setStopHour(stopCreatedTask/2);
		eventComponent.setStopMin(stopCreatedTask%2 == 0 ? 0 : 30);
		eventComponent.setTop(startCreatedTask*stepSize);
		eventComponent.setBottom(stopCreatedTask*stepSize);
		eventComponent.editEventTitle();
	}

	/**
	 * On mouse dragged create a new swing event,
	 * only if the mouse was dragged longer than half an hour.
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
		mousePointer = 20000;
		stopCreatedTask = e.getY() / stepSize;
		if (stopCreatedTask - startCreatedTask > 0 && !creatingEvent) {
			creatingEvent = true;
			eventComponent = new DefaultEventComponent(stepSize);
			eventComponent.setBounds(	widthLabelClock,
										startCreatedTask*stepSize,
										getWidth()-widthLabelClock,
										(stopCreatedTask-startCreatedTask)*stepSize);
			add(eventComponent);
			events.add(eventComponent);
		}
		if (creatingEvent) {
			eventComponent.setBounds(	widthLabelClock,
										startCreatedTask*stepSize,
										getWidth()-widthLabelClock,
										(stopCreatedTask-startCreatedTask)*stepSize);
			 repaint();
		}
	}

	/**
	 * On mouse moved update the current highlighted row.
	 */
	@Override
	public void mouseMoved(MouseEvent e) {
		mousePointer = (e.getY()/20)*20;
		repaint();
	}

	@Override
	public void setScalar(float f) {
		scalar = f;
	}
	
	public void setTopLeft(float x, float y) {}
	public void setBottomLeft(float x, float y) {}
	public void setBottomRight(float x, float y) {}
	public void setTopRight(float x, float y) {}
	public void setCenter(float x, float y) {}
	public void setTop(float y) {}
	public void setBottom(float y) {}
	public void setLeft(float x) {}
	public void setRight(float x) {}

	public List<DefaultEventComponent> getEvents() {
		return events;
	}

	public void setEvents(List<DefaultEventComponent> events) {
		this.events = events;
	}

	public int getMousePointer() {
		return mousePointer;
	}

	public void setMousePointer(int mousePointer) {
		this.mousePointer = mousePointer;
	}

	@Override
	public void addEvent(EventComponent ec) {		
	}

	public void setSelectedEvent(DefaultEventComponent defaultEventComponent) {
		for (DefaultEventComponent dec : events) dec.setSelected(false);
		currentSelectedEventComponent = defaultEventComponent;
		currentSelectedEventComponent.setSelected(true);
	}
}