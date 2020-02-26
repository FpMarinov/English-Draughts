public class NotOwnedPieceException extends Exception {

    public NotOwnedPieceException() {
        super("You haven't selected a piece you own.");
    }
}
