public class PieceCantMoveException extends Exception{
    public PieceCantMoveException() {
        super("The piece you have selected can't move.");
    }
}
