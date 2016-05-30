package mainFiles;

import java.awt.Color;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import myUtility.Pair;
import myUtility.Quadruple;

public class Board {
	Piece[][] gameBoard; 
	boolean bottomBlack;
	int[] pieceCounters;

	public Board(boolean isBottomBlack) // 1 - true, 0 - false
	{
		gameBoard = new Piece[8][8];
		pieceCounters = new int[2];
		pieceCounters[0] = pieceCounters[1] = 12;
		bottomBlack = isBottomBlack;
		for (int i = 0; i < 8; i++)
			for (int j = 0; j < 8; j++)
				gameBoard[i][j] = Piece.EMPTY;
		for (int i = 0; i < 8; i += 2) {
			gameBoard[0][i] = gameBoard[1][i + 1] = gameBoard[2][i] = (bottomBlack) ? Piece.WHITE_PAWN
					: Piece.BLACK_PAWN;
			gameBoard[5][i
					+ 1] = gameBoard[6][i] = gameBoard[7][i + 1] = (!bottomBlack) ? Piece.WHITE_PAWN : Piece.BLACK_PAWN;
		}

	}

	public Board(Board another) {
		this.bottomBlack = another.bottomBlack;
		this.pieceCounters = new int[2];
		this.pieceCounters[0] = another.pieceCounters[0];
		this.pieceCounters[1] = another.pieceCounters[1];
		this.gameBoard = new Piece[8][8];
		for (int i = 0; i < 8; i++)
			for (int j = 0; j < 8; j++)
				this.gameBoard[i][j] = another.gameBoard[i][j];

	}

	public void UpdateStatus(JButton[] whiteButtons) {
		for (int i = 0; i < 32; i++) {
			Piece temp = gameBoard[i / 4][(i % 4) * 2 + (i / 4 % 2)];
			switch (temp) {
			case WHITE_PAWN:
				whiteButtons[i].setIcon(new ImageIcon("Graphics/icon_white_pawn.png"));
				break;
			case WHITE_KNIGHT:
				whiteButtons[i].setIcon(new ImageIcon("Graphics/icon_white_tower.png"));
				break;
			case BLACK_PAWN:
				whiteButtons[i].setIcon(new ImageIcon("Graphics/icon_black_pawn.png"));
				break;
			case BLACK_KNIGHT:
				whiteButtons[i].setIcon(new ImageIcon("Graphics/icon_black_tower.png"));
				break;
			default:
				whiteButtons[i].setIcon(new ImageIcon(""));

			}
		}
	}

	public void handleMove(Move simulatedMove, Pair<Integer, Integer> source, Pair<Integer, Integer> destination,
			Color player) {
		switch (simulatedMove) {
		case HIT:
			int y = source.first - (source.first - destination.first) / 2;
			int x = source.second - (source.second - destination.second) / 2;
			gameBoard[y][x] = Piece.EMPTY;
			if (player == Color.WHITE)
				pieceCounters[0]--;
			else
				pieceCounters[1]--;

		case MOVED:
			Piece piece = gameBoard[source.first][source.second];
			if ((!piece.isKnight()) && (destination.first == 0 || destination.first == 7)) {
				piece = piece.promote();
			}
			gameBoard[source.first][source.second] = Piece.EMPTY;
			gameBoard[destination.first][destination.second] = piece;
			source.set(-1, -1);
		case FAILED:
			break;
		default:
			System.out.println("simulateMove returned unexpected value");
		}
	}

	public Move simulateMove(Pair<Integer, Integer> highlightedPosition, Pair<Integer, Integer> position) {
		int y1, x1, y2, x2;
		y1 = highlightedPosition.first;
		y2 = position.first;
		x1 = highlightedPosition.second;
		x2 = position.second;

		// Checking if move is within bounds of map
		if (y2 > 7 || x2 > 7 || x2 < 0 || y2 < 0)
			return Move.FAILED;

		// Checking if move is correct not hitting move
		if ((y1 + 1 == y2 && x1 + 1 == x2) || (y1 - 1 == y2 && x1 + 1 == x2) || (y1 + 1 == y2 && x1 - 1 == x2)
				|| (y1 - 1 == y2 && x1 - 1 == x2)) {
			if (gameBoard[y2][x2] == Piece.EMPTY) {
				Piece piece = gameBoard[y1][x1];
				if ((piece.getColor() == Color.WHITE && bottomBlack || piece.getColor() == Color.BLACK && !bottomBlack)
						&& y2 > y1)
					return Move.MOVED;
				if ((piece.getColor() == Color.BLACK && bottomBlack || piece.getColor() == Color.WHITE && !bottomBlack)
						&& y2 < y1)
					return Move.MOVED;
				if (piece.isKnight())
					return Move.MOVED;
				else
					return Move.FAILED;
			}
		}
		// Checking if move is correct hitting move
		if ((y1 + 2 == y2 && x1 + 2 == x2) || (y1 - 2 == y2 && x1 + 2 == x2) || (y1 + 2 == y2 && x1 - 2 == x2)
				|| (y1 - 2 == y2 && x1 - 2 == x2)) {
			if (gameBoard[y2][x2] == Piece.EMPTY) {
				int y3 = y1 - (y1 - y2) / 2;
				int x3 = x1 - (x1 - x2) / 2;
				Piece piece = gameBoard[y1][x1];
				Piece piece2 = gameBoard[y3][x3];
				if (piece.getColor() != piece2.getColor() && piece2 != Piece.EMPTY) {
					if ((piece.isKnight())
							|| ((piece.getColor() == Color.BLACK && bottomBlack
									|| piece.getColor() == Color.WHITE && !bottomBlack) && y2 < y1)
							|| ((piece.getColor() == Color.WHITE && bottomBlack
									|| piece.getColor() == Color.BLACK && !bottomBlack) && y2 > y1)) {
						return Move.HIT;
					}
				}
			}
		}
		return Move.FAILED;
	}

