package mainFiles;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

public class ReinforcedLearner {

	private ArrayList<double[]> gameStatesValues;
	static final double alpha = 0.01;
	static final double mutationPower = 0.03;

	public ReinforcedLearner() {
		gameStatesValues = new ArrayList<double[]>();
	}

	public void saveState(Board gameBoard, Color player) {
		gameStatesValues.add(Evaluation.getState(gameBoard, player));
	}

	public void learn(int reward, EvaluationData weights) {
		for (int i = 0; i < gameStatesValues.size(); i++) {
			double eva1 = Evaluation.evaluate(gameStatesValues.get(i), weights);
			double eva2 = eva1 + reward;
			double test = ((double) i / (double) gameStatesValues.size());
			for (int j = 0; j < weights.numberOfWeights; j++)
				weights.weights[j] = weights.weights[j] + test * alpha * (eva2 - eva1) * gameStatesValues.get(i)[j];

		}
	}

	public static void mutate(EvaluationData weights) {
		System.out.println("Mutating!");
		Random dice = new Random();
		for (int j = 0; j < weights.numberOfWeights; j++) {
			weights.weights[j] = weights.weights[j] + (dice.nextInt(100) - 50) * mutationPower;
		}
	}

}
