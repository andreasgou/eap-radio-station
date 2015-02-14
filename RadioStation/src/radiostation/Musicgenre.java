/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package radiostation;

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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author user
 */
@Entity
@Table(name = "MUSICGENRE")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Musicgenre.findAll", query = "SELECT m FROM Musicgenre m"),
    @NamedQuery(name = "Musicgenre.findByGenrename", query = "SELECT m FROM Musicgenre m WHERE m.genrename = :genrename")})
public class Musicgenre implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "GENRENAME")
    private String genrename;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "genre")
    private Collection<Artist> artistCollection;

    public Musicgenre() {
    }

    public Musicgenre(String genrename) {
        this.genrename = genrename;
    }

    public String getGenrename() {
        return genrename;
    }

    public void setGenrename(String genrename) {
        this.genrename = genrename;
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
        if (!(object instanceof Musicgenre)) {
            return false;
        }
        Musicgenre other = (Musicgenre) object;
        if ((this.genrename == null && other.genrename != null) || (this.genrename != null && !this.genrename.equals(other.genrename))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "radiostation.Musicgenre[ genrename=" + genrename + " ]";
    }
    
}
