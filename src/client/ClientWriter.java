package client;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class ClientWriter {
    static class Writer implements Runnable {

        @Override
        public void run() {
            Scanner write = new Scanner(System.in);
            try {
                PrintWriter out = new PrintWriter(ChatClient.clientSocket.getOutputStream(), true);
                while (true) {
                    if (write.hasNext()) {
                        out.println(write.nextLine());
                    }
                }
            } catch (IOException e) {
                write.close();
            }
        }
    }
}
