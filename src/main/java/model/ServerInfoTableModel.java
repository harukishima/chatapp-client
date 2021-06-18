package model;

import javax.swing.table.AbstractTableModel;

public class ServerInfoTableModel extends AbstractTableModel {
    ListServerInfo listServerInfo;
    String[] columnNames = {"Tên", "Địa chỉ", "Port"};

    public ServerInfoTableModel(ListServerInfo listServerInfo) {
        this.listServerInfo = listServerInfo;
    }

    public void setListServerInfo(ListServerInfo listServerInfo) {
        this.listServerInfo = listServerInfo;
        fireTableDataChanged();
    }

    public ListServerInfo getListServerInfo() {
        return listServerInfo;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public int getRowCount() {
        return listServerInfo.getList().size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        ServerInfo serverInfo = listServerInfo.getList().get(rowIndex);
        return switch (columnIndex) {
            case 0 -> serverInfo.getName();
            case 1 -> serverInfo.getServerAddress();
            case 2 -> serverInfo.getPort();
            default -> null;
        };
    }
}
