package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.SortedSet;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollPane;

import model.DataEvent;
import controller.DateConverter;
import controller.Main;

/**
 * This class is a container for the different views in the calendar.
 * It is placed directly on top of a contentPane and is responsible
 * for updating the dimensions of its children. 
 *
 * @author Paolo Boschini
 */
@SuppressWarnings("serial")
public class CalendarContainer extends JComponent {
	
	/**
	 * heightTopArea is the height of the top area that contains
	 * information about current day and other navigation elements.
	 */
	private int heightTopArea;

	/**
	 * insets for spacing around the container's children, i.e.
	 * a scrollPane for a dayView.
	 */
	private int insets;
	
	/**
	 * scrollPane for a dayView or a weekView.
	 */
	private JScrollPane scroll;
	
	/**
	 * Custom buttons for navigating through days. 
	 */
	private JButton yesterdayButton, today, tomorrowButton, undoB, redoB;
	
	/**
	 * List for storing different days in weeks. 
	 */
	@SuppressWarnings("unused")
	private List<CalendarComponent> days;
	
	/**
	 * String representations of the current date. 
	 */
	private String currentDate, currentDay, currentMonth, currentYear;
	
	/**
	 * A calendar for storing the reference to the current navigation day. 
	 */
	private Calendar currentNavigationDay;
	
	/**
	 * DayCard for displaying events in a day. A dayCard is placed in a scrollPane. 
	 */
	private DefaultDayCard dayCard;
	
	/**
	 * seq stores how many events there are in each overlapping sequence   
	 */
    private ArrayList<Integer> seq = new ArrayList<Integer>();

	/**
	 * seqEqualTop stores how many events with same top there are in each overlapping sequence   
	 */
    private ArrayList<Integer> seqEqualTop = new ArrayList<Integer>();

