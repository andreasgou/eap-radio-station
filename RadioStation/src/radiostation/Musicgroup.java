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
import javax.persistence.ManyToMany;
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
@Table(name = "MUSICGROUP")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Musicgroup.findAll", query = "SELECT m FROM Musicgroup m"),
    @NamedQuery(name = "Musicgroup.findById", query = "SELECT m FROM Musicgroup m WHERE m.id = :id"),
    @NamedQuery(name = "Musicgroup.findByName", query = "SELECT m FROM Musicgroup m WHERE m.name = :name"),
    @NamedQuery(name = "Musicgroup.findByFormationdate", query = "SELECT m FROM Musicgroup m WHERE m.formationdate = :formationdate")})
public class Musicgroup implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "NAME")
    private String name;
    @Basic(optional = false)
    @Column(name = "FORMATIONDATE")
    @Temporal(TemporalType.DATE)
    private Date formationdate;
    @ManyToMany(mappedBy = "musicgroupCollection")
    private Collection<Artist> artistCollection;
    @OneToMany(mappedBy = "musicgroupId")
    private Collection<Album> albumCollection;

    public Musicgroup() {
    }

    public Musicgroup(Integer id) {
        this.id = id;
    }

    public Musicgroup(Integer id, String name, Date formationdate) {
        this.id = id;
        this.name = name;
        this.formationdate = formationdate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getFormationdate() {
        return formationdate;
    }

    public void setFormationdate(Date formationdate) {
        this.formationdate = formationdate;
    }

    @XmlTransient
    public Collection<Artist> getArtistCollection() {
        return artistCollection;
    }

    public void setArtistCollection(Collection<Artist> artistCollection) {
        this.artistCollection = artistCollection;
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
        if (!(object instanceof Musicgroup)) {
            return false;
        }
        Musicgroup other = (Musicgroup) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "radiostation.Musicgroup[ id=" + id + " ]";
    }
    
}
