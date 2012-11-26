package controller;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import model.DataEvent;
/**
 * This class handles Undo/Redo behavior for changes in existing events.
 * @author Marcus Ihlar
 *
 */
@SuppressWarnings("serial")
public class ChangeEventEdit extends AbstractUndoableEdit {

	private DataEvent newEvent, oldEvent;
	/**
	 * Takes two events one with the new state and the other with the old state. <br>
	 * Events are atomic in the sense that changes to an event will result in a new event object
	 * being created. This has the nice effect that this simple class will handle all kinds of changes
	 * to events by simply storing the old event and the new event with the changed state.
	 * 
	 * @param newEvent The Event with the new state.
	 * @param oldEvent The Event with unchanged state.
	 */
	public ChangeEventEdit(DataEvent newEvent, DataEvent oldEvent){
		this.newEvent = newEvent;
		this.oldEvent = oldEvent;
	}
	/**
	 * Replaces the event with the updated state with the old one. 
	 */
	public void undo() throws CannotUndoException {
		super.undo();
		Main.getInstance().getEventHandler().removeEventU(newEvent);
		Main.getInstance().getEventHandler().addEventU(oldEvent);
	}
	/**
	 * Replaces the old event with the one that has updated state.
	 */
	public void redo() throws CannotRedoException {
		super.redo();
		Main.getInstance().getEventHandler().removeEventU(oldEvent);
		Main.getInstance().getEventHandler().addEventU(newEvent);
	}
}
