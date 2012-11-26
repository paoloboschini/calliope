package controller;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.undo.UndoManager;

import model.DataBridge;
import model.DataModel;
import model.DefaultDataModel;
import model.InternalDataModel;
import model.XMLHandler;
import view.AnimationEngine;
import view.CalendarContainer;

public class Main {

	private JFrame mainFrame;
	private CalendarContainer calendarContainer;
	
	private AnimationEngine animationEngine;
	
	private DataModel model;
	private DataBridge bridge;
	private EventHandler eventHandler;
	private UndoManager undoManager;
	private int topMargin = 100;

	private static Main instance;

	public static Main getInstance(){
		if (null == instance)
			instance = new Main();
		return instance;
	}

	private Main() {}

	public DataModel getModel() {
		return model;
	}

	public EventHandler getEventHandler(){
		return eventHandler;
	}

	public UndoManager getUndoManager(){
		return undoManager;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Main.getInstance().init();		
	}

	private void init() {
		InternalDataModel im = new DefaultDataModel();
		animationEngine = new AnimationEngine(60);
		undoManager = new UndoManager();
		model = im;
		bridge = new DataBridge(im, new XMLHandler("data/events.xml"));
		eventHandler = bridge;

		mainFrame = new JFrame("Calliope");
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		mainFrame.setSize(Toolkit.getDefaultToolkit().getScreenSize());
		
		mainFrame.setVisible(true);

		calendarContainer = new CalendarContainer(new Color(250,250,250),
				topMargin ,
				mainFrame.getContentPane().getWidth(),
				mainFrame.getContentPane().getHeight());

		mainFrame.getContentPane().add(calendarContainer);
		mainFrame.getContentPane().addComponentListener(new WindowEventHandler());
		mainFrame.addKeyListener(new GlobalKeyListener());
		calendarContainer.updateDisplayedEvents();
		mainFrame.validate();
	}

	private class WindowEventHandler implements ComponentListener {
		@Override
		public void componentHidden(ComponentEvent arg0) {}
		@Override
		public void componentMoved(ComponentEvent arg0) {}
		@Override
		public void componentResized(ComponentEvent arg0) {
			mainFrame.validate();
		}
		@Override
		public void componentShown(ComponentEvent arg0) {}
	}
	
	private class GlobalKeyListener implements KeyListener{
				
		@Override
		public void keyPressed(KeyEvent ke) {
			System.out.println(ke.getKeyChar());
		}

		@Override
		public void keyReleased(KeyEvent ke) {
			if(ke.isMetaDown()){
				System.out.println("META!");
				if(ke.isShiftDown())
					if (ke.getKeyCode() == KeyEvent.VK_Z)
						undoManager.redo();
				else {
					if (ke.getKeyCode() == KeyEvent.VK_Z)
						undoManager.undo();
				}
			}
		}

		@Override
		public void keyTyped(KeyEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public AnimationEngine getAnimationEngine() {
		return animationEngine;
	}
	
	public CalendarContainer getCalendarContainer() {
		return calendarContainer;
	}
}
