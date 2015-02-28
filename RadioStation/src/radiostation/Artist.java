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
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
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
@Table(name = "ARTIST")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Artist.findAll", query = "SELECT a FROM Artist a"),
    @NamedQuery(name = "Artist.findById", query = "SELECT a FROM Artist a WHERE a.id = :id"),
    @NamedQuery(name = "Artist.findByFirstname", query = "SELECT a FROM Artist a WHERE a.firstname = :firstname"),
    @NamedQuery(name = "Artist.findByLastname", query = "SELECT a FROM Artist a WHERE a.lastname = :lastname"),
    @NamedQuery(name = "Artist.findByArtisticname", query = "SELECT a FROM Artist a WHERE a.artisticname = :artisticname"),
    @NamedQuery(name = "Artist.findBySex", query = "SELECT a FROM Artist a WHERE a.sex = :sex"),
    @NamedQuery(name = "Artist.findByBirthday", query = "SELECT a FROM Artist a WHERE a.birthday = :birthday"),
    @NamedQuery(name = "Artist.findByBirthplace", query = "SELECT a FROM Artist a WHERE a.birthplace = :birthplace")})
public class Artist implements Serializable {
    @Transient
    private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "FIRSTNAME")
    private String firstname;
    @Basic(optional = false)
    @Column(name = "LASTNAME")
    private String lastname;
    @Basic(optional = false)
    @Column(name = "ARTISTICNAME")
    private String artisticname;
    @Basic(optional = false)
    @Column(name = "SEX")
    private Character sex;
    @Basic(optional = false)
    @Column(name = "BIRTHDAY")
    @Temporal(TemporalType.DATE)
    private Date birthday;
    @Basic(optional = false)
    @Column(name = "BIRTHPLACE")
    private String birthplace;
    @JoinTable(name = "ARTISTMUSICGROUP", joinColumns = {
        @JoinColumn(name = "ARTIST_ID", referencedColumnName = "ID")}, inverseJoinColumns = {
        @JoinColumn(name = "MUSICGROUP_ID", referencedColumnName = "ID")})
    @ManyToMany
    private Collection<MusicGroup> musicgroupCollection;
    @JoinColumn(name = "GENRE", referencedColumnName = "GENRENAME")
    @ManyToOne(optional = false)
    private MusicGenre genre;
    @OneToMany(mappedBy = "artistId")
    private Collection<Album> albumCollection;

    public Artist() {
    }

    public Artist(Integer id) {
        this.id = id;
    }

    public Artist(Integer id, String firstname, String lastname, String artisticname, Character sex, Date birthday, String birthplace) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.artisticname = artisticname;
        this.sex = sex;
        this.birthday = birthday;
        this.birthplace = birthplace;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        Integer oldId = this.id;
        this.id = id;
        changeSupport.firePropertyChange("id", oldId, id);
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        String oldFirstname = this.firstname;
        this.firstname = firstname;
        changeSupport.firePropertyChange("firstname", oldFirstname, firstname);
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        String oldLastname = this.lastname;
        this.lastname = lastname;
        changeSupport.firePropertyChange("lastname", oldLastname, lastname);
    }

    public String getArtisticname() {
        return artisticname;
    }

    public void setArtisticname(String artisticname) {
        String oldArtisticname = this.artisticname;
        this.artisticname = artisticname;
        changeSupport.firePropertyChange("artisticname", oldArtisticname, artisticname);
    }

    public Character getSex() {
        return sex;
    }

    public void setSex(Character sex) {
        Character oldSex = this.sex;
        this.sex = sex;
        changeSupport.firePropertyChange("sex", oldSex, sex);
    }
    
    /*
     * This special purpose getter/setter methods is to allow binding 
     * with radio buttons
    */
    public boolean isMale() {
        return (getSex()!=null) ? getSex()=='M' : false;
    }
    public void setMale(boolean nl) {
        if (nl) setSex('M');
    }
    
    /*
     * This special purpose getter/setter methods is to allow binding 
     * with radio buttons
    */
    public boolean isFemale() {
        return (getSex()!=null) ? getSex()=='F' : false;
    }
    public void setFemale(boolean nl) {
        if (nl) setSex('F');
    }
    
    
    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        Date oldBirthday = this.birthday;
        this.birthday = birthday;
        changeSupport.firePropertyChange("birthday", oldBirthday, birthday);
    }

    public String getBirthplace() {
        return birthplace;
    }

    public void setBirthplace(String birthplace) {
        String oldBirthplace = this.birthplace;
        this.birthplace = birthplace;
        changeSupport.firePropertyChange("birthplace", oldBirthplace, birthplace);
    }

    @XmlTransient
    public Collection<MusicGroup> getMusicgroupCollection() {
        return musicgroupCollection;
    }

    public void setMusicgroupCollection(Collection<MusicGroup> musicgroupCollection) {
        this.musicgroupCollection = musicgroupCollection;
    }

    public MusicGenre getGenre() {
        return genre;
    }

    public void setGenre(MusicGenre genre) {
        MusicGenre oldGenre = this.genre;
        this.genre = genre;
        changeSupport.firePropertyChange("genre", oldGenre, genre);
    }

    @XmlTransient
    public Collection<Album> getAlbumCollection() {
        return albumCollection;
    }

    public void setAlbumCollection(Collection<Album> albumCollection) {
        this.albumCollection = albumCollection;
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
        if (!(object instanceof Artist)) {
            return false;
        }
        Artist other = (Artist) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "radiostation.Artist[ id=" + id + " ]";
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }
    
}
