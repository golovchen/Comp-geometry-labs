package ru.itmo.golovchenko.trapezoidalmapgui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Point;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JComboBox;
import javax.swing.JSeparator;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JTextArea;

import ru.itmo.golovchenko.trapezoidalmap.EqualsXCoordinatesException;
import ru.itmo.golovchenko.trapezoidalmap.InterspectedLinesException;
import ru.itmo.golovchenko.trapezoidalmap.Line;
import ru.itmo.golovchenko.trapezoidalmap.OverlapingByXException;
import ru.itmo.golovchenko.trapezoidalmap.TrapezoidalMap;

@SuppressWarnings("serial")
public class MainWindow extends JFrame {
	private JPanel contentPane;
	private final Map<String, String> defaultInputs = new HashMap<>();
	
	
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow frame = new MainWindow();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public MainWindow() {
		defaultInputs.put("single line", "0 0 200 300\n");
		defaultInputs.put("two lines", "0 0 200 0\n100 100 300 100\n");
		defaultInputs.put("cross-line", "0 0 100 0\n100 0 200 0\n50 -50 150 50");
		defaultInputs.put("picture", "0 0 100 0\n20 20 30 30\n30 30 40 20\n40 20 50 30\n50 30 60 20\n60 20 70 30\n70 30 80 20\n11 -60 89 -60\n21 -20 79 -20\n31 -30 69 -30\n41 -40 59 -40");
		defaultInputs.put("empty", "");
		inicializeComponents();
	}
	
	private void inicializeComponents() {
		contentPane = new JPanel();
		final Drawer drawer = new Drawer();
		JPanel panel = new JPanel();
		JPanel panel_1 = new JPanel();
		final JSpinner xSpinner = new JSpinner();
		final JSpinner ySpinner = new JSpinner();
		JLabel lblY = new JLabel("Y:");
		final JLabel nodeLabel = new JLabel("Object");
		final JComboBox<String> comboBox = new JComboBox<>();
		final JButton setInputButton = new JButton("Set lines");
		final JCheckBox checkInputBox = new JCheckBox("Check input");
		final JTextArea textArea = new JTextArea();
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 751, 577);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		panel.setPreferredSize(new Dimension(170, 10));
		panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		contentPane.add(panel, BorderLayout.EAST);
		panel.setLayout(new BorderLayout(0, 0));
		
		panel_1.setBorder(new EmptyBorder(0, 0, 0, 0));
		panel_1.setPreferredSize(new Dimension(10, 193));
		panel.add(panel_1, BorderLayout.NORTH);
		panel_1.setLayout(null);
		
		JLabel lblX = new JLabel("X:");
		lblX.setBounds(12, 14, 14, 15);
		panel_1.add(lblX);
		
		//spinners
		xSpinner.setBounds(33, 12, 51, 20);
		xSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent event) {
				int newX = (Integer)xSpinner.getValue();
				int newY = (Integer)ySpinner.getValue();
				if (newX != drawer.getPoint().x) {
					drawer.setPoint(new Point(newX, newY));
				}
			}
		});
		ySpinner.setBounds(117, 12, 51, 20);
		ySpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent event) {
				int newX = (Integer)xSpinner.getValue();
				int newY = (Integer)ySpinner.getValue();
				if (newY != drawer.getPoint().y) {
					drawer.setPoint(new Point(newX, newY));
				}
			}
		});
		panel_1.add(ySpinner);
		panel_1.add(xSpinner);
		
		lblY.setBounds(96, 14, 14, 15);
		panel_1.add(lblY);
		
		
		nodeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		nodeLabel.setBounds(12, 32, 146, 43);
		panel_1.add(nodeLabel);
		
		JSeparator separator = new JSeparator();
		separator.setBounds(12, 79, 146, 2);
		panel_1.add(separator);
		
		//button
		setInputButton.setBounds(12, 131, 146, 25);
		setInputButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					TrapezoidalMap map = new TrapezoidalMap();
					String[] lines = textArea.getText().split("\n");
					for (String line : lines) {
						if (line.isEmpty()) {
							continue;
						}						
						String[] nums = line.split(" ");
						Point point1 = new Point(Integer.parseInt(nums[0]), Integer.parseInt(nums[1]));
						Point point2 = new Point(Integer.parseInt(nums[2]), Integer.parseInt(nums[3]));
						if (checkInputBox.isSelected()) {
							map.checkAndAdd(new Line(point1, point2));
						} else {
							map.add(new Line(point1, point2));
						}
					}
					drawer.setMap(map);
				} catch (EqualsXCoordinatesException exc) {
					JOptionPane.showMessageDialog(null, "Line " + exc.newLine + " have equal x-coordinates in both points.");
				} catch (InterspectedLinesException exc) {
					JOptionPane.showMessageDialog(null, "Line " + exc.newLine + " interspect " + exc.existingLine);
				} catch (OverlapingByXException exc) {
					JOptionPane.showMessageDialog(null, "Lines " + exc.newLine + " and " + exc.overlapingLine + " have equal x-coordinates by one side.");
				} catch (Exception exc) {
					JOptionPane.showMessageDialog(null, "Incorrect input " + exc.toString());
					exc.printStackTrace();
				}
			}
		});
		panel_1.add(setInputButton);
	
		//checkInputBox
		checkInputBox.setBounds(8, 164, 129, 23);
		checkInputBox.setSelected(true);
		panel_1.add(checkInputBox);
		
		panel.add(textArea, BorderLayout.CENTER);
		
		//drawer
		contentPane.add(drawer, BorderLayout.CENTER);
		drawer.addPointChangedListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				Point p = drawer.getPoint();
				if (!xSpinner.getValue().equals(p.x)) {
					xSpinner.setValue(p.x);
				}
				if (!ySpinner.getValue().equals(p.y)) {
					ySpinner.setValue(p.y);
				}
			}
		});
		drawer.addMapNodeChangedListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				nodeLabel.setText(drawer.getMapNode());
				setTitle(drawer.getMapNode());
			}
		});
		
		//comboBox
		comboBox.setBounds(12, 93, 146, 24);
		for (String name : defaultInputs.keySet()) {
			comboBox.addItem(name);
		}
		panel_1.add(comboBox);
		
		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textArea.setText(defaultInputs.get(comboBox.getSelectedItem()));
				setInputButton.doClick();
			}
		});
	}
}
