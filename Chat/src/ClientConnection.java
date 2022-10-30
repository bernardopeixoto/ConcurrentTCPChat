import java.io.*;
import java.net.Socket;

public class ClientConnection implements Runnable {

    private Socket clientSocket;

    private Server server;

    private String name = "guest-";

    private BufferedReader clientInput;
    private PrintWriter clientOutput;


    public ClientConnection(Socket clientSocket, Server server) {
        this.clientSocket = clientSocket;
        this.server = server;
    }

    @Override
    public void run() {

        try {

            setupStreams();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void setupStreams() throws IOException {
        this.clientInput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        this.clientOutput = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
    }

    public void send(String message){
        clientOutput.println(message);
    }



    public Socket getClientSocket(){
        return this.clientSocket;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }



}
