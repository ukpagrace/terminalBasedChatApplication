package org.example;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ClientHandler implements Runnable {
    private static ArrayList<ClientHandler> users = new ArrayList<>();
    private static ConcurrentHashMap<String, ArrayList<String>> friendShips = new ConcurrentHashMap<>();

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientName;

    private static final String[] ACTIONS = {
            "/list",
            "/suggest",
            "/friends",
            "/add <username>",
            "/accept <username>",
            "/decline <username>",
            "/setbio <username>"
    };

    public ClientHandler(Socket socket) throws IOException {
        try {
            this.socket = socket;
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            clientName = bufferedReader.readLine();
            System.out.println(clientName + " just joined the chat");
            users.add(this);
        } catch (IOException e) {
            closeEverything();
        }

    }


    public void userOptions(String clientMessage) {
        try {
            String[] parts = clientMessage.split(" ", 2);
            String baseCommand = parts[0];
            String argument = parts.length > 1 ? parts[1] : null;
            switch (baseCommand) {
                case "/help":
                    String actionList = String.join(System.lineSeparator(), ACTIONS);
                    bufferedWriter.write(actionList);
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                    break;
                case "/list":
                    String usersString = users.stream()
                            .map(user -> user.clientName)
                            .filter(username -> !username.equals(this.clientName))
                            .collect(Collectors.joining(System.lineSeparator()));
                    if(usersString.isEmpty()){
                        bufferedWriter.write("no friends have joined the platform yet");
                    }else{
                        bufferedWriter.write(usersString);
                    }
                        bufferedWriter.newLine();
                        bufferedWriter.flush();
                    break;
                case "/add":
                    for(ClientHandler user: users){
                        if(user.clientName.equals(argument)){
                            user.bufferedWriter.write("Notification:  You have a friend request from" +
                                            this.clientName + ".\n"
                                            + "type \"/accept\" to accept or \"/decline\" to decline ");
                            user.bufferedWriter.newLine();
                            user.bufferedWriter.flush();
                        }
                    }
                    break;
                case "/accept":
                    friendShips.putIfAbsent(this.clientName, new ArrayList<>());
                    for(ClientHandler user: users){
                        if(user.clientName.equals(argument)){
                            user.bufferedWriter.write("Friend request accepted. " + this.clientName + " has now been added to your friend list.");
                            user.bufferedWriter.newLine();
                            user.bufferedWriter.flush();

                            friendShips.get(this.clientName).add(user.clientName);
                            friendShips.putIfAbsent(user.clientName, new ArrayList<>());
                            friendShips.get(user.clientName).add(user.clientName);
                            break;
                        }
                    }
                  break;

                case "/friends":
                        ArrayList<String> friends = friendShips.get(this.clientName);
                        this.bufferedWriter.write(String.join(System.lineSeparator(), friends));
                        this.bufferedWriter.newLine();
                        this.bufferedWriter.flush();
                    break;

                case "/chat":
                    break;

                default:
                    bufferedWriter.write("command does not exist, type /list to see available commands");
                    bufferedWriter.newLine();
                    bufferedWriter.flush();

            }
        } catch (IOException e) {
            closeEverything();
        }
    }



    public void closeEverything() {
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


    @Override
    public void run() {
        String clientMessage;
        while (socket.isConnected()) {
            try {
                clientMessage = bufferedReader.readLine();
                userOptions(clientMessage);
            } catch (IOException e) {
                closeEverything();
                break;
            }

        }

    }
}
