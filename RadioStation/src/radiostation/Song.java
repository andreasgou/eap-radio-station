/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package radiostation;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author user
 */
@Entity
@Table(name = "SONG")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Song.findAll", query = "SELECT s FROM Song s"),
    @NamedQuery(name = "Song.findById", query = "SELECT s FROM Song s WHERE s.id = :id"),
    @NamedQuery(name = "Song.findByTitle", query = "SELECT s FROM Song s WHERE s.title = :title"),
    @NamedQuery(name = "Song.findByDuration", query = "SELECT s FROM Song s WHERE s.duration = :duration"),
    @NamedQuery(name = "Song.findByTracknr", query = "SELECT s FROM Song s WHERE s.tracknr = :tracknr")})
public class Song implements Serializable, Cloneable {
    @Transient
    private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Column(name = "TITLE")
    private String title;
    @Basic(optional = false)
    @Column(name = "DURATION")
    private int duration;
    @Basic(optional = false)
    @Column(name = "TRACKNR")
    private short tracknr;
    @ManyToMany(mappedBy = "songCollection")
    private Collection<Playlist> playlistCollection;
    @JoinColumn(name = "ALBUM_ID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Album albumId;

    public Song() {
    }

    public Song(Integer id) {
        this.id = id;
    }

    public Song(Integer id, int duration, short tracknr) {
        this.id = id;
        this.duration = duration;
        this.tracknr = tracknr;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        Integer oldId = this.id;
        this.id = id;
        changeSupport.firePropertyChange("id", oldId, id);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        String oldTitle = this.title;
        this.title = title;
        changeSupport.firePropertyChange("title", oldTitle, title);
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        int oldDuration = this.duration;
        this.duration = duration;
        changeSupport.firePropertyChange("duration", oldDuration, duration);
    }

    public short getTracknr() {
        return tracknr;
    }

    public void setTracknr(short tracknr) {
        short oldTracknr = this.tracknr;
        this.tracknr = tracknr;
        changeSupport.firePropertyChange("tracknr", oldTracknr, tracknr);
    }

    @XmlTransient
    public Collection<Playlist> getPlaylistCollection() {
        return playlistCollection;
    }

    public void setPlaylistCollection(Collection<Playlist> playlistCollection) {
        this.playlistCollection = playlistCollection;
    }

    public Album getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Album albumId) {
        Album oldAlbumId = this.albumId;
        this.albumId = albumId;
        changeSupport.firePropertyChange("albumId", oldAlbumId, albumId);
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : (title != null ? title.hashCode() : 0));
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Song)) {
            return false;
        }
        Song other = (Song) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)) &&
                (this.title == null && other.title != null) || (this.title != null && !this.title.equals(other.title)))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "radiostation.Song[ id=" + id + " ]";
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
}
