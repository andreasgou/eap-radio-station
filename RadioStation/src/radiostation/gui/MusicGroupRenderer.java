package radiostation.gui;

import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import radiostation.MusicGroup;

public class MusicGroupRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(
            JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (value instanceof MusicGroup) {
            MusicGroup m = (MusicGroup) value;
            setText(m.getName());
        }
        return this;
    }
}