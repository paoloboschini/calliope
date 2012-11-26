package controller;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import model.DataEvent;
/**
 * This class specifies Undo/Redo behavior for adding events.
 * 
 * @author Marcus Ihlar
 *
 */
@SuppressWarnings("serial")
public class AddEventEdit extends AbstractUndoableEdit {
	
	private DataEvent newEvent;
	/**
	 * Adds the newly created event so that it can be handled by the undo / redo callback methods.
	 * @param newEvent
	 */
	public AddEventEdit(DataEvent newEvent){
		this.newEvent = newEvent;
	}
	/**
	 * Undo will remove the added event.
	 */
	public void undo() throws CannotUndoException {
		super.undo();
		Main.getInstance().getEventHandler().removeEventU(newEvent);
	}
	/**
	 * Redo will re add the event that was removed by undo.
	 */
	public void redo() throws CannotRedoException {
		super.redo();
		Main.getInstance().getEventHandler().addEventU(newEvent);
	}
	
}
