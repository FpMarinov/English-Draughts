import java.io.Serializable;

/**
 * Represents a packet of data sent from the Server to a Client in the Draughts game.
 */
public class ResponsePacket implements Serializable {

    //ResponsePacket fields
    private boolean isFirstTurn;
    private String errorMessage;
    private Board board;
    private boolean isGameOver;
    private boolean isDraw;
    private boolean hasPlayerWon;
    private boolean isNewGameAboutToBegin;
    private boolean hasOpponentProposedDraw;
    private boolean hasOpponentDeniedDraw;
    private boolean hasToEndTurn;
    private boolean hasToProposePiece;
    private boolean hasToProposeMove;
    private boolean hasToFlipBoard;
    private boolean hasToDisplayInitialConnection;

    /**
     * Constructor.
     * @param playerID playerID of the Client the ResponsePacket is for
     */
    public ResponsePacket(int playerID) {
        //default values
        board = null;
        errorMessage = null;
        isFirstTurn = false;
        isGameOver = false;
        isDraw = false;
        hasPlayerWon = false;
        hasOpponentProposedDraw = false;
        hasOpponentDeniedDraw = false;
        hasToEndTurn = false;
        hasToProposePiece = false;
        hasToProposeMove = false;
        isNewGameAboutToBegin = false;
        hasToDisplayInitialConnection = false;

        //the Server's Model stores the board with topPlayer on top
        //flip it on rendering if necessary
        switch (playerID) {
            case Model.TOP_PLAYER_ID:
                hasToFlipBoard = true;
                break;

            case Model.BOTTOM_PLAYER_ID:
                hasToFlipBoard = false;
                break;
        }
    }

    //Getters and Setters.

    public boolean hasToDisplayInitialConnection() {
        return hasToDisplayInitialConnection;
    }

    public void setHasToDisplayInitialConnection(boolean hasToDisplayInitialConnection) {
        this.hasToDisplayInitialConnection = hasToDisplayInitialConnection;
    }

    public boolean hasToFlipBoard() {
        return hasToFlipBoard;
    }

    public void setHasToFlipBoard(boolean hasToFlipBoard) {
        this.hasToFlipBoard = hasToFlipBoard;
    }

    public boolean isNewGameAboutToBegin() {
        return isNewGameAboutToBegin;
    }

    public void setNewGameAboutToBegin(boolean newGameAboutToBegin) {
        isNewGameAboutToBegin = newGameAboutToBegin;
    }

    public boolean hasPlayerWon() {
        return hasPlayerWon;
    }

    public void setHasPlayerWon(boolean hasPlayerWon) {
        this.hasPlayerWon = hasPlayerWon;
    }

    public boolean isFirstTurn() {
        return isFirstTurn;
    }

    public void setFirstTurn(boolean firstTurn) {
        this.isFirstTurn = firstTurn;
    }

    public boolean hasToProposePiece() {
        return hasToProposePiece;
    }

    public void setHasToProposePiece(boolean hasToProposePiece) {
        this.hasToProposePiece = hasToProposePiece;
    }

    public boolean hasToProposeMove() {
        return hasToProposeMove;
    }

    public void setHasToProposeMove(boolean hasToProposeMove) {
        this.hasToProposeMove = hasToProposeMove;
    }

    public boolean hasToEndTurn() {
        return hasToEndTurn;
    }

    public void setHasToEndTurn(boolean hasToEndTurn) {
        this.hasToEndTurn = hasToEndTurn;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.isGameOver = gameOver;
    }

    public boolean isDraw() {
        return isDraw;
    }

    public void setDraw(boolean draw) {
        this.isDraw = draw;
    }

    public boolean hasOpponentProposedDraw() {
        return hasOpponentProposedDraw;
    }

    public void setHasOpponentProposedDraw(boolean hasOpponentProposedDraw) {
        this.hasOpponentProposedDraw = hasOpponentProposedDraw;
    }

    public boolean hasOpponentDeniedDraw() {
        return hasOpponentDeniedDraw;
    }

    public void setHasOpponentDeniedDraw(boolean hasOpponentDeniedDraw) {
        this.hasOpponentDeniedDraw = hasOpponentDeniedDraw;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

}
