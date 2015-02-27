/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package radiostation;

import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author a.gounaris
 */
@Entity
@Table(name = "MUSICGROUP", catalog = "", schema = "APP")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "MusicGroup.findAll", query = "SELECT m FROM MusicGroup m"),
    @NamedQuery(name = "MusicGroup.findById", query = "SELECT m FROM MusicGroup m WHERE m.id = :id"),
    @NamedQuery(name = "MusicGroup.findByName", query = "SELECT m FROM MusicGroup m WHERE m.name = :name"),
    @NamedQuery(name = "MusicGroup.findByFormationdate", query = "SELECT m FROM MusicGroup m WHERE m.formationdate = :formationdate")})
public class MusicGroup implements Serializable {
    @Transient
    private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    public MusicGroup() {
    }

    public MusicGroup(Integer id) {
        this.id = id;
    }

    public MusicGroup(Integer id, String name, Date formationdate) {
        this.id = id;
        this.name = name;
        this.formationdate = formationdate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        Integer oldId = this.id;
        this.id = id;
        changeSupport.firePropertyChange("id", oldId, id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        String oldName = this.name;
        this.name = name;
        changeSupport.firePropertyChange("name", oldName, name);
    }

    public Date getFormationdate() {
        return formationdate;
    }

    public void setFormationdate(Date formationdate) {
        Date oldFormationdate = this.formationdate;
        this.formationdate = formationdate;
        changeSupport.firePropertyChange("formationdate", oldFormationdate, formationdate);
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
        if (!(object instanceof MusicGroup)) {
            return false;
        }
        MusicGroup other = (MusicGroup) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "radiostation.MusicGroup[ id=" + id + " ]";
    }
    
}
