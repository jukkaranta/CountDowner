package me.jukkarantagithub.CountDowner;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.WindowConstants;

public class CountDowner extends JFrame {

	private static final long serialVersionUID = 1L;

	private JPanel basePanel;

	int butHeight = 10;
	int panelWidth = 500;

	private JLabel countLabel; // remaining time in numbers
	private JLabel timeProgressBar; // decreasing length

	long fullTime = 30 * 60 * 1000; // how long is the full count down
	long timeRemaining = 0; // how much time left in the count down
	int timeTick = 1000; // time progress step size in milliseconds

	int countLabelMinWidth = 50;

	Color countLabelRunningColor = Color.WHITE;
	Color countLabelBorderColor = Color.RED;
	Color countLabelfinishedBlinkColor = Color.PINK;
	Color timeProgressBarColor = Color.RED;
	Color timeProgressBarBorderColor = Color.PINK;

	String titleTxt = "Time remaining";
	private CountDowner thisCountDowner;

	public CountDowner() {
		thisCountDowner = this;

		basePanel = new JPanel() {
			private static final long serialVersionUID = 1L;

			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				resizeBars();
			}
		};

		basePanel.setLayout(null);
		basePanel.setPreferredSize(new Dimension(panelWidth, butHeight));
		add(basePanel, BorderLayout.CENTER);
		basePanel.addMouseListener(new ClickListener());

		countLabel = new JLabel();
		// countLabel.setBackground(countLabelRunningColor);
		countLabel.setBorder(BorderFactory.createLineBorder(countLabelBorderColor, 1));
		countLabel.setOpaque(true);
		countLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		timeProgressBar = new JLabel();
		timeProgressBar.setBackground(timeProgressBarColor);
		timeProgressBar.setBorder(BorderFactory.createLineBorder(timeProgressBarBorderColor, 1));
		timeProgressBar.setOpaque(true);

		basePanel.add(countLabel);
		basePanel.add(timeProgressBar);

		Timer timer = new Timer(timeTick, new TickTock());
		timer.start();

		this.setMinimumSize(new Dimension(100, 50));

		// try {
		// InputStream is = this.getClass().getClassLoader()
		// .getResourceAsStream("res/Pomodoro.png");
		// Image image = ImageIO.read(is);
		// this.setIconImage(image);
		// } catch (Exception e) {
		// }

		this.setUndecorated(false);
		pack();
		setTitle(titleTxt);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

