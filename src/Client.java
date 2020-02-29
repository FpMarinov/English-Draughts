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

public class Client extends JFrame implements ActionListener, MouseListener {

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


    private static final int PORT = 8765;
    private static final String SERVER_IP = "127.0.0.1";
    private static final int UNIT = 50;
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


    public Client() {

        connectButton = new JButton("Connect");
        submitButton = new JButton("Submit");
        proposeDrawButton = new JButton("Propose Draw");
        denyDrawButton = new JButton("Deny Draw");
        mainMessageField = new JTextField();
        secondaryMessageField = new JTextField();
        boardPanel = new BoardPanel(this);

        connectButton.addActionListener(this);
        submitButton.addActionListener(this);
        proposeDrawButton.addActionListener(this);
        denyDrawButton.addActionListener(this);

        submitButton.setEnabled(false);
        proposeDrawButton.setEnabled(false);
        denyDrawButton.setEnabled(false);
        mainMessageField.setEditable(false);
        secondaryMessageField.setEditable(false);


        setSize(12 * UNIT, 16 * UNIT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Draughts");
        setVisible(true);

        layoutComponents();

    }

    private void disableButtons() {
        connectButton.setEnabled(false);
        submitButton.setEnabled(false);
        proposeDrawButton.setEnabled(false);
        denyDrawButton.setEnabled(false);
    }

    private void enableSubmitAndProposeDrawButtons() {
        submitButton.setEnabled(true);
        proposeDrawButton.setEnabled(true);
    }

    private JPanel makeButtonsPanel(JButton topButton, JButton bottomButton) {
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(2, 1));
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(0, UNIT, UNIT, UNIT));
        buttonsPanel.add(topButton);
        buttonsPanel.add(bottomButton);
        return buttonsPanel;
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JPanel topPanel = new JPanel();
        topPanel.setBorder(BorderFactory.createEmptyBorder(UNIT, UNIT, UNIT, UNIT));
        topPanel.setLayout(new BorderLayout());
        topPanel.add(boardPanel);
        mainPanel.add(topPanel);

        JPanel midPanel = new JPanel();
        midPanel.setLayout(new GridLayout(1, 2));
        midPanel.add(makeButtonsPanel(submitButton, connectButton));
        midPanel.add(makeButtonsPanel(proposeDrawButton, denyDrawButton));
        mainPanel.add(midPanel);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, UNIT, UNIT, UNIT));
        bottomPanel.setLayout(new GridLayout(2, 1));
        bottomPanel.add(secondaryMessageField);
        bottomPanel.add(mainMessageField);
        mainPanel.add(bottomPanel);

        add(mainPanel);
    }

    public void handleResponse(ResponsePacket response) {

        //update board
        boardPanel.updateBoard(response.getBoard());


        if (response.isGameOver()) {
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
            mainMessageField.setText("A new game is about to begin.");
            secondaryMessageField.setText("");
            disableButtons();
        } else if (response.hasToEndTurn()) {
            mainMessageField.setText("Your turn has ended. Wait for your opponent's turn to finish.");
            secondaryMessageField.setText("");
            disableButtons();
        } else if (response.isFirstTurn()) {

            mainMessageField.setText("The game has begun. Choose your active piece " +
                    "by clicking it and pressing \"submit\".");

            enableSubmitAndProposeDrawButtons();

            if (response.hasOpponentProposedDraw()) {
                secondaryMessageField.setText("Your opponent proposed a draw. " +
                        "Press \"Propose Draw\" to accept or \"Deny Draw\" to deny.");
                denyDrawButton.setEnabled(true);
                /**
                 *
                 */
            }


        } else if (response.hasToProposePiece()) {

            mainMessageField.setText("Choose your active piece by clicking it and pressing \"submit\".");
            enableSubmitAndProposeDrawButtons();

            if (response.hasOpponentProposedDraw()) {
                secondaryMessageField.setText("Your opponent proposed a draw. " +
                        "Press \"Propose Draw\" to accept or \"Deny Draw\" to deny.");
                denyDrawButton.setEnabled(true);
                /**
                 *
                 */
            } else if (response.hasOpponentDeniedDraw()) {
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

            mainMessageField.setText("Choose where to move your active piece by clicking " +
                    "the new position and pressing \"submit\".");
            enableSubmitAndProposeDrawButtons();

            //handle error message
            if (response.getErrorMessage() != null) {
                secondaryMessageField.setText(response.getErrorMessage());
            } else {
                secondaryMessageField.setText("");
            }

        }


    }

    public void sendRequest(RequestPacket request) {
        try {
            outputStream.writeObject(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void connect() {
        try {
            server = new Socket(SERVER_IP, PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        connectButton.setEnabled(false);
        mainMessageField.setText("Wait for another player to connect.");

        try{
            inputStream = new ObjectInputStream(server.getInputStream());
            outputStream = new ObjectOutputStream(server.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        ReadWorker readWorker = new ReadWorker();
        readWorker.execute();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == connectButton) {
            connect();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        TilePanel source = (TilePanel) e.getSource();
        int row = source.getRow();
        int column = source.getColumn();
        mainMessageField.setText(row + " " + column);
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                new Client();
                new Client();
            }
        });
    }
}
