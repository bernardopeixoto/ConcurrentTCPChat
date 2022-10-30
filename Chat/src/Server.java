import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private ServerSocket serverSocket;

    private Socket clientSocket;

    private final int PORT = 8080;

    private int connections;

    private List<ClientConnection> clientConnectionsList;

    private ExecutorService service;

    private BufferedReader serverInput;
    private PrintWriter serverOutput;


    public Server() {
        this.connections = 0;
        this.clientConnectionsList = Collections.synchronizedList(new LinkedList<>());
        this.service = Executors.newCachedThreadPool();
        start();
    }

    private void start(){

        try {
            serverSocket = new ServerSocket(PORT);

            while(true) {

                listen();

            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void listen() throws IOException {

        this.clientSocket = serverSocket.accept();
        connections++;
        System.out.println("New connection from " + clientSocket.toString());


        ClientConnection clientConnection = new ClientConnection(clientSocket, this);
        clientConnectionsList.add(clientConnection);
        service.submit(clientConnection);

        setupStreams();

        greet();

        clientConnection.setName(clientConnection.getName() + connections);

        broadcast(clientConnection.getName() + BroadcastMessages.CONNECTED);

        changeName(clientConnection);

        optionReader(clientConnection);
    }

    private void setupStreams(){
        try {
            this.serverOutput = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);
            this.serverInput =  new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void greet(){
        System.out.println("here");
        serverOutput.println(" ");
        serverOutput.println("                        =======================================");
        serverOutput.println(" ");
        serverOutput.println("                        Welcome to bernas' concurrent ChatServer");
        serverOutput.println(" ");
        serverOutput.println("                        =======================================");
    }

    private void broadcast(String message){

        for (ClientConnection connection: clientConnectionsList) {

            try {

                serverOutput = new PrintWriter(new OutputStreamWriter(connection.getClientSocket().getOutputStream()), true);
                serverOutput.println(" ");
                serverOutput.println("_____________________________________");
                serverOutput.println(" ");
                serverOutput.println(message);
                serverOutput.println("_____________________________________");

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public void currentPeopleOnline(){

        for (ClientConnection client: clientConnectionsList) {
            serverOutput.println(" ");
            serverOutput.println(ServerToClientMessages.GET_PEOPLE_ONLINE);
            serverOutput.println(client.getName());
            serverOutput.println(" ");
        }

    }

    private void optionReader(ClientConnection clientConnection){

        serverOutput.println("__________________________");
        serverOutput.println(" What do you want to do?");
        serverOutput.println("__________________________");

        try {

                serverOutput.println(" ");
                serverOutput.println("/list, /whisper or /exit");
                String action = serverInput.readLine();

                if (action.startsWith("/")) {
                    String command = action.substring(1);
                    Commands.compare(command, this, clientConnection);
                }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void send(String message){
        serverOutput.println(" ");
        serverOutput.println(" * ");
        serverOutput.println(message);
        serverOutput.print(" * ");
        serverOutput.print(" ");
    }

    public String read(){

        String message;

        try {

            message = serverInput.readLine();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return message;

    }

    public void whisper(){



    }

    private void changeName(ClientConnection clientConnection){

        send(ServerToClientMessages.CHANGE_NAME);

        try {

            String newName = clientConnection.getName();

            String name = serverInput.readLine();
            clientConnection.setName(name);

            broadcast(newName + BroadcastMessages.CHANGE_NAME + name);

            serverOutput.println(" ");

            serverOutput.println("* * * * * ");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


}
