import java.io.Serializable;

/**
 * Represents a packet of data sent from a Client to the Server in the Draughts game.
 */
public class RequestPacket implements Serializable {

    //RequestPacket fields
    private boolean hasProposedDraw;
    private boolean hasDeniedDraw;
    private boolean hasProposedPiece;
    private boolean hasProposedMove;
    private int proposedRow;
    private int proposedColumn;

    /**
     * Constructor.
     * @param proposedRow
     * @param proposedColumn
     */
    public RequestPacket(int proposedRow, int proposedColumn) {
        hasProposedDraw = false;
        hasDeniedDraw = false;
        hasProposedPiece = false;
        hasProposedMove = false;
        this.proposedRow = proposedRow;
        this.proposedColumn = proposedColumn;
    }

    //Getters and Setters.

    public boolean hasDeniedDraw() {
        return hasDeniedDraw;
    }

    public void setHasDeniedDraw(boolean hasDeniedDraw) {
        this.hasDeniedDraw = hasDeniedDraw;
    }

    public boolean hasProposedDraw() {
        return hasProposedDraw;
    }

    public void setHasProposedDraw(boolean hasProposedDraw) {
        this.hasProposedDraw = hasProposedDraw;
    }

    public boolean hasProposedPiece() {
        return hasProposedPiece;
    }

    public void setHasProposedPiece(boolean hasProposedPiece) {
        this.hasProposedPiece = hasProposedPiece;
    }

    public boolean hasProposedMove() {
        return hasProposedMove;
    }

    public void setHasProposedMove(boolean hasProposedMove) {
        this.hasProposedMove = hasProposedMove;
    }

    public int getProposedRow() {
        return proposedRow;
    }

    public void setProposedRow(int proposedRow) {
        this.proposedRow = proposedRow;
    }

    public int getProposedColumn() {
        return proposedColumn;
    }

    public void setProposedColumn(int proposedColumn) {
        this.proposedColumn = proposedColumn;
    }

}
