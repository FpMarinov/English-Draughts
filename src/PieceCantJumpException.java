public class PieceCantJumpException extends Exception {
    public PieceCantJumpException() {
        super("You have a piece that can jump and haven't selected it.");
    }
}
