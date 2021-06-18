package controller;

import run.App;

import java.io.IOException;
import java.net.Socket;

public class SocketController {
    public static void closeSocket(Socket socket) {
        Message closeMessage = new Message(App.username, "server", "Close connection", -2);
        try {
            MessageController.send(socket, closeMessage);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
