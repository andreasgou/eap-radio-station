/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package radiostation;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
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
public class Album implements Serializable, Cloneable {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "TITLE")
    private String title;
    @Basic(optional = false)
    @Column(name = "TYPE1")
    private String type1;
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
    @Column(name = "DISKNUMBER")
    private Short disknumber;
    @Column(name = "RELEASEDATE")
    @Temporal(TemporalType.DATE)
    private Date releasedate;
    @OneToMany(mappedBy = "parentalbumId")
    private Collection<Album> albumCollection;
    @JoinColumn(name = "PARENTALBUM_ID", referencedColumnName = "ID")
    @ManyToOne
    private Album parentalbumId;
    @Column(name = "TOTALDISKS")
    private Short totaldisks;

    @Transient
    private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    @Transient
    private Short currentDisk;
    private static final long serialVersionUID = 1L;

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

        /*
     * This special purpose getter/setter methods is to allow binding 
     * with radio buttons
    */
    public boolean isCdSingle() {
        return getType1()!=null ? getType1().equals("CS") : false;
    }
    public void setCdSingle(boolean nl) {
        if (nl) setType1("CS");
    }
    public boolean isExtendedPlay() {
        return getType1()!=null ? getType1().equals("EP") : false;
    }
    public void setExtendedPlay(boolean nl) {
        if (nl) setType1("EP");
    }
    public boolean isLongPlay() {
        return getType1()!=null ? getType1().equals("LP") : false;
    }
    public void setLongPlay(boolean nl) {
        if (nl) setType1("LP");
    }

    @XmlTransient
    public Collection<Song> getSongCollection() {
        Collection<Song> songs = new ArrayList<>();
        if (this.getCurrentDisk() == null)
            this.setCurrentDisk((short)1);
        if (this.getCurrentDisk() == 1) {
            songs = songCollection;
        } else {
            for (Album album : getAlbumCollection()) {
                if (album.getDisknumber().equals(this.getCurrentDisk())) {
                    songs = album.getSongCollection();
                    break;
                }
            }
        }
        return songs;
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
    
    /**
     *
     * 
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        Object clone = super.clone();
        // clone lists manually
        Collection<Song> songCollection = new ArrayList<Song>();
        if (this.songCollection != null) {
            songCollection.addAll(this.songCollection);
            ((Album)clone).setSongCollection(songCollection);
        }
        Collection<Album> albumCollection = new ArrayList<Album>();
        if (this.albumCollection != null) {
            albumCollection.addAll(this.albumCollection);
            ((Album)clone).setAlbumCollection(albumCollection);
        }
        return clone;
    }

    public void restore(Album album) {
        setTitle(album.getTitle());
        setType1(album.getType1());
        setReleasedate(album.getReleasedate());
        setArtistId(album.getArtistId());
        setMusicgroupId(album.getMusicgroupId());
        setCompanyId(album.getCompanyId());
        setSongCollection(album.getSongCollection());
        setAlbumCollection(album.getAlbumCollection());
    }

    public Short getDisknumber() {
        return disknumber;
    }

    public void setDisknumber(Short disknumber) {
        this.disknumber = disknumber;
    }

    public Date getReleasedate() {
        return releasedate;
    }

    public void setReleasedate(Date releasedate) {
        this.releasedate = releasedate;
    }

    @XmlTransient
    public Collection<Album> getAlbumCollection() {
        return albumCollection;
    }

    public void setAlbumCollection(Collection<Album> albumCollection) {
        this.albumCollection = albumCollection;
    }

    @XmlTransient
    public Collection<Song> getSongCollection(int diskNumber) {
        Collection<Song> songs = new ArrayList<>();
        if (diskNumber == 1) {
            songs = getSongCollection();
        } else {
            for (Album album : getAlbumCollection()) {
                if (album.getDisknumber() == diskNumber) {
                    songs = album.getSongCollection();
                    break;
                }
            }
        }
        return songs;
    }

    public Album getParentalbumId() {
        return parentalbumId;
    }

    public void setParentalbumId(Album parentalbumId) {
        this.parentalbumId = parentalbumId;
    }

    public Short getTotaldisks() {
        return totaldisks;
    }

    public void setTotaldisks(Short totaldisks) {
        this.totaldisks = totaldisks;
    }

    /**
     * @return the currentDisk
     */
    public Short getCurrentDisk() {
        return currentDisk;
    }

    /**
     * @param currentDisk the currentDisk to set
     */
    public void setCurrentDisk(Short currentDisk) {
        this.currentDisk = currentDisk;
    }
}
