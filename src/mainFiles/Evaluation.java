package mainFiles;

import java.awt.Color;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

public class Evaluation {

	EvaluationData weights;
	private ObjectInputStream in;

	public Evaluation(String filename) {

		boolean loaded = true;
		try {
			in = new ObjectInputStream(new FileInputStream(filename));
			try {
				weights = (EvaluationData) in.readObject();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e1) {
			loaded = false;
		} catch (IOException e1) {
			loaded = false;
			e1.printStackTrace();
		}

		if (!loaded) {
			weights = new EvaluationData(4);
			try {
				ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(filename));
				os.writeObject(weights);
				os.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public void update(String filename) {
		try {
			ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(filename));
			os.writeObject(weights);
			os.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static double[] getState(Board gameBoard, Color player) {
		double[] state = new double[] { 0, 0, 0, 0, 0, 0, 0, 0 };

		for (int i = 0; i < 8; i++)
			for (int j = 0; j < 8; j++) {
				if (gameBoard.gameBoard[i][j].isMan()) {
					if (gameBoard.gameBoard[i][j].getColor() == player)
						state[0]++;
					else
						state[1]++;
				} else if (gameBoard.gameBoard[i][j].isKnight()) {
					if (gameBoard.gameBoard[i][j].getColor() == player)
						state[2]++;
					else
						state[3]++;
				}
			}

		return state;
	}

	public double evaluate(Board gameBoard, Color player) {
		double[] state = getState(gameBoard, player);
		double ret = 0;
		for (int i = 0; i < weights.numberOfWeights; i++)
			ret += state[i] * weights.weights[i];
		return ret;
	}

	public static double evaluate(double[] state, EvaluationData weights) {
		double ret = 0;
		for (int i = 0; i < weights.numberOfWeights; i++)
			ret += state[i] * weights.weights[i];
		return ret;
	}

	public void printWeights() {
		System.out.println(Arrays.toString(weights.weights));
	}

}
