public class Model {

    private Player player1;
    private Player player2;
    private Board board;
    private Player activePlayer;
    private Piece activePiece;

    public Model() {
        player1 = new Player();
        player2 = new Player();
        board = new Board(player1,player2);
    }

    public void proposeActivePiece(int row, int column) throws NotJumpablePieceException, NotOwnedPieceException {
        Piece proposedPiece = board.getPiece(row,column);

        if(proposedPiece == null || !proposedPiece.isOwnedBy(activePlayer)) {
            throw new NotOwnedPieceException();
        }

        if(activePlayerCanJump()) {
            if(!board.pieceCanJump(proposedPiece)) {
                throw new NotJumpablePieceException();
            }
        }

        activePiece = proposedPiece;

    }

    //returns true if the active piece can still jump after the move
    //and it didn't become a king, false otherwise
    public boolean proposeActivePieceMove(int newRow, int newColumn) throws IllegalMoveException {
        //////////////
        if(board.isLegalMove(activePiece,newRow,newColumn)) {
            boolean becameKing = board.movePiece(activePiece,newRow,newColumn);

            if(becameKing) {
                return false;
            }

            return board.pieceCanJump(activePiece);
        } else {
            throw new IllegalMoveException();
        }
    }

    private boolean activePlayerCanJump() {
        for(Piece piece: activePlayer.getPieces()) {
            if(board.pieceCanJump(piece)) {
                return true;
            }
        }
        return false;
    }

}
