import java.io.*;

public class MessageService {
    static void saveMessage(String message, String room) {
        String filePath = room + ".txt";
        FileWriter fileWriter = null;

        try {
            fileWriter = new FileWriter(filePath, true);
            fileWriter.write(message);
            fileWriter.write("\n");
            fileWriter.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }


    public static void printHistoryMessages(String room, PrintWriter out) {
        File file = new File(room + ".txt");
        if (file.exists()) {
            BufferedReader reader;
            try {
                reader = new BufferedReader(new FileReader(file));
                String line = reader.readLine();
                while (line != null) {
                    out.println(line);
                    line = reader.readLine();
                }
                reader.close();
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }

    static void broadcastMessage(String message, String room) {
        for (String name : ChatServer.connectedClients.keySet()) {
            if (ChatServer.chatRooms.get(name).equals(room)) {
                ChatServer.connectedClients.get(name).println(message);
            }
        }
    }

}