	private class TickTock implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (!paused)
				timeRemaining = Math.max(0, timeRemaining - timeTick);
			resizeBars();
			// System.out.println("DEBUG: CountDowner: timeRemaining "
			// + timeRemaining);
		}
	}

	private void resizeBars() {
		String pauseTxtInBar = "";
		if (paused)
			pauseTxtInBar = "P ";

		float progress = (float) (timeRemaining) / fullTime;
		progress = Math.min(progress, 1.0f);
		int barLength = (int) (Math.max(0, basePanel.getSize().width - countLabelMinWidth) * progress);

		int countLabelWidth = Math.max(countLabelMinWidth, basePanel.getSize().width - barLength);
		countLabel.setBounds(0, 0, countLabelWidth, basePanel.getSize().height);
		timeProgressBar.setBounds(countLabelWidth, 0, barLength, basePanel.getSize().height);
		if (timeRemaining < 1000 * 60) {
			countLabel.setForeground(Color.RED);
			countLabel.setText(pauseTxtInBar + Integer.toString((int) (timeRemaining / 1000)) + "  sec");
		} else {
			countLabel.setForeground(Color.BLACK);
			countLabel.setText(pauseTxtInBar + Integer.toString((int) (timeRemaining / 1000 / 60)) + "  ");
		}

		if (timeRemaining > 0) {
			if (countLabel.getBackground() != countLabelRunningColor)
				countLabel.setBackground(countLabelRunningColor);
		} else {
			if (countLabel.getBackground() == countLabelRunningColor)
				countLabel.setBackground(countLabelfinishedBlinkColor);
			else
				countLabel.setBackground(countLabelRunningColor);
		}
	}

	private void adjustTimer(int x) {
		int w = basePanel.getSize().width;
		w = Math.max(0, w - countLabelMinWidth);
		double x2 = Math.max(0, x - countLabelMinWidth);
		float relativePos = 1.0f - Math.max(0, Math.min(1, (float) x2 / w));
		timeRemaining = (long) (relativePos * fullTime);
		resizeBars();
	}

	private boolean paused;

	public void togglePause() {
		if (paused) {
			paused = false;
			resizeBars();
		} else {
			paused = true;
			resizeBars();
		}

	}

	private class ClickListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent arg0) {
			if (arg0.getButton() >= 2) { // right click
				new SettingsDialog(thisCountDowner, fullTime);
			} else {
				if (arg0.getClickCount() > 1) {
				} else {
					adjustTimer(arg0.getX());
				}
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}

	}

	public void adjustSettings(int newFullTime) {
		fullTime = newFullTime * 60 * 1000;
		timeRemaining = Math.min(fullTime, timeRemaining);
		resizeBars();
	}

	public static void main(String[] args) {

		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				CountDowner cd = new CountDowner();
				cd.setVisible(true);
			}
		});

	}

	/**
	 * Dialog for changing length of work and break times
	 */
	public class SettingsDialog extends JDialog {

		private static final long serialVersionUID = 1L;

		private JLabel workTimeLabel;
		private JSlider workTimeSlider;

		private JButton doneButton;
		private JButton pauseButton;
		private JButton exitButton;
		private JButton resizeButton;
		private boolean undecorated;

		private JPanel contentPane;

		private int wT;
		private int maxWorkTime = 60;
		private CountDowner m;

		private JDialog thisSettingsDialog;

		public SettingsDialog(CountDowner master, long fullTime) {
			super();

			m = master;
			wT = (int) (fullTime / 1000 / 60);
			if (wT > maxWorkTime / 2) {
				maxWorkTime = 2 * wT;
			}
			// if (wT > maxWorkTime - 10)
			// maxWorkTime = 2 * (wT + 10);
			create();
			thisSettingsDialog = this;
			this.setModal(true);
			this.setVisible(true);
		}

		private void create() {

			int labelWidth = 100;
			int sliderWidth = 380;
			int buttonWidth = 150;

			int wTScale = 1; // 1 minute increments
			if (maxWorkTime < 70) {
				wTScale = 1; // 1 minute increments
				workTimeLabel = new JLabel("Time (minutes)");
				workTimeLabel.setBounds(5, 25, labelWidth, 20);
				workTimeSlider = new JSlider(JSlider.HORIZONTAL, 0, maxWorkTime, wT);
				workTimeSlider.setMajorTickSpacing(5);
				workTimeSlider.setMinorTickSpacing(1);
				workTimeSlider.setPaintTicks(true);
				workTimeSlider.setPaintLabels(true);
				workTimeSlider.setBounds(5 + labelWidth, 15, sliderWidth, 40);
			} else {
				wTScale = 10; // 10 minute increments
				workTimeLabel = new JLabel("Time (minutes)");
				workTimeLabel.setBounds(5, 25, labelWidth, 20);
				workTimeSlider = new JSlider(JSlider.HORIZONTAL, 0, maxWorkTime, wT);
				workTimeSlider.setMajorTickSpacing(5 * wTScale);
				workTimeSlider.setMinorTickSpacing(1 * wTScale);
				workTimeSlider.setPaintTicks(true);
				workTimeSlider.setPaintLabels(true);
				workTimeSlider.setBounds(5 + labelWidth, 15, sliderWidth, 40);
			}
			doneButton = new JButton("Set new times");
			doneButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					m.adjustSettings(workTimeSlider.getValue());
					thisSettingsDialog.dispose();
				}
			});
			doneButton.setBounds(5, 80, buttonWidth, 20);

			undecorated = m.isUndecorated();
			if (undecorated)
				resizeButton = new JButton("Resize bar");
			else
				resizeButton = new JButton("Fix bar");
			resizeButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					undecorated = !undecorated; // toggle
					int x = m.getLocation().x;
					int y = m.getLocation().y;
					int w = m.getSize().width;
					int h = m.getSize().height;
					if (undecorated) {
						m.setMinimumSize(new Dimension(100, 10));
						m.setLocation(x, y + 30);
						h = h - 30;
					} else {
						m.setMinimumSize(new Dimension(100, 50));
						m.setLocation(x, y - 30);
						h = h + 30;
					}
					// System.out.println("DEBUG: sizing: " + w + " " + h);
					m.dispose();
					m.setUndecorated(undecorated);
					m.setVisible(true);
					m.setSize(w, h);
					thisSettingsDialog.dispose();
				}
			});
			resizeButton.setBounds(5 + buttonWidth + 50, 80, buttonWidth, 20);

			pauseButton = new JButton("Pause");
			pauseButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					m.togglePause();
					thisSettingsDialog.dispose();
				}
			});
			pauseButton.setBounds(5, 110, buttonWidth, 20);

			exitButton = new JButton("Exit");
			exitButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					thisSettingsDialog.dispose();
					System.exit(0);
				}
			});
			exitButton.setBounds(5 + buttonWidth + 50, 110, buttonWidth, 20);

			contentPane = (JPanel) this.getContentPane();
			contentPane.setLayout(null);
			contentPane.setBorder(BorderFactory.createEtchedBorder());
			contentPane.setBackground(Color.WHITE);
			contentPane.add(workTimeLabel);
			contentPane.add(workTimeSlider);
			contentPane.add(doneButton);
			contentPane.add(resizeButton);
			contentPane.add(pauseButton);
			contentPane.add(exitButton);

			this.setTitle("Timer Settings");
			if (m.getSize().height < 300) {
				if (m.getLocation().y > 225)
					this.setLocation(new Point(m.getLocation().x + 5, m.getLocation().y - 175));
				else
					this.setLocation(new Point(m.getLocation().x + 5, m.getLocation().y + m.getSize().height + 5));
			} else {
				this.setLocation(new Point(m.getLocation().x + 5, m.getLocation().y + m.getSize().height / 2));
			}
			this.setSize(new Dimension(500, 170));
			this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
			this.setResizable(false);
		}

	}

}