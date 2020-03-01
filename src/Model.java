import java.awt.*;

public class Model {

    private final Player player1;
    private final Player player2;
    private final Board board;
    private Player activePlayer;
    private Piece activePiece;

    public Model() {
        player1 = new Player(Color.RED);
        player2 = new Player(Color.WHITE);
        board = new Board(player1,player2);
        activePlayer = player1;
    }

    public void newGame() {
        board.reset();
    }

    public Board getBoard() {
        return board;
    }

    /**
     * Checks for game over.
     * @return ID of winning player or -1 if the game isn't yet over.
     * @throws DrawException if there is a draw
     */
    public int checkGameOverReturnGameWinnerID() throws DrawException {
        if(player1.getPiecesInGame().isEmpty()) {
            return 2;
        } else if(player2.getPiecesInGame().isEmpty()) {
            return 1;
        } else {

            boolean player1HasLegalMovesLeft = playerHasLegalMovesLeft(player1);
            boolean player2HasLegalMovesLeft = playerHasLegalMovesLeft(player2);

            if(!player1HasLegalMovesLeft && !player2HasLegalMovesLeft) {
                throw new DrawException();
            } else if(!player1HasLegalMovesLeft) {
                return 2;
            } else if(!player2HasLegalMovesLeft) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    /**
     *
     * @param row row of proposed piece
     * @param column column of proposed piece
     * @throws PieceCantJumpException if the active player has a piece that can jump and hasn't selected it
     * @throws NotOwnedPieceException if the active player hasn't selected a piece he owns
     * @throws PieceCantMoveException if the active player hasn't selected a piece that can move
     */
    public void proposeActivePiece(int row, int column) throws PieceCantJumpException, NotOwnedPieceException, PieceCantMoveException {
        Piece proposedPiece = board.getPiece(row,column);

        if(proposedPiece == null || !proposedPiece.isOwnedBy(activePlayer)) {
            throw new NotOwnedPieceException();
        }

        if(playerCanJump(activePlayer)) {
            if(!board.pieceCanJump(proposedPiece)) {
                throw new PieceCantJumpException();
            } else {
                activePiece = proposedPiece;
            }
        } else {
            if(board.pieceHasPossibleSimpleMove(proposedPiece)) {
                activePiece = proposedPiece;
            } else {
                throw new PieceCantMoveException();
            }
        }



    }

    //returns true if the active piece can still jump after the move
    //and it didn't become a king, false otherwise
    /**
     *
     * @param newRow proposed new row for the active piece
     * @param newColumn proposed new column for the active piece
     * @return true if the active player must make another move,
     * false otherwise
     * @throws IllegalMoveException if the proposed move isn't legal
     */
    public boolean proposeActivePieceMove(int newRow, int newColumn) throws IllegalMoveException {
        if(board.isLegalMove(activePiece,newRow,newColumn)) {
            //(legal move) and (can jump) = move is a jump
            boolean isMoveAJump = board.pieceCanJump(activePiece);

            boolean becameKing = board.movePiece(activePiece,newRow,newColumn);

            if(becameKing) {
                return false;
            }

            //if the previous move was a jump and the piece
            //can still jump it must
            return isMoveAJump && board.pieceCanJump(activePiece);
        } else {
            throw new IllegalMoveException();
        }
    }

    public void setActivePlayer(int playerID) {
        switch(playerID) {
            case 1:
                activePlayer = player1;
                break;

            case 2:
                activePlayer = player2;
                break;
        }
    }

    public int getActivePlayerID() {
        if(activePlayer == player1) {
            return 1;
        } else {
            return 2;
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

    private boolean playerHasLegalMovesLeft(Player player) {
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

}
