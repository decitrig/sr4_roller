package net.decitrig.sr4.gui;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import net.decitrig.sr4.tests.ExtendedTest;
import net.decitrig.sr4.tests.SR4Test;

public class RollTableModel extends AbstractTableModel {
	private static final String LAST_TEST_NAME = "previous";

	private String[] colNames = new String[] { "Name", "Ext", "Pool" };

	private ArrayList<SR4Test> list = new ArrayList<SR4Test>();
	SR4Test lastTest;

	public void saveTest(SR4Test test) {
		list.add(test);
		int idx = list.indexOf(test) + 1;
		fireTableRowsInserted(idx, idx);
	}

	public void deleteTest(int idx) {
		if (idx == 0)
			return;
		list.remove(idx - 1);
		fireTableRowsDeleted(idx, idx);
	}

	@Override
	public int getColumnCount() {
		return colNames.length;
	}

	@Override
	public int getRowCount() {
		return list.size() + ((lastTest == null) ? 0 : 1); // include last test;
	}

	@Override
	public Object getValueAt(int row, int col) {
		SR4Test t = (row == 0) ? lastTest : list.get(row - 1);
		switch (col) {
		case 0:
			return t.getName();
		case 1:
			return (Boolean) (t instanceof ExtendedTest);
		case 2:
			return (Integer) (t.getPool());
		default:
			throw new IndexOutOfBoundsException("Invalid column number: " + col);
		}
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return String.class;
		case 1:
			return Boolean.class;
		case 2:
			return Integer.class;
		default:
			throw new IndexOutOfBoundsException("Invalid column number: "
					+ columnIndex);
		}
	}

	@Override
	public String getColumnName(int column) {
		return colNames[column];
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex == 0;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		SR4Test test;
		if (columnIndex == 0) {
			if (rowIndex == 0) {
				// make a copy of the previous test, instead of overwriting
				SR4Test newTest = lastTest.copyAndReroll();
				newTest.setName((String) aValue);
				saveTest(newTest);
			} else {
				test = list.get(rowIndex - 1);
				test.setName((String) aValue);
			}
		} else {
			return;
		}
	}

	public void setLastTest(SR4Test test) {
		test.setName(LAST_TEST_NAME);
		boolean wasNull = (lastTest == null);// if true, need to fire
												// RowsInserted
		lastTest = test;
		if (wasNull)
			fireTableRowsInserted(0, 0);
		else
			fireTableRowsUpdated(0, 0);
	}

	public SR4Test loadTest(int row) {
		SR4Test t;
		if (row == 0) {
			t = lastTest.copyAndReroll();
		} else {
			t = list.get(row-1).copyAndReroll();
		}
		t.setThres(0);
		return t;
	}
}
