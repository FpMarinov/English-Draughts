import java.io.Serializable;

public class ResponsePacket implements Serializable {

    private int playerID;
    private boolean gameOver;
    private boolean draw;
    private boolean proposedDraw;
    private int winningPlayerID;
    private Board board;
}
