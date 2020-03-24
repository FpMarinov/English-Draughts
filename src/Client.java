import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Represents a Client in a game of Draughts.
 */
public class Client extends JFrame implements ActionListener, MouseListener {

    /**
     * A class that inherits from SwingWorker and handles the reception
     * of ResponsePackets from the Server.
     */
    private class ReadWorker extends SwingWorker<Void, Void> {
        public Void doInBackground() {
            ResponsePacket response = null;
            try {
                while ((response = (ResponsePacket) inputStream.readObject()) != null) {
                    handleResponse(response);
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                return null;
            }
        }
    }

    //Client fields
    private static final int PORT = 8765;
    private static final String SERVER_IP = "127.0.0.1";
    private static final int UNIT = 40;
    private Socket server = null;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private final JButton connectButton;
    private final JButton submitButton;
    private final JButton proposeDrawButton;
    private final JButton denyDrawButton;
    private final JTextField mainMessageField;
    private final JTextField secondaryMessageField;
    private final BoardPanel boardPanel;
    private int selectedRow;
    private int selectedColumn;
    private boolean hasToFlipBoard;
    private boolean hasProposedDraw;
    private boolean hasDeniedDraw;
    private boolean hasToProposePiece;
    private boolean hasToProposeMove;

    /**
     * Constructor.
     */
    public Client() {
        //initial fields setup
        selectedRow = 0;
        selectedColumn = 0;
        hasProposedDraw = false;
        hasDeniedDraw = false;
        hasToProposePiece = false;
        hasToProposeMove = false;

        //Initialization of buttons, textFields and the boardPanel
        connectButton = new JButton("Connect");
        submitButton = new JButton("Submit");
        proposeDrawButton = new JButton("Propose Draw");
        denyDrawButton = new JButton("Deny Draw");
        mainMessageField = new JTextField();
        secondaryMessageField = new JTextField();
        boardPanel = new BoardPanel(this);

        //addition of actionListener to buttons
        connectButton.addActionListener(this);
        submitButton.addActionListener(this);
        proposeDrawButton.addActionListener(this);
        denyDrawButton.addActionListener(this);

        //Initial enabled/editable setup for buttons/textFields
        submitButton.setEnabled(false);
        proposeDrawButton.setEnabled(false);
        denyDrawButton.setEnabled(false);
        mainMessageField.setEditable(false);
        secondaryMessageField.setEditable(false);

        //GUI window visual setup and defaultCloseOperation handling
        setSize(17 * UNIT, 20 * UNIT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Draughts");
        setVisible(true);
        layoutComponents();
    }

    /**
     * Disables all buttons.
     */
    private void disableButtons() {
        connectButton.setEnabled(false);
        submitButton.setEnabled(false);
        proposeDrawButton.setEnabled(false);
        denyDrawButton.setEnabled(false);
    }

    /**
     * Enables the submit and proposeDraw buttons.
     */
    private void enableSubmitAndProposeDrawButtons() {
        submitButton.setEnabled(true);
        proposeDrawButton.setEnabled(true);
    }

    /**
     * Makes a JPanel to hold 2 buttons.
     * @param topButton
     * @param bottomButton
     * @return JPanel that holds 2 buttons
     */
    private JPanel makeButtonsPanel(JButton topButton, JButton bottomButton) {
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(2, 1));
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(0, UNIT, UNIT, UNIT));
        buttonsPanel.add(topButton);
        buttonsPanel.add(bottomButton);
        return buttonsPanel;
    }

