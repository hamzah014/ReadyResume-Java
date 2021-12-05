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
import jpa.entities.Admin;
import jpa.entities.Users;

/**
 *
 * @author user
 */
@Stateless
public class AdminFacade extends AbstractFacade<Admin> {

    @PersistenceContext(unitName = "ReadyResumePU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AdminFacade() {
        super(Admin.class);
    }
    
    public List<Admin> findLogin(String email, String password){
        
        Query q = em.createNamedQuery("Admin.findByEmailPassword");
        
        q.setParameter("email",email).setParameter("password", password);
        
        
        System.out.println("oii mula");
        System.out.println(q.getResultList());
        
        if(q.getResultList().equals(null)){
            
            return null;
            
        }else{
            
            return q.getResultList();
            
        }
        
    }
    
    
}
