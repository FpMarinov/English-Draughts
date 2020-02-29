import java.io.Serializable;

public class RequestPacket implements Serializable {

    private boolean hasProposedDraw;
    private boolean hasDeniedDraw;
    private boolean hasProposedPiece;
    private boolean hasProposedMove;
    private int proposedRow;
    private int proposedColumn;


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
