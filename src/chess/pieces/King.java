package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.Color;

public class King extends ChessPiece {
	
	private ChessMatch chessMatch;

	public King(Board board, Color color, ChessMatch chessMatch) {
		super(board, color);
		this.chessMatch = chessMatch;
	}
	
	@Override
	public String toString() {
		return "K";
	}

	@Override
	public boolean[][] possibleMoves() {
		boolean [][] matrix = new boolean[getBoard().getRows()][getBoard().getColumns()];
		Position p = new Position(0, 0);
		
		p.setValues(position.getRow() - 1, position.getColumn());
		if (getBoard().positionExists(p) && canMove(p)) {
			matrix[p.getRow()][p.getColumn()] = true;
		}
		
		p.setValues(position.getRow() - 1, position.getColumn() + 1);
		if (getBoard().positionExists(p) && canMove(p)) {
			matrix[p.getRow()][p.getColumn()] = true;
		}
		
		p.setValues(position.getRow(), position.getColumn() + 1);
		if (getBoard().positionExists(p) && canMove(p)) {
			matrix[p.getRow()][p.getColumn()] = true;
		}
		
		p.setValues(position.getRow() + 1, position.getColumn() + 1);
		if (getBoard().positionExists(p) && canMove(p)) {
			matrix[p.getRow()][p.getColumn()] = true;
		}
		
		p.setValues(position.getRow() + 1, position.getColumn());
		if (getBoard().positionExists(p) && canMove(p)) {
			matrix[p.getRow()][p.getColumn()] = true;
		}
		
		p.setValues(position.getRow() + 1, position.getColumn() - 1);
		if (getBoard().positionExists(p) && canMove(p)) {
			matrix[p.getRow()][p.getColumn()] = true;
		}
		
		p.setValues(position.getRow(), position.getColumn() - 1);
		if (getBoard().positionExists(p) && canMove(p)) {
			matrix[p.getRow()][p.getColumn()] = true;
		}
		
		p.setValues(position.getRow() - 1, position.getColumn() - 1);
		if (getBoard().positionExists(p) && canMove(p)) {
			matrix[p.getRow()][p.getColumn()] = true;
		}
		
		if (getMoveCount() == 0 && !chessMatch.getCheck()) {
			Position rPosT1 = new Position(position.getRow(), position.getColumn() + 3);
			if (testRookCastling(rPosT1)) {
				Position p1 = new Position(position.getRow(), position.getColumn() + 1);
				Position p2 = new Position(position.getRow(), position.getColumn() + 2);
				if (getBoard().piece(p1) == null && getBoard().piece(p2) == null) {
					matrix[position.getRow()][position.getColumn() + 2] = true;
				}
			}
		}
		
		if (getMoveCount() == 0 && !chessMatch.getCheck()) {
			Position rPosT2 = new Position(position.getRow(), position.getColumn() - 4);
			if (testRookCastling(rPosT2)) {
				Position p1 = new Position(position.getRow(), position.getColumn() - 1);
				Position p2 = new Position(position.getRow(), position.getColumn() - 2);
				Position p3 = new Position(position.getRow(), position.getColumn() - 3);
				if (getBoard().piece(p1) == null && getBoard().piece(p2) == null && getBoard().piece(p3) == null) {
					matrix[position.getRow()][position.getColumn() -2] = true;
				}
			}
		}
		
		return matrix;
	}
	
	private boolean testRookCastling(Position position) {
		ChessPiece p = (ChessPiece) getBoard().piece(position);
		return p != null && p instanceof Rook && p.getColor() == getColor() && p.getMoveCount() == 0;
	}
	
	private boolean canMove(Position position) {
		ChessPiece piece = (ChessPiece)getBoard().piece(position);
		return piece == null || piece.getColor() != getColor();
	}

}
