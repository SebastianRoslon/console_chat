package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ClientListener {
    static class Listener implements Runnable {
        private BufferedReader in;

        @Override
        public void run() {
            try {
                in = new BufferedReader(
                        new InputStreamReader(ChatClient.clientSocket.getInputStream()));

                String read;
                while (true) {
                    read = in.readLine();
                    if (read != null && !(read.isEmpty())) {
                        System.out.println(read);
                    }
                }
            } catch (IOException e) {
            }
        }
    }
}
