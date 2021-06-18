package view;

import controller.Message;
import controller.MessageController;
import run.App;
import util.EmojiUtils;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

public class ChatBox extends JFrame {
    private JPanel panel1;
    private JButton sendButton;
    private JTextPane chatArea;
    private JTextArea messageArea;
    private JButton settingsButton;
    private final String receiver;
    private List<Message> messageList;
    private KeyAdapter sendListener;

    public ChatBox(String receiver) {
        this.receiver = receiver;
        messageList = new ArrayList<>();
        initForm();
    }

    public List<Message> getMessageList() {
        return messageList;
    }

    public String getReceiver() {
        return receiver;
    }

    private void initForm() {
        add(panel1);
        setTitle(receiver);
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setVisible(true);
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
                Message sendMessage = new Message(App.username, receiver, messageArea.getText().trim(), 2);
                MessageController.sendAsync(App.socketHandler.getSocket(), sendMessage);
                newMessage(sendMessage);
                messageArea.setText("");
            }
            messageArea.setText("");
        });
        //Font notocolor = FontUtils.CustomFont("/fonts/NotoEmoji-Regular.ttf", this.getClass());
        //chatArea.setFont(notocolor);
    }

    public void disconnectAndClose() {
        Message message = new Message(App.username, receiver, "", 3);
        MessageController.sendAsync(App.socketHandler.getSocket(), message);
        dispose();
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
        String[] messageBody = body.split(" ");
        EmojiUtils emojiUtils = EmojiUtils.getInstance();
        for (String s : messageBody) {
            String html = emojiUtils.getHTMLCode(s);
            if (html != null) {
                body = body.replaceAll(s, html);
            }
        }
        body = body.replaceAll("\n", "<br>");
        try {
            if (message.getCode() == 2) {
                if (message.getSender().equals(receiver)) {

                    text = "<p style=\"font-family: " + font +  " \"><span style=\"color: Red\">" + message.getSender() + "</span>: " + body +"</p>";
                } else {
                    text = "<p style=\"font-family: " + font +  " \"><span style=\"color: Blue\">" + message.getSender() + "</span>: " + body +"</p>";
                }
            } else if (message.getCode() == 3) {
                text = "<p style=\"color: Gray; text-align: center\">" + receiver + " is offline" + "</p>";
            }
            document.insertBeforeEnd(element, text);
        } catch (BadLocationException | IOException e) {
            e.printStackTrace();
        }

    }

    public static ChatBox findChatBox(Vector<ChatBox> chatBoxes, String receiver) {
        List<ChatBox> list = chatBoxes.stream().filter(c -> c.getReceiver().equals(receiver)).collect(Collectors.toList());
        if (!list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

}
