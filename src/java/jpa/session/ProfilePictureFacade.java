/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpa.session;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import jpa.entities.ProfilePicture;

/**
 *
 * @author user
 */
@Stateless
public class ProfilePictureFacade extends AbstractFacade<ProfilePicture> {

    @PersistenceContext(unitName = "ReadyResumePU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ProfilePictureFacade() {
        super(ProfilePicture.class);
    }
    
}
