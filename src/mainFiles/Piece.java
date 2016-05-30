package mainFiles;

import java.awt.Color;

public enum Piece {
	EMPTY, WHITE_PAWN, WHITE_KNIGHT, BLACK_PAWN, BLACK_KNIGHT;

	Color getColor() {
		if (this == EMPTY)
			return Color.GRAY;
		return (this == WHITE_PAWN || this == WHITE_KNIGHT) ? Color.WHITE : Color.BLACK;
	}

	boolean isMan() {
		return (this == WHITE_PAWN || this == BLACK_PAWN) ? true : false;
	}

	boolean isKnight() {
		return (this == WHITE_KNIGHT || this == BLACK_KNIGHT) ? true : false;
	}

	Piece promote() {
		return (this == WHITE_PAWN) ? WHITE_KNIGHT : BLACK_KNIGHT;
	}

	public static Color reverseColor(Color player) {
		return (player == Color.BLACK) ? Color.WHITE : Color.BLACK;
	}

	public static Color reverseColor(Piece player) {
		return (player.getColor() == Color.BLACK) ? Color.WHITE : Color.BLACK;
	}
}
