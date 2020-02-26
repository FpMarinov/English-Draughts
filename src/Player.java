import java.util.ArrayList;
import java.util.List;

public class Player {
    private List<Piece> pieces;

    public Player() {
        pieces = new ArrayList<>(12);
        for(int i = 1; i <= 12; i++) {
            pieces.add(new Piece(this));
        }
    }

    public Piece getPiece(int index) {
        return pieces.get(index);
    }

    public List<Piece> getPieces() {
        return pieces;
    }


}
