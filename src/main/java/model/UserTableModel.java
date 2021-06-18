package model;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class UserTableModel extends AbstractTableModel {
    List<String> list;
    String[] columnNames = {"Online user"};

    public UserTableModel(List<String> list) {
        this.list = list;
    }

    public void setList(List<String> list) {
        this.list = list;
        fireTableDataChanged();
    }

    public List<String> getList() {
        return list;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public int getRowCount() {
        return list.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return list.get(rowIndex);
    }
}
