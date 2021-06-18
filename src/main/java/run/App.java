package run;

import controller.SocketHandler;
import view.ChatBox;
import view.RoomChatForm;
import view.SelectChatForm;
import view.ServerSelectForm;

import java.util.Vector;

public class App {
    public static ServerSelectForm serverSelectForm = null;
    public static String username = "";
    public static SocketHandler socketHandler;
    public static SelectChatForm selectChatForm = null;
    public static Vector<ChatBox> chatBoxVector;
    public static Vector<RoomChatForm> roomChatFormVector;

    public static void main(String[] args) {
        serverSelectForm = new ServerSelectForm();
    }
}
