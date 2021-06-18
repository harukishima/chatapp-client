package view;

import controller.Message;
import controller.MessageController;
import controller.SocketController;
import model.UserTableModel;
import run.App;
import util.XmlUtils;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class SelectChatForm extends JFrame {
    private JTable userTable;
    private JButton startChatButton;
    private JButton logOutButton;
    private JPanel panel;
    private JTextField roomIdField;
    private JButton joinRoomButton;
    private JButton createRoomButton;
    private JLabel usernameLabel;
    private JLabel serverAddressLabel;
    private JLabel portLabel;
    private UserTableModel userTableModel;

    public SelectChatForm() {
        add(panel);
        setTitle("Trang chủ");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        initForm();
    }

    private void initForm() {
        usernameLabel.setText(App.username);
        serverAddressLabel.setText(App.socketHandler.getSocket().getInetAddress().toString());
        portLabel.setText(String.valueOf(App.socketHandler.getSocket().getPort()));
        userTableModel = new UserTableModel(new ArrayList<>());
        userTable.setModel(userTableModel);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                disconnectFromServer();
            }
        });
        logOutButton.addActionListener(e -> {
            disconnectFromServer();
            App.serverSelectForm = new ServerSelectForm();
            dispose();
        });
        Message askList = new Message(App.username, "server", "", 6);
        MessageController.send(App.socketHandler.getSocket(), askList);
        App.chatBoxVector = new Vector<>();
        App.roomChatFormVector = new Vector<>();
        startChatButton.addActionListener(e -> startChat());
        joinRoomButton.addActionListener(e -> joinRoom());
        createRoomButton.addActionListener(e -> createRoom());
    }

    private List<String> getListUser(Message message) {
        List<String> listUser;
        listUser = XmlUtils.documentToListUser(XmlUtils.stringXmlToDocument(message.getBody()));
        return listUser;
    }

    public void updateListUserOnline(Message message) {
        List<String> listUser = getListUser(message);
        userTableModel.setList(listUser);
        System.out.println("Update user list");
    }

    private void startChat() {
        int row = userTable.getSelectedRow();
        if (row < 0 || userTable.getValueAt(row, 0).equals(App.username)) {
            return;
        }
        String username = (String) userTable.getValueAt(row, 0);
        ChatBox chatBox = ChatBox.findChatBox(App.chatBoxVector, username);
        if (chatBox != null) {
            chatBox.setVisible(true);
        } else {
            App.chatBoxVector.add(new ChatBox(username));
        }
    }

    private void disconnectFromServer() {
        for(ChatBox c : App.chatBoxVector) {
            c.disconnectAndClose();
        }
        for(RoomChatForm c : App.roomChatFormVector) {
            c.dispose();
        }
        App.chatBoxVector.clear();
        App.roomChatFormVector.clear();
        SocketController.closeSocket(App.socketHandler.getSocket());
        try {
            App.socketHandler.stopExecute();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public void serverDisconnect() {
        JOptionPane.showMessageDialog(this, "Server đã ngắt kết nối");
        for(ChatBox c : App.chatBoxVector) {
            c.dispose();
        }
        App.chatBoxVector.clear();
        App.roomChatFormVector.clear();
        SocketController.closeSocket(App.socketHandler.getSocket());
        try {
            App.socketHandler.stopExecute();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        new ServerSelectForm();
        dispose();
    }

    private void joinRoom() {
        if (roomIdField.getText().isEmpty()) {
            return;
        }
        String id = "#"+roomIdField.getText();
        RoomChatForm roomChatForm = RoomChatForm.findChatRoom(App.roomChatFormVector, id);
        if (roomChatForm != null) {
            roomChatForm.setVisible(true);
        } else {
            Message joinMessage = new Message(App.username, id, "", 10);
            MessageController.sendAsync(App.socketHandler.getSocket(), joinMessage);
        }
    }

    private void createRoom() {
        Message createRoomM = new Message(App.username, "server", "", 9);
        MessageController.sendAsync(App.socketHandler.getSocket(), createRoomM);
    }

}
