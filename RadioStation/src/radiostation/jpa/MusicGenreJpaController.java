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
import radiostation.MusicGenre;
import radiostation.jpa.exceptions.IllegalOrphanException;
import radiostation.jpa.exceptions.NonexistentEntityException;
import radiostation.jpa.exceptions.PreexistingEntityException;

/**
 *
 * @author user
 */
public class MusicGenreJpaController implements Serializable {

    public MusicGenreJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(MusicGenre musicGenre) throws PreexistingEntityException, Exception {
        if (musicGenre.getArtistCollection() == null) {
            musicGenre.setArtistCollection(new ArrayList<Artist>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Artist> attachedArtistCollection = new ArrayList<Artist>();
            for (Artist artistCollectionArtistToAttach : musicGenre.getArtistCollection()) {
                artistCollectionArtistToAttach = em.getReference(artistCollectionArtistToAttach.getClass(), artistCollectionArtistToAttach.getId());
                attachedArtistCollection.add(artistCollectionArtistToAttach);
            }
            musicGenre.setArtistCollection(attachedArtistCollection);
            em.persist(musicGenre);
            for (Artist artistCollectionArtist : musicGenre.getArtistCollection()) {
                MusicGenre oldGenreOfArtistCollectionArtist = artistCollectionArtist.getGenre();
                artistCollectionArtist.setGenre(musicGenre);
                artistCollectionArtist = em.merge(artistCollectionArtist);
                if (oldGenreOfArtistCollectionArtist != null) {
                    oldGenreOfArtistCollectionArtist.getArtistCollection().remove(artistCollectionArtist);
                    oldGenreOfArtistCollectionArtist = em.merge(oldGenreOfArtistCollectionArtist);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findMusicGenre(musicGenre.getGenrename()) != null) {
                throw new PreexistingEntityException("MusicGenre " + musicGenre + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(MusicGenre musicGenre) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            MusicGenre persistentMusicGenre = em.find(MusicGenre.class, musicGenre.getGenrename());
            Collection<Artist> artistCollectionOld = persistentMusicGenre.getArtistCollection();
            Collection<Artist> artistCollectionNew = musicGenre.getArtistCollection();
            List<String> illegalOrphanMessages = null;
            for (Artist artistCollectionOldArtist : artistCollectionOld) {
                if (!artistCollectionNew.contains(artistCollectionOldArtist)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Artist " + artistCollectionOldArtist + " since its genre field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Artist> attachedArtistCollectionNew = new ArrayList<Artist>();
            for (Artist artistCollectionNewArtistToAttach : artistCollectionNew) {
                artistCollectionNewArtistToAttach = em.getReference(artistCollectionNewArtistToAttach.getClass(), artistCollectionNewArtistToAttach.getId());
                attachedArtistCollectionNew.add(artistCollectionNewArtistToAttach);
            }
            artistCollectionNew = attachedArtistCollectionNew;
            musicGenre.setArtistCollection(artistCollectionNew);
            musicGenre = em.merge(musicGenre);
            for (Artist artistCollectionNewArtist : artistCollectionNew) {
                if (!artistCollectionOld.contains(artistCollectionNewArtist)) {
                    MusicGenre oldGenreOfArtistCollectionNewArtist = artistCollectionNewArtist.getGenre();
                    artistCollectionNewArtist.setGenre(musicGenre);
                    artistCollectionNewArtist = em.merge(artistCollectionNewArtist);
                    if (oldGenreOfArtistCollectionNewArtist != null && !oldGenreOfArtistCollectionNewArtist.equals(musicGenre)) {
                        oldGenreOfArtistCollectionNewArtist.getArtistCollection().remove(artistCollectionNewArtist);
                        oldGenreOfArtistCollectionNewArtist = em.merge(oldGenreOfArtistCollectionNewArtist);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = musicGenre.getGenrename();
                if (findMusicGenre(id) == null) {
                    throw new NonexistentEntityException("The musicGenre with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            MusicGenre musicGenre;
            try {
                musicGenre = em.getReference(MusicGenre.class, id);
                musicGenre.getGenrename();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The musicGenre with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Artist> artistCollectionOrphanCheck = musicGenre.getArtistCollection();
            for (Artist artistCollectionOrphanCheckArtist : artistCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This MusicGenre (" + musicGenre + ") cannot be destroyed since the Artist " + artistCollectionOrphanCheckArtist + " in its artistCollection field has a non-nullable genre field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(musicGenre);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<MusicGenre> findMusicGenreEntities() {
        return findMusicGenreEntities(true, -1, -1);
    }

    public List<MusicGenre> findMusicGenreEntities(int maxResults, int firstResult) {
        return findMusicGenreEntities(false, maxResults, firstResult);
    }

    private List<MusicGenre> findMusicGenreEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(MusicGenre.class));
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

    public MusicGenre findMusicGenre(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(MusicGenre.class, id);
        } finally {
            em.close();
        }
    }

    public int getMusicGenreCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<MusicGenre> rt = cq.from(MusicGenre.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