    /**
	 * Constructor for a new CalendarContainer.
	 * 
	 * @param background the background of the calendar container
	 * @param heightTopArea the height of the top area in pixels 
	 * @param widthParent the width of the parent, usually a contentPane
	 * @param heightParent the width of the parent, usually a contentPane
	 */
	public CalendarContainer(Color background, final int heightTopArea, int widthParent, int heightParent) {
		
		// initialize fields
		insets = 20;
		currentNavigationDay = Calendar.getInstance();
		this.heightTopArea = heightTopArea;

		// setup the container
		setBounds(0,0,widthParent,heightParent);
		setOpaque(true);
		setBackground(background);
		updateCurrentNavigationDay();
		addDay(new DefaultDayCard());
		
		undoB = new CustomButton("UNDO");
		undoB.setBounds(getWidth()-insets-372, heightTopArea-26-insets, 100, 30);
		add(undoB);
		undoB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Main.getInstance().getUndoManager().undo();
				updateDisplayedEvents();
				repaint();
			}
		});
		redoB = new CustomButton("REDO");
		redoB.setBounds(getWidth()-insets-271, heightTopArea-26-insets, 100, 30);
		add(redoB);
		redoB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Main.getInstance().getUndoManager().redo();
				updateDisplayedEvents();
				repaint();
			}
		});

		// setup the navigation buttons
		yesterdayButton = new CustomButton("◀");
		yesterdayButton.setBounds(getWidth()-insets-162, heightTopArea-26-insets, 30, 30);
		add(yesterdayButton);
		yesterdayButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				currentNavigationDay.add(Calendar.DATE, -1);
				updateCurrentNavigationDay();
				updateDisplayedEvents();
			}
		});
		
		today = new CustomButton("Today");
		today.setBounds(getWidth()-insets-131, heightTopArea-26-insets, 100, 30);
		add(today);
		today.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				currentNavigationDay = Calendar.getInstance();
				updateCurrentNavigationDay();
				updateDisplayedEvents();
			}
		});

		tomorrowButton = new CustomButton("▶");
		tomorrowButton.setBounds(getWidth()-insets-30, heightTopArea-26-insets, 30, 30);
		add(tomorrowButton);
		tomorrowButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				currentNavigationDay.add(Calendar.DATE, 1);
				updateCurrentNavigationDay();
				updateDisplayedEvents();
			}
		});
		// end setup the navigation buttons
	}

	/**
	 * This method adds a dayView to this calendar container through a scrollPane.
	 * @param day the dayCard to be added
	 */
	public void addDay(DefaultDayCard day){
		dayCard = day;
		scroll = new JScrollPane(day);
		scroll.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
		scroll.setBounds(insets, heightTopArea, getWidth()-insets*2, getHeight()-heightTopArea-insets);
		add(scroll);
	}
	
	/**
	 * This method updates the displayed current navigation day by extracting
	 * date, name of the day, month and year from the current navigation day.
	 */
	private void updateCurrentNavigationDay() {
		String dayNames[] = new DateFormatSymbols().getWeekdays();
	    String monthNames[] = new DateFormatSymbols().getMonths();
	    currentDate = Integer.toString(currentNavigationDay.get(Calendar.DAY_OF_MONTH));
		currentDay = dayNames[currentNavigationDay.get(Calendar.DAY_OF_WEEK)];
		currentMonth = monthNames[currentNavigationDay.get(Calendar.MONTH)];
		currentYear = Integer.toString(currentNavigationDay.get(Calendar.YEAR));
		repaint();
	}
	
	/**
	 * This method repaint the events in a dayView according to the current navigation day.
	 */
	public void updateDisplayedEvents() {
		
		// remove first all events
		for (DefaultEventComponent dec : dayCard.getEvents())
			dayCard.remove(dec);
		
		// extract those events occurring in the current navigation day
		SortedSet<DataEvent> dataEvents = Main.getInstance().getModel().getEventsInRange(
				DateConverter.getInstance().intToDate(currentNavigationDay.getTime(), 0, 0),
				DateConverter.getInstance().intToDate(currentNavigationDay.getTime(), 24, 0)); 

		// clear the list of events held by a day card
		dayCard.getEvents().clear();
		
		// add events to dayCard
		for (DataEvent e : dataEvents) {
			dayCard.addEvent(e);
		}
		
		/**
		 * Fix the right painting order for each event.
		 * If two events overlap, the latest one ion the time line
		 * has to be over the the previous one.  
		 */
		fixZIndex();

		// calculate for each event how n overlapping other events 
		calculateOverlappingEvents();

		repaint();
	}

	/**
	 * Calculate for each event how n overlapping other events.
	 */
	private void calculateOverlappingEvents() {
		DefaultEventComponent x, y;
		for (int i = 0; i < dayCard.getEvents().size(); i++) {
			x = dayCard.getEvents().get(i);
			for (int j = 0; j < dayCard.getEvents().size(); j++) {
				y = dayCard.getEvents().get(j);
				if (x != y && (x.getTop() < y.getBottom() && x.getBottom() > y.getTop())) {
					x.addNeighbour();
					if (x.getTop() == y.getTop())
						x.equalTopEvents++;
				}
			}
		}
	}

	/**
	 * Fix the right painting order for each event.
	 * If two events overlap, the latest one ion the time line
	 * has to be over the the previous one.  
	 */
	private void fixZIndex() {
		for (int i = 0; i < dayCard.getEvents().size() ; i++) {
			dayCard.setComponentZOrder(dayCard.getEvents().get(i), dayCard.getEvents().size() - 1 - i);	
		}
	}
	
	/**
	 * Paint this calendar container and updates the children's position and dimension.
	 */
	protected void paintComponent(Graphics gg){
		
		gg.setColor(getBackground());
		gg.fillRect(0, 0, getWidth(), getHeight());
		
		Graphics2D g = (Graphics2D)gg;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(Color.LIGHT_GRAY);
		Font font = new Font("Averia Serif", Font.PLAIN, 90);

		// begin to write at insets position so that the day label align vertically with the scroll
		int caret = insets;

		// date
		g.setFont(font);
//		g.setColor(dateColor);
	    g.drawString(currentDate, caret, heightTopArea-insets);
	    caret += g.getFontMetrics(g.getFont()).stringWidth(currentDate);
	    
		// day week
		g.setColor(Color.LIGHT_GRAY);
	    g.setFont(g.getFont().deriveFont(50f));
	    g.drawString(currentDay, caret, heightTopArea-insets);
	    caret += g.getFontMetrics(g.getFont()).stringWidth(currentDay);

	    // month + year
	    g.setFont(g.getFont().deriveFont(30f));
	    g.drawString(", "+currentMonth+" "+currentYear, caret, heightTopArea-insets);

//	    g.setColor(Color.BLUE);
//	    g.drawRect(0, 80, getWidth(), 19);
	    
	    // updates position of sub-components
	    scroll.setBounds(insets, heightTopArea, getWidth()-insets*2, getHeight()-heightTopArea-insets);
	    dayCard.setBounds(dayCard.getX(), dayCard.getY(), dayCard.getParent().getWidth()-50, 20*48);	    	
	    
	    /**
	     * Count how many events there are in each overlapping sequence.
	     * Store sequences in seq, so that each event will know how many other
	     * events there will be in its sequence, and its index in the sequence.
	     */
		calculateOvelappingSequences();
		calculateOvelappingSequencesEqualTop();

	    /**
	     * Save in each event its index in the sequence and n events in the sequence.
	     */
	    calculateOverlappingIndexes();
	    calculateOvelappingIndexesEqualTop();

	    // paint events
	    for (DefaultEventComponent eee : dayCard.getEvents()) {
	    	if (eee.isSelected()) eee.requestFocusInWindow();
	    	
	    	// default painting
	    	eee.setBounds(50,eee.getY(), dayCard.getWidth(), eee.getHeight()); 

	    	// paint according to the sequence index
	    	if (eee.overlappingEventsInSequence > 0) {
    			eee.setBounds(eee.getX() + (eee.overlappingLocalIndex*10), eee.getY(), dayCard.getWidth() - ((eee.overlappingEventsInSequence-1) * 10), eee.getHeight());
    			if (eee.equalTopEvents > 0)
	    			eee.setBounds(eee.getX() + (eee.overlappingTopLocalIndex * (dayCard.getWidth() / eee.overlappingTopEventsInSequence)) - (eee.overlappingLocalIndex*10), eee.getY(),
	    							(dayCard.getWidth() / eee.overlappingTopEventsInSequence), eee.getHeight());
	    	}
	    } // end paint events

	    yesterdayButton.setBounds(getWidth()-insets-162, heightTopArea-26-insets, 30, 30);
		today.setBounds(getWidth()-insets-131, heightTopArea-26-insets, 100, 30);
		tomorrowButton.setBounds(getWidth()-insets-30, heightTopArea-26-insets, 30, 30);
		undoB.setBounds(getWidth()-insets-372, heightTopArea-26-insets, 100, 30);
		redoB.setBounds(getWidth()-insets-271, heightTopArea-26-insets, 100, 30);
		validate();
	}

    /**
     * Count how many events there are in each overlapping sequence.
     * Store sequences in seq, so that each event will know how many other
     * events there will be in its sequence, and its index in the sequence.
     */
	private void calculateOvelappingSequences() {
		if (dayCard.getEvents().size() < 2) return;
		seq.clear();
		int i = 0, k = 0;
	    DefaultEventComponent e;
	    for (int j = 0; j < dayCard.getEvents().size(); j++) {
	    	e = dayCard.getEvents().get(j);
	    	
	    	// first index
	    	if (j == 0 && e.getNeighbour() > 0) k++;

	    	// middle of array
	    	if (j > 0 && j < dayCard.getEvents().size() - 1) {
	    		if (e.getNeighbour() == 0) { // single event, no overlapping
	    			if (dayCard.getEvents().get(j-1).getNeighbour() > 0) {
	    				seq.add(i++, k); k = 0;
	    			}
	    		} else { // overlapping
					if (e.getTop() >= dayCard.getEvents().get(j-1).getBottom() && dayCard.getEvents().get(j-1).getNeighbour() > 0) {
	    				seq.add(i++, k); k = 1;
					} else { k++; }
	    		}
	    	}

	    	// last index
	    	if (j == dayCard.getEvents().size() - 1) {
	    		if (e.getNeighbour() == 0) {// single event, no overlapping
	    			if (dayCard.getEvents().get(j-1).getNeighbour() > 0) {
	    				seq.add(i, k);
	    			}
	    		} else { seq.add(i, ++k); }
	    	}
	    } // for loop
	}

    /**
     * Count how many events with the same top there are in each overlapping sequence.
     */
	private void calculateOvelappingSequencesEqualTop() {
		if (dayCard.getEvents().size() < 2) return;
		seqEqualTop.clear();
		int i = 0, k = 0;
	    DefaultEventComponent e;
	    for (int j = 0; j < dayCard.getEvents().size(); j++) {
	    	e = dayCard.getEvents().get(j);

	    	// first index
	    	if (j == 0 && e.equalTopEvents > 0) k++;

	    	// middle of array
	    	if (j > 0 && j < dayCard.getEvents().size() - 1) {
	    		if (e.equalTopEvents == 0) { // single event, no overlapping
	    			if (dayCard.getEvents().get(j-1).equalTopEvents > 0) {
	    				seqEqualTop.add(i++, k);
	    				k = 0;
	    			}
	    		} else { // overlapping
					if (e.getTop() >= dayCard.getEvents().get(j-1).getBottom() && dayCard.getEvents().get(j-1).equalTopEvents > 0) {
						seqEqualTop.add(i++, k); k = 1;
					} else { k++; }
	    		}
	    	}

	    	// last index
	    	if (j == dayCard.getEvents().size() - 1) {
	    		if (e.equalTopEvents == 0) {// single event, no overlapping
	    			if (dayCard.getEvents().get(j-1).equalTopEvents > 0) {
	    				seqEqualTop.add(i, k);
	    			}
	    		} else { seqEqualTop.add(i, ++k); }
	    	}
	    }
	}

    /**
     * Save in each event its index in the sequence and n events in the sequence.
     */
	private void calculateOverlappingIndexes() {
		int i, k; i = k = 0;
	    DefaultEventComponent e;
	    for (int j = 0; j < dayCard.getEvents().size(); j++) {
	    	e = dayCard.getEvents().get(j);

	    	if (j < dayCard.getEvents().size() - 1 ) {
	    		if (e.getNeighbour() > 0) {
	    			e.overlappingEventsInSequence = seq.get(i);
	    			e.overlappingLocalIndex = k++;
	    			if (k == seq.get(i)) {
	    				k = 0; i++;
	    			}
	    		}
	    	}
	    	
	    	if (j == dayCard.getEvents().size() - 1) {
	    		if (e.getNeighbour() > 0) {
	    			e.overlappingEventsInSequence = seq.get(i);
	    			e.overlappingLocalIndex = k;
	    		}	    		
	    	}
	    } // for loop
	}

    /**
     * Save in each event its index in the sequence and n events in the sequence
     * if they have the same top.
     */
	private void calculateOvelappingIndexesEqualTop() {
		int i, k; i = k = 0;
	    DefaultEventComponent e;
	    for (int j = 0; j < dayCard.getEvents().size(); j++) {
	    	e = dayCard.getEvents().get(j);

	    	if (j < dayCard.getEvents().size() - 1 ) {
	    		if (e.equalTopEvents > 0) {
	    			e.overlappingTopEventsInSequence = seqEqualTop.get(i);
	    			e.overlappingTopLocalIndex = k++;
	    			if (k == seqEqualTop.get(i)) {
	    				k = 0; i++;
	    			}
	    		}
	    	}
	    	
	    	if (j == dayCard.getEvents().size() - 1) {
	    		if (e.equalTopEvents > 0) {
	    			e.overlappingTopEventsInSequence = seq.get(i);
	    			e.overlappingTopLocalIndex = k;
	    		}	    		
	    	}
	    } // for loop		
	}

	/**
	 * @return the currentNavigationDay
	 */
	public Calendar getCurrentNavigationDay() {
		return currentNavigationDay;
	}
}