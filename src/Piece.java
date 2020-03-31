import java.awt.*;
import java.io.Serializable;

/**
 * Represents a piece in a game of Draughts.
 */
public class Piece implements Serializable {

    //Piece fields
    private boolean isKing;
    private transient boolean hasJumped;
    private final Color color;
    private final transient Player pieceOwner;
    private transient int row;
    private transient int column;
    private transient boolean isInGame;

    /**
     * Constructor.
     * @param pieceOwner owner of the piece
     * @param color color of the piece
     */
    public Piece(Player pieceOwner, Color color) {
        this.isKing = false;
        this.hasJumped = false;
        this.pieceOwner = pieceOwner;
        this.color = color;

        //new pieces aren't initially in game
        removeFromGame();
    }

    /**
     * Turns the piece into a king.
     * @return boolean, showing whether
     * the transformation was successful
     */
    public boolean becomeKing() {
        if(!isKing) {
            isKing = true;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Removes the crown of a piece and resets the hasJumped field.
     */
    public void becomeUncrownedAndResetHasJumped() {
        isKing = false;
        hasJumped = false;
    }

    /**
     * Shows if the piece is a king.
     * @return true/false
     */
    public boolean isKing() {
        return isKing;
    }

    /**
     * Shows if the piece is in game.
     * @return true/false
     */
    public boolean isInGame() {
        return isInGame;
    }

    /**
     * Removes the piece from game.
     */
    public void removeFromGame() {
        isInGame = false;
        placeOutsideBoard();
    }

    /**
     * Inserts the piece in game.
     */
    public void insertInGame() {
        isInGame = true;
    }

    /**
     * Checks if other has a different
     * owner to this piece
     * @param other other piece
     * @return true/false
     */
    public boolean hasDifferentPieceOwner(Piece other) {
        return !this.pieceOwner.equals(other.pieceOwner);
    }

    /**
     * Checks if the piece is owned
     * by the passed player.
     * @param player passed player
     * @return true/false
     */
    public boolean isOwnedBy(Player player) {
        return pieceOwner.equals(player);
    }

    //Methods that check if the piece attempts simple moves.
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

    //Methods that check if the piece attempts jumps.
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

    /**
     * Places the piece outside the board.
     */
    private void placeOutsideBoard() {
        setRow(-1);
        setColumn(-1);
    }

    //Setters and Getters

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

    public boolean hasJumped() {
        return hasJumped;
    }

    public void setHasJumped(boolean hasJumped) {
        this.hasJumped = hasJumped;
    }
}
