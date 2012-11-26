package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.JTextField;

/**
 * EventTitleField addds support for editing the title of an event.
 * @author Paolo Boschini
 *
 */
@SuppressWarnings("serial")
public class EventTitleField extends JTextField {
	public EventTitleField(String name) {
		super(name);
		setFont(new Font("Lucida Grande", Font.PLAIN, 11));
		setForeground(Color.WHITE);
		setBackground(new Color(0.0f,0.0f,0.0f,0.0f));
		setBorder(BorderFactory.createLineBorder(Color.white, 0));
		setCaretColor(Color.WHITE);
		setSelectedTextColor(Color.WHITE);
		setSelectionColor(new Color(0.2f,0.2f,0.2f,1.0f));
		setBounds(7, 1, 200, 18);
		addKeyListener(new KeyListener() {			
			public void keyTyped(KeyEvent e) {}
			public void keyReleased(KeyEvent e) {}
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					getParent().requestFocusInWindow();
					((DefaultEventComponent)getParent()).setEventTitle(getText());
				}
			}
		});
	}
}