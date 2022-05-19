package client;

import java.net.Socket;

public class ChatClient {

    static Socket clientSocket;
    public static void main(String[] args) {

        String host = "localhost";
        if (args.length == 1) {
            host = args[0];
        } else if (args.length > 1) {
            throw new IllegalArgumentException();
        }

        try {
            clientSocket = new Socket(host, 10000);
        } catch (Exception e) {
            System.out.println(e);
        }

        new Thread(new ClientWriter.Writer()).start();
        new Thread(new ClientListener.Listener()).start();
    }
}
