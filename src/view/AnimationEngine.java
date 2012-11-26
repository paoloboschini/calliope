package view;

import java.util.HashMap;
import java.util.Iterator;

/**
 * 
 * This class handles all animations in the GUI system. It processes Animateable objects in an animation thread.<br>
 * This class is thread safe.
 * @author Marcus Ihlar, Paolo Boschini
 *
 */
public class AnimationEngine implements Runnable {
	private boolean running;
	private HashMap<Animateable, Integer> objects;
	
	private int FPS;
	/**
	 * Initializes the engine with a specified Frames Per Second value, this will determine the animation-speed.
	 * @param FPS
	 */
	public AnimationEngine(int FPS) {
		this.FPS = FPS;
		objects = new HashMap<Animateable, Integer>();
	}
	/**
	 * Updates all Animateable objects at the FPS rate.
	 */
	@Override
	public void run() {
		
		while (! objects.isEmpty()){
			try {
				running = true;
				updateObjects();
				Thread.sleep(1000/FPS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		running = false;
	}
	/*
	 * Objects are updated here by iterating over the key set of the objects hash map.
	 * Each Animateable in the objects HashMap is associated with a value that is incremented
	 * each time this method is called. If this value modulo the speed of the Animateable is = 0 the update() method of the 
	 * Animateable is called. When the incremented value is equal to the duration of the Animateable the Animateable is removed
	 * from the objects HashMap and the finished() method is called.
	 * Access to the objects HashMap is thread safe.
	 */
	private void updateObjects(){
		synchronized(objects){
			Iterator<Animateable> iter;
			iter = objects.keySet().iterator();
			Animateable a;
			int a_timer;
			
			while (iter.hasNext()){
				a = iter.next();
				a_timer = objects.get(a);
				if (a_timer % a.getSpeed() == 0)
					a.update();
				objects.put(a, a_timer + 1);
				if (a_timer >= a.getDuration()*FPS){
					iter.remove();
					a.finished();
				}
			}
			
		}
	}
	/**
	 * 
	 * @return The Frames Per Second value at which the animation engine runs.
	 */
	public int getFPS() {
		return FPS;
	}
	/**
	 * Adds an Animateable object to the animation queue. If the queue is empty
	 * a new animation thread will be spawned. If it's not empty it will simply be 
	 * added to the queue and be processed by the current running thread. <br>
	 * When the queue is empty the thread will be killed.
	 * @param object
	 */
	public void addAnimationObject(Animateable object){
		synchronized(objects){
			objects.put(object, 0);
		}
		if (! running){
			new Thread(this).start();
			running = true;
		}
	}
}
