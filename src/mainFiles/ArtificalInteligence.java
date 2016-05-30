package mainFiles;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

import myUtility.Pair;
import myUtility.Quadruple;

public class ArtificalInteligence {

	Evaluation heuristic;
	ReinforcedLearner learner;
	Stack<Board> gameStates;
	int maxDepth;
	boolean doubleHit;
	String filename;

	public ArtificalInteligence(String fileName) {
		this.filename = fileName;
		heuristic = new Evaluation(filename);
		learner = new ReinforcedLearner();
	}

	public void setGameState(Board gameBoard) {
		gameStates = new Stack<Board>();
		gameStates.push(gameBoard);
		doubleHit = false;
		if ((gameBoard.pieceCounters[0] + gameBoard.pieceCounters[1]) >= 6)
			maxDepth = 4;
		else
			maxDepth = 5;
	}

	// Picking best move
	// Side effect: top element of gameStates stack will be our state after minmaxTree 
	public double minMax(int depth, boolean maximizingPlayer, Color player) {
		ArrayList<Quadruple<Integer>> moveList = gameStates.peek().getMoveList(player);
		double temp, minmax = (maximizingPlayer) ? Integer.MIN_VALUE : Integer.MAX_VALUE;
		Board maxBoard = gameStates.peek();
		for (int i = 0; i < moveList.size(); i++) {
			gameStates.push(new Board(gameStates.get(gameStates.size() - 1)));
			Board currentBoard = gameStates.get(gameStates.size() - 1);
			Pair<Integer, Integer> source = new Pair<Integer, Integer>(moveList.get(i).first, moveList.get(i).second);
			Pair<Integer, Integer> destination = new Pair<Integer, Integer>(moveList.get(i).third,
					moveList.get(i).fourth);

			// Processing move
			Move simulatedMove = gameStates.get(gameStates.size() - 1).simulateMove(source, destination);
			currentBoard.handleMove(simulatedMove, source, destination, player);
			boolean doublehit2 = (simulatedMove == Move.HIT
					&& currentBoard.canHitAgain(destination.first, destination.second));
			
			//checking if our move is winning one
			if (currentBoard.pieceCounters[0] == 0 || currentBoard.pieceCounters[1] == 0
					|| maximizingPlayer && currentBoard.countMoves(Piece.reverseColor(player)).first == 0
					|| (!maximizingPlayer) && currentBoard.countMoves(player).first == 0) {
				if (depth != maxDepth)
					gameStates.pop();
				if (maximizingPlayer)
					return Integer.MAX_VALUE;
				else
					return Integer.MIN_VALUE;
			}
			if (depth == 0) {
				temp = heuristic.evaluate(currentBoard, player);

			} else {
				temp = (doublehit2) ? minMax(depth - 1, maximizingPlayer, player)
						: minMax(depth - 1, !maximizingPlayer, player);
			}

			double minmax2 = (maximizingPlayer) ? Math.max(minmax, temp) : Math.min(minmax, temp);
			Random dice = new Random();
			if (minmax2 == minmax && minmax != Integer.MIN_VALUE && minmax != Integer.MAX_VALUE
					&& dice.nextInt(2) == 1) {
				minmax = minmax2;
				maxBoard = currentBoard;
			}

			if (minmax2 != minmax || minmax == Integer.MIN_VALUE || minmax == Integer.MAX_VALUE) {
				minmax = minmax2;
				maxBoard = currentBoard;
			}
			
			doubleHit = (simulatedMove == Move.HIT && currentBoard.canHitAgain(destination.first, destination.second)); 																									// of
			gameStates.pop();
		}
		if (depth == maxDepth) {
			gameStates.push(maxBoard);
		}
		return minmax;
	}

	public EvaluationData getData() {
		return heuristic.weights;
	}

	public void saveState(Board gameBoard, Color player) {
		learner.saveState(gameBoard, player);
	}
}
