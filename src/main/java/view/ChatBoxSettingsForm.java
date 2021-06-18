package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyListener;

public class ChatBoxSettingsForm extends JFrame {
    private JRadioButton newlineRadioButton;
    private JRadioButton sendRadioButton;
    private JButton cancelButton;
    private JButton OKButton;
    private JPanel panel;
    private final JTextArea messageArea;
    private final KeyAdapter sendListenter;

    public ChatBoxSettingsForm(JTextArea messageArea, KeyAdapter sendListenter) throws HeadlessException {
        this.messageArea = messageArea;
        this.sendListenter = sendListenter;
        initForm();
    }

    private void initForm() {
        add(panel);
        setSize(300, 150);
        setLocationRelativeTo(null);
        getRootPane().setDefaultButton(OKButton);
        setTitle("Cài đặt");
        setVisible(true);
        cancelButton.addActionListener(e -> dispose());
        OKButton.addActionListener(e -> {
            changeEnterKeyBehavior();
            dispose();
        });
        KeyListener[] keyListeners = messageArea.getKeyListeners();
        if (keyListeners.length == 0) {
            newlineRadioButton.setSelected(true);
        } else {
            sendRadioButton.setSelected(true);
        }
    }

    private void changeEnterKeyBehavior() {
        if (newlineRadioButton.isSelected()) {
            messageArea.removeKeyListener(sendListenter);
        } else {
            messageArea.addKeyListener(sendListenter);
        }
    }
}
