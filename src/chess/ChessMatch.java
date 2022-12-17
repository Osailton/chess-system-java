package chess;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.Bishop;
import chess.pieces.King;
import chess.pieces.Knight;
import chess.pieces.Pawn;
import chess.pieces.Queen;
import chess.pieces.Rook;

public class ChessMatch {
	
	private Board board;
	private int turn;
	private Color currentPlayer;
	private boolean check;
	private boolean checkMate;
	
	private List<Piece> piecesOnBoard = new ArrayList<>();
	private List<Piece> capturedPieces = new ArrayList<>();
	
	public ChessMatch() {
		this.board = new Board(8, 8);
		this.turn = 1;
		this.currentPlayer = Color.WHITE;
		initialSetup();
	}
	
	public int getTurn() {
		return this.turn;
	}

	public Color getCurrentPlayer() {
		return this.currentPlayer;
	}
	
	public boolean getCheck() {
		return this.check;
	}
	
	public boolean getCheckMate() {
		return this.checkMate;
	}

	public ChessPiece[][] getPieces() {
		ChessPiece[][] piecesMat = new ChessPiece[this.board.getRows()][this.board.getColumns()];
		for (int i=0; i<board.getRows(); i++) {
			for (int j=0; j<board.getColumns(); j++) {
				piecesMat[i][j] = (ChessPiece) this.board.piece(i, j);
			}
		}
		return piecesMat;
	}
	
	public boolean[][] possibleMoves(ChessPosition originPosition){
		Position position = originPosition.toPosition();
		validateOriginPosition(position);
		return board.piece(position).possibleMoves();
	}
	
	public ChessPiece performChessMove(ChessPosition originPosition, ChessPosition targetPosition) {
		Position origin = originPosition.toPosition();
		Position target = targetPosition.toPosition();
		validateOriginPosition(origin);
		validateTargetPosition(origin, target);
		Piece captured = makeMove(origin, target);
		
		if (testCheck(currentPlayer)) {
			undoMove(origin, target, captured);
			throw new ChessException("You cannot put your king in a check position!");
		}
		
		check = (testCheck(opponent(currentPlayer))) ? true : false;
		
		if (testCheckMate(opponent(currentPlayer))) {
			this.checkMate = true;
		} else {
			nextTurn();
		}
		
		return (ChessPiece) captured;
	}
	
	private Piece makeMove(Position origin, Position target) {
		ChessPiece p = (ChessPiece)this.board.removePiece(origin);
		p.increaseMoveCount();
		Piece captured = this.board.removePiece(target);
		this.board.placePiece(p, target);
		
		if (captured != null) {
			this.piecesOnBoard.remove(captured);
			this.capturedPieces.add(captured);
		}
		
		return captured;
	}
	
	private void undoMove(Position origin, Position target, Piece captured) {
		ChessPiece p = (ChessPiece)this.board.removePiece(target);
		p.decreaseMoveCount();
		this.board.placePiece(p, origin);
		
		if (captured != null) {
			this.board.placePiece(captured, target);
			this.capturedPieces.remove(captured);
			this.piecesOnBoard.add(captured);
		}
	}
	
	private void validateOriginPosition(Position position) {
		if (!this.board.thereIsAPiece(position)) {
			throw new ChessException("There is no piece in this position!");
		}
		if (this.currentPlayer != ((ChessPiece)this.board.piece(position)).getColor()) {
			throw new ChessException("You cannot move a piece of your adversary!");
		}
		if (!this.board.piece(position).isThereAnyPossibleMove()) {
			throw new ChessException("There's no possible movement for this piece!");
		}
	}
	
	private void validateTargetPosition(Position origin, Position target) {
		if (!board.piece(origin).possibleMove(target)) {
			throw new ChessException("Isn't possible to move to the targe with this piece!");
		}
	}
	
	private Color opponent(Color color) {
		return (color == Color.WHITE) ? Color.BLACK : Color.WHITE;
	}
	
	private ChessPiece king(Color color) {
		List<Piece> list = this.piecesOnBoard.stream().filter(
				x -> ((ChessPiece)x).getColor() == color).collect(Collectors.toList());
		for (Piece p : list) {
			if (p instanceof King) {
				return (ChessPiece)p;
			}
		}
		throw new IllegalStateException("There is no " + color + " king on the board!");
	}
	
