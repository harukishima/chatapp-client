package view;

import controller.Message;
import controller.MessageController;
import run.App;
import util.EmojiUtils;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

public class RoomChatForm extends JFrame {
    private JPanel panel;
    private JButton sendButton;
    private JList onlineuserList;
    private JTextPane chatArea;
    private JTextArea messageArea;
    private JButton settingsButton;
    private List<Message> messageList;
    private final String roomId;
    private KeyAdapter sendListener;

    public String getRoomId() {
        return roomId;
    }

    public RoomChatForm(String roomId) {
        this.roomId = roomId;
        messageList = new ArrayList<>();
        add(panel);
        pack();
        setLocationRelativeTo(null);
        setTitle(this.roomId);
        setVisible(true);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        initForm();
    }

    private void initForm() {
        //model = new ListToJListModel(new ArrayList<>());
        //onlineuserList.setModel(model);
        Message getUser = new Message(App.username, this.roomId, "", 8);
        MessageController.sendAsync(App.socketHandler.getSocket(), getUser);

        chatArea.setContentType("text/html");
        chatArea.setEditable(false);
        DefaultCaret caret = (DefaultCaret) chatArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        sendListener = new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendButton.doClick(0);
                }
            }
        };
        messageArea.addKeyListener(sendListener);
        settingsButton.addActionListener(e -> new ChatBoxSettingsForm(messageArea, sendListener));

        sendButton.addActionListener(e -> {
            if (!messageArea.getText().trim().isEmpty()) {
                Message sendMessage = new Message(App.username, this.roomId, messageArea.getText().trim(), 2);
                MessageController.sendAsync(App.socketHandler.getSocket(), sendMessage);
                newMessage(sendMessage);
                messageArea.setText("");
            }
        });
    }

    public void updateUserList(String[] data) {
        //model.setList(list);
        onlineuserList.setListData(data);
        //onlineuserList.updateUI();
    }

    public void newMessage(Message message) {
        messageList.add(message);
        chatBoxRenderer(message);
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }

    private void chatBoxRenderer(Message message) {
        HTMLDocument document = (HTMLDocument) chatArea.getStyledDocument();
        Element element = document.getElement(document.getDefaultRootElement(), StyleConstants.NameAttribute, HTML.Tag.BODY);
        String text = "";
        String font = "Segoe UI Emoji";
        String body = message.getBody();
        String[] messageBody = body.split(" |`");
        EmojiUtils emojiUtils = EmojiUtils.getInstance();
        for (String s : messageBody) {
            String html = emojiUtils.getHTMLCode(s);
            if (html != null) {
                body = body.replaceAll(s, html);
            }
        }
        body = body.replaceFirst("`", ": ");
        body = body.replaceAll("\n", "<br>");
        try {
            if (message.getCode() == 2) {
                if (message.getSender().equals(App.username)) {

                    text = "<p style=\"font-family: " + font +  " \"><span style=\"color: Blue\">" + message.getSender() + "</span>: " + body +"</p>";
                } else {
                    text = "<p style=\"font-family: " + font +  " \">" + body +"</p>";
                }
            } else if (message.getCode() == 3) {
                text = "<p style=\"color: Gray; text-align: center\">" + body + " is offline" + "</p>";
            }
            document.insertBeforeEnd(element, text);
        } catch (BadLocationException | IOException e) {
            e.printStackTrace();
        }

    }

    public static RoomChatForm findChatRoom(Vector<RoomChatForm> roomChatForms, String id) {
        List<RoomChatForm> list = roomChatForms.stream().filter(c -> c.getRoomId().equals(id)).collect(Collectors.toList());
        if (!list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }


}
