import java.io.*;
import java.net.*;
import java.util.HashMap;

public class ChatServer {

    static final HashMap<String, PrintWriter> connectedClients = new HashMap<>();
    static final HashMap<String, String> chatRooms = new HashMap<>();
    private static final int MAX_CONNECTED = 50;
    private static final int PORT = 10000;
    private static ServerSocket listener;

    public static void main(String[] args) throws IOException {
        start();
    }

    private static class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String name;
        private String room;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }


        @Override
        public void run() {
            System.out.println("Connected " + socket.getInetAddress());
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                while (true) {
                    out.println("Enter user name: ");
                    name = in.readLine();
                    if (name == null) {
                        return;
                    }
                    synchronized (connectedClients) {
                        if (!name.isEmpty() && !connectedClients.keySet().contains(name)) {
                            break;
                        } else {
                            out.println("Username is ");
                        }
                    }
                }

                out.println("Enter room name (public is deflaut)");
                room = in.readLine();
                room = room.toUpperCase();
                chatRooms.put(name, room);

                out.println("Assigned as " + name);
                out.println("Assignet to room " + room);
                System.out.println(name + " is assinging to room " + room);
                MessageService.broadcastMessage(name + " is assinging to room " + room, room);

                connectedClients.put(name, out);
                MessageService.printHistoryMessages(room, out);
                out.println("Now you can sending messages. To see help type: /help");

                String message;
                label:
                while ((message = in.readLine()) != null) {
                    if (!message.isEmpty()) {
                        switch (message.trim()) {
                            case "/help":
                                shwowHelp();
                                break;
                            case "/send-file":
                                if (sendFile())
                                    continue;
                                break;
                            case "/change-room":
                                changeRoom();
                                break;
                            case "/exit":
                                break label;
                            default:
                                MessageService.broadcastMessage(name + ": " + message, room);
                                MessageService.saveMessage(name + ": " + message, room);
                                break;
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println(e);
            } finally {
                if (name != null) {
                    System.out.println(name + " is exiting  " + room);
                    MessageService.broadcastMessage(name + " is exiting " + room, room);
                    connectedClients.remove(name);
                }
            }
        }




        private void changeRoom() throws IOException {
            out.println("Exit room " + room + ".");
            System.out.println(name + " is exiting room " + room);
            MessageService.broadcastMessage(name + " is exiting room " + room, room);

            out.println("Enter room name ('public', is default):");
            room = in.readLine();
            room = room.toUpperCase();
            chatRooms.put(name, room);

            out.println("Is in room " + room + ".");
            System.out.println(name + " is in room " + room);
            MessageService.broadcastMessage(name + " is in room " + room, room);

            MessageService.printHistoryMessages(room, out);
        }

        private boolean sendFile() throws IOException {
            out.println("Enter filepath:");
            String path = in.readLine();

            File file = null;
            try {
                file = new File(path);
                if (!file.exists()) {
                    out.println("Filepath is not correct");
                    return true;
                }
            } catch (Exception e) {
                out.println("No file found");
                return true;
            }

            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            FileInputStream fileInputStream = new FileInputStream(file);

            int bytes = 0;
            dataOutputStream.writeLong(file.length());
            byte[] buffer = new byte[4 * 1024];
            while ((bytes = fileInputStream.read(buffer)) != -1) {
                dataOutputStream.write(buffer, 0, bytes);
                dataOutputStream.flush();
            }

            out.println("File is send");
            MessageService.broadcastMessage(name + " sending file " + file.getName() + " on room " + room, room);

            bytes = 0;
            FileOutputStream fileOutputStream = new FileOutputStream(file.getName());

            long size = dataInputStream.readLong();
            buffer = new byte[4 * 1024];
            while (size > 0 && (bytes = fileInputStream.read(buffer, 0,
                    (int) Math.min(buffer.length, size))) != -1) {
                fileOutputStream.write(buffer, 0, bytes);
                size -= bytes;
            }

            fileInputStream.close();
            fileOutputStream.close();
            dataOutputStream.close();
            dataInputStream.close();
            return false;
        }

        private void shwowHelp() {
            out.println(
                    "Command list:\n/help - show command list\n/send-file - sending file\n/change-room - change room\n/exit - exit");
        }
    }


    public static void start() {
        try {
            listener = new ServerSocket(PORT);

            System.out.println("Server start on port: " + PORT);
            System.out.println("Waiting on connection...");

            while (true) {
                if (connectedClients.size() <= MAX_CONNECTED) {
                    Thread newClient = new Thread(
                            new ClientHandler(listener.accept()));
                    newClient.start();
                }
            }
        } catch (BindException e) {
        } catch (Exception e) {
            System.out.println("\nException : \n");
            System.out.println(e);
            System.out.println("\nExiting...");
        }
    }

    public static void stop() throws IOException {
        if (!listener.isClosed()) {
            listener.close();
        }
    }
}
