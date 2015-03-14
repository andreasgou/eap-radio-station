package radiostation.gui;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import radiostation.Song;

public class SongTableModel extends AbstractTableModel {
    private static final String[] columnNames = {"Σειρά", "Τίτλος Τραγουδιού", "Διάρκεια"};
    private String[] header;
    private List<Object[]> songs;
    private int songsAdded;

    public SongTableModel(List<Song> songs) {
        songsAdded = 0;
        this.header = columnNames;
        this.songs = new ArrayList<>();
        if( songs != null && songs.size() > 0 ){
            for(Song song:songs){   
                this.songs.add(new Object[]{song.getTracknr(), song.getTitle(), song.getDuration()});
            }
        }
    }

    @Override
    public int getColumnCount() {
        return header.length;
    }

    @Override
    public String getColumnName(int column) {
        return header[column];
    }

    @Override
    public int getRowCount() {
        return songs.size();
    }

    @Override
    public Object getValueAt(int row, int column) {
        return songs.get(row)[column];
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        songs.get(row)[column] = value;
        fireTableCellUpdated(row, column);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }
    
    public void addRow(){
//        int rowsBeforeAddition = getRowCount();
        songs.add(new Object[header.length]);
        songsAdded++;
//        fireTableRowsInserted((rowsBeforeAddition == 0)?0:rowsBeforeAddition-1, rowsBeforeAddition);
        fireTableRowsInserted(0, (songs.isEmpty())?0:songs.size()-1);
    }
    
    public void deleteRow(int index){
        if( index <= songs.size()-1 && index >= songs.size()-songsAdded ){
            songsAdded--;
        }
        songs.remove(index);
        fireTableRowsDeleted(0, (songs.isEmpty())?0:songs.size()-1);
    }

    public List<Object[]> getSongs() {
        return songs;
    }

    public int getSongsAdded() {
        return songsAdded;
    }
}
