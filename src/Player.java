import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a player in a game of Draughts.
 */
public class Player {

    //Player fields
    private final List<Piece> pieces;

    /**
     * Constructor
     * @param pieceColor color of the player's pieces
     */
    public Player(Color pieceColor) {
        pieces = new ArrayList<>(12);
        for(int i = 1; i <= 12; i++) {
            pieces.add(new Piece(this, pieceColor));
        }
    }

    /**
     * Returns the player's piece with the corresponding index
     * @param index integer between 0(inclusive) and 12(exclusive)
     * @return player's piece with the corresponding index
     */
    public Piece getPiece(int index) {
        return pieces.get(index);
    }

    /**
     * Returns a List of the player's pieces still in game.
     * @return List<Piece> piecesInGame
     */
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
