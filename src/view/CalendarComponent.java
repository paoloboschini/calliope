package view;

import java.awt.geom.Point2D;

import javax.swing.JComponent;

/**
 * Abstract class that defines methods for calendar components. 
 *
 * @author Paolo Boschini
 * @author Marcus Ihlar
 *
 */
@SuppressWarnings("serial")
public abstract class CalendarComponent extends JComponent {
	
	/**
	 * Sets an extent relative to the parent components size. The extent is specified as floats ranging from 0 to 1.
	 * 
	 * @param width The width of the component relative to its parent.
	 * @param height The height of the component relative to its parent.
	 */
	public abstract void setExtent(float width, float height);
	
	/**
	 * Returns the size of the component relative to its parent.
	 * @return A float pair representing width and height.
	 */
	public abstract Point2D.Float getExtent();
	
	/**
	 * Sets the top left position of the component within the bounds of the parent component.
	 * 
	 * @param x The horizontal value of the coordinate point representing the top left position of the component.
	 * @param y The vertical value of the coordinate point representing the top left position of the component.
	 */
	public abstract void setTopLeft(float x, float y);
	
	/**
	 * Sets the bottom left position of the component within the bounds of the parent component.
	 * 
	 * @param x The horizontal value of the coordinate point representing the bottom left position of the component.
	 * @param y The vertical value of the coordinate point representing the bottom left position of the component.
	 */
	public abstract void setBottomLeft(float x, float y);
	/**
	 * Sets the bottom right position of the component within the bounds of the parent component.
	 * 
	 * @param x The horizontal value of the coordinate point representing the bottom right position of the component.
	 * @param y The vertical value of the coordinate point representing the bottom right position of the component.
	 */
	public abstract void setBottomRight(float x, float y);
	/**
	 * Sets the top right position of the component within the bounds of the parent component.
	 * 
	 * @param x The horizontal value of the coordinate point representing the top right position of the component.
	 * @param y The vertical value of the coordinate point representing the top right position of the component.
	 */
	public abstract void setTopRight(float x, float y);
	
	/**
	 * Centers the component within the bounds of the parent component.
	 * 
	 * @param x The horizontal value of the coordinate which represents the center of the parent component.
	 * @param y The vertical value of the coordinate which represents the center of the parent component.
	 */
	public abstract void setCenter(float x, float y);
	
	/**
	 * Sets the top position of a component within the bounds of its parent component without changing its horizontal position.
	 * 
	 * @param y The vertical value of the coordinate which represents the top of the parent component.
	 */
	public abstract void setTop(float y);
	/**
	 * Sets the bottom position of a component within the bounds of its parent component without changing its horizontal position.
	 * 
	 * @param y The vertical value of the coordinate which represents the bottom of the parent component.
	 */
	public abstract void setBottom(float y);
	/**
	 * Sets the left position of a component within the bounds of its parent component without changing its vertical position.
	 * 
	 * @param x The horizontal value of the coordinate which represents the left side of the parent component.
	 */
	public abstract void setLeft(float x);
	/**
	 * Sets the right position of a component within the bounds of its parent component without changing its vertical position.
	 * 
	 * @param x The horizontal value of the coordinate which represents the right side of the parent component.
	 */
	public abstract void setRight(float x);
	/**
	 * Sets a value by which sub components and graphics should be rescaled.
	 * @param f A value which the component uses to re scale its graphics.  
	 */
	public abstract void setScalar(float f);
	
	//public abstract void smoothResize(Dimension intitialSize, Dimension finalSize); TODO: Animation engine...
	/**
	 * Sets the component to an expanded state. What an expanded state means is implementation specific.
	 */
	public abstract void expand();
	/**
	 * Sets the component to a collapsed state. What a collapsed state means is implementation specific.
	 */
	public abstract void collapse();
}
