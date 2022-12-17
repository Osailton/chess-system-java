package chess;

import java.util.ArrayList;
import java.util.List;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.King;
import chess.pieces.Rook;

public class ChessMatch {
	
	private Board board;
	private int turn;
	private Color currentPlayer;
	
	private List<Piece> piecesOnBoard = new ArrayList<>();
	private List<Piece> capturedPieces = new ArrayList<>();
	
	public ChessMatch() {
		this.board = new Board(8, 8);
		this.turn = 1;
		this.currentPlayer = Color.WHITE;
		initialSetup();
	}
	
	public int getTurn() {
		return turn;
	}

	public Color getCurrentPlayer() {
		return currentPlayer;
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
		nextTurn();
		return (ChessPiece) captured;
	}
	
	private Piece makeMove(Position origin, Position target) {
		Piece p = this.board.removePiece(origin);
		Piece captured = this.board.removePiece(target);
		this.board.placePiece(p, target);
		
		if (captured != null) {
			this.piecesOnBoard.remove(captured);
			this.capturedPieces.add(captured);
		}
		
		return captured;
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
	
	private void placeNewPiece(char column, int row, ChessPiece piece) {
		this.board.placePiece(piece, new ChessPosition(column, row).toPosition());
		this.piecesOnBoard.add(piece);
	}
	
	private void nextTurn() {
		this.turn ++;
		this.currentPlayer = (this.currentPlayer == Color.WHITE) ? Color.BLACK : Color.WHITE;
	}
	
	private void initialSetup() {
		placeNewPiece('c', 1, new Rook(board, Color.WHITE));
        placeNewPiece('c', 2, new Rook(board, Color.WHITE));
        placeNewPiece('d', 2, new Rook(board, Color.WHITE));
        placeNewPiece('e', 2, new Rook(board, Color.WHITE));
        placeNewPiece('e', 1, new Rook(board, Color.WHITE));
        placeNewPiece('d', 1, new King(board, Color.WHITE));

        placeNewPiece('c', 7, new Rook(board, Color.BLACK));
        placeNewPiece('c', 8, new Rook(board, Color.BLACK));
        placeNewPiece('d', 7, new Rook(board, Color.BLACK));
        placeNewPiece('e', 7, new Rook(board, Color.BLACK));
        placeNewPiece('e', 8, new Rook(board, Color.BLACK));
        placeNewPiece('d', 8, new King(board, Color.BLACK));
	}
}
