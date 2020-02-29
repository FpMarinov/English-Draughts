import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Player {
    private final List<Piece> pieces;

    public Player(Color pieceColor) {
        pieces = new ArrayList<>(12);
        for(int i = 1; i <= 12; i++) {
            pieces.add(new Piece(this, pieceColor));
        }
    }

    public Piece getPiece(int index) {
        return pieces.get(index);
    }

    public List<Piece> getPiecesInGame() {

        List<Piece> piecesInGame = new ArrayList<>(12);

        for(Piece piece: pieces) {
            if(piece.isInGame()) {
                piecesInGame.add(piece);
            }
        }

        return piecesInGame;
    }



}
