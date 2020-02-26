public class Board {

    private Player player1;
    private Player player2;
    private Piece[][] piecePositions;

    public Board(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
        piecePositions = new Piece[8][8];
        reset();
    }

    public void reset() {

        int pieceCounterPlayer1 = 0;
        int pieceCounterPlayer2 = 0;

        //init 1st and 3rd row of 1st player
        for(int i = 0; i < 3; i += 2) {
            for(int j = 1; j < 8; j += 2) {
                movePiece(player1.getPiece(pieceCounterPlayer1++),i,j);
            }
        }

        //init 2nd row of 1st player
        for(int j = 0; j < 7; j += 2) {
            movePiece(player1.getPiece(pieceCounterPlayer1++),1,j);
        }

        //init 1st and 3rd row of 2nd player
        for(int i = 5; i < 8; i += 2) {
            for(int j = 0; j < 7; j += 2) {
                movePiece(player2.getPiece(pieceCounterPlayer2++),i,j);
            }
        }

        //init 2nd row of 2nd player
        for(int j = 1; j < 8; j += 2) {
            movePiece(player2.getPiece(pieceCounterPlayer2++),6,j);
        }
    }

    public boolean pieceCanJump(Piece currentPiece) {
        if(currentPiece.isKing()) {
            return pieceCanJumpUp(currentPiece) || pieceCanJumpDown(currentPiece);
        } else if(currentPiece.isOwnedBy(player1)) {
            return pieceCanJumpDown(currentPiece);
        } else {
            //held by player 2
            return pieceCanJumpUp(currentPiece);
        }
    }

    public Piece getPiece(int row, int column) {
        return piecePositions[row][column];
    }

    public boolean isLegalMove(Piece activePiece, int newRow, int newColumn) {

        if(piecePositions[newRow][newColumn] != null) {
            return false;
        }

        //selected position is at least empty

        int currentRow = activePiece.getRow();
        int currentColumn = activePiece.getColumn();

        if(activePiece.isKing()) {
            if(pieceCanJumpUpLeft(activePiece)) {
                return pieceAttemptsToJumpUpLeft(activePiece, newRow, newColumn);
            }

            if(pieceCanJumpUpRight(activePiece)){
                return pieceAttemptsToJumpUpRight(activePiece, newRow, newColumn);
            }

            if(pieceCanJumpDownLeft(activePiece)){
                return pieceAttemptsToJumpDownLeft(activePiece, newRow, newColumn);
            }

            if(pieceCanJumpDownRight(activePiece)){
                return pieceAttemptsToJumpDownRight(activePiece, newRow, newColumn);
            }

            //diagonal up moves
            if(newRow == currentRow - 1 && newColumn == currentColumn + 1) {
                return true;
            }
            if(newRow == currentRow - 1 && newColumn == currentColumn - 1) {
                return true;
            }

            //diagonal down moves
            if(newRow == currentRow + 1 && newColumn == currentColumn + 1) {
                return true;
            }
            if(newRow == currentRow + 1 && newColumn == currentColumn - 1) {
                return true;
            }

            return false;
        } else if (activePiece.isOwnedBy(player1)){
            if(pieceCanJumpDownLeft(activePiece)){
                return pieceAttemptsToJumpDownLeft(activePiece, newRow, newColumn);
            }

            if(pieceCanJumpDownRight(activePiece)){
                return pieceAttemptsToJumpDownRight(activePiece, newRow, newColumn);
            }

            //diagonal down moves
            if(newRow == currentRow + 1 && newColumn == currentColumn + 1) {
                return true;
            }
            if(newRow == currentRow + 1 && newColumn == currentColumn - 1) {
                return true;
            }

            return false;
        } else {
            //held by player 2
            if(pieceCanJumpUpLeft(activePiece)) {
                return pieceAttemptsToJumpUpLeft(activePiece, newRow, newColumn);
            }

            if(pieceCanJumpUpRight(activePiece)){
                return pieceAttemptsToJumpUpRight(activePiece, newRow, newColumn);
            }

            //diagonal up moves
            if(newRow == currentRow - 1 && newColumn == currentColumn + 1) {
                return true;
            }
            if(newRow == currentRow - 1 && newColumn == currentColumn - 1) {
                return true;
            }

            return false;
        }


    }

    //returns if the piece became a king on this move
    public boolean movePiece(Piece piece, int newRow, int newColumn) {
        int oldRow = piece.getRow();
        int oldColumn = piece.getColumn();
        piecePositions[oldRow][oldColumn] = null;

        piecePositions[newRow][newColumn] = piece;
        piece.setRow(newRow);
        piece.setColumn(newColumn);

        if(piece.isOwnedBy(player1) && newRow == 7) {
            return piece.becomeKing();
        }

        if(piece.isOwnedBy(player2) && newRow == 0) {
            return piece.becomeKing();
        }
        return false;
    }

    private boolean pieceCanJumpUp(Piece currentPiece) {
        return pieceCanJumpUpLeft(currentPiece) || pieceCanJumpUpRight(currentPiece);
    }

    private boolean pieceCanJumpDown(Piece currentPiece) {
        return pieceCanJumpDownLeft(currentPiece) || pieceCanJumpDownRight(currentPiece);
    }

    private boolean pieceCanJumpUpLeft(Piece currentPiece) {
        int currentRow = currentPiece.getRow();
        int currentColumn = currentPiece.getColumn();

        Piece otherPiece = null;
        Piece availableSpace = null;

        int availableSpaceRow = currentRow-2;
        int availableSpaceColumn = currentColumn-2;

        if((availableSpaceRow <= 7 && availableSpaceRow >= 0) && (availableSpaceColumn <= 7 && availableSpaceColumn >= 0)) {
            otherPiece = piecePositions[currentRow - 1][currentColumn - 1];
            availableSpace = piecePositions[availableSpaceRow][availableSpaceColumn];

            return otherPiece != null && otherPiece.hasDifferentPieceOwner(currentPiece) && availableSpace == null;
        }
        return false;
    }

    private boolean pieceCanJumpUpRight(Piece currentPiece) {
        int currentRow = currentPiece.getRow();
        int currentColumn = currentPiece.getColumn();

        Piece otherPiece = null;
        Piece availableSpace = null;

        int availableSpaceRow = currentRow-2;
        int availableSpaceColumn = currentColumn+2;

        if((availableSpaceRow <= 7 && availableSpaceRow >= 0) && (availableSpaceColumn <= 7 && availableSpaceColumn >= 0)) {
            otherPiece = piecePositions[currentRow - 1][currentColumn + 1];
            availableSpace = piecePositions[availableSpaceRow][availableSpaceColumn];

            return otherPiece != null && otherPiece.hasDifferentPieceOwner(currentPiece) && availableSpace == null;
        }

        return false;
    }

    private boolean pieceCanJumpDownLeft(Piece currentPiece) {
        int currentRow = currentPiece.getRow();
        int currentColumn = currentPiece.getColumn();

        Piece otherPiece = null;
        Piece availableSpace = null;

        int availableSpaceRow = currentRow+2;
        int availableSpaceColumn = currentColumn-2;

        if((availableSpaceRow <= 7 && availableSpaceRow >= 0) && (availableSpaceColumn <= 7 && availableSpaceColumn >= 0)) {
            otherPiece = piecePositions[currentRow + 1][currentColumn - 1];
            availableSpace = piecePositions[availableSpaceRow][availableSpaceColumn];

            return otherPiece != null && otherPiece.hasDifferentPieceOwner(currentPiece) && availableSpace == null;
        }

        return false;
    }

    private boolean pieceCanJumpDownRight(Piece currentPiece) {
        int currentRow = currentPiece.getRow();
        int currentColumn = currentPiece.getColumn();

        Piece otherPiece = null;
        Piece availableSpace = null;

        int availableSpaceRow = currentRow+2;
        int availableSpaceColumn = currentColumn+2;

        if((availableSpaceRow <= 7 && availableSpaceRow >= 0) && (availableSpaceColumn <= 7 && availableSpaceColumn >= 0)) {
            otherPiece = piecePositions[currentRow + 1][currentColumn + 1];
            availableSpace = piecePositions[availableSpaceRow][availableSpaceColumn];

            return otherPiece != null && otherPiece.hasDifferentPieceOwner(currentPiece) && availableSpace == null;
        }
        return false;
    }

    private boolean pieceAttemptsToJumpDownRight(Piece currentPiece, int newRow, int newColumn) {
        int currentRow = currentPiece.getRow();
        int currentColumn = currentPiece.getColumn();

        return newRow == currentRow + 2 && newColumn == currentColumn + 2;
    }

    private boolean pieceAttemptsToJumpDownLeft(Piece currentPiece, int newRow, int newColumn) {
        int currentRow = currentPiece.getRow();
        int currentColumn = currentPiece.getColumn();

        return newRow == currentRow + 2 && newColumn == currentColumn - 2;
    }

    private boolean pieceAttemptsToJumpUpRight(Piece currentPiece, int newRow, int newColumn) {
        int currentRow = currentPiece.getRow();
        int currentColumn = currentPiece.getColumn();

        return newRow == currentRow - 2 && newColumn == currentColumn + 2;
    }

    private boolean pieceAttemptsToJumpUpLeft(Piece currentPiece, int newRow, int newColumn) {
        int currentRow = currentPiece.getRow();
        int currentColumn = currentPiece.getColumn();

        return newRow == currentRow - 2 && newColumn == currentColumn - 2;
    }

}