    /**
     * Lays out the components of the GUI
     */
    private void layoutComponents() {
        //handle main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        //handle top panel and add it to the main panel
        JPanel topPanel = new JPanel();
        topPanel.setBorder(BorderFactory.createEmptyBorder(UNIT, UNIT, UNIT, UNIT));
        topPanel.setLayout(new BorderLayout());
        topPanel.add(boardPanel);
        mainPanel.add(topPanel);

        //handle middle panel and add it to the main panel
        JPanel midPanel = new JPanel();
        midPanel.setLayout(new GridLayout(1, 2));
        midPanel.add(makeButtonsPanel(submitButton, connectButton));
        midPanel.add(makeButtonsPanel(proposeDrawButton, denyDrawButton));
        mainPanel.add(midPanel);

        //handle bottom panel and add it to the main panel
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, UNIT, UNIT, UNIT));
        bottomPanel.setLayout(new GridLayout(2, 1));
        bottomPanel.add(secondaryMessageField);
        bottomPanel.add(mainMessageField);
        mainPanel.add(bottomPanel);

        //add main panel
        setLayout(new BorderLayout());
        add(mainPanel);
    }

    /**
     * Handles the ResponsePacket received from the Server.
     * @param response ResponsePacket received from the Server
     */
    public void handleResponse(ResponsePacket response) {

        //check if the Client's board needs to be flipped
        hasToFlipBoard = response.hasToFlipBoard();

        //update board
        boardPanel.updateBoard(response.getBoard(), hasToFlipBoard);

        if (response.hasToDisplayInitialConnection()) {
            //display initial connection message
            mainMessageField.setText("You have found an opponent.");
            secondaryMessageField.setText("");
        } else if (response.isGameOver()) {
            //handle game over
            if (response.isDraw()) {
                //handle draw
                mainMessageField.setText("The game ended in a draw.");
                secondaryMessageField.setText("");
            } else {
                //handle not draw
                if (response.hasPlayerWon()) {
                    //player won
                    mainMessageField.setText("You won the Game!");
                    secondaryMessageField.setText("");
                } else {
                    //player lost
                    mainMessageField.setText("You lost the Game!");
                    secondaryMessageField.setText("");
                }
            }
            disableButtons();
        } else if (response.isNewGameAboutToBegin()) {
            //handle new game
            mainMessageField.setText("A new game is about to begin.");
            secondaryMessageField.setText("");
            disableButtons();
        } else if (response.hasToEndTurn()) {
            //handle turn end
            mainMessageField.setText("Your turn has ended. Wait for your opponent's turn to finish.");
            hasToProposePiece = false;
            hasToProposeMove = false;
            secondaryMessageField.setText("");
            disableButtons();
        } else if (response.hasToProposePiece()) {
            //handle piece proposal
            if (response.isFirstTurn()) {
                mainMessageField.setText("The game has begun. Choose your active piece " +
                        "by clicking it and pressing \"Submit\".");
            } else {
                mainMessageField.setText("Choose your active piece by clicking it and pressing \"Submit\".");
            }
            hasToProposePiece = true;
            hasToProposeMove = false;
            enableSubmitAndProposeDrawButtons();

            if (response.hasOpponentProposedDraw()) {
                //handle draw proposal by opponent
                secondaryMessageField.setText("Your opponent proposed a draw. " +
                        "Press \"Deny Draw\" to deny or \"Propose Draw\", then \"Submit\", to accept.");
                denyDrawButton.setEnabled(true);
            } else if (response.hasOpponentDeniedDraw()) {
                //handle draw denial by opponent
                secondaryMessageField.setText("Your opponent has denied your draw proposal.");
            } else {
                //handle error message
                if (response.getErrorMessage() != null) {
                    secondaryMessageField.setText(response.getErrorMessage());
                } else {
                    secondaryMessageField.setText("");
                }
            }
        } else if (response.hasToProposeMove()) {
            //handle move proposal
            mainMessageField.setText("Choose where to move your active piece by clicking " +
                    "the new position and pressing \"Submit\".");
            hasToProposePiece = false;
            hasToProposeMove = true;
            enableSubmitAndProposeDrawButtons();

            //handle error message
            if (response.getErrorMessage() != null) {
                secondaryMessageField.setText(response.getErrorMessage());
            } else {
                secondaryMessageField.setText("");
            }
        }
    }

    /**
     * Sends a RequestPacket to the Server.
     * @param request RequestPacket sent to the Server
     */
    public void sendRequest(RequestPacket request) {
        try {
            outputStream.writeObject(request);
            outputStream.flush();
            outputStream.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Connects to the Server.
     */
    public void connect() {
        try {
            server = new Socket(SERVER_IP, PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        connectButton.setEnabled(false);
        mainMessageField.setText("Wait for another player to connect.");

        try {
            inputStream = new ObjectInputStream(server.getInputStream());
            outputStream = new ObjectOutputStream(server.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        ReadWorker readWorker = new ReadWorker();
        readWorker.execute();
    }

    /**
     * actionPerformed method.
     * @param e ActionEvent
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == connectButton) {
            connect();
        } else if (e.getSource() == denyDrawButton) {
            hasDeniedDraw = true;
            hasProposedDraw = false;
            denyDrawButton.setEnabled(false);
        } else if (e.getSource() == proposeDrawButton) {
            hasDeniedDraw = false;
            hasProposedDraw = true;
            proposeDrawButton.setEnabled(false);
            denyDrawButton.setEnabled(false);
        } else if (e.getSource() == submitButton) {
            RequestPacket request = new RequestPacket(selectedRow, selectedColumn);

            if (hasProposedDraw) {
                request.setHasProposedDraw(true);
            } else if (hasDeniedDraw) {
                request.setHasDeniedDraw(true);
            }

            if (hasToProposePiece) {
                request.setHasProposedPiece(true);
            } else if (hasToProposeMove) {
                request.setHasProposedMove(true);
            }

            hasToProposeMove = false;
            hasToProposePiece = false;
            hasDeniedDraw = false;
            hasProposedDraw = false;

            disableButtons();
            sendRequest(request);
        }
    }

    /**
     * mouseClicked method.
     * @param e MouseEvent
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        TilePanel source = (TilePanel) e.getSource();

        //the server stores the board with player1 on top
        //it is flipped on rendering if necessary
        //flip selection if the board has been flipped

        if (!hasToFlipBoard) {
            selectedRow = source.getRow();
            selectedColumn = source.getColumn();
        } else {
            selectedRow = 7 - source.getRow();
            selectedColumn = 7 - source.getColumn();
        }
    }

    //methods that aren't implemented from MouseListener
    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    /**
     * Main method. Creates two clients.
     * @param args Program arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                //create two clients
                new Client();
                new Client();
            }
        });
    }
}
