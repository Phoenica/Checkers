package mainFiles;

import java.awt.Color;
import java.util.Random;

import javax.swing.JButton;

import myUtility.Pair;

public class GameStatus {
	Board gameBoard;
	Color currentTurn; // 0 - game over, 1 - white player, 2 - black player
	Pair<Integer, Integer> highlightedPosition;
	ArtificalInteligence[] computerPlayer;
	int turnCounter = 0;
	GameMode gameMode;

	public GameStatus(GameMode _gameMode, JButton[] whiteButtons, int games) {
		gameMode = _gameMode;
		initialize("data1.bin", "data2.bin");
		boolean bothLearn = (games > 1) ? true : false;
		highlightedPosition = new Pair<Integer, Integer>(-1, -1);
		if (gameMode == GameMode.AvA)
			simulateAIvsAI(whiteButtons, games, bothLearn);
		else if (isItAITurn()) {
			handleAIMove(0);
			gameBoard.UpdateStatus(whiteButtons);
			currentTurn = Color.BLACK;
		}
	}

	void initialize(String filename1, String filename2) {
		if (gameMode != GameMode.HvH) {
			Random temp = new Random();
			gameBoard = (temp.nextInt(2) == 1) ? new Board(true) : new Board(false);
			computerPlayer = new ArtificalInteligence[2];
			computerPlayer[0] = new ArtificalInteligence(filename1);
			if (gameMode == GameMode.AvA)
				computerPlayer[1] = new ArtificalInteligence(filename2);
		} else {
			computerPlayer = null;
			gameBoard = new Board(true);
		}
		turnCounter = 0;
		currentTurn = Color.WHITE;
	}

	public void buttonClicked(int buttonNumber, JButton[] whiteButtons) {

		if (currentTurn != Color.WHITE && currentTurn != Color.BLACK || isItAITurn()) {
			System.out.println("It's computer turn");
			return;
		}

		Pair<Integer, Integer> destination = convertToPosition(buttonNumber);
		if (isHighlighted(highlightedPosition)) {
			if (destination.equals(highlightedPosition)) {
				whiteButtons[buttonNumber].setBackground(Color.WHITE);
				highlightedPosition.set(-1, -1);
			} else {
				Pair<Integer, Integer> source = new Pair<Integer, Integer>(highlightedPosition.first,
						highlightedPosition.second);
				Move simulatedMove = gameBoard.simulateMove(highlightedPosition, destination);
				if (simulatedMove == Move.MOVED || simulatedMove == Move.HIT) {
					if ((!(Move.HIT == simulatedMove)) && gameBoard.findAllHits(currentTurn))
						return;
					gameBoard.handleMove(simulatedMove, highlightedPosition, destination, currentTurn);
					gameBoard.UpdateStatus(whiteButtons);
					if (!(gameBoard.canHitAgain(destination.first, destination.second) && simulatedMove == Move.HIT)) {
						currentTurn = (currentTurn == Color.WHITE) ? Color.BLACK : Color.WHITE;
					}
					whiteButtons[convertToButtonNumber(source)].setBackground(Color.WHITE);
				}
				if (simulatedMove == Move.FAILED) {
					if (currentTurn == gameBoard.gameBoard[destination.first][destination.second].getColor()) {
						whiteButtons[convertToButtonNumber(highlightedPosition)].setBackground(Color.WHITE);
						highlightedPosition = destination;
						whiteButtons[buttonNumber].setBackground(Color.DARK_GRAY);
					}
				}
			}
		} else {
			if (currentTurn == gameBoard.gameBoard[destination.first][destination.second].getColor()) {
				highlightedPosition = destination;
				whiteButtons[buttonNumber].setBackground(Color.DARK_GRAY);
			}
		}
		if (gameBoard.pieceCounters[0] == 0
				|| (currentTurn == Color.WHITE && gameBoard.countMoves(Color.WHITE).first == 0)) {
			System.out.println("Black player won!");
			currentTurn = Color.GRAY;
			if (computerPlayer != null)
				learnAndSave(false, 0);
			return;
		}
		if (gameBoard.pieceCounters[1] == 0
				|| (currentTurn == Color.BLACK && gameBoard.countMoves(Color.BLACK).first == 0)) {
			System.out.println("White player won!");
			currentTurn = Color.GRAY;
			if (computerPlayer != null)
				learnAndSave(false, 0);
			return;
		}

		gameBoard.UpdateStatus(whiteButtons);
		if (isItAITurn())
			if (handleAIMove(0) == Move.WIN) {
				learnAndSave(true, 0);
				System.out.println("Computer player won!2");
				currentTurn = Color.GRAY;
			} else
				currentTurn = (currentTurn == Color.WHITE) ? Color.BLACK : Color.WHITE;
		gameBoard.UpdateStatus(whiteButtons);
	}

