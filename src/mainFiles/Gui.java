package mainFiles;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JTextPane;

public class Gui extends JFrame {

	private static final long serialVersionUID = 1L;

	private GameStatus game;

	private JFrame frame;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private JTextField textField;
	private JTextField textField_1;

	public Gui() {

		initialize();
		frame.setVisible(true);
	}

	private void initialize() {
		frame = new JFrame();
		frame.setBounds(150, 150, 700, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JPanel mainMenu = new JPanel();
		mainMenu.setBounds(0, 0, 684, 461);
		frame.getContentPane().add(mainMenu);
		mainMenu.setLayout(null);

		JButton btnNewGame = new JButton("New Game");

		btnNewGame.setBounds(91, 352, 135, 23);
		mainMenu.add(btnNewGame);

		JRadioButton radioButtonHvH = new JRadioButton("Human vs Human");
		buttonGroup.add(radioButtonHvH);
		radioButtonHvH.setSelected(true);
		radioButtonHvH.setBounds(91, 382, 135, 23);
		mainMenu.add(radioButtonHvH);

		JRadioButton radioButtonAvA = new JRadioButton("AI vs AI");

		radioButtonAvA.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});

		buttonGroup.add(radioButtonAvA);
		radioButtonAvA.setBounds(91, 431, 135, 23);
		mainMenu.add(radioButtonAvA);

		JPanel panel_gameBoard = new JPanel();
		panel_gameBoard.setBounds(10, 11, 335, 330);
		mainMenu.add(panel_gameBoard);
		panel_gameBoard.setLayout(new GridLayout(8, 8));

		JPanel panel_statusScreen = new JPanel();
		panel_statusScreen.setBounds(366, 17, 257, 324);
		mainMenu.add(panel_statusScreen);
		panel_statusScreen.setLayout(null);

		JLabel lblPlayer = new JLabel("Player 1");
		lblPlayer.setForeground(Color.BLACK);
		lblPlayer.setBounds(10, 47, 118, 27);
		panel_statusScreen.add(lblPlayer);

		JLabel lblPlayer_1 = new JLabel("Player 2");
		lblPlayer_1.setBounds(10, 80, 118, 27);
		panel_statusScreen.add(lblPlayer_1);

		textField = new JTextField();
		textField.setEditable(false);
		textField.setBounds(87, 50, 86, 20);
		panel_statusScreen.add(textField);
		textField.setColumns(10);

		textField_1 = new JTextField();
		textField_1.setEditable(false);
		textField_1.setBounds(87, 85, 86, 20);
		panel_statusScreen.add(textField_1);
		textField_1.setColumns(10);

		JRadioButton radioButtonHvA = new JRadioButton("Human vs AI");
		radioButtonHvA.setBounds(91, 408, 135, 23);
		mainMenu.add(radioButtonHvA);
		buttonGroup.add(radioButtonHvA);

		JTextPane textPane = new JTextPane();
		textPane.setEnabled(false);
		textPane.setText("1");
		textPane.setBounds(244, 431, 47, 19);
		mainMenu.add(textPane);

		JButton[] blackButtons = new JButton[32];
		JButton[] whiteButtons = new JButton[32];

		for (int i = 0; i < whiteButtons.length; i++) {
			final int j = i;
			whiteButtons[i] = new JButton("");
			whiteButtons[i].setBackground(Color.WHITE);
			whiteButtons[i].addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent arg0) {
					game.buttonClicked(j, whiteButtons);

				}
			});

		}
		for (int i = 0; i < blackButtons.length; i++) {
			blackButtons[i] = new JButton("");
			blackButtons[i].setBackground(Color.BLACK);
		}
		for (int i = 0; i < 8; i++) {
			if (i % 2 == 0) {
				for (int j = 0; j < 4; j++) {
					panel_gameBoard.add(whiteButtons[4 * i + j]);
					panel_gameBoard.add(blackButtons[4 * i + j]);
				}
			} else {
				for (int j = 0; j < 4; j++) {
					panel_gameBoard.add(blackButtons[4 * i + j]);
					panel_gameBoard.add(whiteButtons[4 * i + j]);
				}
			}
		}

		radioButtonAvA.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				textPane.setEnabled(true);
			}
		});
		radioButtonHvA.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				textPane.setEnabled(false);
			}
		});
		radioButtonHvH.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				textPane.setEnabled(false);
			}
		});

		btnNewGame.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (radioButtonHvH.isSelected()) {
					game = new GameStatus(GameMode.HvH, whiteButtons, 0);
				} else if (radioButtonHvA.isSelected())
					game = new GameStatus(GameMode.HvA, whiteButtons, 0);
				else
					game = new GameStatus(GameMode.AvA, whiteButtons, Integer.parseInt(textPane.getText()));
				game.gameBoard.UpdateStatus(whiteButtons);
				validate();
			}
		});
	}
}
