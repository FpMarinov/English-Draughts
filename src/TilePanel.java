import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

/**
 * Represents a JPanel for a tile of the boardPanel on the Client side.
 */
public class TilePanel extends JPanel {

    //TilePanel fields
    private final Color backgroundColor;
    private final int row;
    private final int column;
    private boolean hasPiece;
    private boolean isPieceKing;
    private Color pieceColor;

    /**
     * Constructor.
     * @param mouseListener MouseListener for the TilePanel
     * @param backgroundColor background color for the Tilepanel
     * @param row the row of the TilePanel on the BoardPanel
     * @param column the column of the TilePanel on the BoardPanel
     */
    public TilePanel(MouseListener mouseListener, Color backgroundColor, int row, int column) {
        this.backgroundColor = backgroundColor;
        setBackground(backgroundColor);
        this.row = row;
        this.column = column;
        this.hasPiece = false;
        isPieceKing = false;
        this.pieceColor = null;
        addMouseListener(mouseListener);
    }

    /**
     * Updates the TilePanel.
     * @param piece piece currently on the pile
     *              or null
     */
    public void updateTile(Piece piece) {
        hasPiece = piece != null;
        if(hasPiece) {
            pieceColor = piece.getColor();
            isPieceKing = piece.isKing();
        }
    }

    /**
     * paintComponent method.
     * @param g Graphics
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        int widthTile = getWidth();
        int heightTile = getHeight();

        Graphics2D g2 = (Graphics2D) g;

        setBackground(backgroundColor);

        if(hasPiece) {
            //there is a piece on the tile
            //draw it
            g2.setColor(pieceColor);
            Ellipse2D.Double circle = new Ellipse2D.Double(0,0,widthTile,heightTile);
            g2.draw(circle);
            g2.fill(circle);
            if (isPieceKing) {
                //the piece is a king
                //draw a crown
                drawCrown(g2);
            }
        }
    }

    /**
     * Returns the row of the TilePanel on the BoardPanel.
     * @return row
     */
    public int getRow() {
        return row;
    }

    /**
     * Returns the column of the TilePanel on the BoardPanel.
     * @return column
     */
    public int getColumn() {
        return column;
    }

    /**
     * Draws a crown on the TilePanel.
     * @param g2
     */
    private void drawCrown(Graphics2D g2) {

        int widthTile = getWidth();
        int heightTile = getHeight();

        g2.setColor(Color.BLACK);
        Line2D.Double bottomCrownSegment = new Line2D.Double(widthTile/4.0,3*(heightTile/4.0),3*(widthTile/4.0),3*(heightTile/4.0));
        g2.draw(bottomCrownSegment);
        Line2D.Double leftCrownSegment = new Line2D.Double(widthTile/4.0,heightTile/4.0, widthTile/4.0, 3*(heightTile/4.0));
        g2.draw(leftCrownSegment);
        Line2D.Double rightCrownSegment = new Line2D.Double(3*(widthTile/4.0),heightTile/4.0,3*(widthTile/4.0),3*(heightTile/4.0));
        g2.draw(rightCrownSegment);
        Line2D.Double topLeftCrownSegment = new Line2D.Double(widthTile/4.0, heightTile/4.0, widthTile/2.0,heightTile/2.0);
        g2.draw(topLeftCrownSegment);
        Line2D.Double topRightCrownSegment = new Line2D.Double(widthTile/2.0, heightTile/2.0, 3*(widthTile/4.0), heightTile/4.0);
        g2.draw(topRightCrownSegment);
    }
}
