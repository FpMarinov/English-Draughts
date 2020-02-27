import java.awt.*;

public class Model {

    private Player player1;
    private Player player2;
    private Board board;
    private Player activePlayer;
    private Piece activePiece;

    public Model() {
        player1 = new Player(Color.RED);
        player2 = new Player(Color.WHITE);
        board = new Board(player1,player2);
    }

    public Player checkForWinner() throws DrawException {
        if(player1.getPiecesInGame().isEmpty()) {
            return player2;
        } else if(player2.getPiecesInGame().isEmpty()) {
            return player1;
        } else {

            boolean player1HasLegalMovesLeft = playerHasLegalMovesLeft(player1);
            boolean player2HasLegalMovesLeft = playerHasLegalMovesLeft(player2);

            if(!player1HasLegalMovesLeft && !player2HasLegalMovesLeft) {
                throw new DrawException();
            } else if(!player1HasLegalMovesLeft) {
                return player2;
            } else if(!player2HasLegalMovesLeft) {
                return player1;
            } else {
                return null;
            }
        }
    }

    public boolean playerHasLegalMovesLeft(Player player) {
        if(playerCanJump(player)) {
            return true;
        } else {
            for(Piece piece: player.getPiecesInGame()) {
                if(board.pieceHasPossibleSimpleMove(piece)) {
                    return true;
                }
            }
            return false;
        }
    }

    public void newGame() {
        board.reset();
    }

    public void proposeActivePiece(int row, int column) throws NotJumpablePieceException, NotOwnedPieceException {
        Piece proposedPiece = board.getPiece(row,column);

        if(proposedPiece == null || !proposedPiece.isOwnedBy(activePlayer)) {
            throw new NotOwnedPieceException();
        }

        if(playerCanJump(activePlayer)) {
            if(!board.pieceCanJump(proposedPiece)) {
                throw new NotJumpablePieceException();
            }
        }

        activePiece = proposedPiece;

    }

    //returns true if the active piece can still jump after the move
    //and it didn't become a king, false otherwise
    public boolean proposeActivePieceMove(int newRow, int newColumn) throws IllegalMoveException {
        //////////////
        if(board.isLegalMove(activePiece,newRow,newColumn)) {
            boolean becameKing = board.movePiece(activePiece,newRow,newColumn);

            if(becameKing) {
                return false;
            }

            return board.pieceCanJump(activePiece);
        } else {
            throw new IllegalMoveException();
        }
    }

    private boolean playerCanJump(Player player) {
        for(Piece piece: player.getPiecesInGame()) {
            if(board.pieceCanJump(piece)) {
                return true;
            }
        }
        return false;
    }

}
