/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package radiostation;

import javax.persistence.EntityManager;

/**
 *
 * @author theo
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
}
