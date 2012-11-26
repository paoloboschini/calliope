package view;
/**
 * Interface specification for animateable GUI components.<br>
 * Animateable objects are process by the <a href=AnimationEngine>AnimationEngine</a>.  
 * @author marcus
 *
 */
public interface Animateable {
	/**
	 * Returns an integer representing the speed of the animation.<br>
	 * If 1 is returned the animation speed will be the same as the FPS rate in the <a href=AnimationEngine>AnimationEngine</a>.
	 * 2 will run the animation at half the FPS rate etc. 
	 * @return The animation speed.
	 */
	public int getSpeed();
	/**
	 * Returns the length of the animation as a fraction of a second. If 2 is returned the animateable object will be processed
	 * for two seconds and half a second if 0.5 is returned etc.
	 * @return Duration of the animation as fraction of a second.
	 */
	public float getDuration();
	/**
	 * This is the animation callback, all animation work is performed here. 
	 * This method called from the animation engine at the intervals specified
	 * by getSpeed().
	 * 
	 */
	public void update();
	/**
	 * This method is called when the animation is finished, can be useful if some cleanup is needed.
	 */
	public void finished();
	
}
