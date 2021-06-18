package view;

import model.ListServerInfo;
import model.ServerInfo;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.text.NumberFormat;

public class CreateServerForm extends JFrame {
    private JTextField nameField;
    private JTextField addressField;
    private JFormattedTextField portField;
    private JButton OKButton;
    private JButton cancelButton;
    private JPanel panel;
    private final ListServerInfo listServerInfo;
    private final ServerSelectForm serverSelectForm;

    public CreateServerForm(ListServerInfo listServerInfo, ServerSelectForm serverSelectForm) {
        this.listServerInfo = listServerInfo;
        this.serverSelectForm = serverSelectForm;
        initForm();
    }

    private void initForm() {
        add(panel);
        setTitle("Thêm server");
        setSize(500,200);
        setLocationRelativeTo(null);
        getRootPane().setDefaultButton(OKButton);
        setVisible(true);
        cancelButton.addActionListener(e -> dispose());
        OKButton.addActionListener(e -> addServer());
    }

    private void addServer() {
        if (addressField.getText().isEmpty() || portField.getText().isEmpty()) {
            JOptionPane.showConfirmDialog(this, "Chưa nhập địa chỉ",
                    "Lỗi", JOptionPane.DEFAULT_OPTION);
            return;
        }
        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setName(nameField.getText());
        serverInfo.setServerAddress(addressField.getText());
        serverInfo.setPort((int) portField.getValue());
        listServerInfo.getList().add(serverInfo);
        serverSelectForm.updateConfigFile();
        dispose();
    }

    private void createUIComponents() {
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setGroupingUsed(false);
        NumberFormatter numberFormatter = new NumberFormatter(numberFormat);
        numberFormatter.setValueClass(Integer.class);
        numberFormatter.setAllowsInvalid(false);
        portField = new JFormattedTextField(numberFormatter);
        portField.setValue(5678);
    }
}
