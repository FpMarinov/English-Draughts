import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Server extends Thread {

    private class ClientThread extends Thread {

        private class ServerRequestReader extends Thread {

            public void run() {
                RequestPacket request;
                try {
                    while ((request = (RequestPacket) inputStream.readObject()) != null) {
                        READ_WRITE_LOCK.lock();

                        //if there already was a request the reader thread for this
                        //client waits(this shouldn't normally happen as requests
                        //should only be sent after a response from the server
                        //has been received and this needs to be enforced by the client GUI
                        while (hasRequest) {
                            try {
                                REQUEST_CONDITION.await();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        ///////DEAL WITH REQUEST////////////

                        if (request.hasProposedPiece()) {
                            //deal with a piece proposal
                            int row = request.getProposedRow();
                            int column = request.getProposedColumn();
                            try {
                                MODEL.proposeActivePiece(row, column);
                                needToProposePiece = false;
                                needToProposeMove = true;
                            } catch (PieceCantJumpException | NotOwnedPieceException e) {
                                errorMessage = e.getMessage();
                                needToProposePiece = true;
                                needToProposeMove = false;
                            }
                        } else if (request.hasProposedMove()) {
                            //deal with a move proposal
                            int row = request.getProposedRow();
                            int column = request.getProposedColumn();
                            try {
                                //try and propose a move and if successful
                                //record if the player needs to make another
                                //move in needToProposeMove
                                needToProposeMove = MODEL.proposeActivePieceMove(row, column);
                            } catch (IllegalMoveException e) {
                                errorMessage = e.getMessage();
                                needToProposeMove = true;
                            }
                        }

                        //deal with a draw proposal
                        if (request.hasProposedDraw()) {
                            drawProposals++;
                        }
                        //deal with a draw denial
                        if (request.hasDeniedDraw()) {
                            drawProposals = 0;
                            deniedDraw = true;
                        }

                        hasRequest = true;
                        REQUEST_CONDITION.signal();
                        READ_WRITE_LOCK.unlock();
                    }
                    inputStream.close();
                } catch (ClassNotFoundException | IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private class ServerResponseWriter extends Thread {

            private void turnEnd(ResponsePacket response) {
                //turn needs to end
                response.setHasToEndTurn(true);
                //signal the writer thread of the
                //other client
                ACTIVE_PLAYER_CONDITION.signal();

                //change active player
                int otherPlayerID = 0;
                switch (playerID) {
                    case 1:
                        otherPlayerID = 2;
                        break;

                    case 2:
                        otherPlayerID = 1;
                        break;
                }
                MODEL.setActivePlayer(otherPlayerID);
            }

            public void run() {
                while (!client.isClosed()) {
                    PLAYER_LOCK.lock();

                    //only the writer thread that belongs to the client
                    //for the active player can continue
                    //otherwise wait to become active
                    while (MODEL.getActivePlayerID() != playerID) {
                        try {
                            ACTIVE_PLAYER_CONDITION.await();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    READ_WRITE_LOCK.lock();

                    //if there is a request, the writer thread for
                    //this client continues, otherwise it waits
                    //for a request(the initial connection is counted
                    // as a request, even though it doesn't send a
                    // request packet)
                    while (!hasRequest) {
                        try {
                            REQUEST_CONDITION.await();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    //make a response object and reference the game board
                    //in it
                    ResponsePacket response = new ResponsePacket(MODEL.getBoard());

                    if (gameOverSignals == 2) {
                        //game has finished and both
                        //players know about it

                        //restart game
                        resetClientThreadFields();
                        resetServerFields();
                        fieldResets++;

                        //if both resets have happened
                        //make sure the next turn skips
                        //the current code block
                        if(fieldResets == 2) {
                            fieldResets = 0;
                            gameOverSignals = 0;
                        }

                        //change player
                        turnEnd(response);

                    } else if (drawProposals == 2) {
                        //deal with an agreed upon draw
                        //at any point during the turn
                        gameOver = true;
                        draw = true;

                        response.setGameOver(true);
                        response.setDraw(true);

                        //change player
                        turnEnd(response);
                        //this player has been signalled
                        //about the end of the game
                        gameOverSignals++;
                    } else if (needToProposePiece) {
                        //turn start

                        //deal with a draw proposal
                        if (drawProposals == 1) {
                            response.setHasOpponentProposedDraw(true);
                        }
                        //deal with draw denial
                        if (deniedDraw) {
                            response.setHasOpponentDeniedDraw(true);
                            deniedDraw = false;
                        }

                        //check for first turn
                        if (firstTurn) {
                            response.setFirstTurn(true);
                            firstTurn = false;
                        }

                        //check for game over at turn start
                        if (!gameOver) {
                            try {
                                winningPlayerID = MODEL.checkGameOverReturnGameWinnerID();
                                if (winningPlayerID != -1) {
                                    gameOver = true;
                                }
                            } catch (DrawException e) {
                                gameOver = true;
                                draw = true;
                            }
                        }

                        if (gameOver) {
                            //game over

                            response.setGameOver(true);
                            if (!draw) {
                                if (winningPlayerID == playerID) {
                                    response.setHasPlayerWon(true);
                                }
                            } else {
                                response.setDraw(true);
                            }


                            turnEnd(response);
                            gameOverSignals++;

                        } else {
                            //game not over, turn start normally
                            //propose a piece to move
                            response.setHasToProposePiece(true);
                            needToProposePiece = false;
                            if (errorMessage != null) {
                                //handle incorrect piece proposal
                                response.setErrorMessage(errorMessage);
                                errorMessage = null;
                            }
                        }
                    } else if (needToProposeMove) {
                        //propose a move
                        response.setHasToProposeMove(true);
                        needToProposeMove = false;
                        if (errorMessage != null) {
                            //handle incorrect move proposal
                            response.setErrorMessage(errorMessage);
                            errorMessage = null;
                        }
                    } else {

                        //setup the next turn start
                        needToProposePiece = true;


                        turnEnd(response);
                    }


                    //send response
                    try {
                        outputStream.writeObject(response);
                        outputStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //clear last request
                    hasRequest = false;
                    //signal the read thread of the client
                    REQUEST_CONDITION.signal();


                    READ_WRITE_LOCK.unlock();
                    PLAYER_LOCK.unlock();
                }
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private Socket client = null;
        private int playerID;
        private final ReentrantLock READ_WRITE_LOCK;
        private final Condition REQUEST_CONDITION;
        private boolean hasRequest;
        private boolean firstTurn;
        private ObjectOutputStream outputStream = null;
        private ObjectInputStream inputStream = null;
        private boolean needToProposePiece;
        private boolean needToProposeMove;
        private String errorMessage;

        public ClientThread(Socket client, int playerID) {
            this.client = client;
            this.playerID = playerID;
            READ_WRITE_LOCK = new ReentrantLock();
            REQUEST_CONDITION = READ_WRITE_LOCK.newCondition();

            resetClientThreadFields();

            try {
                outputStream = new ObjectOutputStream(this.client.getOutputStream());
                inputStream = new ObjectInputStream(this.client.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void resetClientThreadFields() {
            hasRequest = true;
            firstTurn = true;
            needToProposePiece = true;
            needToProposeMove = false;
            errorMessage = null;
        }

        public void run() {
            ServerResponseWriter responseWriter = new ServerResponseWriter();
            ServerRequestReader requestReader = new ServerRequestReader();

            responseWriter.start();
            requestReader.start();

            try {
                /**
                 * possibly remove writer join so that
                 * the client can close
                 */
                responseWriter.join();
                requestReader.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                try {
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

    }

    private final int PORT;
    private final Random RANDOM;
    private final Model MODEL;
    private ServerSocket server;
    private final ReentrantLock PLAYER_LOCK;
    private final Condition ACTIVE_PLAYER_CONDITION;
    private int drawProposals;
    private boolean deniedDraw;
    private boolean gameOver;
    private boolean draw;
    private int winningPlayerID;
    private int gameOverSignals;
    private int fieldResets;

    public Server() {
        PORT = 8765;
        RANDOM = new Random();
        MODEL = new Model();

        resetServerFields();
        gameOverSignals = 0;
        fieldResets = 0;

        try {
            server = new ServerSocket(PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PLAYER_LOCK = new ReentrantLock();
        ACTIVE_PLAYER_CONDITION = PLAYER_LOCK.newCondition();
    }

    public void resetServerFields() {
        drawProposals = 0;
        deniedDraw = false;
        draw = false;
        winningPlayerID = -1;
        gameOver = false;
    }

    public void run() {
        //PlayerID of 1st client to connect is randomly selected
        int firstClientPlayerID = RANDOM.nextInt(2) + 1;

        ClientThread player1Client = null;
        ClientThread player2Client = null;

        switch (firstClientPlayerID) {
            case 1:
                try {
                    player1Client = new ClientThread(server.accept(), 1);
                    player2Client = new ClientThread(server.accept(), 2);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case 2:
                try {
                    player2Client = new ClientThread(server.accept(), 2);
                    player1Client = new ClientThread(server.accept(), 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }


        player1Client.start();
        player2Client.start();


        try {
            player1Client.join();
            player2Client.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
        try {
            server.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
