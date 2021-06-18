package controller;

import run.App;
import view.ChatBox;
import view.RoomChatForm;

import javax.swing.*;
import java.io.IOException;
import java.net.Socket;
import java.util.*;

public class SocketHandler extends Thread {
    private final Socket socket;
    private boolean execute;

    public SocketHandler(Socket socket) {
        this.socket = socket;
        execute = true;
    }

    public void stopExecute() throws IOException {
        execute = false;
        socket.close();
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public void run() {
        System.out.println("Start socket handler");
        try {
            while (execute) {
                Message message = MessageController.receive(socket);
                System.out.println("Message receive: " + message);
                switch (message.getCode()) {
                    case -3:
                        App.username = "";
                        App.selectChatForm.serverDisconnect();
                        break;
                    case -2:
                        App.username = "";
                        stopExecute();
                        break;
                    case 2: case 3:
                        if (message.getSender().toCharArray()[0] == '#') {
                            RoomChatForm roomChatForm = RoomChatForm.findChatRoom(App.roomChatFormVector, message.getSender());
                            if (roomChatForm != null) {
                                roomChatForm.newMessage(message);
                            }
                        } else {
                            ChatBox chatBox = ChatBox.findChatBox(App.chatBoxVector, message.getSender());
                            if (chatBox == null) {
                                chatBox = new ChatBox(message.getSender());
                                App.chatBoxVector.add(chatBox);
                            }
                            chatBox.newMessage(message);
                        }
                        break;
                    case 5:
                        Message askList = new Message(App.username, "server", "", 6);
                        MessageController.send(App.socketHandler.getSocket() ,askList);
                        break;
                    case 6:
                        SwingUtilities.invokeLater(() -> App.selectChatForm.updateListUserOnline(message));
                        break;
                    case 7:
                        Message needList = new Message(App.username, message.getSender(), "", 8);
                        MessageController.sendAsync(this.getSocket(), needList);
                        break;
                    case 8:
                        RoomChatForm room = RoomChatForm.findChatRoom(App.roomChatFormVector, message.getSender());
                        if (room != null) {
                            String userString = message.getBody();
                            String[] users = userString.split(",");
                            //List<String> userList = Arrays.asList(users);
                            room.updateUserList(users);
                        }
                        break;
                    case 9:

                    case 11:
                        App.roomChatFormVector.add(new RoomChatForm(message.getSender()));
                        break;
                    default:
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Stop socket handler");
    }
}
