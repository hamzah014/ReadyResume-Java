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
import jpa.entities.Education;
import jpa.entities.Experience;
import jpa.entities.Users;

/**
 *
 * @author user
 */
@Stateless
public class ExperienceFacade extends AbstractFacade<Experience> {

    @PersistenceContext(unitName = "ReadyResumePU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ExperienceFacade() {
        super(Experience.class);
    }
    
    public List<Experience> findByUserId(Users userid){
        
        try{
        
            Query q = em.createNamedQuery("Experience.findByUserId");

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
