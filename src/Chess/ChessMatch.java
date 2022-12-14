package Chess;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import Chess.pieces.Bishop;
import Chess.pieces.King;
import Chess.pieces.Knight;
import Chess.pieces.Pawn;
import Chess.pieces.Queen;
import Chess.pieces.Rook;
import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;

	
	

public class ChessMatch {

	private  Board board;
	private int turn;
	private Color currentPlayer;
	private boolean check;
	private boolean checkMate;
	
	private List<Piece> piecesOnTheBoard = new ArrayList<>();
	private List<Piece> capturedPieces = new ArrayList<>();

	public ChessMatch() {
		board = new Board(8, 8);
		turn = 1;
		currentPlayer = Color.WHITE;
		initialSetup();
	}
	
	public int getTurn() {
		return turn;
	}
	
	public Color getCurrentPlayer() {
		return currentPlayer;
	}
	
	public boolean getCheck() {
		return check;
	}
	public boolean getChekMate() {
		return checkMate;
	}

	public ChessPiece[][] getPieces() {
		ChessPiece[][] mat = new ChessPiece[board.getRows()][board.getColumns()];
		for (int i = 0; i < board.getRows(); i++) {
			for (int j = 0; j < board.getColumns(); j++) {
				mat[i][j] = (ChessPiece) board.piece(i, j);
			}
		}
		return mat;
	}
	
	public boolean [][] possibleMoves (ChessPosition sourcePosition){
		Position position = sourcePosition.toPosition();
		ValidateSourcePosition(position);
		return board.piece(position).possibleMoves();
	}
	
	public ChessPiece peformChessMove(ChessPosition sourcePosition, ChessPosition targetPosition) {
		Position source = sourcePosition.toPosition();
		Position target = targetPosition.toPosition();
		ValidateSourcePosition(source);
		ValidateTargetPosition(source,target);
		Piece capturedPiece = makeMove(source,target );
		
		if(testCheck(currentPlayer)) {
			undoMove(source, target, capturedPiece);
			throw new ChessExcepcion("you can't put yourself in check");
		}
		
		check = (testCheck(opponent(currentPlayer))) ? true : false;
		
		if (testCheckMate(opponent(currentPlayer))) {
			checkMate = true;
		}
		else {
		nextTurn();
	}
		return (ChessPiece)capturedPiece;
	}
		
		private  Piece makeMove(Position source, Position target) {
			ChessPiece p = (ChessPiece)board.removePiece(source);
			p.increaseMoveCount();
			Piece capturedPiece = board.removePiece(target);   
			board.placePiece(p, target);
			
			if(capturedPiece != null) {
				piecesOnTheBoard.remove(capturedPiece);
				capturedPieces.add(capturedPiece);
			}
			
			return capturedPiece;
		}
		
	
		private void undoMove(Position source, Position target, Piece capturedPiece) {
			ChessPiece p = (ChessPiece) board.removePiece(target);
			p.deacreaseMoveCount();
			board.placePiece(p, source);
			
			if(capturedPiece != null) {
				board.placePiece(capturedPiece, target);
				capturedPieces.remove(capturedPiece);
				piecesOnTheBoard.add(capturedPiece);
			}
	}
	
	    private  void ValidateSourcePosition(Position position) {
			if (!board.thereIsAPiece(position)) {
				throw new ChessExcepcion("There is no piece on source position");	
		}	
			if(currentPlayer != ((ChessPiece)board.piece(position)).getColor()) {
				
				throw new ChessExcepcion("The chosen piece is not yours");
		
			}
			if(!board.piece(position).IsThereAnyPossibleMove()) {
				throw new ChessExcepcion("There is no possible moves for the chosen piece");
			}
	}
		private void ValidateTargetPosition(Position source, Position target) {
			if(!board.piece(source).possibleMove(target)) {
				throw new ChessExcepcion("The chosen piece can't move to target position ");
			}
		}
		
		private void nextTurn() {
			turn++;
			currentPlayer = (currentPlayer == Color.WHITE) ? Color.BLACK : Color.WHITE;
		}
		
		private Color opponent (Color color) {
			return (color == Color.WHITE) ? Color.BLACK : Color.WHITE;
		}
		
		private ChessPiece King(Color color) {
			List <Piece> list = piecesOnTheBoard.stream() .filter(x -> ((ChessPiece)x).getColor() == color).collect(Collectors.toList());
			for (Piece p : list) {
				if(p instanceof King) {
					return (ChessPiece)p;
				}
			}
			throw new IllegalStateException("There is no" + color + "King on the board");
		}
		
		
		private boolean testCheck(Color color) {
			Position KingPosition = King(color).getChessPosition().toPosition();
			List <Piece> opponentPieces = piecesOnTheBoard.stream() .filter(x -> ((ChessPiece)x).getColor() == opponent(color)).collect(Collectors.toList());
			for(Piece p : opponentPieces) {
				boolean[][] mat = p.possibleMoves();
				if(mat[KingPosition.getRow()][KingPosition.getColumn()]) {
					return true;
				}
			}
			return false;
		}
		
			private void placeNewPiece(char column, int row, ChessPiece piece) {
				board.placePiece(piece, new ChessPosition(column, row).toPosition());
				piecesOnTheBoard.add(piece);
			}
			
			private boolean testCheckMate(Color color) {
				if(!testCheck(color)) {
					return false;
				}
				
		List <Piece> list = piecesOnTheBoard.stream() .filter(x -> ((ChessPiece)x).getColor() == color).collect(Collectors.toList());
			for(Piece p : list) {
				boolean [][] mat = p.possibleMoves();
				for(int i = 0; i<board.getRows(); i++) {
					for(int j =0; j<board.getColumns(); j++) {
						if(mat [i][j]) {
							Position source = ((ChessPiece)p).getChessPosition().toPosition();
							Position target = new Position (i, j);
							Piece capturedPiece = makeMove(source,target);
							boolean testCheck = testCheck(color);
							undoMove(source, target, capturedPiece);
							if(!testCheck) {
								return false;
							}
						}
					}
				}
			}	
			
			return true;
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
				placeNewPiece('g', 8, new Knight(board, Color.WHITE));
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
