import java.io.Serializable;

public class Board implements Serializable {

    private final transient Player player1;
    private final transient Player player2;
    private final Piece[][] piecePositions;

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
                Piece piece = player1.getPiece(pieceCounterPlayer1++);
                piece.becomeUncrowned();
                movePiece(piece,i,j);
            }
        }

        //init 2nd row of 1st player
        for(int j = 0; j < 7; j += 2) {
            Piece piece = player1.getPiece(pieceCounterPlayer1++);
            piece.becomeUncrowned();
            movePiece(piece,1,j);
        }

        //init 1st and 3rd row of 2nd player
        for(int i = 5; i < 8; i += 2) {
            for(int j = 0; j < 7; j += 2) {
                Piece piece = player2.getPiece(pieceCounterPlayer2++);
                piece.becomeUncrowned();
                movePiece(piece,i,j);
            }
        }

        //init 2nd row of 2nd player
        for(int j = 1; j < 8; j += 2) {
            Piece piece = player2.getPiece(pieceCounterPlayer2++);
            piece.becomeUncrowned();
            movePiece(piece,6,j);
        }
    }

    public boolean pieceCanJump(Piece currentPiece) {
        if(currentPiece.isKing()) {
            return pieceCanJumpUp(currentPiece) || pieceCanJumpDown(currentPiece);
        } else if(currentPiece.isOwnedBy(player1)) {
            return pieceCanJumpDown(currentPiece);
        } else {
            //owned by player 2
            return pieceCanJumpUp(currentPiece);
        }
    }

    public Piece getPiece(int row, int column) {
        return piecePositions[row][column];
    }

    public boolean pieceHasPossibleSimpleMove(Piece piece) {
        int row = piece.getRow();
        int column = piece.getColumn();

        boolean upLeftOnBoard = row - 1 >= 0 && row - 1 <= 7 && column - 1 >= 0 && column - 1 <=7;
        boolean upRightOnBoard = row - 1 >= 0 && row - 1 <= 7 && column + 1 >= 0 && column + 1 <=7;
        boolean downLeftOnBoard = row + 1 >= 0 && row + 1 <= 7 && column - 1 >= 0 && column - 1 <=7;
        boolean downRightOnBoard = row + 1 >= 0 && row + 1 <= 7 && column + 1 >= 0 && column + 1 <=7;

        boolean upLeftEmpty = false;
        if(upLeftOnBoard) {
            upLeftEmpty = piecePositions[row - 1][column - 1] == null;
        }
        boolean upRightEmpty = false;
        if(upRightOnBoard) {
            upRightEmpty = piecePositions[row - 1][column + 1] == null;
        }
        boolean downLeftEmpty = false;
        if(downLeftOnBoard) {
            downLeftEmpty = piecePositions[row + 1][column - 1] == null;
        }
        boolean downRightEmpty = false;
        if(downRightOnBoard) {
            downRightEmpty = piecePositions[row + 1][column + 1] == null;
        }

        if(piece.isKing()) {
            return upLeftEmpty || upRightEmpty || downLeftEmpty || downRightEmpty;
        } else if(piece.isOwnedBy(player1)) {
            return downLeftEmpty || downRightEmpty;
        } else {
            //owned by player 2
            return upLeftEmpty || upRightEmpty;
        }
    }

    public boolean isLegalMove(Piece activePiece, int newRow, int newColumn) {

        //check if the selected position is empty
        if(piecePositions[newRow][newColumn] != null) {
            return false;
        }

        //selected position is at least empty

        boolean pieceAttemptsToJumpUpLeft = activePiece.attemptsToJumpUpLeft(newRow,newColumn);
        boolean pieceAttemptsToJumpUpRight = activePiece.attemptsToJumpUpRight(newRow,newColumn);
        boolean pieceAttemptsToJumpDownLeft = activePiece.attemptsToJumpDownLeft(newRow,newColumn);
        boolean pieceAttemptsToJumpDownRight = activePiece.attemptsToJumpDownRight(newRow,newColumn);

        boolean pieceAttemptsSimpleMoveUpLeft = activePiece.attemptsSimpleMoveUpLeft(newRow,newColumn);
        boolean pieceAttemptsSimpleMoveUpRight = activePiece.attemptsSimpleMoveUpRight(newRow,newColumn);
        boolean pieceAttemptsSimpleMoveDownLeft = activePiece.attemptsSimpleMoveDownLeft(newRow,newColumn);
        boolean pieceAttemptsSimpleMoveDownRight = activePiece.attemptsSimpleMoveDownRight(newRow,newColumn);

        if(activePiece.isKing()) {

            if(pieceCanJump(activePiece)) {
                return pieceAttemptsToJumpDownLeft || pieceAttemptsToJumpDownRight || pieceAttemptsToJumpUpLeft || pieceAttemptsToJumpUpRight;
            }

            return pieceAttemptsSimpleMoveDownLeft || pieceAttemptsSimpleMoveDownRight || pieceAttemptsSimpleMoveUpLeft || pieceAttemptsSimpleMoveUpRight;

        } else if (activePiece.isOwnedBy(player1)){

            if(pieceCanJump(activePiece)) {
                return pieceAttemptsToJumpDownLeft || pieceAttemptsToJumpDownRight;
            }

            return pieceAttemptsSimpleMoveDownLeft || pieceAttemptsSimpleMoveDownRight;

        } else {
            //owned by player 2

            if(pieceCanJump(activePiece)) {
                return pieceAttemptsToJumpUpLeft || pieceAttemptsToJumpUpRight;
            }

            return pieceAttemptsSimpleMoveUpLeft || pieceAttemptsSimpleMoveUpRight;

        }


    }

    //returns if the piece became a king on this move
    public boolean movePiece(Piece piece, int newRow, int newColumn) {
        if(piece.isInGame()) {
            int oldRow = piece.getRow();
            int oldColumn = piece.getColumn();

            piecePositions[oldRow][oldColumn] = null;

            //remove jumped over piece if a jump is happening
            if(piece.attemptsToJumpUpLeft(newRow,newColumn)) {
                piecePositions[oldRow - 1][oldColumn - 1].removeFromGame();
                piecePositions[oldRow - 1][oldColumn - 1] = null;
            }
            if(piece.attemptsToJumpUpRight(newRow,newColumn)) {
                piecePositions[oldRow - 1][oldColumn + 1].removeFromGame();
                piecePositions[oldRow - 1][oldColumn + 1] = null;
            }
            if(piece.attemptsToJumpDownLeft(newRow,newColumn)) {
                piecePositions[oldRow + 1][oldColumn - 1].removeFromGame();
                piecePositions[oldRow + 1][oldColumn - 1] = null;
            }
            if(piece.attemptsToJumpDownRight(newRow,newColumn)) {
                piecePositions[oldRow + 1][oldColumn + 1].removeFromGame();
                piecePositions[oldRow + 1][oldColumn + 1] = null;
            }
        } else {
            //used when calling reset()
            piece.insertInGame();
        }

        piecePositions[newRow][newColumn] = piece;
        piece.setRow(newRow);
        piece.setColumn(newColumn);

        //check if the piece needs to become a king
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

}
