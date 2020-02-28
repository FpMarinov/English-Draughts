import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Server extends Thread{

    private class ClientThread extends Thread {

        private class ServerRequestReader extends Thread {

            public void run() {
                RequestPacket request;
                try {
                    while ((request = (RequestPacket) inputStream.readObject()) != null) {
                        READ_WRITE_LOCK.lock();

                        while(hasRequest) {
                            try {
                                RESPONSE_CONDITION.await();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        ///////DEAL WITH REQUEST////////////

                        if(request.isProposePiece()) {
                            int row = request.getProposedRow();
                            int column = request.getProposedColumn();
                            try {
                                MODEL.proposeActivePiece(row,column);
                                needToProposePiece = false;
                                needToProposeMove = true;
                            } catch (NotJumpablePieceException | NotOwnedPieceException e) {
                                errorMessage = e.getMessage();
                                needToProposePiece = true;
                                needToProposeMove = false;
                            }
                        } else if(request.isProposeMove()){
                            int row = request.getProposedRow();
                            int column = request.getProposedColumn();
                            try {
                                needToProposeMove = MODEL.proposeActivePieceMove(row,column);
                            } catch (IllegalMoveException e) {
                                errorMessage = e.getMessage();
                                needToProposeMove = true;
                            }
                        }

                        hasRequest = true;
                        RESPONSE_CONDITION.signal();
                        READ_WRITE_LOCK.unlock();
                    }
                    inputStream.close();
                }catch (ClassNotFoundException | IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private class ServerResponseWriter extends Thread {

            public void run() {
                while(!client.isClosed()) {
                    PLAYER_LOCK.lock();

                    while(MODEL.getActivePlayerID() != playerID) {
                        try {
                            ACTIVE_PLAYER_CONDITION.await();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    READ_WRITE_LOCK.lock();

                    while(!hasRequest) {
                        try {
                            RESPONSE_CONDITION.await();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    ResponsePacket response = new ResponsePacket(MODEL.getBoard());


                    if(needToProposePiece) {
                        //turn start

                        //check for first turn
                        if(firstTurn) {
                            response.setFirstTurn(true);
                            firstTurn = false;
                        }

                        //check for game over
                        try {
                            winningPlayerID = MODEL.checkGameOverReturnGameWinnerID();
                            if(winningPlayerID != -1) {
                                gameOver = true;
                            }
                        } catch (DrawException e) {
                            gameOver = true;
                            draw = true;
                        }

                        if(gameOver) {
                            //game over
                            response.setGameOver(true);
                            response.setDraw(draw);
                            response.setWinningPlayerID(winningPlayerID);
                        } else {
                            //game not over, turn start normally
                            response.setNeedToProposePiece(true);
                            needToProposePiece = false;
                            if(errorMessage != null) {
                                response.setErrorMessage(errorMessage);
                                errorMessage = null;
                            }
                        }
                    } else if(needToProposeMove) {
                            response.setNeedToProposeMove(true);
                            needToProposeMove = false;
                            if(errorMessage != null) {
                                response.setErrorMessage(errorMessage);
                                errorMessage = null;
                            }
                        }


                    //send response
                    try {
                        outputStream.writeObject(response);
                        outputStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    hasRequest = false;
                    RESPONSE_CONDITION.signal();
                    READ_WRITE_LOCK.unlock();


                    /**
                     * if turn over signal active player condition
                     * set needToProposePiece to true
                     */

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
        private final Condition RESPONSE_CONDITION;
        private boolean hasRequest;
        private boolean firstTurn;
        private boolean endTurn;
        private ObjectOutputStream outputStream = null;
        private ObjectInputStream inputStream = null;
        private boolean needToProposePiece;
        private boolean needToProposeMove;
        private String errorMessage;

        public ClientThread(Socket client, int playerID) {
            this.client = client;
            this.playerID = playerID;
            READ_WRITE_LOCK = new ReentrantLock();
            RESPONSE_CONDITION = READ_WRITE_LOCK.newCondition();
            hasRequest = true;
            firstTurn = true;
            endTurn = false;
            needToProposePiece = true;
            needToProposeMove = false;
            errorMessage = null;
            try{
                outputStream = new ObjectOutputStream(this.client.getOutputStream());
                inputStream = new ObjectInputStream(this.client.getInputStream());
            }catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void setPlayerID(int playerID) {
            this.playerID = playerID;
        }

        public void run() {
            ServerResponseWriter responseWriter = new ServerResponseWriter();
            ServerRequestReader requestReader = new ServerRequestReader();

            responseWriter.start();
            requestReader.start();

            try{
                responseWriter.join();
                requestReader.join();
            }catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
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
    private int newGameProposals;
    private boolean deniedNewGame;

    public Server() {
        PORT = 8765;
        RANDOM = new Random();
        MODEL = new Model();
        drawProposals = 0;
        deniedDraw = false;
        gameOver = false;
        draw = false;
        winningPlayerID = -1;
        newGameProposals = 0;
        deniedNewGame = false;
        try {
            server = new ServerSocket(PORT);
        }catch (IOException e) {
            e.printStackTrace();
        }
        PLAYER_LOCK = new ReentrantLock();
        ACTIVE_PLAYER_CONDITION = PLAYER_LOCK.newCondition();
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
                }catch(IOException e){
                    e.printStackTrace();
                }
                break;

            case 2:
                try {
                    player2Client = new ClientThread(server.accept(), 2);
                    player1Client = new ClientThread(server.accept(), 1);
                }catch(IOException e){
                    e.printStackTrace();
                }
                break;
        }


        player1Client.start();
        player2Client.start();


        try{
            player1Client.join();
            player2Client.join();
        }catch(InterruptedException e) {
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
        try{
            server.join();
        }catch(InterruptedException e) {
            e.printStackTrace();
        }

    }
}
