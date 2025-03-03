package chess;

import java.util.*;
import pieces.*;

public class Chess {

    enum Player { white, black }
    
    public static HashMap<String, Piece> board = new HashMap<>(70);
    private static boolean is_white_move = true;

    public static ReturnPlay play(String move) {
        ReturnPlay returnPlay = new ReturnPlay();
    
        String[] inputstr_as_arr = move.split(" ");
        if (inputstr_as_arr.length < 2) {
            if ("resign".equals(move)) {
                returnPlay.message = is_white_move ? ReturnPlay.Message.RESIGN_BLACK_WINS : ReturnPlay.Message.RESIGN_WHITE_WINS;
                returnPlay.piecesOnBoard = getBoardAsList();
                return returnPlay;
            }
            returnPlay.message = ReturnPlay.Message.ILLEGAL_MOVE;
            returnPlay.piecesOnBoard = getBoardAsList();
            return returnPlay;
        }
    
        String oldPos = inputstr_as_arr[0];
        String newPos = inputstr_as_arr[1];
        String promotion = inputstr_as_arr.length > 2 ? inputstr_as_arr[2] : null;
    
        Piece piece_oldPos = board.get(oldPos);
    
        if ((is_white_move && piece_oldPos.getvalue().charAt(0) == 'w') ||
            (!is_white_move && piece_oldPos.getvalue().charAt(0) == 'b')) {
    
            boolean is_move_valid = piece_oldPos.isMoveValid(oldPos, newPos);
            if (is_move_valid) {
                // Simulate the move temporarily
                Piece piece_at_newPos = board.get(newPos);  // Save the piece at destination (might be captured)
                piece_oldPos.move(oldPos, newPos, promotion != null && !"draw?".equals(promotion) ? promotion.charAt(0) : '0');
    
                // Check if the move puts own king in check
                boolean inCheckAfterMove = isKingInCheck(is_white_move);
    
                // Revert the move if illegal (restore board state)
                if (inCheckAfterMove) {
                    board.put(oldPos, piece_oldPos);  // Put the piece back
                    board.put(newPos, piece_at_newPos);  // Put captured piece (or empty square) back
                    returnPlay.message = ReturnPlay.Message.ILLEGAL_MOVE;  // Self-check is illegal
                    returnPlay.piecesOnBoard = getBoardAsList();
                    return returnPlay;
                }
    
                // Move is legal, proceed with game
                piece_oldPos.sethasMoved(true);
    
                if ("draw?".equals(promotion)) {
                    returnPlay.message = ReturnPlay.Message.DRAW;
                    returnPlay.piecesOnBoard = getBoardAsList();
                    return returnPlay;
                }
    
                if (isKingCaptured(!is_white_move)) {
                    returnPlay.message = is_white_move ? ReturnPlay.Message.CHECKMATE_WHITE_WINS : ReturnPlay.Message.CHECKMATE_BLACK_WINS;
                    returnPlay.piecesOnBoard = getBoardAsList();
                    return returnPlay;
                }
    
                if (isKingInCheck(!is_white_move)) {
                    returnPlay.message = ReturnPlay.Message.CHECK;
                }
    
                is_white_move = !is_white_move;
                returnPlay.piecesOnBoard = getBoardAsList();
                return returnPlay;
            }
        }
    
        returnPlay.message = ReturnPlay.Message.ILLEGAL_MOVE;
        returnPlay.piecesOnBoard = getBoardAsList();
        return returnPlay;
    }
        
    
    public static ReturnPlay start() {
        initboard();
        is_white_move = true;

        ReturnPlay returnPlay = new ReturnPlay();
        returnPlay.piecesOnBoard = getBoardAsList();
        return returnPlay;
    }

    private static void initboard() {
        for (char alpha = 'a'; alpha <= 'h'; alpha++) {
            for (int num = 1; num <= 8; num++) {
                String filerank = Character.toString(alpha) + num;
                board.put(filerank, isBlackBox(alpha, num) ? new EmptySquare("##") : new EmptySquare("  "));
                
                if (num == 8 || num == 7) {
                    addPieces(filerank, num, 'b');
                }
                if (num == 1 || num == 2) {
                    addPieces(filerank, num, 'w');
                }
            }
        }
    }

