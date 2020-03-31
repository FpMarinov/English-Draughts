import java.io.Serializable;

/**
 * Represents the board in a game of Draughts.
 */
public class Board implements Serializable {

    //Board fields
    private final transient Player topPlayer;
    private final transient Player bottomPlayer;
    private final Piece[][] piecePositions;

    /**
     * Constructor.
     * @param topPlayer top player from server perspective
     * @param bottomPlayer bottom player from server perspective
     */
    public Board(Player topPlayer, Player bottomPlayer) {
        this.topPlayer = topPlayer;
        this.bottomPlayer = bottomPlayer;
        piecePositions = new Piece[8][8];
        reset();
    }

    /**
     * Resets the board for a new game.
     */
    public void reset() {

        int pieceCounterPlayer1 = 0;
        int pieceCounterPlayer2 = 0;

        //empty board
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                piecePositions[i][j] = null;
            }
        }

        //init 1st and 3rd row of 1st player
        for (int i = 0; i < 3; i += 2) {
            for (int j = 1; j < 8; j += 2) {
                Piece piece = topPlayer.getPiece(pieceCounterPlayer1++);
                movePieceForReset(piece, i, j);
            }
        }

        //init 2nd row of 1st player
        for (int j = 0; j < 7; j += 2) {
            Piece piece = topPlayer.getPiece(pieceCounterPlayer1++);
            movePieceForReset(piece, 1, j);
        }

        //init 1st and 3rd row of 2nd player
        for (int i = 5; i < 8; i += 2) {
            for (int j = 0; j < 7; j += 2) {
                Piece piece = bottomPlayer.getPiece(pieceCounterPlayer2++);
                movePieceForReset(piece, i, j);
            }
        }

        //init 2nd row of 2nd player
        for (int j = 1; j < 8; j += 2) {
            Piece piece = bottomPlayer.getPiece(pieceCounterPlayer2++);
            movePieceForReset(piece, 6, j);
        }
    }

    /**
     * Checks if the current piece can jump.
     * @param currentPiece
     * @return true/false
     */
    public boolean pieceCanJump(Piece currentPiece) {
        if (currentPiece.isKing() || currentPiece.hasJumped()) {
            return pieceCanJumpUp(currentPiece) || pieceCanJumpDown(currentPiece);
        } else if (currentPiece.isOwnedBy(topPlayer)) {
            return pieceCanJumpDown(currentPiece);
        } else {
            //owned by bottom player
            return pieceCanJumpUp(currentPiece);
        }
    }

    /**
     * Returns the piece at the given
     * row and column.
     * @param row
     * @param column
     * @return chosen piece
     */
    public Piece getPiece(int row, int column) {
        return piecePositions[row][column];
    }

    /**
     * Checks if the given piece
     * has a possible simple move.
     * @param piece
     * @return true/false
     */
    public boolean pieceHasPossibleSimpleMove(Piece piece) {
        int row = piece.getRow();
        int column = piece.getColumn();

        boolean upLeftOnBoard = row - 1 >= 0 && row - 1 <= 7 && column - 1 >= 0 && column - 1 <= 7;
        boolean upRightOnBoard = row - 1 >= 0 && row - 1 <= 7 && column + 1 >= 0 && column + 1 <= 7;
        boolean downLeftOnBoard = row + 1 >= 0 && row + 1 <= 7 && column - 1 >= 0 && column - 1 <= 7;
        boolean downRightOnBoard = row + 1 >= 0 && row + 1 <= 7 && column + 1 >= 0 && column + 1 <= 7;

        boolean upLeftEmpty = false;
        if (upLeftOnBoard) {
            upLeftEmpty = piecePositions[row - 1][column - 1] == null;
        }
        boolean upRightEmpty = false;
        if (upRightOnBoard) {
            upRightEmpty = piecePositions[row - 1][column + 1] == null;
        }
        boolean downLeftEmpty = false;
        if (downLeftOnBoard) {
            downLeftEmpty = piecePositions[row + 1][column - 1] == null;
        }
        boolean downRightEmpty = false;
        if (downRightOnBoard) {
            downRightEmpty = piecePositions[row + 1][column + 1] == null;
        }

        if (piece.isKing()) {
            return upLeftEmpty || upRightEmpty || downLeftEmpty || downRightEmpty;
        } else if (piece.isOwnedBy(topPlayer)) {
            return downLeftEmpty || downRightEmpty;
        } else {
            //owned by bottom player
            return upLeftEmpty || upRightEmpty;
        }
    }

    /**
     * Checks if the proposed move to the new row
     * and column for the active piece is legal.
     * @param activePiece
     * @param newRow
     * @param newColumn
     * @return true/false
     */
    public boolean isLegalMove(Piece activePiece, int newRow, int newColumn) {

        //check if the selected position is empty
        if (piecePositions[newRow][newColumn] != null) {
            return false;
        }

        //selected position is at least empty

        boolean pieceAttemptsToJumpUpLeft = activePiece.attemptsToJumpUpLeft(newRow, newColumn);
        boolean pieceAttemptsToJumpUpRight = activePiece.attemptsToJumpUpRight(newRow, newColumn);
        boolean pieceAttemptsToJumpDownLeft = activePiece.attemptsToJumpDownLeft(newRow, newColumn);
        boolean pieceAttemptsToJumpDownRight = activePiece.attemptsToJumpDownRight(newRow, newColumn);

        boolean pieceAttemptsSimpleMoveUpLeft = activePiece.attemptsSimpleMoveUpLeft(newRow, newColumn);
        boolean pieceAttemptsSimpleMoveUpRight = activePiece.attemptsSimpleMoveUpRight(newRow, newColumn);
        boolean pieceAttemptsSimpleMoveDownLeft = activePiece.attemptsSimpleMoveDownLeft(newRow, newColumn);
        boolean pieceAttemptsSimpleMoveDownRight = activePiece.attemptsSimpleMoveDownRight(newRow, newColumn);

        if (activePiece.isKing() || activePiece.hasJumped()) {

            if (pieceCanJump(activePiece)) {
                return pieceAttemptsToJumpDownLeft || pieceAttemptsToJumpDownRight || pieceAttemptsToJumpUpLeft || pieceAttemptsToJumpUpRight;
            }

            return pieceAttemptsSimpleMoveDownLeft || pieceAttemptsSimpleMoveDownRight || pieceAttemptsSimpleMoveUpLeft || pieceAttemptsSimpleMoveUpRight;

        } else if (activePiece.isOwnedBy(topPlayer)) {

            if (pieceCanJump(activePiece)) {
                return pieceAttemptsToJumpDownLeft || pieceAttemptsToJumpDownRight;
            }

            return pieceAttemptsSimpleMoveDownLeft || pieceAttemptsSimpleMoveDownRight;

        } else {
            //owned by bottom player

            if (pieceCanJump(activePiece)) {
                return pieceAttemptsToJumpUpLeft || pieceAttemptsToJumpUpRight;
            }

            return pieceAttemptsSimpleMoveUpLeft || pieceAttemptsSimpleMoveUpRight;

        }
    }

    /**
     * Meant to be used at game reset.
     * Moves the given piece to the new row
     * and column, inserts it in game and
     * removes its crown.
     * @param piece
     * @param newRow
     * @param newColumn
     */
    private void movePieceForReset(Piece piece, int newRow, int newColumn) {
        piece.insertInGame();
        piece.becomeUncrownedAndResetHasJumped();
        piecePositions[newRow][newColumn] = piece;
        piece.setRow(newRow);
        piece.setColumn(newColumn);
    }

    /**
     * Moves the given piece to the new row and column.
     * @param piece
     * @param newRow
     * @param newColumn
     * @return if the piece became a king in this move
     */
    public boolean movePiece(Piece piece, int newRow, int newColumn) {

        int oldRow = piece.getRow();
        int oldColumn = piece.getColumn();

        piecePositions[oldRow][oldColumn] = null;

        //remove jumped over piece if a jump is happening
        if (piece.attemptsToJumpUpLeft(newRow, newColumn)) {
            piecePositions[oldRow - 1][oldColumn - 1].removeFromGame();
            piecePositions[oldRow - 1][oldColumn - 1] = null;
        }
        if (piece.attemptsToJumpUpRight(newRow, newColumn)) {
            piecePositions[oldRow - 1][oldColumn + 1].removeFromGame();
            piecePositions[oldRow - 1][oldColumn + 1] = null;
        }
        if (piece.attemptsToJumpDownLeft(newRow, newColumn)) {
            piecePositions[oldRow + 1][oldColumn - 1].removeFromGame();
            piecePositions[oldRow + 1][oldColumn - 1] = null;
        }
        if (piece.attemptsToJumpDownRight(newRow, newColumn)) {
            piecePositions[oldRow + 1][oldColumn + 1].removeFromGame();
            piecePositions[oldRow + 1][oldColumn + 1] = null;
        }

        //change the piece position
        piecePositions[newRow][newColumn] = piece;
        piece.setRow(newRow);
        piece.setColumn(newColumn);

        //check if the piece needs to become a king
        if (piece.isOwnedBy(topPlayer) && newRow == 7) {
            return piece.becomeKing();
        }
        if (piece.isOwnedBy(bottomPlayer) && newRow == 0) {
            return piece.becomeKing();
        }
        return false;
    }

    /**
     * Checks if the current piece can jump up.
     * @param currentPiece
     * @return true/false
     */
    private boolean pieceCanJumpUp(Piece currentPiece) {
        return pieceCanJumpUpLeft(currentPiece) || pieceCanJumpUpRight(currentPiece);
    }

    /**
     * Checks if the current piece can jump down.
     * @param currentPiece
     * @return true/false
     */
    private boolean pieceCanJumpDown(Piece currentPiece) {
        return pieceCanJumpDownLeft(currentPiece) || pieceCanJumpDownRight(currentPiece);
    }

    /**
     * Checks if the current piece can jump up and left diagonally.
     * @param currentPiece
     * @return true/false
     */
    private boolean pieceCanJumpUpLeft(Piece currentPiece) {
        int currentRow = currentPiece.getRow();
        int currentColumn = currentPiece.getColumn();

        Piece otherPiece = null;
        Piece availableSpace = null;

        int availableSpaceRow = currentRow - 2;
        int availableSpaceColumn = currentColumn - 2;

        if ((availableSpaceRow <= 7 && availableSpaceRow >= 0) && (availableSpaceColumn <= 7 && availableSpaceColumn >= 0)) {
            otherPiece = piecePositions[currentRow - 1][currentColumn - 1];
            availableSpace = piecePositions[availableSpaceRow][availableSpaceColumn];

            return otherPiece != null && otherPiece.hasDifferentPieceOwner(currentPiece) && availableSpace == null;
        }
        return false;
    }

    /**
     * Checks if the current piece can jump up and right diagonally.
     * @param currentPiece
     * @return true/false
     */
    private boolean pieceCanJumpUpRight(Piece currentPiece) {
        int currentRow = currentPiece.getRow();
        int currentColumn = currentPiece.getColumn();

        Piece otherPiece = null;
        Piece availableSpace = null;

        int availableSpaceRow = currentRow - 2;
        int availableSpaceColumn = currentColumn + 2;

        if ((availableSpaceRow <= 7 && availableSpaceRow >= 0) && (availableSpaceColumn <= 7 && availableSpaceColumn >= 0)) {
            otherPiece = piecePositions[currentRow - 1][currentColumn + 1];
            availableSpace = piecePositions[availableSpaceRow][availableSpaceColumn];

            return otherPiece != null && otherPiece.hasDifferentPieceOwner(currentPiece) && availableSpace == null;
        }

        return false;
    }

    /**
     * Checks if the current piece can jump down and left diagonally.
     * @param currentPiece
     * @return true/false
     */
    private boolean pieceCanJumpDownLeft(Piece currentPiece) {
        int currentRow = currentPiece.getRow();
        int currentColumn = currentPiece.getColumn();

        Piece otherPiece = null;
        Piece availableSpace = null;

        int availableSpaceRow = currentRow + 2;
        int availableSpaceColumn = currentColumn - 2;

        if ((availableSpaceRow <= 7 && availableSpaceRow >= 0) && (availableSpaceColumn <= 7 && availableSpaceColumn >= 0)) {
            otherPiece = piecePositions[currentRow + 1][currentColumn - 1];
            availableSpace = piecePositions[availableSpaceRow][availableSpaceColumn];

            return otherPiece != null && otherPiece.hasDifferentPieceOwner(currentPiece) && availableSpace == null;
        }

        return false;
    }

    /**
     * Checks if the current piece can jump down and right diagonally.
     * @param currentPiece
     * @return true/false
     */
    private boolean pieceCanJumpDownRight(Piece currentPiece) {
        int currentRow = currentPiece.getRow();
        int currentColumn = currentPiece.getColumn();

        Piece otherPiece = null;
        Piece availableSpace = null;

        int availableSpaceRow = currentRow + 2;
        int availableSpaceColumn = currentColumn + 2;

        if ((availableSpaceRow <= 7 && availableSpaceRow >= 0) && (availableSpaceColumn <= 7 && availableSpaceColumn >= 0)) {
            otherPiece = piecePositions[currentRow + 1][currentColumn + 1];
            availableSpace = piecePositions[availableSpaceRow][availableSpaceColumn];

            return otherPiece != null && otherPiece.hasDifferentPieceOwner(currentPiece) && availableSpace == null;
        }
        return false;
    }
}
