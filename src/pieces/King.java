package pieces;

import chess.Chess;

public class King extends Piece {

    private boolean hasMoved = false;

    public King(String value) {
        super(value);
    }

    public boolean isMoveValid(String oldPos, String newPos) {
        if (!Chess.board.containsKey(newPos)) return false;

        int x = Math.abs(oldPos.charAt(0) - newPos.charAt(0));
        int y = Math.abs(oldPos.charAt(1) - newPos.charAt(1));

        if ((x == 0 && y == 1) || (x == 1 && y == 0) || (x == y && x == 1)) {
            return true;
        }

        if (y == 0 && x == 2) {
            return isCastlingValid(oldPos, newPos);
        }

        return false;
    }

    private boolean isCastlingValid(String oldPos, String newPos) {
        if (hasMoved) return false;

        int rank = oldPos.charAt(1) - '0';
        int dir = (newPos.charAt(0) > oldPos.charAt(0)) ? 1 : -1;
        String rookPos = (dir == 1) ? "h" + rank : "a" + rank;

        Piece rook = Chess.board.get(rookPos);
        if (!(rook instanceof Rook) || ((Rook) rook).hasMoved()) return false;

        char file = (char) (oldPos.charAt(0) + dir);
        while (file != rookPos.charAt(0)) {
            if (!(Chess.board.get(file + "" + rank) instanceof EmptySquare)) {
                return false;
            }
            file += dir;
        }

        return true;
    }

    public void move(String oldPos, String newPos, char promopiece) {
		int fileChange = Math.abs(oldPos.charAt(0) - newPos.charAt(0));
		int rankChange = Math.abs(oldPos.charAt(1) - newPos.charAt(1));
	
		if (rankChange == 0 && fileChange == 2) {
			// This is a castling move (2-square move horizontally)
			performCastling(oldPos, newPos);
		} else {
			// Regular king move (1 square in any direction)
			Piece piece_oldPos = Chess.board.get(oldPos);
			Chess.board.put(newPos, piece_oldPos);
			if (Chess.isBlackBox(oldPos.charAt(0), oldPos.charAt(1) - '0')) {
				Chess.board.put(oldPos, new EmptySquare("##"));
			} else {
				Chess.board.put(oldPos, new EmptySquare("  "));
			}
		}
	
		sethasMoved(true);  // King has moved in both cases (castling or regular)
	}
	
	

    private void performCastling(String oldPos, String newPos) {
		int rank = oldPos.charAt(1) - '0';
		int dir = (newPos.charAt(0) > oldPos.charAt(0)) ? 1 : -1;
	
		// Move king
		Chess.board.put(newPos, this);
		Chess.board.put(oldPos, Chess.isBlackBox(oldPos.charAt(0), rank) ? new EmptySquare("##") : new EmptySquare("  "));
	
		// Move rook
		String oldRookPos = (dir == 1) ? "h" + rank : "a" + rank;
		String newRookPos = (dir == 1) ? "f" + rank : "d" + rank;
	
		Chess.board.put(newRookPos, Chess.board.get(oldRookPos));
		Chess.board.put(oldRookPos, Chess.isBlackBox(oldRookPos.charAt(0), rank) ? new EmptySquare("##") : new EmptySquare("  "));
	
		((Rook) Chess.board.get(newRookPos)).setHasMoved(true);
	}
	
	

    public void sethasMoved(boolean hasMoved) {
		this.hasMoved = hasMoved;
	}
	public boolean hasMoved() {
		return hasMoved;
	}
}
