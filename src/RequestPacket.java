import java.io.Serializable;

public class RequestPacket implements Serializable {

    private boolean proposeDraw;
    private boolean denyDraw;
    private boolean proposePiece;
    private boolean proposeMove;
    private int proposedRow;
    private int proposedColumn;


    public boolean isDenyDraw() {
        return denyDraw;
    }

    public void setDenyDraw(boolean denyDraw) {
        this.denyDraw = denyDraw;
    }

    public boolean isProposeDraw() {
        return proposeDraw;
    }

    public void setProposeDraw(boolean proposeDraw) {
        this.proposeDraw = proposeDraw;
    }

    public boolean isProposePiece() {
        return proposePiece;
    }

    public void setProposePiece(boolean proposePiece) {
        this.proposePiece = proposePiece;
    }

    public boolean isProposeMove() {
        return proposeMove;
    }

    public void setProposeMove(boolean proposeMove) {
        this.proposeMove = proposeMove;
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
