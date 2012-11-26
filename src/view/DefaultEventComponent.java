package view;

import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D.Float;
import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import model.DataEvent;
import controller.DateConverter;
import controller.Main;

/**
 * DefaultEventComponent represents a ui component. 
 *
 * @author Paolo Boschini
 *
 */@SuppressWarnings("serial")
public class DefaultEventComponent extends CalendarComponent implements MouseListener, MouseMotionListener {

	private int startHour, startMin, stopHour, stopMin, grabBorder, stepSize, top, bottom, height;
	private String eventTitle = "";
	private Point startClickPoint;

	private Color borderTaskColor = new Color(0.0f,0.0f,1.0f,1.0f);
	private Color selectedTaskColor = new Color(0.0f,0.0f,1.0f,0.8f);
	private Color unselectedTaskColor = new Color(0.0f,0.0f,1.0f,0.4f);

	EventTitleField titleEventField;
	private DataEvent dataEvent;
	private boolean isSelected;

	private int neighbours;

	public int overlappingLocalIndex, overlappingEventsInSequence, equalTopEvents, overlappingTopLocalIndex, overlappingTopEventsInSequence;

	/**
	 * Called when creating a new event by dragging the mouse
	 * @param stepSize
	 */
	public DefaultEventComponent(int stepSize) {
		this();
		titleEventField = new EventTitleField("New Event");
		add(titleEventField);
		this.stepSize = stepSize;
	}

	/**
	 * Called when loading an event from the database
	 * 
	 * @param stepSize
	 * @param dataEvent
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param title
	 * @param startHour
	 * @param startMin
	 * @param stopHour
	 * @param stopMin
	 * @param currentTop
	 * @param currentBottom
	 */
	public DefaultEventComponent(	int stepSize, DataEvent dataEvent,
			int x, int y, int w, int h,
			String title, int startHour, int startMin, int stopHour, int stopMin,
			int top, int bottom) {

		this();
		this.stepSize = stepSize;
		setBounds(x,y,w,h);
		this.dataEvent = dataEvent;
		eventTitle = title;
		this.startHour = startHour;
		this.startMin = startMin;
		this.stopHour = stopHour;
		this.stopMin = stopMin;
		this.top = top;
		this.bottom = bottom;
	}

