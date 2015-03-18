package radiostation.gui;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import radiostation.Album;

public class PlaylistArtistGroupRenderer implements TableCellRenderer {
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel editor = new JLabel();
        //SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
        if (value instanceof Album) {
            Album album = (Album) value;
            if (album.getArtistId() != null) 
                editor.setText(album.getArtistId().getArtisticname());
            else
                editor.setText(album.getMusicgroupId().getName());
        }
        return editor;
    }
}