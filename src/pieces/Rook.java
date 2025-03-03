package pieces;

import chess.Chess;

/**
 * The Rook class is used to implement the Rook piece in the game of chess.
 * @author Aarav Makadia
 * @author Kuber Kupuriya
 */
public class Rook extends Piece {

	private boolean hasMoved = false;  // New field to track movement for castling

	public Rook(String value) {
		super(value);
	}

	/** isMoveValid takes in the src,destination of the piece's move and returns true if it is a valid move for Rook.
	 * @param oldPos is the position the piece is trying to move from
	 * @param newPos is the position the piece is trying to move to
	 * 
	 * @return true if the move is valid or false if not. 
	 */
	public boolean isMoveValid(String oldPos, String newPos) {
		if (!Chess.board.containsKey(newPos)) {
			return false;
		}
		
		String piece_oldPos = Chess.board.get(oldPos).getvalue();
		String piece_newPos = Chess.board.get(newPos).getvalue();
		
		if ((oldPos.charAt(0) == newPos.charAt(0) || oldPos.charAt(1) == newPos.charAt(1)) && !(oldPos.equals(newPos))) {
			if (Chess.board.get(newPos).getvalue().equals("  ") || Chess.board.get(newPos).getvalue().equals("##")) {
				if (isPathEmpty(oldPos, newPos)) {
					return true;
				} else {
					return false;
				}
			} else {
				if (piece_oldPos.charAt(0) == piece_newPos.charAt(0)) {
					return false;
				} else {
					if (isPathEmpty(oldPos, newPos)) {
						return true;
					} else {
						return false;
					}
				}
			}
		} else {
			return false;
		}	
	}
	
	/**
	 * move implements the actual movement, here the Rook moves from its src to the position specified 
	 * @param oldPos is the src of the current Rook Piece
	 * @param newPos is the destination for the current Rook Piece
	 */
	public void move(String oldPos, String newPos, char promopiece) {
		Piece piece_oldPos = Chess.board.get(oldPos);

		// Move piece to newPos
		Chess.board.put(newPos, piece_oldPos);

		// Make oldPos an empty box
		if (Chess.isBlackBox(oldPos.charAt(0), oldPos.charAt(1) - '0')) {
			Chess.board.put(oldPos, new EmptySquare("##"));
		} else {
			Chess.board.put(oldPos, new EmptySquare("  "));
		}

		// Set hasMoved to true after the first move
		hasMoved = true;
	}

	/**
	 * isPathEmpty checks if the path is clear for the Rook to move from its src to its destination.
	 * @param oldPos old position
	 * @param newPos new position
	 * 
	 * @return true if the path is clear otherwise false
	 */
	public boolean isPathEmpty(String oldPos, String newPos) {
		if (oldPos.charAt(0) == newPos.charAt(0)) {
			int i;
			int numoldPos = oldPos.charAt(1) - '0';
			int numnewPos = newPos.charAt(1) - '0';

			if (numoldPos < numnewPos) {
				for (i = numoldPos + 1; i < numnewPos; i++) {
					if (!isBoxEmpty(oldPos.charAt(0), i)) {
						return false;
					}
				}
			} else {
				for (i = numnewPos + 1; i < numoldPos; i++) {
					if (!isBoxEmpty(oldPos.charAt(0), i)) {
						return false;
					}
				}
			}
		} else if (oldPos.charAt(1) == newPos.charAt(1)) {
			char letter;
			char letteroldPos = oldPos.charAt(0);
			char letternewPos = newPos.charAt(0);

			if (letteroldPos < letternewPos) {
				for (letter = (char)(letteroldPos + 1); letter < letternewPos; letter++) {
					if (!isBoxEmpty(letter, oldPos.charAt(1) - '0')) {
						return false;
					}
				}
			} else {
				for (letter = (char)(letternewPos + 1); letter < letteroldPos; letter++) {
					if (!isBoxEmpty(letter, oldPos.charAt(1) - '0')) {
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * isBoxEmpty is a helper function for isPathEmpty to check if the boxes in the path of the move are empty.
	 * @param alpha  column
	 * @param num    row
	 * 
	 * @return  true if the box/square is empty, else false. 
	 */
	private static boolean isBoxEmpty(char alpha, int num) {
		String filerank = alpha + "" + num;
		return Chess.board.get(filerank).getvalue().equals("##") || Chess.board.get(filerank).getvalue().equals("  ");
	}

	/**
	 * Getter for castling support.
	 * @return true if rook has already moved, false if it hasn't.
	 */
	public boolean hasMoved() {
		return hasMoved;
	}

	/**
	 * Setter for hasMoved (useful if board resets or castling logic needs to manually reset).
	 * @param hasMoved true to mark rook as having moved.
	 */
	public void setHasMoved(boolean hasMoved) {
		this.hasMoved = hasMoved;
	}
}