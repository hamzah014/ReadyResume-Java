/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpa.session;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import jpa.entities.Certificate;
import jpa.entities.Education;
import jpa.entities.Users;

/**
 *
 * @author user
 */
@Stateless
public class CertificateFacade extends AbstractFacade<Certificate> {

    @PersistenceContext(unitName = "ReadyResumePU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CertificateFacade() {
        super(Certificate.class);
    }
    
    public List<Certificate> findByUserId(Users userid){
        
        try{
        
            Query q = em.createNamedQuery("Certificate.findByUserId");

            q.setParameter("userid", userid);

            System.out.println("mula findByUserId");
            System.out.println(q.getResultList());

            if (q.getResultList().equals(null)) {

                return null;

            } else {

                return q.getResultList();

            }
            
        }catch(Exception e){
            System.out.println("exception msg - " + e.getMessage());
            return null;
        
        }
    
    }
    
}
