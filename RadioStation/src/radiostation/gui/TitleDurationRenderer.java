package radiostation.gui;

import java.awt.Component;
import java.text.SimpleDateFormat;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class TitleDurationRenderer implements TableCellRenderer {
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel editor = new JLabel();
        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
        if (value != null)
            editor.setText(sdf.format(new java.util.Date(((Integer)value).longValue()*1000)));
        return editor;
    }
}