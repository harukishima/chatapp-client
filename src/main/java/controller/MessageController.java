package controller;

import java.io.*;
import java.net.Socket;

class MessageSender extends Thread {
    Socket socket;
    Message message;

    public MessageSender(Socket socket, Message message) {
        this.socket = socket;
        this.message = message;
    }

    @Override
    public void run() {
        MessageController.send(socket, message);
    }
}

public class MessageController {
    public static Message receive(Socket socket){
        Message message = null;
        try {
            ObjectInputStream stream = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
            message = (Message) stream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return message;
    }

    public static boolean send(Socket socket,Message message) {
        try {
            ObjectOutputStream stream = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            stream.writeObject(message);
            stream.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void sendAsync(Socket socket, Message message) {
        MessageSender messageSender = new MessageSender(socket, message);
        messageSender.start();
    }
}
