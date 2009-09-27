package net.decitrig.sr4.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import net.decitrig.sr4.tests.*;

public class RollController {
	private static RollController instance;
	private static ActionListener listener;
	private SR4Test test;
	RollerPanel rp;
	RollTablePanel rtp;

	public static RollController getController() {
		if (instance == null)
			instance = new RollController();
		return instance;
	}

	public ActionListener getListener() {
		if (listener == null)
			listener = new ControlListener();
		return listener;
	}
	
	public void setRollerPanel(RollerPanel rp){
		this.rp = rp;
	}
	public void setRollTablePanel(RollTablePanel rtp){
		this.rtp = rtp;
	}

	private RollController() {
	}

	public double getSuccessProb() {
		return RollerMath.successProb(test.getPool(),
										test.getThres());
	}

	public double getGlitchProb() {
		return RollerMath.glitchProb(test.getPool());
	}

	public double getCritProb() {
		return RollerMath.critProb(test.getPool());
	}

	public int getExpected() {
		return RollerMath.expectedHits(test.getPool());
	}

	private class ControlListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getActionCommand().equals(RollerPanel.ROLL_ACTION_CMD)){
				rp.doTest(test);
				rtp.setLastTest(test);
				test = test.copyAndReroll();
			} else if (e.getActionCommand().equals(RollTablePanel.LOAD_CMD)){
				setTest(rtp.getSelectedTest());
				rp.loadTest(test);
			} else if (e.getActionCommand().equals(RollTablePanel.SAVE_CMD)){
				String name = JOptionPane.showInputDialog("Name:");
				test.setName(name);
				rtp.saveTest(test);
			}
		}
	}

	public int getEighty() {
		return RollerMath.findEighty(test.getPool());
	}

	public void setTest(SR4Test test) {
		this.test = test;
		rp.updateStats();
	}
}
