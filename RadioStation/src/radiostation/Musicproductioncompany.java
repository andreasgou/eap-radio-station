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
@Table(name = "MUSICPRODUCTIONCOMPANY")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Musicproductioncompany.findAll", query = "SELECT m FROM Musicproductioncompany m"),
    @NamedQuery(name = "Musicproductioncompany.findById", query = "SELECT m FROM Musicproductioncompany m WHERE m.id = :id"),
    @NamedQuery(name = "Musicproductioncompany.findByCompanyname", query = "SELECT m FROM Musicproductioncompany m WHERE m.companyname = :companyname"),
    @NamedQuery(name = "Musicproductioncompany.findByCompanyaddress", query = "SELECT m FROM Musicproductioncompany m WHERE m.companyaddress = :companyaddress"),
    @NamedQuery(name = "Musicproductioncompany.findByCompanytelephone", query = "SELECT m FROM Musicproductioncompany m WHERE m.companytelephone = :companytelephone")})
public class Musicproductioncompany implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Column(name = "COMPANYNAME")
    private String companyname;
    @Basic(optional = false)
    @Column(name = "COMPANYADDRESS")
    private String companyaddress;
    @Basic(optional = false)
    @Column(name = "COMPANYTELEPHONE")
    private int companytelephone;
    @OneToMany(mappedBy = "companyId")
    private Collection<Album> albumCollection;

    public Musicproductioncompany() {
    }

    public Musicproductioncompany(Integer id) {
        this.id = id;
    }

    public Musicproductioncompany(Integer id, String companyaddress, int companytelephone) {
        this.id = id;
        this.companyaddress = companyaddress;
        this.companytelephone = companytelephone;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCompanyname() {
        return companyname;
    }

    public void setCompanyname(String companyname) {
        this.companyname = companyname;
    }

    public String getCompanyaddress() {
        return companyaddress;
    }

    public void setCompanyaddress(String companyaddress) {
        this.companyaddress = companyaddress;
    }

    public int getCompanytelephone() {
        return companytelephone;
    }

    public void setCompanytelephone(int companytelephone) {
        this.companytelephone = companytelephone;
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
        if (!(object instanceof Musicproductioncompany)) {
            return false;
        }
        Musicproductioncompany other = (Musicproductioncompany) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "radiostation.Musicproductioncompany[ id=" + id + " ]";
    }
    
}
