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
import radiostation.Playlist;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import radiostation.Song;
import radiostation.jpa.exceptions.NonexistentEntityException;

/**
 *
 * @author a.gounaris
 */
public class SongJpaController implements Serializable {

    public SongJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Song song) {
        if (song.getPlaylistCollection() == null) {
            song.setPlaylistCollection(new ArrayList<Playlist>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Album albumId = song.getAlbumId();
            if (albumId != null) {
                albumId = em.getReference(albumId.getClass(), albumId.getId());
                song.setAlbumId(albumId);
            }
            Collection<Playlist> attachedPlaylistCollection = new ArrayList<Playlist>();
            for (Playlist playlistCollectionPlaylistToAttach : song.getPlaylistCollection()) {
                playlistCollectionPlaylistToAttach = em.getReference(playlistCollectionPlaylistToAttach.getClass(), playlistCollectionPlaylistToAttach.getId());
                attachedPlaylistCollection.add(playlistCollectionPlaylistToAttach);
            }
            song.setPlaylistCollection(attachedPlaylistCollection);
            em.persist(song);
            if (albumId != null) {
                albumId.getSongCollection().add(song);
                albumId = em.merge(albumId);
            }
            for (Playlist playlistCollectionPlaylist : song.getPlaylistCollection()) {
                playlistCollectionPlaylist.getSongCollection().add(song);
                playlistCollectionPlaylist = em.merge(playlistCollectionPlaylist);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Song song) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Song persistentSong = em.find(Song.class, song.getId());
            Album albumIdOld = persistentSong.getAlbumId();
            Album albumIdNew = song.getAlbumId();
            Collection<Playlist> playlistCollectionOld = persistentSong.getPlaylistCollection();
            Collection<Playlist> playlistCollectionNew = song.getPlaylistCollection();
            if (albumIdNew != null) {
                albumIdNew = em.getReference(albumIdNew.getClass(), albumIdNew.getId());
                song.setAlbumId(albumIdNew);
            }
            Collection<Playlist> attachedPlaylistCollectionNew = new ArrayList<Playlist>();
            for (Playlist playlistCollectionNewPlaylistToAttach : playlistCollectionNew) {
                playlistCollectionNewPlaylistToAttach = em.getReference(playlistCollectionNewPlaylistToAttach.getClass(), playlistCollectionNewPlaylistToAttach.getId());
                attachedPlaylistCollectionNew.add(playlistCollectionNewPlaylistToAttach);
            }
            playlistCollectionNew = attachedPlaylistCollectionNew;
            song.setPlaylistCollection(playlistCollectionNew);
            song = em.merge(song);
            if (albumIdOld != null && !albumIdOld.equals(albumIdNew)) {
                albumIdOld.getSongCollection().remove(song);
                albumIdOld = em.merge(albumIdOld);
            }
            if (albumIdNew != null && !albumIdNew.equals(albumIdOld)) {
                albumIdNew.getSongCollection().add(song);
                albumIdNew = em.merge(albumIdNew);
            }
            for (Playlist playlistCollectionOldPlaylist : playlistCollectionOld) {
                if (!playlistCollectionNew.contains(playlistCollectionOldPlaylist)) {
                    playlistCollectionOldPlaylist.getSongCollection().remove(song);
                    playlistCollectionOldPlaylist = em.merge(playlistCollectionOldPlaylist);
                }
            }
            for (Playlist playlistCollectionNewPlaylist : playlistCollectionNew) {
                if (!playlistCollectionOld.contains(playlistCollectionNewPlaylist)) {
                    playlistCollectionNewPlaylist.getSongCollection().add(song);
                    playlistCollectionNewPlaylist = em.merge(playlistCollectionNewPlaylist);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = song.getId();
                if (findSong(id) == null) {
                    throw new NonexistentEntityException("The song with id " + id + " no longer exists.");
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
            Song song;
            try {
                song = em.getReference(Song.class, id);
                song.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The song with id " + id + " no longer exists.", enfe);
            }
            Album albumId = song.getAlbumId();
            if (albumId != null) {
                albumId.getSongCollection().remove(song);
                albumId = em.merge(albumId);
            }
            Collection<Playlist> playlistCollection = song.getPlaylistCollection();
            for (Playlist playlistCollectionPlaylist : playlistCollection) {
                playlistCollectionPlaylist.getSongCollection().remove(song);
                playlistCollectionPlaylist = em.merge(playlistCollectionPlaylist);
            }
            em.remove(song);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Song> findSongEntities() {
        return findSongEntities(true, -1, -1);
    }

    public List<Song> findSongEntities(int maxResults, int firstResult) {
        return findSongEntities(false, maxResults, firstResult);
    }

    private List<Song> findSongEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Song.class));
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

    public Song findSong(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Song.class, id);
        } finally {
            em.close();
        }
    }

    public int getSongCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Song> rt = cq.from(Song.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
