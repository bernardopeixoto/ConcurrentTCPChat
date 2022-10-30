import java.io.IOException;

public class Commands {

    public static final String EXIT = "exit";
    public static final String LIST = "list";
    public static final String WHISPER = "whisper";

    public static void compare(String command, Server server, ClientConnection clientConnection) {

        if (command.equals(EXIT)) {
            try {

                clientConnection.getClientSocket().close();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (command.equals(LIST)) {
            server.currentPeopleOnline();
        } else if (command.equals(WHISPER)) {
            server.send(" ");
            server.send("_____________________________________________________");
            server.send("Who do you want to start a private conversation with?");
            server.send("_____________________________________________________");
            server.send(" ");
            server.read();
            server.whisper();

        }

    }


}
