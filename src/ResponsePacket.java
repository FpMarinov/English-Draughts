import java.io.Serializable;

public class ResponsePacket implements Serializable {

    private boolean firstTurn;
    private String errorMessage;
    private Board board;
    private boolean gameOver;
    private boolean draw;
    private boolean playerWon;
    private boolean opponentProposedDraw;
    private boolean opponentDeniedDraw;
    private boolean endTurn;
    private boolean needToProposePiece;
    private boolean needToProposeMove;


    public ResponsePacket(Board board) {
        //default values
        this.board = board;
        errorMessage = null;
        firstTurn = false;
        gameOver = false;
        draw = false;
        playerWon = false;
        opponentProposedDraw = false;
        opponentDeniedDraw = false;
        endTurn = false;
        needToProposePiece = false;
        needToProposeMove = false;
    }

    public boolean isPlayerWon() {
        return playerWon;
    }

    public void setPlayerWon(boolean playerWon) {
        this.playerWon = playerWon;
    }

    public boolean isFirstTurn() {
        return firstTurn;
    }

    public void setFirstTurn(boolean firstTurn) {
        this.firstTurn = firstTurn;
    }

    public boolean isNeedToProposePiece() {
        return needToProposePiece;
    }

    public void setNeedToProposePiece(boolean needToProposePiece) {
        this.needToProposePiece = needToProposePiece;
    }

    public boolean isNeedToProposeMove() {
        return needToProposeMove;
    }

    public void setNeedToProposeMove(boolean needToProposeMove) {
        this.needToProposeMove = needToProposeMove;
    }

    public boolean isEndTurn() {
        return endTurn;
    }

    public void setEndTurn(boolean endTurn) {
        this.endTurn = endTurn;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public boolean isDraw() {
        return draw;
    }

    public void setDraw(boolean draw) {
        this.draw = draw;
    }

    public boolean isOpponentProposedDraw() {
        return opponentProposedDraw;
    }

    public void setOpponentProposedDraw(boolean opponentProposedDraw) {
        this.opponentProposedDraw = opponentProposedDraw;
    }

    public boolean isOpponentDeniedDraw() {
        return opponentDeniedDraw;
    }

    public void setOpponentDeniedDraw(boolean opponentDeniedDraw) {
        this.opponentDeniedDraw = opponentDeniedDraw;
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
