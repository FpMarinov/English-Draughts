import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;

public class TilePanel extends JPanel {

    private final Color backgroundColor;
    private final int row;
    private final int column;

    public TilePanel(MouseListener mouseListener, Color backgroundColor, int row, int column) {
        this.backgroundColor = backgroundColor;
        setBackground(backgroundColor);
        this.row = row;
        this.column = column;
        addMouseListener(mouseListener);
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }
}
