package view;

import model.ListServerInfo;
import model.ServerInfo;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.text.NumberFormat;

public class EditServerForm extends JFrame {
    private JTextField nameField;
    private JTextField addressField;
    private JFormattedTextField portField;
    private JButton cancelButton;
    private JButton OKButton;
    private JPanel panel;
    //private final ListServerInfo listServerInfo;
    private final ServerSelectForm serverSelectForm;
    private final ServerInfo serverInfo;

    public EditServerForm(ServerSelectForm serverSelectForm, ServerInfo serverInfo) {
        //this.listServerInfo = listServerInfo;
        this.serverSelectForm = serverSelectForm;
        this.serverInfo = serverInfo;
        initForm();
    }

    private void initForm() {
        add(panel);
        setTitle("Edit server");
        setSize(500,200);
        setLocationRelativeTo(null);
        getRootPane().setDefaultButton(OKButton);
        setVisible(true);
        cancelButton.addActionListener(e -> dispose());
        OKButton.addActionListener(e -> editServer());
        nameField.setText(serverInfo.getName());
        addressField.setText(serverInfo.getServerAddress());
        portField.setValue(serverInfo.getPort());
    }

    private void editServer() {
        if (addressField.getText().isEmpty() || portField.getText().isEmpty()) {
            JOptionPane.showConfirmDialog(this, "Chưa nhập địa chỉ",
                    "Lỗi", JOptionPane.DEFAULT_OPTION);
            return;
        }
        serverInfo.setName(nameField.getText());
        serverInfo.setServerAddress(addressField.getText());
        serverInfo.setPort((int) portField.getValue());
        serverSelectForm.updateConfigFile();
        dispose();
    }

    private void createUIComponents() {
        NumberFormat numberFormat = NumberFormat.getIntegerInstance();
        numberFormat.setGroupingUsed(false);
        NumberFormatter numberFormatter = new NumberFormatter(numberFormat);
        numberFormatter.setValueClass(Integer.class);
        numberFormatter.setAllowsInvalid(false);
        portField = new JFormattedTextField(numberFormatter);
    }
}
