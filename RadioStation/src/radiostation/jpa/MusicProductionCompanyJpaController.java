/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package radiostation.jpa;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import radiostation.Album;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import radiostation.MusicProductionCompany;
import radiostation.jpa.exceptions.NonexistentEntityException;

/**
 *
 * @author user
 */
public class MusicProductionCompanyJpaController implements Serializable {

    public MusicProductionCompanyJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(MusicProductionCompany musicProductionCompany) {
        if (musicProductionCompany.getAlbumCollection() == null) {
            musicProductionCompany.setAlbumCollection(new ArrayList<Album>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Album> attachedAlbumCollection = new ArrayList<Album>();
            for (Album albumCollectionAlbumToAttach : musicProductionCompany.getAlbumCollection()) {
                albumCollectionAlbumToAttach = em.getReference(albumCollectionAlbumToAttach.getClass(), albumCollectionAlbumToAttach.getId());
                attachedAlbumCollection.add(albumCollectionAlbumToAttach);
            }
            musicProductionCompany.setAlbumCollection(attachedAlbumCollection);
            em.persist(musicProductionCompany);
            for (Album albumCollectionAlbum : musicProductionCompany.getAlbumCollection()) {
                MusicProductionCompany oldCompanyIdOfAlbumCollectionAlbum = albumCollectionAlbum.getCompanyId();
                albumCollectionAlbum.setCompanyId(musicProductionCompany);
                albumCollectionAlbum = em.merge(albumCollectionAlbum);
                if (oldCompanyIdOfAlbumCollectionAlbum != null) {
                    oldCompanyIdOfAlbumCollectionAlbum.getAlbumCollection().remove(albumCollectionAlbum);
                    oldCompanyIdOfAlbumCollectionAlbum = em.merge(oldCompanyIdOfAlbumCollectionAlbum);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(MusicProductionCompany musicProductionCompany) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            MusicProductionCompany persistentMusicProductionCompany = em.find(MusicProductionCompany.class, musicProductionCompany.getId());
            Collection<Album> albumCollectionOld = persistentMusicProductionCompany.getAlbumCollection();
            Collection<Album> albumCollectionNew = musicProductionCompany.getAlbumCollection();
            Collection<Album> attachedAlbumCollectionNew = new ArrayList<Album>();
            for (Album albumCollectionNewAlbumToAttach : albumCollectionNew) {
                albumCollectionNewAlbumToAttach = em.getReference(albumCollectionNewAlbumToAttach.getClass(), albumCollectionNewAlbumToAttach.getId());
                attachedAlbumCollectionNew.add(albumCollectionNewAlbumToAttach);
            }
            albumCollectionNew = attachedAlbumCollectionNew;
            musicProductionCompany.setAlbumCollection(albumCollectionNew);
            musicProductionCompany = em.merge(musicProductionCompany);
            for (Album albumCollectionOldAlbum : albumCollectionOld) {
                if (!albumCollectionNew.contains(albumCollectionOldAlbum)) {
                    albumCollectionOldAlbum.setCompanyId(null);
                    albumCollectionOldAlbum = em.merge(albumCollectionOldAlbum);
                }
            }
            for (Album albumCollectionNewAlbum : albumCollectionNew) {
                if (!albumCollectionOld.contains(albumCollectionNewAlbum)) {
                    MusicProductionCompany oldCompanyIdOfAlbumCollectionNewAlbum = albumCollectionNewAlbum.getCompanyId();
                    albumCollectionNewAlbum.setCompanyId(musicProductionCompany);
                    albumCollectionNewAlbum = em.merge(albumCollectionNewAlbum);
                    if (oldCompanyIdOfAlbumCollectionNewAlbum != null && !oldCompanyIdOfAlbumCollectionNewAlbum.equals(musicProductionCompany)) {
                        oldCompanyIdOfAlbumCollectionNewAlbum.getAlbumCollection().remove(albumCollectionNewAlbum);
                        oldCompanyIdOfAlbumCollectionNewAlbum = em.merge(oldCompanyIdOfAlbumCollectionNewAlbum);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = musicProductionCompany.getId();
                if (findMusicProductionCompany(id) == null) {
                    throw new NonexistentEntityException("The musicProductionCompany with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            MusicProductionCompany musicProductionCompany;
            try {
                musicProductionCompany = em.getReference(MusicProductionCompany.class, id);
                musicProductionCompany.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The musicProductionCompany with id " + id + " no longer exists.", enfe);
            }
            Collection<Album> albumCollection = musicProductionCompany.getAlbumCollection();
            for (Album albumCollectionAlbum : albumCollection) {
                albumCollectionAlbum.setCompanyId(null);
                albumCollectionAlbum = em.merge(albumCollectionAlbum);
            }
            em.remove(musicProductionCompany);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<MusicProductionCompany> findMusicProductionCompanyEntities() {
        return findMusicProductionCompanyEntities(true, -1, -1);
    }

    public List<MusicProductionCompany> findMusicProductionCompanyEntities(int maxResults, int firstResult) {
        return findMusicProductionCompanyEntities(false, maxResults, firstResult);
    }

    private List<MusicProductionCompany> findMusicProductionCompanyEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(MusicProductionCompany.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public MusicProductionCompany findMusicProductionCompany(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(MusicProductionCompany.class, id);
        } finally {
            em.close();
        }
    }

    public int getMusicProductionCompanyCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<MusicProductionCompany> rt = cq.from(MusicProductionCompany.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
