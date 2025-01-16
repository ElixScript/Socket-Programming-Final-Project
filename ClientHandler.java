import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashSet;

public class ClientHandler implements Runnable {
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;
    private HashSet<String> blockedUsers; // To store blocked usernames

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUsername = bufferedReader.readLine();
            this.blockedUsers = new HashSet<>();
            clientHandlers.add(this);
            broadcastMessage("SERVER: " + clientUsername + " has entered the chat!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        String messageFromClient;

        while (socket.isConnected()) {
            try {
                messageFromClient = bufferedReader.readLine();
                if (messageFromClient.startsWith("/")) {
                    processCommand(messageFromClient);
                } else {
                    broadcastMessage(clientUsername + ": " + messageFromClient);
                }
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    public void broadcastMessage(String messageToSend) {
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                // Do not send the message to users who have blocked the sender
                if (!clientHandler.clientUsername.equals(clientUsername)
                        && !clientHandler.blockedUsers.contains(clientUsername)) {
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    public void processCommand(String command) {
        if (command.startsWith("/whisper ")) {
            WhisperCommand(command);
        } else if (command.startsWith("/block ")) {
            BlockCommand(command);
        } else if (command.startsWith("/unblock ")) {
            UnblockCommand(command);
        } else if (command.equalsIgnoreCase("/list")) {
            listOnlineUsers();
        } else {
            try {
                bufferedWriter.write("SERVER: Unknown command!");
                bufferedWriter.newLine();
                bufferedWriter.flush();
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    public void WhisperCommand(String command) {
        int firstSpaceIndex = command.indexOf(" ");
        int secondSpaceIndex = command.indexOf(" ", firstSpaceIndex + 1);
        if (secondSpaceIndex != -1) {
            String targetUsername = command.substring(firstSpaceIndex + 1, secondSpaceIndex).trim();
            String messageToSend = command.substring(secondSpaceIndex + 1).trim();

            boolean userFound = false;
            for (ClientHandler clientHandler : clientHandlers) {
                if (clientHandler.clientUsername.equalsIgnoreCase(targetUsername)) {
                    userFound = true;

                    // Check if the target user has blocked the sender
                    if (clientHandler.blockedUsers.contains(clientUsername)) {
                        try {
                            bufferedWriter.write("SERVER: You cannot send a whisper to " + targetUsername + " as they have blocked you.");
                            bufferedWriter.newLine();
                            bufferedWriter.flush();
                        } catch (IOException e) {
                            closeEverything(socket, bufferedReader, bufferedWriter);
                        }
                    } else {
                        try {
                            clientHandler.bufferedWriter.write("Whisper from " + clientUsername + ": " + messageToSend);
                            clientHandler.bufferedWriter.newLine();
                            clientHandler.bufferedWriter.flush();
                            bufferedWriter.flush();
                        } catch (IOException e) {
                            System.err.println("Error sending whisper to " + targetUsername);
                            closeEverything(socket, bufferedReader, bufferedWriter);
                        }
                    }
                    break;
                }
            }

            if (!userFound) {
                try {
                    bufferedWriter.write("SERVER: User " + targetUsername + " not found!");
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                } catch (IOException e) {
                    closeEverything(socket, bufferedReader, bufferedWriter);
                }
            }
        } else {
            try {
                bufferedWriter.write("SERVER: Invalid whisper format. Use /whisper username message");
                bufferedWriter.newLine();
                bufferedWriter.flush();
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }
    public void BlockCommand(String command) {
        String targetUsername = command.substring(7).trim(); // Extract username after /block
        if (!targetUsername.equalsIgnoreCase(clientUsername)) {
            blockedUsers.add(targetUsername);
            try {
                bufferedWriter.write("SERVER: You have blocked " + targetUsername);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        } else {
            try {
                bufferedWriter.write("SERVER: You cannot block yourself!");
                bufferedWriter.newLine();
                bufferedWriter.flush();
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    public void UnblockCommand(String command) {
        String targetUsername = command.substring(9).trim(); // Extract username after /unblock

        if (blockedUsers.remove(targetUsername)) {
            try {
                bufferedWriter.write("SERVER: You have unblocked " + targetUsername);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        } else {
            try {
                bufferedWriter.write("SERVER: User " + targetUsername + " is not in your blocked list.");
                bufferedWriter.newLine();
                bufferedWriter.flush();
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    public void listOnlineUsers() {
        try {
            bufferedWriter.write("SERVER: Online Users:");
            bufferedWriter.newLine();
            for (ClientHandler clientHandler : clientHandlers) {
                bufferedWriter.write("- " + clientHandler.clientUsername);
                bufferedWriter.newLine();
            }
            bufferedWriter.flush();
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void removeClientHandler() {
        clientHandlers.remove(this);
        broadcastMessage("SERVER: " + clientUsername + " has left the chat!");
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClientHandler();
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
