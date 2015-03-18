/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package radiostation.jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author gounaris
 */
public class DBManager {
    private static DBManager instance;
    private static EntityManager pu;

    private DBManager(String pu_name) {
        this.pu = javax.persistence.Persistence.createEntityManagerFactory(pu_name).createEntityManager();
    }
    
    /**
     * @return the em
     */
    public static EntityManager getEm(String pu_name) {
        if (instance == null) {
            DBManager.instance = new DBManager(pu_name);
        }
        return DBManager.pu;
    }
    
    public static EntityManagerFactory createEmFactory(String pu_name) {
        return javax.persistence.Persistence.createEntityManagerFactory(pu_name);
    }
}
