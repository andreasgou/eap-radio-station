/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package radiostation.gui;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author user
 */
@Embeddable
public class PlaylistSongPK implements Serializable {
    @Basic(optional = false)
    @Column(name = "PLAYLIST_ID")
    private int playlistId;
    @Basic(optional = false)
    @Column(name = "SONG_ID")
    private int songId;

    public PlaylistSongPK() {
    }

    public PlaylistSongPK(int playlistId, int songId) {
        this.playlistId = playlistId;
        this.songId = songId;
    }

    public int getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(int playlistId) {
        this.playlistId = playlistId;
    }

    public int getSongId() {
        return songId;
    }

    public void setSongId(int songId) {
        this.songId = songId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) playlistId;
        hash += (int) songId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PlaylistSongPK)) {
            return false;
        }
        PlaylistSongPK other = (PlaylistSongPK) object;
        if (this.playlistId != other.playlistId) {
            return false;
        }
        if (this.songId != other.songId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "radiostation.gui.PlaylistSongPK[ playlistId=" + playlistId + ", songId=" + songId + " ]";
    }
    
}
