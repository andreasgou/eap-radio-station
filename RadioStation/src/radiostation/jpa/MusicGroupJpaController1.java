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
import radiostation.Artist;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import radiostation.MusicGroup;
import radiostation.jpa.exceptions.NonexistentEntityException;

/**
 *
 * @author user
 */
public class MusicGroupJpaController1 implements Serializable {

    public MusicGroupJpaController1(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(MusicGroup musicGroup) {
        if (musicGroup.getArtistCollection() == null) {
            musicGroup.setArtistCollection(new ArrayList<Artist>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Artist> attachedArtistCollection = new ArrayList<Artist>();
            for (Artist artistCollectionArtistToAttach : musicGroup.getArtistCollection()) {
                artistCollectionArtistToAttach = em.getReference(artistCollectionArtistToAttach.getClass(), artistCollectionArtistToAttach.getId());
                attachedArtistCollection.add(artistCollectionArtistToAttach);
            }
            musicGroup.setArtistCollection(attachedArtistCollection);
            em.persist(musicGroup);
            for (Artist artistCollectionArtist : musicGroup.getArtistCollection()) {
                artistCollectionArtist.getMusicgroupCollection().add(musicGroup);
                artistCollectionArtist = em.merge(artistCollectionArtist);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(MusicGroup musicGroup) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            MusicGroup persistentMusicGroup = em.find(MusicGroup.class, musicGroup.getId());
            Collection<Artist> artistCollectionOld = persistentMusicGroup.getArtistCollection();
            Collection<Artist> artistCollectionNew = musicGroup.getArtistCollection();
            Collection<Artist> attachedArtistCollectionNew = new ArrayList<Artist>();
            for (Artist artistCollectionNewArtistToAttach : artistCollectionNew) {
                artistCollectionNewArtistToAttach = em.getReference(artistCollectionNewArtistToAttach.getClass(), artistCollectionNewArtistToAttach.getId());
                attachedArtistCollectionNew.add(artistCollectionNewArtistToAttach);
            }
            artistCollectionNew = attachedArtistCollectionNew;
            musicGroup.setArtistCollection(artistCollectionNew);
            musicGroup = em.merge(musicGroup);
            for (Artist artistCollectionOldArtist : artistCollectionOld) {
                if (!artistCollectionNew.contains(artistCollectionOldArtist)) {
                    artistCollectionOldArtist.getMusicgroupCollection().remove(musicGroup);
                    artistCollectionOldArtist = em.merge(artistCollectionOldArtist);
                }
            }
            for (Artist artistCollectionNewArtist : artistCollectionNew) {
                if (!artistCollectionOld.contains(artistCollectionNewArtist)) {
                    artistCollectionNewArtist.getMusicgroupCollection().add(musicGroup);
                    artistCollectionNewArtist = em.merge(artistCollectionNewArtist);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = musicGroup.getId();
                if (findMusicGroup(id) == null) {
                    throw new NonexistentEntityException("The musicGroup with id " + id + " no longer exists.");
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
            MusicGroup musicGroup;
            try {
                musicGroup = em.getReference(MusicGroup.class, id);
                musicGroup.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The musicGroup with id " + id + " no longer exists.", enfe);
            }
            Collection<Artist> artistCollection = musicGroup.getArtistCollection();
            for (Artist artistCollectionArtist : artistCollection) {
                artistCollectionArtist.getMusicgroupCollection().remove(musicGroup);
                artistCollectionArtist = em.merge(artistCollectionArtist);
            }
            em.remove(musicGroup);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<MusicGroup> findMusicGroupEntities() {
        return findMusicGroupEntities(true, -1, -1);
    }

    public List<MusicGroup> findMusicGroupEntities(int maxResults, int firstResult) {
        return findMusicGroupEntities(false, maxResults, firstResult);
    }

    private List<MusicGroup> findMusicGroupEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(MusicGroup.class));
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

    public MusicGroup findMusicGroup(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(MusicGroup.class, id);
        } finally {
            em.close();
        }
    }

    public int getMusicGroupCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<MusicGroup> rt = cq.from(MusicGroup.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
