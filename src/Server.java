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
                        readWriteLock.lock();

                        //if there already was a request the reader thread for this
                        //client waits(this shouldn't normally happen as requests
                        //should only be sent after a response from the server
                        //has been received and this needs to be enforced by the client GUI
                        while (hasRequest) {
                            try {
                                requestCondition.await();
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
                                model.proposeActivePiece(row, column);
                                hasToProposePiece = false;
                                hasToProposeMove = true;
                            } catch (PieceCantJumpException | NotOwnedPieceException e) {
                                errorMessage = e.getMessage();
                                hasToProposePiece = true;
                                hasToProposeMove = false;
                            }
                        } else if (request.hasProposedMove()) {
                            //deal with a move proposal
                            int row = request.getProposedRow();
                            int column = request.getProposedColumn();
                            try {
                                //try and propose a move and if successful
                                //record if the player needs to make another
                                //move in needToProposeMove
                                hasToProposeMove = model.proposeActivePieceMove(row, column);
                            } catch (IllegalMoveException e) {
                                errorMessage = e.getMessage();
                                hasToProposeMove = true;
                            }
                        }

                        //deal with a draw proposal
                        if (request.hasProposedDraw()) {
                            drawProposals++;
                            hasPlayerProposedDraw = true;
                        }
                        //deal with a draw denial
                        if (request.hasDeniedDraw()) {
                            drawProposals = 0;
                            isDeniedDraw = true;
                            hasPlayerDeniedDraw = true;
                        }

                        hasRequest = true;
                        requestCondition.signal();
                        readWriteLock.unlock();
                    }
                    inputStream.close();
                } catch (ClassNotFoundException | IOException e) {
                    e.printStackTrace();
                    System.exit(0);
                }
            }
        }

        private class ServerResponseWriter extends Thread {

            private void turnEnd(ResponsePacket response) {
                //turn needs to end
                response.setHasToEndTurn(true);
                //signal the writer thread of the
                //other client
                activePlayerCondition.signal();

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
                model.setActivePlayer(otherPlayerID);

                //reset hasRequest to account for extra response
                //to "wake up" client
                hasRequest = true;
            }

            public void run() {
                while (!client.isClosed()) {
                    playerLock.lock();

                    //only the writer thread that belongs to the client
                    //for the active player can continue
                    //otherwise wait to become active
                    while (model.getActivePlayerID() != playerID) {
                        try {
                            activePlayerCondition.await();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    readWriteLock.lock();

                    //if there is a request, the writer thread for
                    //this client continues, otherwise it waits
                    //for a request(the initial connection is counted
                    // as a request, even though it doesn't send a
                    // request packet)
                    while (!hasRequest) {
                        try {
                            requestCondition.await();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    //clear last request
                    hasRequest = false;

                    //make a response object and reference the game board
                    //in it
                    ResponsePacket response = new ResponsePacket(model.getBoard(),playerID);

                    if (gameOverSignals == 2) {
                        //game has finished and both
                        //players know about it

                        //wait 3 seconds
                        //for player to read
                        //game over
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }


                        //restart game
                        resetClientThreadFields();
                        resetServerFields();
                        fieldResets++;

                        //inform client that a
                        //new game is about to begin
                        response.setNewGameAboutToBegin(true);

                        //if both resets have happened
                        //make sure the next turn skips
                        //the current code block
                        if (fieldResets == 2) {
                            fieldResets = 0;
                            gameOverSignals = 0;
                        }

                        //change player
                        turnEnd(response);

                    } else if (drawProposals == 2) {
                        //deal with an agreed upon draw
                        //at any point during the turn
                        isGameOver = true;
                        isDraw = true;

                        response.setGameOver(true);
                        response.setDraw(true);

                        //change player
                        turnEnd(response);
                        //this player has been signalled
                        //about the end of the game
                        gameOverSignals++;
                    } else if (hasToProposePiece) {
                        //turn start

                        //reset hasPlayerDeniedDraw boolean
                        //if still true from previous denial
                        if (!isDeniedDraw && hasPlayerDeniedDraw) {
                            hasPlayerDeniedDraw = false;
                        }

                        if (drawProposals == 1 && !hasPlayerProposedDraw) {
                            //deal with a draw proposal
                            response.setHasOpponentProposedDraw(true);
                        } else if (isDeniedDraw && !hasPlayerDeniedDraw) {
                            //deal with draw denial
                            response.setHasOpponentDeniedDraw(true);
                            //reset booleans
                            isDeniedDraw = false;
                            hasPlayerProposedDraw = false;
                        }


                        //check for first turn
                        if (isFirstTurn) {
                            response.setFirstTurn(true);
                            isFirstTurn = false;

                            //sleep for 3 seconds on first turn
                            //for player to read new game
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        //check for game over at turn start
                        if (!isGameOver) {
                            try {
                                winningPlayerID = model.checkGameOverReturnGameWinnerID();
                                if (winningPlayerID != -1) {
                                    isGameOver = true;
                                }
                            } catch (DrawException e) {
                                isGameOver = true;
                                isDraw = true;
                            }
                        }

                        if (isGameOver) {
                            //game over

                            response.setGameOver(true);
                            if (!isDraw) {
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
                            hasToProposePiece = false;
                            if (errorMessage != null) {
                                //handle incorrect piece proposal
                                response.setErrorMessage(errorMessage);
                                errorMessage = null;
                            }
                        }
                    } else if (hasToProposeMove) {
                        //propose a move
                        response.setHasToProposeMove(true);
                        hasToProposeMove = false;
                        if (errorMessage != null) {
                            //handle incorrect move proposal
                            response.setErrorMessage(errorMessage);
                            errorMessage = null;
                        }
                    } else {

                        //setup the next turn start
                        hasToProposePiece = true;


                        turnEnd(response);
                    }


                    response.setBoard(model.getBoard());

                    //send response
                    try {
                        outputStream.writeObject(response);
                        outputStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    //signal the read thread of the client
                    requestCondition.signal();


                    readWriteLock.unlock();
                    playerLock.unlock();
                }
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private final Socket client;
        private final int playerID;
        private final ReentrantLock readWriteLock;
        private final Condition requestCondition;
        private boolean hasRequest;
        private boolean isFirstTurn;
        private ObjectOutputStream outputStream;
        private ObjectInputStream inputStream;
        private boolean hasToProposePiece;
        private boolean hasToProposeMove;
        private String errorMessage;
        private boolean hasPlayerProposedDraw;
        private boolean hasPlayerDeniedDraw;

        public ClientThread(Socket client, int playerID) {
            this.client = client;
            this.playerID = playerID;
            readWriteLock = new ReentrantLock();
            requestCondition = readWriteLock.newCondition();

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
            isFirstTurn = true;
            hasToProposePiece = true;
            hasToProposeMove = false;
            errorMessage = null;
            hasPlayerProposedDraw = false;
            hasPlayerDeniedDraw = false;
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
//                responseWriter.join();
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

    private static final int PORT = 8765;
    private final Model model;
    private ServerSocket server;
    private final ReentrantLock playerLock;
    private final Condition activePlayerCondition;
    private int drawProposals;
    private boolean isDeniedDraw;
    private boolean isGameOver;
    private boolean isDraw;
    private int winningPlayerID;
    private int gameOverSignals;
    private int fieldResets;

    public Server() {

        model = new Model();
        resetServerFields();
        gameOverSignals = 0;
        fieldResets = 0;

        try {
            server = new ServerSocket(PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        playerLock = new ReentrantLock();
        activePlayerCondition = playerLock.newCondition();
    }

    public void resetServerFields() {
        drawProposals = 0;
        isDeniedDraw = false;
        isDraw = false;
        winningPlayerID = -1;
        isGameOver = false;
    }

    public void run() {

        Random random = new Random();

        //PlayerID of 1st client to connect is randomly selected
        int firstClientPlayerID = random.nextInt(2) + 1;

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
