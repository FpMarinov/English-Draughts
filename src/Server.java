import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private class ClientThread extends Thread {

        private class ServerRequestReader extends Thread {

            public void run() {

            }
        }

        private class ServerResponseWriter extends Thread {

            public void run() {

            }
        }

        private Socket client = null;
        private ObjectInputStream inputStream = null;
        private ObjectOutputStream outputStream = null;

        public ClientThread(Socket client) {
            this.client = client;
            try{
                outputStream = new ObjectOutputStream(this.client.getOutputStream());
                inputStream = new ObjectInputStream(this.client.getInputStream());
            }catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {

        }

    }

    private ServerSocket server;
    private final int PORT = 8765;

    public Server() {
        try {
            server = new ServerSocket(PORT);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
	// write your code here
    }
}
