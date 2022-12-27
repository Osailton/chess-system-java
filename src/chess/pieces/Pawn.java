package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.Color;

public class Pawn extends ChessPiece {
	
	private ChessMatch chessMatch;

	public Pawn(Board board, Color color, ChessMatch chessMatch) {
		super(board, color);
		this.chessMatch = chessMatch;
	}
	
	@Override
	public String toString() {
		return "P";
	}

	@Override
	public boolean[][] possibleMoves() {
		boolean [][] matrix = new boolean[getBoard().getRows()][getBoard().getColumns()];
		Position p = new Position(0, 0);
		
		if (getColor() == Color.WHITE) {
			p.setValues(position.getRow() - 1, position.getColumn());
			if (getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)) {
				matrix[p.getRow()][p.getColumn()] = true;
			}
		
			p.setValues(position.getRow() - 2, position.getColumn());
			Position p2 = new Position(position.getRow() - 1, position.getColumn());
			if (getBoard().positionExists(p) && !getBoard().thereIsAPiece(p) 
					&& getBoard().positionExists(p2) && !getBoard().thereIsAPiece(p)
					&& getMoveCount() == 0) {
				matrix[p.getRow()][p.getColumn()] = true;
			}
			
			p.setValues(position.getRow() - 1, position.getColumn() - 1);
			if (getBoard().positionExists(p) && isThereOpponentPiece(p)) {
				matrix[p.getRow()][p.getColumn()] = true;
			}
			
			p.setValues(position.getRow() - 1, position.getColumn() + 1);
			if (getBoard().positionExists(p) && isThereOpponentPiece(p)) {
				matrix[p.getRow()][p.getColumn()] = true;
			}
			
			if (position.getRow() == 3) {
				Position leftPassant = new Position(position.getRow(), position.getColumn() - 1);
				if (getBoard().positionExists(leftPassant) && isThereOpponentPiece(leftPassant)
						&& getBoard().piece(leftPassant) == chessMatch.getEnPassantVunerable()) {
					matrix[leftPassant.getRow() - 1][leftPassant.getColumn()] = true;
				}
				Position rightPassant = new Position(position.getRow(), position.getColumn() + 1);
				if (getBoard().positionExists(rightPassant) && isThereOpponentPiece(rightPassant)
						&& getBoard().piece(rightPassant) == chessMatch.getEnPassantVunerable()) {
					matrix[rightPassant.getRow() - 1][rightPassant.getColumn()] = true;
				}
			}
		} else {
			p.setValues(position.getRow() + 1, position.getColumn());
			if (getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)) {
				matrix[p.getRow()][p.getColumn()] = true;
			}
		
			p.setValues(position.getRow() + 2, position.getColumn());
			Position p2 = new Position(position.getRow() + 1, position.getColumn());
			if (getBoard().positionExists(p) && !getBoard().thereIsAPiece(p) 
					&& getBoard().positionExists(p2) && !getBoard().thereIsAPiece(p)
					&& getMoveCount() == 0) {
				matrix[p.getRow()][p.getColumn()] = true;
			}
			
			p.setValues(position.getRow() + 1, position.getColumn() - 1);
			if (getBoard().positionExists(p) && isThereOpponentPiece(p)) {
				matrix[p.getRow()][p.getColumn()] = true;
			}
			
			p.setValues(position.getRow() + 1, position.getColumn() + 1);
			if (getBoard().positionExists(p) && isThereOpponentPiece(p)) {
				matrix[p.getRow()][p.getColumn()] = true;
			}
			
			if (position.getRow() == 4) {
				Position leftPassant = new Position(position.getRow(), position.getColumn() - 1);
				if (getBoard().positionExists(leftPassant) && isThereOpponentPiece(leftPassant)
						&& getBoard().piece(leftPassant) == chessMatch.getEnPassantVunerable()) {
					matrix[leftPassant.getRow() + 1][leftPassant.getColumn()] = true;
				}
				Position rightPassant = new Position(position.getRow(), position.getColumn() + 1);
				if (getBoard().positionExists(rightPassant) && isThereOpponentPiece(rightPassant)
						&& getBoard().piece(rightPassant) == chessMatch.getEnPassantVunerable()) {
					matrix[rightPassant.getRow() + 1][rightPassant.getColumn()] = true;
				}
			}
		}
		
		return matrix;
	}

}
