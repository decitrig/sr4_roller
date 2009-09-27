package net.decitrig.sr4.gui;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.*;
import java.util.EventObject;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;

public class RollTable extends JTable {
	private static int[] colWidths = new int[] { 150, 30, 50 };
	private final RollTableModel model;

	public RollTable(RollTableModel rtm) {
		super(rtm);
		model = rtm;
		initColumns();
		getTableHeader().setResizingAllowed(false);
		getTableHeader().setReorderingAllowed(false);
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}
	
	@Override
	public Dimension getPreferredScrollableViewportSize() {
		int width = getColumnModel().getTotalColumnWidth();
		int height = model.getRowCount() * getRowHeight();
		height = Math.max(100, height);
		return new Dimension(width, height);
	}

	private void initColumns() {
		for (int i = 0; i < model.getColumnCount(); i++) {
			TableColumn col = getColumnModel().getColumn(i);
			col.setMinWidth(colWidths[i]);
			col.setMaxWidth(colWidths[i]);
		}

		TableColumn poolColumn = getColumn("Pool");
		poolColumn.setCellEditor(new PoolEditor());
	}

	private class PoolEditor extends AbstractCellEditor implements
			TableCellEditor {
		JSpinner spin;

		public PoolEditor() {
			spin = new JSpinner();
			spin.addMouseWheelListener(new MouseWheelListener() {
				@Override
				public void mouseWheelMoved(MouseWheelEvent e) {
					JSpinner src = (JSpinner) (e.getSource());
					int val = (Integer) (src.getValue());
					src.setValue(val - e.getWheelRotation());
				}
			});
		}

		@Override
		public boolean isCellEditable(EventObject e) {
			if (!(e instanceof MouseEvent))
				return false;
			MouseEvent mevt = (MouseEvent) e;
			return mevt.getClickCount() == 2;
		}

		@Override
		public Object getCellEditorValue() {
			return spin.getValue();
		}

		@Override
		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			spin.setValue(value);
			return spin;
		}
	}
}
