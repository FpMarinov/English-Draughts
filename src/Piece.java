import java.awt.*;
import java.io.Serializable;

public class Piece implements Serializable {

    private boolean isKing;
    private final Color color;
    private final transient Player pieceOwner;
    private transient int row;
    private transient int column;
    private transient boolean isInGame;

    public Piece(Player pieceOwner, Color color) {
        this.isKing = false;
        this.pieceOwner = pieceOwner;
        this.color = color;

        //new pieces aren't initially in game
        removeFromGame();
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public Color getColor() {
        return color;
    }

    public boolean becomeKing() {
        if(!isKing) {
            isKing = true;
            return true;
        } else {
            return false;
        }
    }

    public void becomeUncrowned() {
        isKing = false;
    }

    public boolean isKing() {
        return isKing;
    }

    public boolean isInGame() {
        return isInGame;
    }

    public void removeFromGame() {
        isInGame = false;
        placeOutsideBoard();
    }

    public void insertInGame() {
        isInGame = true;
    }

    public boolean hasDifferentPieceOwner(Piece other) {
        return !this.pieceOwner.equals(other.pieceOwner);
    }

    public boolean isOwnedBy(Player player) {
        return pieceOwner.equals(player);
    }

    public boolean attemptsSimpleMoveUpLeft(int newRow, int newColumn) {
        return newRow == row - 1 && newColumn == column - 1;
    }
    public boolean attemptsSimpleMoveUpRight(int newRow, int newColumn) {
        return newRow == row - 1 && newColumn == column + 1;
    }
    public boolean attemptsSimpleMoveDownLeft(int newRow, int newColumn) {
        return newRow == row + 1 && newColumn == column - 1;
    }
    public boolean attemptsSimpleMoveDownRight(int newRow, int newColumn) {
        return newRow == row + 1 && newColumn == column + 1;
    }

    public boolean attemptsToJumpUpLeft(int newRow, int newColumn) {
        return newRow == row - 2 && newColumn == column - 2;
    }
    public boolean attemptsToJumpUpRight(int newRow, int newColumn) {
        return newRow == row - 2 && newColumn == column + 2;
    }
    public boolean attemptsToJumpDownLeft(int newRow, int newColumn) {
        return newRow == row + 2 && newColumn == column - 2;
    }
    public boolean attemptsToJumpDownRight(int newRow, int newColumn) {
        return newRow == row + 2 && newColumn == column + 2;
    }

    private void placeOutsideBoard() {
        setRow(-1);
        setColumn(-1);
    }

}
