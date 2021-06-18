package view;

import controller.MessageController;
import controller.SocketController;
import controller.SocketHandler;
import model.ListServerInfo;
import controller.Message;
import model.ServerInfo;
import model.ServerInfoTableModel;
import run.App;
import util.XmlUtils;

import javax.swing.*;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class ServerSelectForm extends JFrame {
    private JPanel panel1;
    private JTable serverTable;
    private JButton deleteServerButton;
    private JButton addServerButton;
    private JButton editServerButton;
    private JTextField usernameField;
    private JButton connectButton;
    private ServerInfoTableModel serverInfoTableModel;

    public ServerSelectForm() {
        add(panel1);
        setTitle("Kết nối");
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        initForm();
    }

    private void initForm() {
        ListServerInfo listServerInfo = XmlUtils.readFromFile("servers.xml");
        if (listServerInfo == null) {
            listServerInfo = new ListServerInfo(new ArrayList<>());
        }
        serverInfoTableModel = new ServerInfoTableModel(listServerInfo);
        serverTable.setModel(serverInfoTableModel);
        addServerButton.addActionListener(e -> new CreateServerForm(serverInfoTableModel.getListServerInfo(), this));
        connectButton.addActionListener(e -> connectServer());
        editServerButton.addActionListener(e -> editServer());
        deleteServerButton.addActionListener(e -> deleteServer());
    }

    public void updateListServerTable() {
        ListServerInfo listServerInfo = XmlUtils.readFromFile("servers.xml");
        if (listServerInfo != null) {
            updateServerTable(listServerInfo);
        }
    }

    public void updateServerTable(ListServerInfo list) {
        serverInfoTableModel.setListServerInfo(list);
    }

    public void fireTableUpdate() {
        serverInfoTableModel.fireTableDataChanged();
    }

    public void updateConfigFile() {
        XmlUtils.writeToFile("servers.xml", serverInfoTableModel.getListServerInfo());
        fireTableUpdate();
    }

    public void connectServer() {
        if (serverTable.getSelectedRow() < 0) {
            JOptionPane.showConfirmDialog(this, "Chưa chọn server", "Lỗi", JOptionPane.DEFAULT_OPTION);
            return;
        }
        if (usernameField.getText().isEmpty()) {
            JOptionPane.showConfirmDialog(this, "Chưa nhập tên", "Lỗi", JOptionPane.DEFAULT_OPTION);
            return;
        }
        ServerInfo serverInfo = serverInfoTableModel.getListServerInfo().getList().get(serverTable.getSelectedRow());
        try {
            Socket socket = new Socket(serverInfo.getServerAddress(), serverInfo.getPort());
            Message message = new Message(usernameField.getText(), "server", "create connection", 0);
            MessageController.send(socket, message);
            Message res = MessageController.receive(socket);
            if (res != null && res.getCode() == 1) {
                JOptionPane.showMessageDialog(this, "Kết nối thành công");
                App.socketHandler = new SocketHandler(socket);
                App.socketHandler.start();
                App.username = usernameField.getText();
                App.serverSelectForm = null;
                App.selectChatForm = new SelectChatForm();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Không thể kết nối");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void editServer() {
        int row = serverTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Chưa chọn server");
            return;
        }
        ServerInfo serverInfo = serverInfoTableModel.getListServerInfo().getList().get(row);
        new EditServerForm(this, serverInfo);
    }

    private void deleteServer() {
        int row = serverTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Chưa chọn server");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có thực sự muốn xoá?", "Xác nhận",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.NO_OPTION) {
            return;
        }
        serverInfoTableModel.getListServerInfo().getList().remove(row);
        updateConfigFile();
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
