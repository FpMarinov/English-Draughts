public class NotJumpablePieceException extends Exception {
    public NotJumpablePieceException() {
        super("You have a piece that can jump and haven't selected it.");
    }
}
