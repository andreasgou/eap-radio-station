package radiostation.gui;

import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import radiostation.MusicProductionCompany;

public class ProductionCompanyRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(
            JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (value instanceof MusicProductionCompany) {
            MusicProductionCompany m = (MusicProductionCompany) value;
            setText(m.getName());
        }
        return this;
    }
}