    private static boolean isKingCaptured(boolean isWhiteKing) {
        String kingSymbol = isWhiteKing ? "wK" : "bK";
        for (Piece piece : board.values()) {
            if (piece != null && kingSymbol.equals(piece.getvalue())) {
                return false;  // King still exists
            }
        }
        return true;  // King is missing = checkmate!
    }

    private static boolean isKingInCheck(boolean isWhiteKing) {
        String kingPos = null;
        String kingSymbol = isWhiteKing ? "wK" : "bK";
    
        // Find the king's current position
        for (Map.Entry<String, Piece> entry : board.entrySet()) {
            if (kingSymbol.equals(entry.getValue().getvalue())) {
                kingPos = entry.getKey();
                break;
            }
        }
    
        if (kingPos == null) {
            return false;  // Shouldn't happen normally
        }
    
        // Check if any opposing piece can attack the king's position
        for (Map.Entry<String, Piece> entry : board.entrySet()) {
            Piece piece = entry.getValue();
            if (piece instanceof EmptySquare) {
                continue;
            }
            boolean isEnemy = isWhiteKing ? piece.getvalue().charAt(0) == 'b' : piece.getvalue().charAt(0) == 'w';
            if (isEnemy && piece.isMoveValid(entry.getKey(), kingPos)) {
                return true;  // King is in check
            }
        }
        return false;
    }    

    private static void addPieces(String filerank, int num, char color) {
        String prefix = color == 'b' ? "b" : "w";
        switch (filerank.charAt(0)) {
            case 'a': case 'h':
                board.put(filerank, new Rook(prefix + "R"));
                break;
            case 'b': case 'g':
                board.put(filerank, new Knight(prefix + "N"));
                break;
            case 'c': case 'f':
                board.put(filerank, new Bishop(prefix + "B"));
                break;
            case 'd':
                board.put(filerank, new Queen(prefix + "Q"));
                break;
            case 'e':
                board.put(filerank, new King(prefix + "K"));
                break;
        }
        if (num == 7 || num == 2) {
            board.put(filerank, new Pawn(prefix + "p"));
        }
    }

    public static boolean isBlackBox(char alpha, int num) {
        return ((num % 2 == 1 && "aceg".indexOf(alpha) >= 0) ||
                (num % 2 == 0 && "bdfh".indexOf(alpha) >= 0));
    }

    public static ArrayList<ReturnPiece> getBoardAsList() {
        ArrayList<ReturnPiece> list = new ArrayList<>();
        for (char file = 'a'; file <= 'h'; file++) {
            for (int rank = 1; rank <= 8; rank++) {
                String filerank = file + Integer.toString(rank);
                Piece piece = board.get(filerank);

                if (piece instanceof EmptySquare) {
                    continue;
                }

                ReturnPiece rp = new ReturnPiece();
                rp.pieceFile = ReturnPiece.PieceFile.valueOf(String.valueOf(file));
                rp.pieceRank = rank;

                switch (piece.getvalue().charAt(1)) {
                    case 'p': rp.pieceType = piece.getvalue().charAt(0) == 'w' ? ReturnPiece.PieceType.WP : ReturnPiece.PieceType.BP; break;
                    case 'R': rp.pieceType = piece.getvalue().charAt(0) == 'w' ? ReturnPiece.PieceType.WR : ReturnPiece.PieceType.BR; break;
                    case 'N': rp.pieceType = piece.getvalue().charAt(0) == 'w' ? ReturnPiece.PieceType.WN : ReturnPiece.PieceType.BN; break;
                    case 'B': rp.pieceType = piece.getvalue().charAt(0) == 'w' ? ReturnPiece.PieceType.WB : ReturnPiece.PieceType.BB; break;
                    case 'Q': rp.pieceType = piece.getvalue().charAt(0) == 'w' ? ReturnPiece.PieceType.WQ : ReturnPiece.PieceType.BQ; break;
                    case 'K': rp.pieceType = piece.getvalue().charAt(0) == 'w' ? ReturnPiece.PieceType.WK : ReturnPiece.PieceType.BK; break;
                }
                list.add(rp);
            }
        }
        return list;
    }
}
