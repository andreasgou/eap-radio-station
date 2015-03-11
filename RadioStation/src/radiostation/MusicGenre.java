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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author a.gounaris
 */
@Entity
@Table(name = "MUSICGENRE", catalog = "", schema = "APP")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "MusicGenre.findAll", query = "SELECT m FROM MusicGenre m"),
    @NamedQuery(name = "MusicGenre.findByGenrename", query = "SELECT m FROM MusicGenre m WHERE m.genrename = :genrename")})
public class MusicGenre implements Serializable {
    @Transient
    private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "GENRENAME")
    private String genrename;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "genre")
    private Collection<Artist> artistCollection;

    public MusicGenre() {
    }

    public MusicGenre(String genrename) {
        this.genrename = genrename;
    }

    public String getGenrename() {
        return genrename;
    }

    public void setGenrename(String genrename) {
        String oldGenrename = this.genrename;
        this.genrename = genrename;
        changeSupport.firePropertyChange("genrename", oldGenrename, genrename);
    }

    @XmlTransient
    public Collection<Artist> getArtistCollection() {
        return artistCollection;
    }

    public void setArtistCollection(Collection<Artist> artistCollection) {
        this.artistCollection = artistCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (genrename != null ? genrename.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MusicGenre)) {
            return false;
        }
        MusicGenre other = (MusicGenre) object;
        if ((this.genrename == null && other.genrename != null) || (this.genrename != null && !this.genrename.equals(other.genrename))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "radiostation.MusicGenre[ genrename=" + genrename + " ]";
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }
    
}
