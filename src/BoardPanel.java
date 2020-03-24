import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;

/**
 * Represents the JPanel for the board on the Client side.
 */
public class BoardPanel extends JPanel {

    //BoardPanel fields.
    private final TilePanel[][] tiles;
    private static final Color DARK_YELLOW = new Color(238,232,170);
    private static final Color DARK_GREEN = new Color(0,153,0);

    /**
     * Constructor
     * @param mouseListener mouseListener for the BoardPanel
     */
    public BoardPanel(MouseListener mouseListener) {

        //handle border
        setBorder(BorderFactory.createLineBorder(Color.BLACK,2));

        //handle tiles
        tiles = new TilePanel[8][8];
        setLayout(new GridLayout(8,8));

        boolean isYellow = true;

        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {

                if(isYellow) {
                    tiles[i][j] = new TilePanel(mouseListener,DARK_YELLOW,i,j);
                    isYellow = false;
                } else {
                    //isGreen
                    tiles[i][j] = new TilePanel(mouseListener,DARK_GREEN,i,j);
                    isYellow = true;
                }

                add(tiles[i][j]);
            }
            //take care of next row starting color
            if(isYellow) {
                isYellow = false;
            } else {
                //isGreen
                isYellow = true;
            }
        }
    }

    /**
     * Updates the boardPanel with the board sent from a ResponsePacket
     * @param board board sent from ResponsePacket
     * @param hasToFlipBoard whether the received board has to be flipped
     */
    public void updateBoard(Board board, boolean hasToFlipBoard) {
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                //the server stores the board with topPlayer on top
                //flip it on rendering if necessary
                if(!hasToFlipBoard) {
                    tiles[i][j].updateTile(board.getPiece(i, j));
                } else {
                    tiles[i][j].updateTile(board.getPiece(7 - i,7 - j));
                }
            }
        }
        repaint();
    }
}
