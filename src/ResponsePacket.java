import java.io.Serializable;

public class ResponsePacket implements Serializable {

    private boolean firstTurn;
    private String errorMessage;
    private Board board;
    private boolean gameOver;
    private boolean draw;
    private boolean playerWon;
    private boolean proposedDraw;
    private boolean deniedDraw;
    private boolean endTurn;
    private boolean needToProposePiece;
    private boolean needToProposeMove;
    private boolean proposedNewGame;
    private boolean deniedNewGame;

    public ResponsePacket(Board board) {
        //default values
        this.board = board;
        errorMessage = null;
        firstTurn = false;
        gameOver = false;
        draw = false;
        playerWon = false;
        proposedDraw = false;
        deniedDraw = false;
        proposedNewGame = false;
        deniedNewGame = false;
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

    public boolean isProposedDraw() {
        return proposedDraw;
    }

    public void setProposedDraw(boolean proposedDraw) {
        this.proposedDraw = proposedDraw;
    }

    public boolean isDeniedDraw() {
        return deniedDraw;
    }

    public void setDeniedDraw(boolean deniedDraw) {
        this.deniedDraw = deniedDraw;
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

    public boolean isProposedNewGame() {
        return proposedNewGame;
    }

    public void setProposedNewGame(boolean proposedNewGame) {
        this.proposedNewGame = proposedNewGame;
    }

    public boolean isDeniedNewGame() {
        return deniedNewGame;
    }

    public void setDeniedNewGame(boolean deniedNewGame) {
        this.deniedNewGame = deniedNewGame;
    }
}
