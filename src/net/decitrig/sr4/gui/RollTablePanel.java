package net.decitrig.sr4.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

import javax.swing.*;

import net.decitrig.sr4.tests.SR4Test;
import net.decitrig.sr4.tests.SuccessTest;

public class RollTablePanel {
	public static final String LOAD_CMD = "LOADTEST";
	public static final String SAVE_CMD = "SAVETEST";

	private RollTableModel model = new RollTableModel();
	private RollTable table = new RollTable(model);
	
	private RollController controller;
	
	public RollTablePanel() {
		controller = RollController.getController();
		controller.setRollTablePanel(this);
	}

	public SR4Test getSelectedTest() {
		if(table.getSelectedRowCount() == 0)
			if(model.getRowCount() > 0){
				return model.loadTest(0);
			} else {
				return new SuccessTest(0,0);
			}
		int row = table.getSelectedRow();
		return model.loadTest(row);
	}

	public JPanel getTablePanel() {
		JPanel panel = new JPanel(new BorderLayout());
		
		JButton loadButton = new JButton("Load");
		loadButton.setActionCommand(LOAD_CMD);
		loadButton.addActionListener(controller.getListener());
		
		JButton saveButton = new JButton("Save");
		saveButton.setActionCommand(SAVE_CMD);
		saveButton.addActionListener(controller.getListener());
		
		panel.add(new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.CENTER);
		
		JPanel southPanel = new JPanel(new GridLayout(1,2));
		southPanel.add(loadButton);
		southPanel.add(saveButton);
		panel.add(southPanel, BorderLayout.SOUTH);
		return panel;
	}

	public void setLastTest(SR4Test test) {
		model.setLastTest(test);
	}

	public void saveTest(SR4Test test) {
		model.saveTest(test);
	}
}
