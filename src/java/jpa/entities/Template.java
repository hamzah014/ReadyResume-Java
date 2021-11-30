/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpa.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author user
 */
@Entity
@Table(name = "TEMPLATE")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Template.findAll", query = "SELECT t FROM Template t")
    , @NamedQuery(name = "Template.findById", query = "SELECT t FROM Template t WHERE t.id = :id")
    , @NamedQuery(name = "Template.findByName", query = "SELECT t FROM Template t WHERE t.name = :name")
    , @NamedQuery(name = "Template.findByDetails", query = "SELECT t FROM Template t WHERE t.details = :details")
    , @NamedQuery(name = "Template.findByPathfile", query = "SELECT t FROM Template t WHERE t.pathfile = :pathfile")
    , @NamedQuery(name = "Template.findByPreimage", query = "SELECT t FROM Template t WHERE t.preimage = :preimage")})
public class Template implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "NAME")
    private String name;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 200)
    @Column(name = "DETAILS")
    private String details;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "PATHFILE")
    private String pathfile;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "PREIMAGE")
    private String preimage;

    public Template() {
    }

    public Template(Integer id) {
        this.id = id;
    }

    public Template(Integer id, String name, String details, String pathfile, String preimage) {
        this.id = id;
        this.name = name;
        this.details = details;
        this.pathfile = pathfile;
        this.preimage = preimage;
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

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getPathfile() {
        return pathfile;
    }

    public void setPathfile(String pathfile) {
        this.pathfile = pathfile;
    }

    public String getPreimage() {
        return preimage;
    }

    public void setPreimage(String preimage) {
        this.preimage = preimage;
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
        if (!(object instanceof Template)) {
            return false;
        }
        Template other = (Template) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "jpa.entities.Template[ id=" + id + " ]";
    }
    
}
