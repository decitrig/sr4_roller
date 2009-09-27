package net.decitrig.sr4.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.Hashtable;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.decitrig.sr4.tests.*;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class RollerPanel {
	/**
	 * Class to set up common {@link JTextField} options, includes two
	 * convenience methods for common operations.
	 * 
	 * @author sliderule
	 * 
	 */
	private class InfoField extends JTextField {

		public InfoField() {
			setHorizontalAlignment(RIGHT);
			setEditable(false);
			setOpaque(true);
		}

		/**
		 * Used for the net hits field, colors the field red if it represents a
		 * failure
		 * 
		 * @param fail
		 *            if <code>true</code>, set background red
		 */
		public void setFail(boolean fail) {
			setBackground(fail ? Color.red : null);
		}

		/**
		 * Used to display glitch status, colors the field's background
		 * according to the current glitch.
		 * 
		 * @param g
		 *            {@link Glitch} status of current roll.
		 */
		public void setGlitch(Glitch g) {
			setText(g.toString());
			switch (g) {
			case Crit:
				setBackground(Color.red);
				break;
			case Glitch:
				setBackground(Color.orange);
				break;
			default:
				setBackground(null);
				break;
			}

		}
	}

	/**
	 * Class with a specialized constructor for setting initial values and a
	 * convenience method for setting the probability.
	 * 
	 * @author sliderule
	 * 
	 */
	private class StatBar extends JProgressBar {
		public StatBar() {
			setMaximum(100);
			setMinimum(0);
			setValue(0);
			setStringPainted(true);
			setProb(0.0);
		}

		/**
		 * Makes the bar display a given probability in percentage form.
		 * 
		 * @param p
		 *            probability to display
		 */
		public void setProb(double p) {
			setValue((int) (100 * p));
			setString(String.format("%.3f%%", p * 100));
		}
	}

	private static final int POOL_MAX = 100;
	public static final String ROLL_ACTION_CMD = "DOROLL";

	private static JLabel makeLabel(String s) {
		final Font labFont = new Font("Sans", Font.PLAIN, 12);
		JLabel temp = new JLabel("<html><u>" + s + "</u></html>");
		temp.setFont(labFont);
		return temp;
	}

	// dice pool components
	private JSpinner poolSpinner = new JSpinner();
	private InfoField expectField = new InfoField();
	private JSlider targetSlider = new JSlider();

	// stat components
	private StatBar successBar = new StatBar();
	private StatBar glitchBar = new StatBar();
	private StatBar critBar = new StatBar();
	// roll components
	private InfoField hitsField = new InfoField();
	private InfoField netHitField = new InfoField();

	private InfoField glitchField = new InfoField();
	private JLabel rollLabel;

	private InfoField rollField = new InfoField();

	private JCheckBox testToggle = new JCheckBox();

	private JButton rollTestButton = new JButton();

	private JPanel rollerPanel;

	private Runnable updateRunner;
	private final RollController controller;

	public RollerPanel() {
		controller = RollController.getController();
		controller.setRollerPanel(this);
		updateRunner = new Runnable() {
			@Override
			public void run() {
				setRoll();
			}
		};
	}

	public JPanel getRollerPanel() {
		initRollerPanel();
		return rollerPanel;
	}

	public SR4Test getTest() {
		int pool = (Integer) poolSpinner.getValue();
		int thres = targetSlider.getValue();
		boolean extended = testToggle.isSelected();
		if (extended) {
			return new ExtendedTest(pool, thres);
		} else {
			return new SuccessTest(pool, thres);
		}
	}

	public void initRollerPanel() {

		FormLayout layout = new FormLayout(
				"5dlu, right:pref, $lcgap, fill:pref, 8dlu, right:pref, $lcgap, pref",
				"p, $lgap, p, $lgap, top:p, $pgap," // dice pool
						+ "p, $lgap, p, $lgap, p, $lgap, p, $pgap," // stats
						+ "p, $lgap, p, $lgap, p, $lgap, p, $pgap, p"); // rolls

		int labelCol1 = 2;
		int labelCol2 = 6;
		int compCol1 = 4;
		int compCol2 = 8;
		int row = 1;

		layout.setColumnGroups(new int[][] { { labelCol1, labelCol2 } });

		initComponents();
		initListeners();
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		CellConstraints labelcc = new CellConstraints();
		builder.setDefaultDialogBorder();
		builder.addSeparator("Dice Pool", cc.xyw(1, row, 8));
		row += 2;
		builder.add(makeLabel("Pool:"), labelcc.xy(labelCol1, row),
					poolSpinner, cc.xy(4, row));
		builder.add(makeLabel("Expected:"), labelcc.xy(labelCol2, row),
					expectField, cc.xy(compCol2, row));
		row += 2;
		builder.add(makeLabel("Threshold:"), labelcc.xy(labelCol1, row),
					targetSlider, cc.xyw(compCol1, row, 5));
		row += 2;
		builder.addSeparator("Statistics", cc.xyw(1, row, 8));
		row += 2;
		builder.add(makeLabel("Success:"), labelcc.xy(labelCol1, row),
					successBar, cc.xyw(compCol1, row, 5));
		row += 2;
		builder.add(makeLabel("Glitch:"), labelcc.xy(labelCol1, row),
					glitchBar, cc.xyw(compCol1, row, 5));
		row += 2;
		builder.add(makeLabel("Crit:"), labelcc.xy(labelCol1, row), critBar, cc
				.xyw(compCol1, row, 5));
		row += 2;
		builder.addSeparator("Test Results", cc.xyw(1, row, 8));
		row += 2;
		builder.add(makeLabel("Hits:"), labelcc.xy(labelCol1, row), hitsField,
					cc.xy(compCol1, row));
		builder.add(makeLabel("Net hits:"), labelcc.xy(labelCol2, row),
					netHitField, cc.xy(compCol2, row));
		row += 2;
		builder.add(makeLabel("Glitch:"), labelcc.xy(labelCol1, row),
					glitchField, cc.xy(compCol1, row));
		builder.add(rollLabel, labelcc.xy(labelCol2, row), rollField, cc
				.xy(compCol2, row));
		row += 2;
		builder.add(testToggle, cc.xyw(compCol1, row, 5));
		row += 2;
		builder.add(rollTestButton, cc.xyw(1, row, 8));

		(new Thread(updateRunner)).start();
		this.rollerPanel = builder.getPanel();
	}

	public void loadTest(SR4Test test) {
		poolSpinner.setValue(test.getPool());
		targetSlider.setValue(0);
	}

	/**
	 * Depending on the value of <code>ext</code>, runs either a success or
	 * extended test with the specified pool & threshold.
	 * 
	 * @param pool
	 *            number of dice in the current pool
	 * @param thres
	 *            threshold to beat
	 * @param ext
	 *            if <code>true</code> run an extended test, otherwise run a
	 *            success test.
	 */
	public void doTest(SR4Test test) {
		int hits = test.getHits();
		int thres = test.getThres();
		hitsField.setText(String.format("%d", hits));
		netHitField.setText(String.format("%d", hits - thres));
		glitchField.setGlitch(test.getGlitch());
		if (test instanceof ExtendedTest) {
			ExtendedTest eTest = (ExtendedTest) test;
			rollField.setText(String.format("%d", eTest.getRolls()));
		}
	}

	private void initComponents() {
		SpinnerNumberModel sModel = new SpinnerNumberModel(0, 0, POOL_MAX, 1);
		poolSpinner.setModel(sModel);
		expectField.setColumns(4);
		expectField.setEditable(false);

		targetSlider.setMaximum(0);
		targetSlider.setPaintTicks(true);
		targetSlider.setMinorTickSpacing(1);
		targetSlider.setMajorTickSpacing(5);
		targetSlider.setPaintLabels(true);
		targetSlider.setSnapToTicks(true);

		rollField.setVisible(false);
		rollLabel = makeLabel("Rolls:");
		rollLabel.setFont(new Font("Sans", Font.ITALIC, 12));
		rollLabel.setForeground(Color.gray);

		testToggle.setText("Extended Test");

		rollTestButton.setText("Roll Test");
		rollTestButton.setActionCommand(ROLL_ACTION_CMD);
	}

	private void initListeners() {
		poolSpinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				targetSlider.setMaximum((Integer) poolSpinner.getValue());
			}
		});
		poolSpinner.addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				int val = ((Integer) poolSpinner.getValue())
						- e.getWheelRotation();
				if (val > POOL_MAX)
					val = POOL_MAX;
				if (val < 0)
					val = 0;
				poolSpinner.setValue(val);
			}
		});
		ChangeListener pListen = new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				(new Thread(updateRunner)).start();
			}
		};
		poolSpinner.addChangeListener(pListen);
		targetSlider.addChangeListener(pListen);
		targetSlider.addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				int val = targetSlider.getValue();
				targetSlider.setValue(val - e.getWheelRotation());
			}
		});
		testToggle.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean state = !rollField.isVisible();
				rollField.setVisible(state);
				if (!state) {
					rollLabel.setFont(new Font("Sans", Font.ITALIC, 12));
					rollLabel.setForeground(Color.gray);
				} else {
					rollLabel.setFont(new Font("Sans", Font.PLAIN, 12));
					rollLabel.setForeground(Color.black);
				}
				(new Thread(updateRunner)).start();
			}
		});
		rollTestButton.addActionListener(controller.getListener());
	}

	/**
	 * Updates statistics in the various components. Calculates success
	 * probability, various glitch probabilities and the threshold with a
	 * success chance of at least 80%.
	 */
	@SuppressWarnings("unchecked")
	public void updateStats() {
		final int expected = controller.getExpected();
		final double sProb = controller.getSuccessProb();
		final double cProb = controller.getCritProb();
		final double gProb = controller.getGlitchProb();
		final int eighty = controller.getEighty();
		expectField.setText("" + expected);
		successBar.setProb(sProb);
		glitchBar.setProb(gProb);
		critBar.setProb(cProb);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				Hashtable<Integer, JLabel> labels = targetSlider
						.createStandardLabels(5, 0);
				JLabel eightyLabel = new JLabel("" + eighty);
				eightyLabel.setFont(new Font("Serif", Font.ITALIC, 12));
				// eightyLabel.setForeground(Color.green);
				labels.put(eighty, eightyLabel);
				targetSlider.setLabelTable(labels);
			}
		});
	}

	private void setRoll() {
		int pool = (Integer) (poolSpinner.getValue());
		int thres = targetSlider.getValue();
		if (testToggle.isSelected()) {
			controller.setTest(new ExtendedTest(pool, thres));
		} else {
			controller.setTest(new SuccessTest(pool, thres));
		}
	}
}