	boolean isHighlighted(Pair<Integer, Integer> position) {
		return (!(position.first > 31 || position.first < 0 || position.second > 31 || position.second < 0));
	}

	Pair<Integer, Integer> convertToPosition(int buttonNumber) {
		Pair<Integer, Integer> position;
		position = new Pair<Integer, Integer>(buttonNumber / 4, (buttonNumber % 4) * 2 + (buttonNumber / 4 % 2));
		return position;
	}

	int convertToButtonNumber(Pair<Integer, Integer> position) {
		return position.first * 4 + position.second / 2;
	}

	boolean isItAITurn() {
		return (computerPlayer != null && (gameBoard.bottomBlack && currentTurn == Color.WHITE)
				|| ((!gameBoard.bottomBlack) && currentTurn == Color.BLACK));
	}

	Color getAIColor() {
		return (gameBoard.bottomBlack) ? Color.WHITE : Color.BLACK;
	}

	public void simulateAIvsAI(JButton[] whiteButtons, int games, boolean bothLearn) {
		int winStreak = 0;
		int iterator = 0;
		int score = 0;
		while (games > iterator) {

			while (true) {
				turnCounter++;
				Move temp;
				if (isItAITurn())
					temp = handleAIMove(0);
				else
					temp = handleAIMove(1);

				if (temp == Move.WIN) {
					if (isItAITurn()) {
						System.out.println("Computer first won!");
						learnAndSave(true, 0);
						if (bothLearn)
							learnAndSave(false, 1);
						score++;
						if (winStreak < 0)
							winStreak = 1;
						else
							winStreak += 1;
					} else {
						System.out.println("Computer second won!");
						learnAndSave(false, 0);
						if (bothLearn)
							learnAndSave(true, 1);
						score--;
						if (winStreak > 0)
							winStreak = -1;
						else
							winStreak -= 1;
					}
					currentTurn = Color.GRAY;
					gameBoard.UpdateStatus(whiteButtons);
					break;
				} else
					currentTurn = Piece.reverseColor(currentTurn);

				gameBoard.UpdateStatus(whiteButtons);
				if (turnCounter > 100) {
					System.out.println("AI are in Stalmate");
					break;
				}
			}
			iterator++;
			System.out.println("Games number: " + iterator + " Games left: " + (games - iterator) + " Win Streak: "
					+ winStreak + " Total Score: " + score);
			System.out.println("");
			initialize(computerPlayer[0].filename, computerPlayer[1].filename);
			if (winStreak > 7) {
				winStreak = 0;
				ReinforcedLearner.mutate((computerPlayer[1].heuristic.weights));
			}
			if (winStreak < -7) {
				winStreak = 0;
				ReinforcedLearner.mutate((computerPlayer[0].heuristic.weights));
			}

		}

	}

	public Move handleAIMove(int AI) {
		Color player = (AI == 0) ? getAIColor() : Piece.reverseColor(getAIColor());
		computerPlayer[AI].setGameState(gameBoard);
		computerPlayer[AI].minMax(computerPlayer[AI].maxDepth, true, player);
		gameBoard = new Board(computerPlayer[AI].gameStates.pop());
		computerPlayer[AI].saveState(gameBoard, player);
		if (computerPlayer[AI].doubleHit)
			return handleAIMove(AI);
		if (gameBoard.pieceCounters[0] == 0 || gameBoard.pieceCounters[1] == 0
				|| gameBoard.countMoves(Piece.reverseColor(player)).first == 0)
			return Move.WIN;
		else
			return Move.HIT;

	}

	void learnAndSave(boolean aiWon, int aiNumber) {
		int reward = (aiWon) ? 1 : -1;
		computerPlayer[aiNumber].learner.learn(reward, computerPlayer[aiNumber].getData());
		computerPlayer[aiNumber].heuristic.update(computerPlayer[aiNumber].filename);

		Color player = getAIColor();
		if (aiNumber == 1)
			player = Piece.reverseColor(player);
		if (player == Color.WHITE)
			System.out.print("WHITE: ");
		else
			System.out.print("BLACK: ");
		computerPlayer[aiNumber].heuristic.printWeights();
	}
}
