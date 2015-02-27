/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package radiostation;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author a.gounaris
 */
@Entity
@Table(name = "MUSICPRODUCTIONCOMPANY", catalog = "", schema = "APP")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "MusicProductionCompany.findAll", query = "SELECT m FROM MusicProductionCompany m"),
    @NamedQuery(name = "MusicProductionCompany.findById", query = "SELECT m FROM MusicProductionCompany m WHERE m.id = :id"),
    @NamedQuery(name = "MusicProductionCompany.findByName", query = "SELECT m FROM MusicProductionCompany m WHERE m.name = :name"),
    @NamedQuery(name = "MusicProductionCompany.findByAddress", query = "SELECT m FROM MusicProductionCompany m WHERE m.address = :address"),
    @NamedQuery(name = "MusicProductionCompany.findByTelephone", query = "SELECT m FROM MusicProductionCompany m WHERE m.telephone = :telephone")})
public class MusicProductionCompany implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Column(name = "NAME")
    private String name;
    @Basic(optional = false)
    @Column(name = "ADDRESS")
    private String address;
    @Basic(optional = false)
    @Column(name = "TELEPHONE")
    private int telephone;
    @OneToMany(mappedBy = "companyId")
    private Collection<Album> albumCollection;

    public MusicProductionCompany() {
    }

    public MusicProductionCompany(Integer id) {
        this.id = id;
    }

    public MusicProductionCompany(Integer id, String address, int telephone) {
        this.id = id;
        this.address = address;
        this.telephone = telephone;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getTelephone() {
        return telephone;
    }

    public void setTelephone(int telephone) {
        this.telephone = telephone;
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
        if (!(object instanceof MusicProductionCompany)) {
            return false;
        }
        MusicProductionCompany other = (MusicProductionCompany) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "radiostation.MusicProductionCompany[ id=" + id + " ]";
    }
    
}