	private boolean testCheck(Color color) {
		Position kPosition = king(color).getChessPosition().toPosition();
		List<Piece> adversaryPieces = this.piecesOnBoard.stream().filter(
				x -> ((ChessPiece)x).getColor() == opponent(color)).collect(Collectors.toList());
		
		for (Piece p : adversaryPieces) {
			boolean[][] matrix = p.possibleMoves();
			if (matrix[kPosition.getRow()][kPosition.getColumn()]) {
				return true;
			}
		}
		
		return false;
	}
	
	private boolean testCheckMate(Color color) {
		if (!testCheck(color)) {
			return false;
		}
		
		List<Piece> list = this.piecesOnBoard.stream().filter(
				x -> ((ChessPiece)x).getColor() == color).collect(Collectors.toList());
		
		for (Piece p : list) {
			boolean[][] matrix = p.possibleMoves();
			for (int i=0; i<this.board.getRows(); i++) {
				for (int j=0; j<this.board.getColumns(); j++) {
					if (matrix[i][j]) {
						Position origin = ((ChessPiece)p).getChessPosition().toPosition();
						Position target = new Position(i, j);
						Piece captured = makeMove(origin, target);
						boolean testCheck = testCheck(color);
						undoMove(origin, target, captured);
						if (!testCheck) {
							return false;
						}
					}
				}
			}
		}
		
		return true;
	}
	
	private void placeNewPiece(char column, int row, ChessPiece piece) {
		this.board.placePiece(piece, new ChessPosition(column, row).toPosition());
		this.piecesOnBoard.add(piece);
	}
	
	private void nextTurn() {
		this.turn ++;
		this.currentPlayer = (this.currentPlayer == Color.WHITE) ? Color.BLACK : Color.WHITE;
	}
	
	private void initialSetup() {
		placeNewPiece('a', 1, new Rook(board, Color.WHITE));
		placeNewPiece('b', 1, new Knight(board, Color.WHITE));
		placeNewPiece('c', 1, new Bishop(board, Color.WHITE));
		placeNewPiece('d', 1, new Queen(board, Color.WHITE));
		placeNewPiece('e', 1, new King(board, Color.WHITE));
		placeNewPiece('f', 1, new Bishop(board, Color.WHITE));
		placeNewPiece('g', 1, new Knight(board, Color.WHITE));
		placeNewPiece('h', 1, new Rook(board, Color.WHITE));
        placeNewPiece('a', 2, new Pawn(board, Color.WHITE));
        placeNewPiece('b', 2, new Pawn(board, Color.WHITE));
        placeNewPiece('c', 2, new Pawn(board, Color.WHITE));
        placeNewPiece('d', 2, new Pawn(board, Color.WHITE));
        placeNewPiece('e', 2, new Pawn(board, Color.WHITE));
        placeNewPiece('f', 2, new Pawn(board, Color.WHITE));
        placeNewPiece('g', 2, new Pawn(board, Color.WHITE));
        placeNewPiece('h', 2, new Pawn(board, Color.WHITE));
        
        placeNewPiece('a', 8, new Rook(board, Color.BLACK));
        placeNewPiece('b', 8, new Knight(board, Color.BLACK));
        placeNewPiece('c', 8, new Bishop(board, Color.BLACK));
        placeNewPiece('d', 8, new Queen(board, Color.BLACK));
        placeNewPiece('e', 8, new King(board, Color.BLACK));
        placeNewPiece('f', 8, new Bishop(board, Color.BLACK));
        placeNewPiece('g', 8, new Knight(board, Color.BLACK));
        placeNewPiece('h', 8, new Rook(board, Color.BLACK));
        placeNewPiece('a', 7, new Pawn(board, Color.BLACK));
        placeNewPiece('b', 7, new Pawn(board, Color.BLACK));
        placeNewPiece('c', 7, new Pawn(board, Color.BLACK));
        placeNewPiece('d', 7, new Pawn(board, Color.BLACK));
        placeNewPiece('e', 7, new Pawn(board, Color.BLACK));
        placeNewPiece('f', 7, new Pawn(board, Color.BLACK));
        placeNewPiece('g', 7, new Pawn(board, Color.BLACK));
        placeNewPiece('h', 7, new Pawn(board, Color.BLACK));
	}
}
