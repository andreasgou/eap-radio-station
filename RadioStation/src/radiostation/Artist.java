/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package radiostation;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
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
    private static final long serialVersionUID = 1L;
    @Id
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
    private Collection<Musicgroup> musicgroupCollection;
    @JoinColumn(name = "GENRE", referencedColumnName = "GENRENAME")
    @ManyToOne(optional = false)
    private Musicgenre genre;
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
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getArtisticname() {
        return artisticname;
    }

    public void setArtisticname(String artisticname) {
        this.artisticname = artisticname;
    }

    public Character getSex() {
        return sex;
    }

    public void setSex(Character sex) {
        this.sex = sex;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getBirthplace() {
        return birthplace;
    }

    public void setBirthplace(String birthplace) {
        this.birthplace = birthplace;
    }

    @XmlTransient
    public Collection<Musicgroup> getMusicgroupCollection() {
        return musicgroupCollection;
    }

    public void setMusicgroupCollection(Collection<Musicgroup> musicgroupCollection) {
        this.musicgroupCollection = musicgroupCollection;
    }

    public Musicgenre getGenre() {
        return genre;
    }

    public void setGenre(Musicgenre genre) {
        this.genre = genre;
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
    
}
