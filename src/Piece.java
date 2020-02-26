public class Piece {

    private boolean isKing;
    private Player pieceOwner;
    private int row;
    private int column;

    public Piece(Player pieceOwner) {
        isKing = false;
        this.pieceOwner = pieceOwner;

        //default location
        this.row = 0;
        this.column = 0;
    }

    public boolean becomeKing() {
        if(!isKing) {
            isKing = true;
            return true;
        } else {
            return false;
        }
    }

    public boolean isKing() {
        return isKing;
    }

    public boolean hasDifferentPieceOwner(Piece other) {
        return !this.pieceOwner.equals(other.pieceOwner);
    }

    public boolean isOwnedBy(Player player) {
        return pieceOwner.equals(player);
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

}
