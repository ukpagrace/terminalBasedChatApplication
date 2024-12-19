package org.example;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    public static CopyOnWriteArrayList<String> users = new CopyOnWriteArrayList<>();
    private ServerSocket serverSocket;

    public Server(ServerSocket serverSocket){
        this.serverSocket = serverSocket;
    }

    public void start(){
        try{
            while (!serverSocket.isClosed()){
                Socket socket = serverSocket.accept();
                System.out.println("A new client just connected");

                ClientHandler clientHandler = new ClientHandler(socket);

                Thread thread = new Thread(clientHandler);
                thread.start();

            }
        } catch (IOException e) {
            closeServerSocket();
        }

    }

    public void closeServerSocket(){
        try{
            if(serverSocket != null){
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        ServerSocket socket = new ServerSocket(1234);
        Server server = new Server(socket);
        server.start();

    }


}