	/**
	 * Constructor for default settings.
	 */
	public DefaultEventComponent() {
		overlappingLocalIndex = overlappingEventsInSequence = equalTopEvents = 0;
		addMouseListener(this);
		addMouseMotionListener(this);
		addKeyListener(new KeyListener() {			
			public void keyTyped(KeyEvent e) {}
			public void keyReleased(KeyEvent e) {}
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
					Container par = getParent();
					Main.getInstance().getEventHandler().removeEvent(dataEvent);
					Main.getInstance().getCalendarContainer().updateDisplayedEvents();
					par.add(DefaultEventComponent.this);
					repaint();
					Main.getInstance().getAnimationEngine().addAnimationObject(new DeleteAnimation(1, 0.3f));
				}
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					titleEventField = new EventTitleField(eventTitle);
					eventTitle = "";
					repaint();
					add(titleEventField);
					titleEventField.requestFocusInWindow();
				}
				if (e.getKeyCode() == KeyEvent.VK_L) {
					((DefaultDayCard)getParent()).setSelectedEvent(DefaultEventComponent.this);
					if (!e.isShiftDown()) {
						Main.getInstance().getAnimationEngine().addAnimationObject(new MoveDownAnimation(1, 0.3f, 30));
					} else {
						Main.getInstance().getAnimationEngine().addAnimationObject(new MoveDownAnimation(1, 0.6f, 60));
					}
				}
				if (e.getKeyCode() == KeyEvent.VK_E) {
					((DefaultDayCard)getParent()).setSelectedEvent(DefaultEventComponent.this);
					if (!e.isShiftDown()) {
						Main.getInstance().getAnimationEngine().addAnimationObject(new MoveUpAnimation(1, 0.3f, 30));
					} else {
						Main.getInstance().getAnimationEngine().addAnimationObject(new MoveUpAnimation(1, 0.6f, 60));
					}
				}
			}
		});

	}

	@Override
	protected void paintComponent(Graphics gg){
		Graphics2D g = (Graphics2D)gg;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setFont(new Font("Lucida Grande", Font.PLAIN, 11));

		g.setColor(isSelected ? selectedTaskColor : unselectedTaskColor);
		g.fillRoundRect(0,0,getWidth(),getHeight(),20,20);

		g.setColor(borderTaskColor);
		g.drawRoundRect(0,0,getWidth()-1,getHeight()-1,20,20);

		g.setColor(isSelected ? Color.WHITE : selectedTaskColor);
		g.drawString(eventTitle, 7, 14);
		super.paintComponent(gg);
	}

	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {
		//		System.out.println(startHour+":"+startMin+" - "+stopHour+":"+stopMin);
		//		System.out.println("currentTop: "+top+","+"currentBottom: "+bottom);
		dataEvent = Main.getInstance().getEventHandler().changeEventTime(dataEvent,
				DateConverter.getInstance().intToDate(Main.getInstance().getCalendarContainer().getCurrentNavigationDay().getTime(), startHour, startMin),
				DateConverter.getInstance().intToDate(Main.getInstance().getCalendarContainer().getCurrentNavigationDay().getTime(), stopHour, stopMin));

		Main.getInstance().getCalendarContainer().updateDisplayedEvents();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		((DefaultDayCard)getParent()).setSelectedEvent(this);

		// depending on where we click, either move or resize the event 
		startClickPoint = e.getPoint();
		if (getCursor().getType() == Cursor.N_RESIZE_CURSOR)
			grabBorder = e.getY() < 10 ? 0 : 1;
	}

	/**
	 * This method is used to move or resize the event
	 * according to the start dragged point.
	 */
	@Override
	public void mouseDragged(MouseEvent e) {

		// move or resize here and update the event!
		int offset = ((e.getY() - startClickPoint.y) / stepSize) * stepSize;

		// move the event
		if (getCursor().getType() == Cursor.DEFAULT_CURSOR) {

			if (getY()+(offset) < 0) // if outside the grid
				top = 0;
			else if ((top + getHeight()) > (stepSize*48-5) && !(e.getY() < startClickPoint.y))
				top = (stepSize*48) - getHeight();
			else
				top = getY()+(offset);

			setBounds(getX(),top, getWidth(), getHeight());
			bottom = top + getHeight();

			startHour = top / (stepSize*2);
			startMin = top % (stepSize*2) == 0 ? 0 : 30;		
			if (top == 0) startHour = startMin = 0; 

			stopHour = (top + getHeight()) / (stepSize*2);				
			if (top == 0) stopHour = getHeight() / (stepSize*2);
			stopMin = (top + getHeight()) % (stepSize*2) == 0 ? 0 : 30;
		}
		else { // resize the event
			if (grabBorder == 0) { // grabbed top border
				if (getY()+(offset) < 0) // if outside the grid
					top = 0;
				else
					top = getY()+(offset);

				setBounds(getX(),top, getWidth(), bottom-top);
				startHour = (top / (stepSize*2));
				startMin = top % (stepSize*2) == 0 ? 0 : 30;
			} else { // grabbed bottom border
				height = e.getY();
				height = height - (height % stepSize);

				if (getY() + height <= stepSize*48) {
					setBounds(getX(),getY(), getWidth(), height);
					if (startMin == 0) {
						stopHour = startHour + height / (stepSize*2);
						stopMin = getHeight() % (stepSize*2) == 0 ? 0 : 30;
					} else {
						stopHour = getHeight() % (stepSize*2) == 0 ? startHour + height / (stepSize*2) : startHour + 1 + height / (stepSize*2);
						stopMin = getHeight() % (stepSize*2) == 0 ? 30 : 0;
					}					
				}
				bottom = (stopHour*stepSize*2) + (stopMin == 0 ? 0 : 20);
			}
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		setCursor(new Cursor(e.getY() > getHeight()-7 || e.getY() < 7 ? Cursor.N_RESIZE_CURSOR : Cursor.DEFAULT_CURSOR));
		((DefaultDayCard)getParent()).setMousePointer(20000);
		((DefaultDayCard)getParent()).repaint();
	}

	public int getStartHour() { return startHour; }
	public void setStartHour(int startHour) { this.startHour = startHour; }
	public int getStartMin() { return startMin;	}
	public void setStartMin(int startMin) { this.startMin = startMin; }
	public int getStopHour() { return stopHour; }
	public void setStopHour(int endHour) { this.stopHour = endHour; }
	public int getStopMin() { return stopMin; }
	public void setStopMin(int endMin) { this.stopMin = endMin; }

	public void editEventTitle() {
		titleEventField.requestFocusInWindow();
	}

	public void updateEventTitle(String title) {
		remove(titleEventField);
		eventTitle = title;
		((DefaultDayCard)getParent()).setSelectedEvent(this);
		Main.getInstance().getCalendarContainer().updateDisplayedEvents();
	}

	public void setEventTitle(String title) {

		if (dataEvent == null)
			dataEvent = Main.getInstance().getEventHandler().createDataEvent(title,
					DateConverter.getInstance().dateToString(DateConverter.getInstance().intToDate(Main.getInstance().getCalendarContainer().getCurrentNavigationDay().getTime(),startHour, startMin)),
					DateConverter.getInstance().dateToString(DateConverter.getInstance().intToDate(Main.getInstance().getCalendarContainer().getCurrentNavigationDay().getTime(),stopHour, stopMin)));
		else
			dataEvent = Main.getInstance().getEventHandler().changeEventTitle(dataEvent, title);


		// remove the textField for the title and draw string in paintComponent,
		// so that the event is resizable even when mouse is over the title
		updateEventTitle(title);

	}

	public String getEventTitle() {
		return eventTitle;
	}

	public void setExtent(float width, float height) {}
	public Float getExtent() {return null;}
	public void setTopLeft(float x, float y) {}
	public void setBottomLeft(float x, float y) {}
	public void setBottomRight(float x, float y) {}
	public void setTopRight(float x, float y) {}
	public void setCenter(float x, float y) {}
	public void setTop(float y) {}
	public void setBottom(float y) {}
	public void setLeft(float x) {}
	public void setRight(float x) {}
	public void setScalar(float f) {}
	public void expand() {}
	public void collapse() {}

	public int getTop() {
		return top;
	}

	public void setTop(int top) {
		this.top = top;
	}

	public int getBottom() {
		return bottom;
	}

	public void setBottom(int bottom) {
		this.bottom = bottom;
	}

	public void setDataEvent(DataEvent event) {
		dataEvent = event;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
		repaint();
	}

	@Override
	public boolean equals(Object other){
		return other instanceof DefaultEventComponent &&
				((DefaultEventComponent)other).dataEvent.getStart().equals(this.dataEvent.getStart()) &&
				((DefaultEventComponent)other).dataEvent.getEnd().equals(this.dataEvent.getEnd()) &&
				((DefaultEventComponent)other).dataEvent.getTitle().equals(this.dataEvent.getTitle());
	}

	@Override
	public int hashCode(){
		return dataEvent.hashCode();
	}

	@Override
	public String toString(){
		return eventTitle+", start: "+startHour+":"+startMin+", stop: "+stopHour+":"+stopMin;
	}

	public void addNeighbour() {
		neighbours++;
	}


	public int getNeighbour() {
		return neighbours;
	}

	/**
	 * DeleteAnimation adds support for deleting this object with an animation
	 * specified in the overrided methods.
	 * @author Paolo Boschini, Marcus Ihlar
	 *
	 */
	private class DeleteAnimation implements Animateable {

		private int speed;
		private float durationAnimation;

		public DeleteAnimation(int speed, float durationAnimation) {
			this.speed = speed;
			this.durationAnimation = durationAnimation;
		}

		@Override
		public int getSpeed() {
			return speed;
		}

		@Override
		public float getDuration() {
			return durationAnimation;
		}

		@Override
		public void update() {
			unselectedTaskColor = new Color(unselectedTaskColor.getRed()/255.0f, unselectedTaskColor.getGreen()/255.0f, unselectedTaskColor.getBlue()/255.0f, Math.max(unselectedTaskColor.getAlpha()/255.0f - 0.4f/(getDuration()*Main.getInstance().getAnimationEngine().getFPS()), 0));
			selectedTaskColor = new Color(selectedTaskColor.getRed()/255.0f, selectedTaskColor.getGreen()/255.0f, selectedTaskColor.getBlue()/255.0f, Math.max(selectedTaskColor.getAlpha()/255.0f - 0.8f/(getDuration()*Main.getInstance().getAnimationEngine().getFPS()), 0));
			borderTaskColor = new Color(borderTaskColor.getRed()/255.0f, borderTaskColor.getGreen()/255.0f, borderTaskColor.getBlue()/255.0f, Math.max(borderTaskColor.getAlpha()/255.0f - 1.0f/(getDuration()*Main.getInstance().getAnimationEngine().getFPS()), 0));

			// implode
			setBounds(getX() + 10, getY() + 10, getWidth() - 10*2, getHeight() - 10*2);

			// explode
//			setBounds(getX() - 10, getY() - 10, getWidth() + 10*2, getHeight() + 10*2);

			// minimize horizontally
			//setBounds(getX() + 20, getY(), getWidth() - 20*2, getHeight());			
		}

		@Override
		public void finished() {
			getParent().remove(DefaultEventComponent.this);
			Main.getInstance().getCalendarContainer().updateDisplayedEvents();	
		}		
	}

	/**
	 * MoveUpAnimation adds support for moving this object upwards with an animation
	 * specified in the overrided methods.
	 * @author Paolo Boschini, Marcus Ihlar
	 *
	 */
	private class MoveUpAnimation implements Animateable {

		private int speed, offset;
		private float durationAnimation;

		public MoveUpAnimation(int speed, float durationAnimation, int offset) {
			this.speed = speed;
			this.durationAnimation = durationAnimation;
			this.offset = offset;
		}

		@Override
		public int getSpeed() {
			return speed;
		}

		@Override
		public float getDuration() {
			return durationAnimation;
		}

		@Override
		public void update() {
			setBounds(getX(), getY() - 1, getWidth(), getHeight());

			Runnable runner = new Runnable(){
				public void run(){
					repaint();
				}
			};
			try {
				SwingUtilities.invokeAndWait(runner);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void finished() {
			if (offset == 30) {
				startHour = startMin == 0 ? startHour - 1 : startHour;
				startMin = startMin == 0 ? 30 : 0;
				stopHour = stopMin == 0 ? stopHour - 1 : stopHour;
				stopMin = stopMin == 0 ? 30 : 0;
				top -= stepSize; 
				bottom -= stepSize;				
			} else {
				startHour -= 1;
				stopHour -= 1;
				top -= stepSize*2; 
				bottom -= stepSize*2;
			}

			dataEvent = Main.getInstance().getEventHandler().changeEventTime(dataEvent,
					DateConverter.getInstance().intToDate(Main.getInstance().getCalendarContainer().getCurrentNavigationDay().getTime(), startHour, startMin),
					DateConverter.getInstance().intToDate(Main.getInstance().getCalendarContainer().getCurrentNavigationDay().getTime(), stopHour, stopMin));
			Main.getInstance().getCalendarContainer().updateDisplayedEvents();
		} // finished
	}

	/**
	 * MoveDownAnimation adds support for moving this object downwards with an animation
	 * specified in the overrided methods.
	 * @author Paolo Boschini, Marcus Ihlar
	 *
	 */
	private class MoveDownAnimation implements Animateable {

		private int speed, offset;
		private float durationAnimation;

		public MoveDownAnimation(int speed, float durationAnimation, int offset) {
			this.speed = speed;
			this.durationAnimation = durationAnimation;
			this.offset = offset;
		}

		@Override
		public int getSpeed() {
			return speed;
		}

		@Override
		public float getDuration() {
			return durationAnimation;
		}

		@Override
		public void update() {
			setBounds(getX(), getY() + 1, getWidth(), getHeight());

			Runnable runner = new Runnable(){
				public void run(){
					repaint();
				}
			};
			try {
				SwingUtilities.invokeAndWait(runner);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}


		@Override
		public void finished() {
			if (offset == 30) {
				startHour = startMin == 0 ? startHour : startHour + 1;
				startMin = startMin == 0 ? 30 : 0;
				stopHour = stopMin == 0 ? stopHour : stopHour + 1;
				stopMin = stopMin == 0 ? 30 : 0;
				top += stepSize; 
				bottom += stepSize;
			} else {
				startHour += 1;
				stopHour += 1;
				top += stepSize*2; 
				bottom += stepSize*2;
			}

			dataEvent = Main.getInstance().getEventHandler().changeEventTime(dataEvent,
					DateConverter.getInstance().intToDate(Main.getInstance().getCalendarContainer().getCurrentNavigationDay().getTime(), startHour, startMin),
					DateConverter.getInstance().intToDate(Main.getInstance().getCalendarContainer().getCurrentNavigationDay().getTime(), stopHour, stopMin));
			Main.getInstance().getCalendarContainer().updateDisplayedEvents();
		} // finished
	}
}