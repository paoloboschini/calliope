package controller;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import model.DataEvent;
/**
 * This class contains Undo/Redo behavior for removing events.
 * @author Marcus Ihlar
 */
@SuppressWarnings("serial")
public class RemoveEventEdit extends AbstractUndoableEdit {
	
	private DataEvent oldEvent;
	/**
	 * Stores the event that has been removed.
	 * @param oldEvent
	 */
	public RemoveEventEdit(DataEvent oldEvent){
		this.oldEvent = oldEvent;
	}
	/**
	 * Re-adds the removed event.
	 */
	public void undo() throws CannotUndoException {
		super.undo();
		Main.getInstance().getEventHandler().addEventU(oldEvent);
	}
	/**
	 * Removes the event that was re-added.
	 */
	public void redo() throws CannotRedoException {
		super.redo();
		Main.getInstance().getEventHandler().removeEventU(oldEvent);
	}
	
}
