package org.example;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    Socket socket;
    BufferedWriter bufferedWriter;
    BufferedReader bufferedReader;

    String username;

    public Client(Socket socket, String username) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = username;
        } catch (IOException e) {
            closeEverything();
        }
    }

    public void sendMessage() {
        try {
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner scanner = new Scanner(System.in);
            String clientMessage;

            while (socket.isConnected()) {
                System.out.print("You:");
                clientMessage = scanner.nextLine();
                bufferedWriter.write(clientMessage);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            closeEverything();
        }
    }

    public void receiveMessage(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String message;
                while(socket.isConnected()){
                    try{
                       message = bufferedReader.readLine();
                       System.out.println(message);
                    }catch (IOException e){
                        closeEverything();
                    }
                }
            }
        }).start();
    }

    public void closeEverything() {
        try {
            if (socket != null) {
                socket.close();
            }
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the chat App");
        System.out.println("Enter your username");
        String username = scanner.nextLine();
        Socket socket = new Socket("localhost", 1234);
        System.out.println("Connecting to the server...");
        System.out.println("You are now online.Type /help to see available commands");
        Client client = new Client(socket, username);
        client.receiveMessage();
        client.sendMessage();


    }
}
