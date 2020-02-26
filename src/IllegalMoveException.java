public class IllegalMoveException extends Exception {
    public IllegalMoveException() {
        super("Your proposed move is illegal.");
    }
}
