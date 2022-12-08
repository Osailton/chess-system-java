package chess.pieces;

import boardgame.Board;
import chess.ChessPiece;
import chess.Color;

public class Rook extends ChessPiece {

	public Rook(Board board, Color color) {
		super(board, color);
	}
	
	@Override
	public String toString() {
		if(this.getColor() == Color.WHITE) {
			return "\u2656";
		} else {
			return "\u265C";
		}
	}

}
