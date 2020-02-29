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
            try{
                while((response = (ResponsePacket) inputStream.readObject()) != null) {
                    displayResponse(response);
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }finally {
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
    private final JTextField messageField;
    private final JTextField errorMessageField;
    private final BoardPanel boardPanel;


    public Client() {
//        try{
//            inputStream = new ObjectInputStream(server.getInputStream());
//            outputStream = new ObjectOutputStream(server.getOutputStream());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        connectButton = new JButton("Connect");
        submitButton = new JButton("Submit");
        proposeDrawButton = new JButton("Propose Draw");
        denyDrawButton = new JButton("Deny Draw");
        messageField = new JTextField();
        errorMessageField = new JTextField();
        messageField.setEditable(false);
        errorMessageField.setEditable(false);
        boardPanel = new BoardPanel(this);

        setSize(10*UNIT,16*UNIT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Draughts");
        setVisible(true);

        layoutComponents();

    }

    private JPanel makeButtonsPanel(JButton topButton, JButton bottomButton) {
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(2,1));
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(0,UNIT,UNIT,UNIT));
        buttonsPanel.add(topButton);
        buttonsPanel.add(bottomButton);
        return buttonsPanel;
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));

        JPanel topPanel = new JPanel();
        topPanel.setBorder(BorderFactory.createEmptyBorder(UNIT,UNIT,UNIT,UNIT));
        topPanel.setLayout(new BorderLayout());
        topPanel.add(boardPanel);
        mainPanel.add(topPanel);

        JPanel midPanel = new JPanel();
        midPanel.setLayout(new GridLayout(1,2));
        midPanel.add(makeButtonsPanel(connectButton,submitButton));
        midPanel.add(makeButtonsPanel(proposeDrawButton,denyDrawButton));
        mainPanel.add(midPanel);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0,UNIT,UNIT,UNIT));
        bottomPanel.setLayout(new GridLayout(2,1));
        bottomPanel.add(messageField);
        bottomPanel.add(errorMessageField);
        mainPanel.add(bottomPanel);

        add(mainPanel);
    }

    public void displayResponse(ResponsePacket response) {

    }

    public void sendRequest(RequestPacket request) {
        try {
            outputStream.writeObject(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void connect() {
        try{
            server = new Socket(SERVER_IP,PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        TilePanel source = (TilePanel) e.getSource();
        int row = source.getRow();
        int column = source.getColumn();
        messageField.setText(row + " " + column);
    }

    //methods that aren't implemented from MouseListener
    @Override
    public void mousePressed(MouseEvent e) {}
    @Override
    public void mouseReleased(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                new Client();
            }
        });
    }
}
