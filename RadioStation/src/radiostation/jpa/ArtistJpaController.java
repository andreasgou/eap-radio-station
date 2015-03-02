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
import radiostation.MusicGenre;
import radiostation.Album;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import radiostation.Artist;
import radiostation.jpa.exceptions.NonexistentEntityException;
import radiostation.jpa.exceptions.PreexistingEntityException;

/**
 *
 * @author a.gounaris
 */
public class ArtistJpaController implements Serializable {

    public ArtistJpaController(EntityManager em) {
        this.em = em;
    }
    private EntityManager em = null;

    public EntityManager getEntityManager() {
        return em;
    }

    public void create(Artist artist) throws PreexistingEntityException, Exception {
        if (artist.getAlbumCollection() == null) {
            artist.setAlbumCollection(new ArrayList<Album>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            MusicGenre genre = artist.getGenre();
            if (genre != null) {
                genre = em.getReference(genre.getClass(), genre.getGenrename());
                artist.setGenre(genre);
            }
            Collection<Album> attachedAlbumCollection = new ArrayList<Album>();
            for (Album albumCollectionAlbumToAttach : artist.getAlbumCollection()) {
                albumCollectionAlbumToAttach = em.getReference(albumCollectionAlbumToAttach.getClass(), albumCollectionAlbumToAttach.getId());
                attachedAlbumCollection.add(albumCollectionAlbumToAttach);
            }
            artist.setAlbumCollection(attachedAlbumCollection);
            em.persist(artist);
            if (genre != null) {
                genre.getArtistCollection().add(artist);
                genre = em.merge(genre);
            }
            for (Album albumCollectionAlbum : artist.getAlbumCollection()) {
                Artist oldArtistIdOfAlbumCollectionAlbum = albumCollectionAlbum.getArtistId();
                albumCollectionAlbum.setArtistId(artist);
                albumCollectionAlbum = em.merge(albumCollectionAlbum);
                if (oldArtistIdOfAlbumCollectionAlbum != null) {
                    oldArtistIdOfAlbumCollectionAlbum.getAlbumCollection().remove(albumCollectionAlbum);
                    oldArtistIdOfAlbumCollectionAlbum = em.merge(oldArtistIdOfAlbumCollectionAlbum);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            //if (findArtist(artist.getId()) != null) {
            //    throw new PreexistingEntityException("Artist " + artist + " already exists.", ex);
            //}
            throw ex;
        } finally {
//            if (em != null) {
//                em.close();
//            }
        }
    }

    public void edit(Artist artist) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Artist persistentArtist = em.find(Artist.class, artist.getId());
            MusicGenre genreOld = persistentArtist.getGenre();
            MusicGenre genreNew = artist.getGenre();
            Collection<Album> albumCollectionOld = persistentArtist.getAlbumCollection();
            Collection<Album> albumCollectionNew = artist.getAlbumCollection();
            if (genreNew != null) {
                genreNew = em.getReference(genreNew.getClass(), genreNew.getGenrename());
                artist.setGenre(genreNew);
            }
            Collection<Album> attachedAlbumCollectionNew = new ArrayList<Album>();
            for (Album albumCollectionNewAlbumToAttach : albumCollectionNew) {
                albumCollectionNewAlbumToAttach = em.getReference(albumCollectionNewAlbumToAttach.getClass(), albumCollectionNewAlbumToAttach.getId());
                attachedAlbumCollectionNew.add(albumCollectionNewAlbumToAttach);
            }
            albumCollectionNew = attachedAlbumCollectionNew;
            artist.setAlbumCollection(albumCollectionNew);
            artist = em.merge(artist);
            if (genreOld != null && !genreOld.equals(genreNew)) {
                genreOld.getArtistCollection().remove(artist);
                genreOld = em.merge(genreOld);
            }
            if (genreNew != null && !genreNew.equals(genreOld)) {
                genreNew.getArtistCollection().add(artist);
                genreNew = em.merge(genreNew);
            }
            for (Album albumCollectionOldAlbum : albumCollectionOld) {
                if (!albumCollectionNew.contains(albumCollectionOldAlbum)) {
                    albumCollectionOldAlbum.setArtistId(null);
                    albumCollectionOldAlbum = em.merge(albumCollectionOldAlbum);
                }
            }
            for (Album albumCollectionNewAlbum : albumCollectionNew) {
                if (!albumCollectionOld.contains(albumCollectionNewAlbum)) {
                    Artist oldArtistIdOfAlbumCollectionNewAlbum = albumCollectionNewAlbum.getArtistId();
                    albumCollectionNewAlbum.setArtistId(artist);
                    albumCollectionNewAlbum = em.merge(albumCollectionNewAlbum);
                    if (oldArtistIdOfAlbumCollectionNewAlbum != null && !oldArtistIdOfAlbumCollectionNewAlbum.equals(artist)) {
                        oldArtistIdOfAlbumCollectionNewAlbum.getAlbumCollection().remove(albumCollectionNewAlbum);
                        oldArtistIdOfAlbumCollectionNewAlbum = em.merge(oldArtistIdOfAlbumCollectionNewAlbum);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = artist.getId();
                if (findArtist(id) == null) {
                    throw new NonexistentEntityException("The artist with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
//            if (em != null) {
//                em.close();
//            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Artist artist;
            try {
                artist = em.getReference(Artist.class, id);
                artist.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The artist with id " + id + " no longer exists.", enfe);
            }
            MusicGenre genre = artist.getGenre();
            if (genre != null) {
                genre.getArtistCollection().remove(artist);
                genre = em.merge(genre);
            }
            Collection<Album> albumCollection = artist.getAlbumCollection();
            for (Album albumCollectionAlbum : albumCollection) {
                albumCollectionAlbum.setArtistId(null);
                albumCollectionAlbum = em.merge(albumCollectionAlbum);
            }
            em.remove(artist);
            em.getTransaction().commit();
        } finally {
//            if (em != null) {
//                em.close();
//            }
        }
    }

    public List<Artist> findArtistEntities() {
        return findArtistEntities(true, -1, -1);
    }

    public List<Artist> findArtistEntities(int maxResults, int firstResult) {
        return findArtistEntities(false, maxResults, firstResult);
    }

    private List<Artist> findArtistEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Artist.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
//            em.close();
        }
    }

    public Artist findArtist(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Artist.class, id);
        } finally {
//            em.close();
        }
    }

    public int getArtistCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Artist> rt = cq.from(Artist.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
//            em.close();
        }
    }
    
}
