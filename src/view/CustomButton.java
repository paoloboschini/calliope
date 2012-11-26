package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.ButtonModel;
import javax.swing.JButton;

/**
 * Paints a custom button.
 * @author Paolo Boschini
 *
 */
@SuppressWarnings("serial")
public class CustomButton extends JButton {

	private Color defaultColor = new Color(200,200,200);
	private Color hoverColor = new Color(150,150,150);
	private Color pressedColor = new Color(100,100,100);
	private Color fillColor = new Color(250,250,250);
	
	/**
	 * Returns a new button.
	 * @param text the text of the button.
	 */
	public CustomButton(String text) {
		super();
		setText(text);
		setBorder(null);
		setContentAreaFilled(false);
		setBorderPainted(false);
		setFont(new Font("Helvetica", Font.BOLD, 14));
		setForeground(new Color(120,120,120));
		setForeground(Color.BLACK);
		setFocusable(false);
		setRolloverEnabled(true);
	}

	/**
	 * Returns a new button without any text.
	 */
	public CustomButton() {
		this("");
	}

	/**
	 * Paints the custom graphics for the button.
	 */
	public void paintComponent(Graphics gg) {
		Graphics2D g = (Graphics2D)gg;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		int h = getHeight();
		int w = getWidth();

		g.setColor(fillColor);
		g.fillRoundRect(0, 0, w - 1, h - 3, 10, 10);

		ButtonModel model = getModel();

		if (!model.isPressed()) {
			if (model.isRollover()) {
				setForeground(hoverColor);
				g.setColor(hoverColor);
			} else {
				setForeground(defaultColor);
				g.setColor(defaultColor);
			}
		}

		if (model.isPressed()) {
			setForeground(pressedColor);
			g.setColor(pressedColor);
		} else {
			if (!model.isRollover()) {
				setForeground(defaultColor);
				g.setColor(defaultColor);
			}
		}

		g.drawRoundRect(0, 0, w - 1, h - 3, 10, 10);
		super.paintComponent(gg);
	}
}