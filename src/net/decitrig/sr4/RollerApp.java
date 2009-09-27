package net.decitrig.sr4;

import java.awt.*;
import javax.swing.*;

import net.decitrig.sr4.gui.RollTablePanel;
import net.decitrig.sr4.gui.RollerPanel;

public class RollerApp {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				createAndShow();
			}
		});
	}

	private static void createAndShow() {
		JFrame frame = new JFrame("sliderule's dice roller");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setResizable(false);
		RollerPanel rp = new RollerPanel();
		RollTablePanel rtp = new RollTablePanel();
		frame.add(rp.getRollerPanel(), BorderLayout.CENTER);
		frame.add(rtp.getTablePanel(), BorderLayout.EAST);
		frame.pack();

		Dimension screenDim = multiDsiplayCenter();
		Dimension frameDim = frame.getSize();
		frame.setLocation(screenDim.width / 2 - frameDim.width / 2,
							screenDim.height / 2 - frameDim.height / 2);
		frame.setVisible(true);
	}

	private static Dimension multiDsiplayCenter() {
		// attempt to center the app on monitor 1
		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		DisplayMode mode = ge.getScreenDevices()[0].getDisplayMode();
		Dimension screenDim = new Dimension(mode.getWidth(), mode
				.getHeight());
		return screenDim;
	}
}
