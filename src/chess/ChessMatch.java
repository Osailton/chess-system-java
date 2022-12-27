package chess;

import java.security.InvalidParameterException;
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
	private ChessPiece enPassantVunerable;
	private ChessPiece promoted;
	
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
	
	public ChessPiece getEnPassantVunerable() {
		return this.enPassantVunerable;
	}
	
	public ChessPiece getPromoted() {
		return this.promoted;
	}

	public ChessPiece[][] getPieces() {
		ChessPiece[][] piecesMat = new ChessPiece[this.board.getRows()][this.board.getColumns()];
		for (int i=0; i<this.board.getRows(); i++) {
			for (int j=0; j<this.board.getColumns(); j++) {
				piecesMat[i][j] = (ChessPiece) this.board.piece(i, j);
			}
		}
		return piecesMat;
	}
	
	public boolean[][] possibleMoves(ChessPosition originPosition){
		Position position = originPosition.toPosition();
		validateOriginPosition(position);
		return this.board.piece(position).possibleMoves();
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
		
		ChessPiece movedPiece = (ChessPiece)this.board.piece(target);
		
		this.promoted = null;
		if (movedPiece instanceof Pawn) {
			if ((movedPiece.getColor() == Color.WHITE && target.getRow() ==0)
					|| (movedPiece.getColor()== Color.BLACK && target.getRow() == 7)) {
				this.promoted = (ChessPiece)this.board.piece(target);
				this.promoted = replacePromotedPiece("Q");
			}
		}
		
		check = (testCheck(opponent(currentPlayer))) ? true : false;
		
		if (testCheckMate(opponent(currentPlayer))) {
			this.checkMate = true;
		} else {
			nextTurn();
		}
		
		if (movedPiece instanceof Pawn && (target.getRow() == origin.getRow() - 2
				|| target.getRow() == origin.getRow() + 2)) {
			this.enPassantVunerable = movedPiece;
		} else {
			this.enPassantVunerable = null;
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
		
		if (p instanceof King && target.getColumn() == origin.getColumn() + 2) {
			Position originRook = new Position(origin.getRow(), origin.getColumn() + 3);
			Position targetRook = new Position(origin.getRow(), origin.getColumn() + 1);
			ChessPiece rook = (ChessPiece)this.board.removePiece(originRook);
			this.board.placePiece(rook, targetRook);
			rook.increaseMoveCount();
		}
		
		if (p instanceof King && target.getColumn() == origin.getColumn() - 2) {
			Position originRook = new Position(origin.getRow(), origin.getColumn() - 4);
			Position targetRook = new Position(origin.getRow(), origin.getColumn() - 1);
			ChessPiece rook = (ChessPiece)this.board.removePiece(originRook);
			this.board.placePiece(rook, targetRook);
			rook.increaseMoveCount();
		}
		
		if (p instanceof Pawn) {
			if (origin.getColumn() != target.getColumn() && captured == null) {
				Position capPawnPosition;
				if (p.getColor() == Color.WHITE) {
					capPawnPosition = new Position(target.getRow() + 1, target.getColumn());
				}
				else {
					capPawnPosition = new Position(target.getRow() - 1, target.getColumn());
				}
				
				captured = this.board.removePiece(capPawnPosition);
				this.capturedPieces.add(captured);
				this.piecesOnBoard.remove(captured);
			}
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
		
		if (p instanceof King && target.getColumn() == origin.getColumn() + 2) {
			Position originRook = new Position(origin.getRow(), origin.getColumn() + 3);
			Position targetRook = new Position(origin.getRow(), origin.getColumn() + 1);
			ChessPiece rook = (ChessPiece)this.board.removePiece(targetRook);
			this.board.placePiece(rook, originRook);
			rook.decreaseMoveCount();
		}
		
		if (p instanceof King && target.getColumn() == origin.getColumn() - 2) {
			Position originRook = new Position(origin.getRow(), origin.getColumn() - 4);
			Position targetRook = new Position(origin.getRow(), origin.getColumn() - 1);
			ChessPiece rook = (ChessPiece)this.board.removePiece(targetRook);
			this.board.placePiece(rook, originRook);
			rook.decreaseMoveCount();
		}
		
		if (p instanceof Pawn) {
			if (origin.getColumn() != target.getColumn() && captured == this.enPassantVunerable) {
				ChessPiece pawn = (ChessPiece)this.board.removePiece(target);
				Position capPawnPosition;
				if (p.getColor() == Color.WHITE) {
					capPawnPosition = new Position(3, target.getColumn());
				}
				else {
					capPawnPosition = new Position(4, target.getColumn());
				}
				
				this.board.placePiece(pawn, capPawnPosition);
			}
		}
	}
	
	public ChessPiece replacePromotedPiece(String pieceCode) {
		if (this.promoted == null) {
			throw new IllegalStateException("There is no piece to be promoted!");
		}
		if (!pieceCode.equals("B") && !pieceCode.equals("N")
				&& !pieceCode.equals("R") && !pieceCode.equals("Q")) {
			throw new InvalidParameterException("Invalid type for promotion!");
		}
		
		Position position = this.promoted.getChessPosition().toPosition();
		Piece p = board.removePiece(position);
		this.piecesOnBoard.remove(p);
		
		ChessPiece newPiece = newPiece(pieceCode, this.promoted.getColor());
		this.board.placePiece(newPiece, position);
		this.piecesOnBoard.add(newPiece);
		
		return newPiece;
	}
	
	private ChessPiece newPiece(String pieceCode, Color color) {
		String code = pieceCode.toLowerCase();
		switch (pieceCode) {
		case "b": {
			return new Bishop(this.board, color);
		}
		case "n": {
			return new Knight(this.board, color);
		}
		case "r": {
			return new Rook(this.board, color);
		}
		default:
			return new Queen(this.board, color);
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
		if (!this.board.piece(origin).possibleMove(target)) {
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
		placeNewPiece('a', 1, new Rook(this.board, Color.WHITE));
		placeNewPiece('b', 1, new Knight(this.board, Color.WHITE));
		placeNewPiece('c', 1, new Bishop(this.board, Color.WHITE));
		placeNewPiece('d', 1, new Queen(this.board, Color.WHITE));
		placeNewPiece('e', 1, new King(this.board, Color.WHITE, this));
		placeNewPiece('f', 1, new Bishop(this.board, Color.WHITE));
		placeNewPiece('g', 1, new Knight(this.board, Color.WHITE));
		placeNewPiece('h', 1, new Rook(this.board, Color.WHITE));
        placeNewPiece('a', 2, new Pawn(this.board, Color.WHITE, this));
        placeNewPiece('b', 2, new Pawn(this.board, Color.WHITE, this));
        placeNewPiece('c', 2, new Pawn(this.board, Color.WHITE, this));
        placeNewPiece('d', 2, new Pawn(this.board, Color.WHITE, this));
        placeNewPiece('e', 2, new Pawn(this.board, Color.WHITE, this));
        placeNewPiece('f', 2, new Pawn(this.board, Color.WHITE, this));
        placeNewPiece('g', 2, new Pawn(this.board, Color.WHITE, this));
        placeNewPiece('h', 2, new Pawn(this.board, Color.WHITE, this));
        
        placeNewPiece('a', 8, new Rook(this.board, Color.BLACK));
        placeNewPiece('b', 8, new Knight(this.board, Color.BLACK));
        placeNewPiece('c', 8, new Bishop(this.board, Color.BLACK));
        placeNewPiece('d', 8, new Queen(this.board, Color.BLACK));
        placeNewPiece('e', 8, new King(this.board, Color.BLACK, this));
        placeNewPiece('f', 8, new Bishop(this.board, Color.BLACK));
        placeNewPiece('g', 8, new Knight(this.board, Color.BLACK));
        placeNewPiece('h', 8, new Rook(this.board, Color.BLACK));
        placeNewPiece('a', 7, new Pawn(this.board, Color.BLACK, this));
        placeNewPiece('b', 7, new Pawn(this.board, Color.BLACK, this));
        placeNewPiece('c', 7, new Pawn(this.board, Color.BLACK, this));
        placeNewPiece('d', 7, new Pawn(this.board, Color.BLACK, this));
        placeNewPiece('e', 7, new Pawn(this.board, Color.BLACK, this));
        placeNewPiece('f', 7, new Pawn(this.board, Color.BLACK, this));
        placeNewPiece('g', 7, new Pawn(this.board, Color.BLACK, this));
        placeNewPiece('h', 7, new Pawn(this.board, Color.BLACK, this));
	}
}
