import java.io.Serializable;

public class ResponsePacket implements Serializable {

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


    public ResponsePacket(Board board, int playerID) {
        //default values
        this.board = board;
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

        //the server stores the board with player1 on top
        //flip it on rendering if necessary
        switch (playerID) {
            case 1:
                hasToFlipBoard = true;
                break;

            case 2:
                hasToFlipBoard = false;
                break;
        }
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
