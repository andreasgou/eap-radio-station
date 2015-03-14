/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package radiostation.gui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 *
 * @author user
 */
@Entity
@Table(name = "PLAYLIST_SONG", catalog = "", schema = "APP")
@NamedQueries({
    @NamedQuery(name = "PlaylistSong.findAll", query = "SELECT p FROM PlaylistSong p"),
    @NamedQuery(name = "PlaylistSong.findByPlaylistId", query = "SELECT p FROM PlaylistSong p WHERE p.playlistSongPK.playlistId = :playlistId"),
    @NamedQuery(name = "PlaylistSong.findBySongId", query = "SELECT p FROM PlaylistSong p WHERE p.playlistSongPK.songId = :songId")})
public class PlaylistSong implements Serializable {
    @Transient
    private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected PlaylistSongPK playlistSongPK;

    public PlaylistSong() {
    }

    public PlaylistSong(PlaylistSongPK playlistSongPK) {
        this.playlistSongPK = playlistSongPK;
    }

    public PlaylistSong(int playlistId, int songId) {
        this.playlistSongPK = new PlaylistSongPK(playlistId, songId);
    }

    public PlaylistSongPK getPlaylistSongPK() {
        return playlistSongPK;
    }

    public void setPlaylistSongPK(PlaylistSongPK playlistSongPK) {
        this.playlistSongPK = playlistSongPK;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (playlistSongPK != null ? playlistSongPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PlaylistSong)) {
            return false;
        }
        PlaylistSong other = (PlaylistSong) object;
        if ((this.playlistSongPK == null && other.playlistSongPK != null) || (this.playlistSongPK != null && !this.playlistSongPK.equals(other.playlistSongPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "radiostation.gui.PlaylistSong[ playlistSongPK=" + playlistSongPK + " ]";
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }
    
}
