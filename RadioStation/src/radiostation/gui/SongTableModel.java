package radiostation.gui;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import radiostation.Song;

public class SongTableModel extends AbstractTableModel {
    private static final String[] columnNames = {"Τίτλος", "Καλλιτέχνης/Συγκρότημα", "Διάρκεια"};
    private final String[] header;
    private final List<Object[]> songs1;
    private int songsAdded;
    private final List<Song> songsModel;

    public SongTableModel(List<Song> songs) {
        songsAdded = 0;
        this.header = columnNames;
        this.songsModel = songs;
        this.songs1 = new ArrayList<>();
        if( songs != null && songs.size() > 0 ){
            for(Song song:songs) {   
                this.songs1.add(new Object[]{
                    song.getTitle(),
                    (song.getAlbumId().getArtistId()!=null) ? song.getAlbumId().getArtistId().getArtisticname()
                            :song.getAlbumId().getMusicgroupId().getName(), 
                    song.getDuration()
                });    
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
        return songs1.size();
    }

    @Override
    public Object getValueAt(int row, int column) {
        return songs1.get(row)[column];
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        songs1.get(row)[column] = value;
        fireTableCellUpdated(row, column);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
    
    public void addRow(){

        songs1.add(new Object[header.length]);
        songsAdded++;

        fireTableRowsInserted(0, (songs1.isEmpty())?0:songs1.size()-1);
    }
    
    public void deleteRow(int index){
        if( index <= songs1.size()-1 && index >= songs1.size()-songsAdded ){
            songsAdded--;
        }
        songs1.remove(index);
        fireTableRowsDeleted(0, (songs1.isEmpty())?0:songs1.size()-1);
    }

    public List<Object[]> getSongs() {
        return songs1;
    }

    public int getSongsAdded() {
        return songsAdded;
    }

    public List<Song> getSongsModel() {
        return songsModel;
    }
    
    public void addSong(Song song) {
        addRow();
        setValueAt(song.getTitle(), getRowCount()-1, 0);
        setValueAt((song.getAlbumId().getArtistId()!=null) ? song.getAlbumId().getArtistId().getArtisticname()
                            : song.getAlbumId().getMusicgroupId().getName(), getRowCount()-1, 1);
        setValueAt(song.getDuration(), getRowCount()-1, 2);
    } 

}
