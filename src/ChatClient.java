import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {
    private static Socket clientSocket;
    public static void main(String[] args) {

        String host = "localhost";
        if (args.length == 1) {
            host = args[0];
        } else if (args.length > 1) {
            throw new IllegalArgumentException();
        }

        try {
            clientSocket = new Socket(host, 59002);
        } catch (Exception e) {
            System.out.println(e);
        }

        new Thread(new Writer()).start();
        new Thread(new Listener()).start();
    }

    private static class Listener implements Runnable {
        private BufferedReader in;

        @Override
        public void run() {
            try {
                in = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));

                String read;
                while (true) {
                    read = in.readLine();
                    if (read != null && !(read.isEmpty())) {
                        System.out.println(read);
                    }
                }
            } catch (IOException e) {
                return;
            }
        }
    }

    private static class Writer implements Runnable {

        @Override
        public void run() {
            Scanner write = new Scanner(System.in);

            try {
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                while (true) {
                    if (write.hasNext()) {
                        out.println(write.nextLine());
                    }
                }
            } catch (IOException e) {
                write.close();
                return;
            }
        }
    }
}
