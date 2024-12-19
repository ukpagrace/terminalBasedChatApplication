package org.example;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements  Runnable {
    private ArrayList<String> users  = new ArrayList<>();

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientName;


    public  ClientHandler(Socket socket) throws IOException {
        try {
            this.socket = socket;
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            clientName = bufferedReader.readLine();

            addUser(clientName);
        } catch (IOException e) {
            closeEverything();
        }

    }

    public void closeEverything(){
        try{
            if(socket != null){
                socket.close();
            }
            if(bufferedReader != null){
                bufferedReader.close();
            }
            if(bufferedWriter != null){
                bufferedWriter.close();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void addUser(String user){
        Server.users.add(user);
    }

    @Override
    public void run(){

    }
}
