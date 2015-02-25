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
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author user
 */
@Entity
@Table(name = "ALBUM")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Album.findAll", query = "SELECT a FROM Album a"),
    @NamedQuery(name = "Album.findById", query = "SELECT a FROM Album a WHERE a.id = :id"),
    @NamedQuery(name = "Album.findByTitle", query = "SELECT a FROM Album a WHERE a.title = :title"),
    @NamedQuery(name = "Album.findByType1", query = "SELECT a FROM Album a WHERE a.type1 = :type1"),
    @NamedQuery(name = "Album.findByDisknumber", query = "SELECT a FROM Album a WHERE a.disknumber = :disknumber")})
public class Album implements Serializable {
    @Transient
    private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "TITLE")
    private String title;
    @Basic(optional = false)
    @Column(name = "TYPE1")
    private String type1;
    @Basic(optional = false)
    @Column(name = "DISKNUMBER")
    private short disknumber;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "albumId")
    private Collection<Song> songCollection;
    @JoinColumn(name = "ARTIST_ID", referencedColumnName = "ID")
    @ManyToOne
    private Artist artistId;
    @JoinColumn(name = "MUSICGROUP_ID", referencedColumnName = "ID")
    @ManyToOne
    private MusicGroup musicgroupId;
    @JoinColumn(name = "COMPANY_ID", referencedColumnName = "ID")
    @ManyToOne
    private MusicProductionCompany companyId;

    public Album() {
    }

    public Album(Integer id) {
        this.id = id;
    }

    public Album(Integer id, String title, String type1, short disknumber) {
        this.id = id;
        this.title = title;
        this.type1 = type1;
        this.disknumber = disknumber;
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

    public String getType1() {
        return type1;
    }

    public void setType1(String type1) {
        String oldType1 = this.type1;
        this.type1 = type1;
        changeSupport.firePropertyChange("type1", oldType1, type1);
    }

    public short getDisknumber() {
        return disknumber;
    }

    public void setDisknumber(short disknumber) {
        short oldDisknumber = this.disknumber;
        this.disknumber = disknumber;
        changeSupport.firePropertyChange("disknumber", oldDisknumber, disknumber);
    }

    @XmlTransient
    public Collection<Song> getSongCollection() {
        return songCollection;
    }

    public void setSongCollection(Collection<Song> songCollection) {
        this.songCollection = songCollection;
    }

    public Artist getArtistId() {
        return artistId;
    }

    public void setArtistId(Artist artistId) {
        Artist oldArtistId = this.artistId;
        this.artistId = artistId;
        changeSupport.firePropertyChange("artistId", oldArtistId, artistId);
    }

    public MusicGroup getMusicgroupId() {
        return musicgroupId;
    }

    public void setMusicgroupId(MusicGroup musicgroupId) {
        MusicGroup oldMusicgroupId = this.musicgroupId;
        this.musicgroupId = musicgroupId;
        changeSupport.firePropertyChange("musicgroupId", oldMusicgroupId, musicgroupId);
    }

    public MusicProductionCompany getCompanyId() {
        return companyId;
    }

    public void setCompanyId(MusicProductionCompany companyId) {
        MusicProductionCompany oldCompanyId = this.companyId;
        this.companyId = companyId;
        changeSupport.firePropertyChange("companyId", oldCompanyId, companyId);
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Album)) {
            return false;
        }
        Album other = (Album) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "radiostation.Album[ id=" + id + " ]";
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }
    
}
