import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

public class TilePanel extends JPanel {

    private final Color backgroundColor;
    private final int row;
    private final int column;
    private boolean hasPiece;
    private boolean isPieceKing;
    private Color pieceColor;

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

    public void updateTile(Piece piece) {
        hasPiece = piece != null;
        if(hasPiece) {
            pieceColor = piece.getColor();
            isPieceKing = piece.isKing();
        }
        System.out.println("Update tile called.");
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        System.out.println("Paint component called");

        int widthTile = getWidth();
        int heightTile = getHeight();

        Graphics2D g2 = (Graphics2D) g;

        //empty tile
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
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }
}
