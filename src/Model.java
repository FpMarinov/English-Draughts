import java.awt.*;

/**
 * Represents the game model(MVC) in a game of Draughts.
 */
public class Model {

    //Model fields.
    public static final int TOP_PLAYER_ID = 1;
    public static final int BOTTOM_PLAYER_ID = 2;
    private final Player topPlayer;
    private final Player bottomPlayer;
    private final Board board;
    private Player activePlayer;
    private Piece activePiece;

    /**
     * Constructor.
     */
    public Model() {
        topPlayer = new Player(Color.RED);
        bottomPlayer = new Player(Color.WHITE);
        board = new Board(topPlayer, bottomPlayer);
        activePlayer = topPlayer;
    }

    /**
     * Resets the board for a new game.
     */
    public void newGame() {
        board.reset();
    }

    /**
     * Returns the board.
     * @return board
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Checks for game over.
     * @return ID of winning player or -1 if the game isn't yet over.
     * @throws DrawException if there is a draw
     */
    public int checkGameOverReturnGameWinnerID() throws DrawException {
        if(topPlayer.getPiecesInGame().isEmpty()) {
            return BOTTOM_PLAYER_ID;
        } else if(bottomPlayer.getPiecesInGame().isEmpty()) {
            return TOP_PLAYER_ID;
        } else {
            boolean topPlayerHasLegalMovesLeft = playerHasLegalMovesLeft(topPlayer);
            boolean bottomPlayerHasLegalMovesLeft = playerHasLegalMovesLeft(bottomPlayer);

            if(!topPlayerHasLegalMovesLeft && !bottomPlayerHasLegalMovesLeft) {
                throw new DrawException();
            } else if(!topPlayerHasLegalMovesLeft) {
                return BOTTOM_PLAYER_ID;
            } else if(!bottomPlayerHasLegalMovesLeft) {
                return TOP_PLAYER_ID;
            } else {
                return -1;
            }
        }
    }

    /**
     * Proposes a piece to become the active piece.
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

    /**
     * Proposes a move for the active piece.
     * @param newRow proposed new row for the active piece
     * @param newColumn proposed new column for the active piece
     * @return true if the active player must make another move,
     * false otherwise
     * @throws IllegalMoveException if the proposed move isn't legal
     */
    public boolean proposeActivePieceMove(int newRow, int newColumn) throws IllegalMoveException {
        if(board.isLegalMove(activePiece,newRow,newColumn)) {
            //(legal move) and (|rowDifference| == 2) => move is a jump
            int oldRow = activePiece.getRow();
            int rowDifference = oldRow - newRow;
            boolean isMoveAJump = rowDifference == 2 || rowDifference == -2;

            boolean hasBecomeKing = board.movePiece(activePiece,newRow,newColumn);

            if(hasBecomeKing) {
                return false;
            }

            //track if the piece has made a jump
            //to allow moving backwards for double
            //jumps
            activePiece.setHasJumped(isMoveAJump);

            //if the previous move was a jump and the piece
            //can still jump, it must do so
            boolean playerHasToMakeAnotherMove = isMoveAJump && board.pieceCanJump(activePiece);

            //reset active piece jump status
            //for next turn
            if(!playerHasToMakeAnotherMove) {
                activePiece.setHasJumped(false);
            }

            return playerHasToMakeAnotherMove;
        } else {
            throw new IllegalMoveException();
        }
    }

    /**
     * Sets the active player, based on his ID.
     * @param playerID ID of new active player
     */
    public void setActivePlayer(int playerID) {
        switch(playerID) {
            case TOP_PLAYER_ID:
                activePlayer = topPlayer;
                break;

            case BOTTOM_PLAYER_ID:
                activePlayer = bottomPlayer;
                break;
        }
    }

    /**
     * Returns the ID of the active player.
     * @return ID of active player
     */
    public int getActivePlayerID() {
        if(activePlayer == topPlayer) {
            return TOP_PLAYER_ID;
        } else {
            //activePlayer == bottomPlayer
            return BOTTOM_PLAYER_ID;
        }
    }

    /**
     * Checks if a player has a piece that can jump.
     * @param player
     * @return true/false
     */
    private boolean playerCanJump(Player player) {
        for(Piece piece: player.getPiecesInGame()) {
            if(board.pieceCanJump(piece)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a player has a possible legal move.
     * @param player
     * @return true/false
     */
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
