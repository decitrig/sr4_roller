package net.decitrig.sr4;

import java.awt.*;
import java.awt.event.*;
import java.util.Hashtable;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.decitrig.sr4.math.*;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class RollerPanel {
	private static final int POOL_MAX = 100;

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
	private JButton rollTest = new JButton();

	private JLabel makeLabel(String s) {
		final Font labFont = new Font("Sans", Font.PLAIN, 12);
		JLabel temp = new JLabel("<html><u>" + s + "</u></html>");
		temp.setFont(labFont);
		return temp;
	}

	/**
	 * Class to set up common {@link JTextField} options, includes two
	 * convenience methods for common operations.
	 * 
	 * @author sliderule
	 * 
	 */
	private class InfoField extends JTextField {
		private static final long serialVersionUID = -2206098431953040990L;

		public InfoField() {
			setHorizontalAlignment(RIGHT);
			setEditable(false);
			setOpaque(true);
		}

		/**
		 * Used for the net hits field, colors the field red if it represents a
		 * failure
		 * 
		 * @param fail if <code>true</code>, set background red
		 */
		public void setFail(boolean fail) {
			setBackground(fail ? Color.red : null);
		}
		
		/** Used to display glitch status, colors the field's background according to the current glitch.
		 * 
		 * @param g {@link Glitch} status of current roll.
		 */
		public void setGlitch(Glitch g) {
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
	 * Class with a specialized constructor for setting inital values and a
	 * convenience method for setting the probability.
	 * 
	 * @author sliderule
	 * 
	 */
	private class StatBar extends JProgressBar {
		private static final long serialVersionUID = -3636650580191397126L;

		public StatBar() {
			setMaximum(100);
			setMinimum(0);
			setValue(0);
			setStringPainted(true);
			setProb(0.0);
		}

		/**
		 * Makes the bar display a given probability in percantage form.
		 * 
		 * @param p
		 *            probability to display
		 */
		public void setProb(double p) {
			setValue((int) (100 * p));
			setString(String.format("%.3f%%", p * 100));
		}
	}

	/**
	 * Lays out & returns the main panel.
	 * 
	 * @return a {@link JPanel} laid out in the desired manner.
	 */
	public JPanel getRollerPanel() {

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

		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		CellConstraints labelcc = new CellConstraints();
		builder.setDefaultDialogBorder();
		builder.addSeparator("Dice Pool", cc.xyw(1, row, 8));
		row += 2;// leave room for spacer
		builder.add(makeLabel("Pool:"), labelcc.xy(labelCol1, row),
					poolSpinner, cc.xy(4, row));
		SpinnerNumberModel sModel = new SpinnerNumberModel(0, 0, POOL_MAX, 1);
		poolSpinner.setModel(sModel);
		expectField.setColumns(4);
		expectField.setEditable(false);
		builder.add(makeLabel("Expected:"), labelcc.xy(labelCol2, row),
					expectField, cc.xy(compCol2, row));
		row++;
		targetSlider.setMaximum(0);
		targetSlider.setPaintTicks(true);
		targetSlider.setMinorTickSpacing(1);
		targetSlider.setMajorTickSpacing(5);
		targetSlider.setPaintLabels(true);
		targetSlider.setSnapToTicks(true);
		row++;
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
		rollField.setVisible(false);
		rollLabel = makeLabel("Rolls:");
		rollLabel.setFont(new Font("Sans", Font.ITALIC, 12));
		rollLabel.setForeground(Color.gray);
		// rollLabel.setVisible(false);
		builder.add(rollLabel, labelcc.xy(labelCol2, row), rollField, cc
				.xy(compCol2, row));
		row += 2;
		testToggle.setText("Extended Test");
		builder.add(testToggle, cc.xyw(compCol1, row, 5));

		row += 2;
		rollTest.setText("Roll Test");
		builder.add(rollTest, cc.xyw(1, row, 8));

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
				final int pool = (Integer) (poolSpinner.getValue());
				final int thres = targetSlider.getValue();
				Thread t = new Thread(new Runnable() {
					@Override
					public void run() {
						updateStats(pool, thres);
					}
				});
				t.start();
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
			}
		});
		rollTest.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Thread t = new Thread(new Runnable() {
					@Override
					public void run() {
						doTest((Integer) (poolSpinner.getValue()), targetSlider
								.getValue(), testToggle.isSelected());
					}

				});
				t.start();
			}
		});
		final int pool = (Integer) (poolSpinner.getValue());
		final int thres = targetSlider.getValue();
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				updateStats(pool, thres);
			}
		});
		t.start();
		return builder.getPanel();
	}

	/**
	 * Updates statistics in the various components. Calculates success
	 * probability, various glitch probabilities and the threshold with a
	 * success chance of at least 80%.
	 * 
	 * @param pool
	 *            number of dice in the current pool
	 * @param thres
	 *            threshold to beat
	 */
	private void updateStats(int pool, int thres) {
		final int expected = RollerMath.expectedHits(pool);
		final double sProb = RollerMath.hitProb(pool, thres);
		final double cProb = RollerMath.critProb(pool);
		final double gProb = RollerMath.glitchProb(pool);
		final int eighty = RollerMath.findEighty(pool);

		SwingUtilities.invokeLater(new Runnable() {
			@SuppressWarnings("unchecked")
			@Override
			public void run() {
				expectField.setText("" + expected);
				successBar.setProb(sProb);
				glitchBar.setProb(gProb);
				critBar.setProb(cProb);

				Hashtable labels = targetSlider.createStandardLabels(5, 0);
				JLabel eightyLabel = new JLabel("" + eighty);
				eightyLabel.setFont(new Font("Serif", Font.ITALIC, 12));
				// eightyLabel.setForeground(Color.green);
				labels.put(eighty, eightyLabel);
				targetSlider.setLabelTable(labels);
			}
		});
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
	private void doTest(int pool, final int thres, boolean ext) {
		final int hits;
		final Glitch g;
		if (!ext) {
			SuccessTest t = new SuccessTest(pool);
			hits = t.getHits();
			g = t.getGlitch();
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					hitsField.setText("" + hits);
					netHitField.setText("" + (hits - thres));
					netHitField.setFail(hits < thres);
					glitchField.setGlitch(g);
					glitchField.setText(g.toString());
				}
			});
		} else {
			final int rolls;
			ExtendedTest t = new ExtendedTest(pool, thres);
			hits = t.getHits();
			g = t.getGlitch();
			rolls = t.getRolls();
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					hitsField.setText("" + hits);
					netHitField.setText("" + (hits - thres));
					netHitField.setFail(hits < thres);
					glitchField.setGlitch(g);
					rollField.setText("" + rolls);
					glitchField.setText(g.toString());
				}
			});
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame frame = new JFrame("sliderule's dice roller");
				frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
				RollerPanel rp = new RollerPanel();

				frame.add(rp.getRollerPanel(), BorderLayout.CENTER);
				frame.setResizable(false);
				frame.pack();
				frame.setVisible(true);
			}
		});
	}

}