	// getMoveList generates list of possible moves for AI. If possible, it will
	// only generate moves that result in hitting
	public ArrayList<Quadruple<Integer>> getMoveList(Color player) {
		ArrayList<Quadruple<Integer>> moveList = new ArrayList<Quadruple<Integer>>();
		ArrayList<Quadruple<Integer>> hitList = new ArrayList<Quadruple<Integer>>();
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (gameBoard[i][j].getColor() == player) {
					Pair<Integer, Integer> startPosition = new Pair<Integer, Integer>(i, j);
					if (Move.HIT == simulateMove(startPosition, new Pair<Integer, Integer>(i + 2, j + 2)))
						hitList.add(new Quadruple<Integer>(i, j, i + 2, j + 2));
					if (Move.HIT == simulateMove(startPosition, new Pair<Integer, Integer>(i + 2, j - 2)))
						hitList.add(new Quadruple<Integer>(i, j, i + 2, j - 2));
					if (Move.HIT == simulateMove(startPosition, new Pair<Integer, Integer>(i - 2, j + 2)))
						hitList.add(new Quadruple<Integer>(i, j, i - 2, j + 2));
					if (Move.HIT == simulateMove(startPosition, new Pair<Integer, Integer>(i - 2, j - 2)))
						hitList.add(new Quadruple<Integer>(i, j, i - 2, j - 2));

					if (hitList.isEmpty()) {
						if (Move.MOVED == simulateMove(startPosition, new Pair<Integer, Integer>(i + 1, j + 1)))
							moveList.add(new Quadruple<Integer>(i, j, i + 1, j + 1));
						if (Move.MOVED == simulateMove(startPosition, new Pair<Integer, Integer>(i + 1, j - 1)))
							moveList.add(new Quadruple<Integer>(i, j, i + 1, j - 1));
						if (Move.MOVED == simulateMove(startPosition, new Pair<Integer, Integer>(i - 1, j + 1)))
							moveList.add(new Quadruple<Integer>(i, j, i - 1, j + 1));
						if (Move.MOVED == simulateMove(startPosition, new Pair<Integer, Integer>(i - 1, j - 1)))
							moveList.add(new Quadruple<Integer>(i, j, i - 1, j - 1));
					}
				}

			}
		}
		if (hitList.isEmpty())
			return moveList;
		else
			return hitList;
	}

	Pair<Integer, Integer> countMoves(Color player) {
		// znajdujemy wszystkie pionki
		Pair<Integer, Integer> moveCounter = new Pair<Integer, Integer>(0, 0);
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (gameBoard[i][j].getColor() == player) {
					Pair<Integer, Integer> tempCounter = simulatePiece(i, j);
					moveCounter.first += tempCounter.first;
					moveCounter.second += tempCounter.second;

				}

			}
		}
		return moveCounter;
	}

	Pair<Integer, Integer> simulatePiece(int y, int x) {
		int moveCounter = 0;
		int hitCounter = 0;
		Pair<Integer, Integer> startPosition = new Pair<Integer, Integer>(y, x);

		moveCounter += (Move.MOVED == simulateMove(startPosition, new Pair<Integer, Integer>(y + 1, x + 1))) ? 1 : 0;
		moveCounter += (Move.MOVED == simulateMove(startPosition, new Pair<Integer, Integer>(y + 1, x - 1))) ? 1 : 0;
		moveCounter += (Move.MOVED == simulateMove(startPosition, new Pair<Integer, Integer>(y - 1, x - 1))) ? 1 : 0;
		moveCounter += (Move.MOVED == simulateMove(startPosition, new Pair<Integer, Integer>(y - 1, x + 1))) ? 1 : 0;

		hitCounter += (Move.HIT == simulateMove(startPosition, new Pair<Integer, Integer>(y + 2, x + 2)))
				? ((canHitAgain(y + 2, x + 2)) ? +2 : +1) : 0;
		hitCounter += (Move.HIT == simulateMove(startPosition, new Pair<Integer, Integer>(y + 2, x - 2)))
				? ((canHitAgain(y + 2, x - 2)) ? +2 : +1) : 0;
		hitCounter += (Move.HIT == simulateMove(startPosition, new Pair<Integer, Integer>(y - 2, x - 2)))
				? ((canHitAgain(y - 2, x - 2)) ? +2 : +1) : 0;
		hitCounter += (Move.HIT == simulateMove(startPosition, new Pair<Integer, Integer>(y - 2, x + 2)))
				? ((canHitAgain(y - 2, x + 2)) ? +2 : +1) : 0;
		return new Pair<Integer, Integer>(moveCounter, hitCounter);
	}

	// checks if player can kill any piece this turn
	boolean findAllHits(Color player) {
		Pair<Integer, Integer> moveCounter = countMoves(player);
		if (moveCounter.second > 0)
			return true;
		else
			return false;
	}

	boolean canHitAgain(int x, int y) {
		Pair<Integer, Integer> moveCounter = simulatePiece(x, y);
		if (moveCounter.second > 0)
			return true;
		else
			return false;
	}
}
