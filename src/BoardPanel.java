import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;

public class BoardPanel extends JPanel {

    private final TilePile[][] tiles;
    private static final Color DARK_YELLOW = new Color(238,232,170);
    private static final Color DARK_GREEN = new Color(0,153,0);

    public BoardPanel(MouseListener mouseListener) {

        setBorder(BorderFactory.createLineBorder(Color.BLACK,2));

        tiles = new TilePile[8][8];
        setLayout(new GridLayout(8,8));

        boolean isYellow = true;

        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {

                if(isYellow) {
                    tiles[i][j] = new TilePile(mouseListener,DARK_YELLOW,i,j);
                    isYellow = false;
                } else {
                    //isGreen
                    tiles[i][j] = new TilePile(mouseListener,DARK_GREEN,i,j);
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
}
