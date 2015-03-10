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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import radiostation.Artist;
import radiostation.MusicGroup;
import radiostation.gui.ApplicationForm;
import radiostation.gui.Utility;
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
    
    /* Methods triggered by form events */
    public void newArtist(ApplicationForm form) {
        // create object
        Artist artist1 = new Artist();
        // keep the object in the form
        form.setArtist(artist1);
        // init object
        artist1.setArtisticname("<New Artist>");
        // add the new entry to the table
        form.getArtistList().add(artist1);
        int idx = form.getjTable_Artists().getRowCount()-1;
        form.getjTable_Artists().setRowSelectionInterval(idx, idx);
        form.setEditableArtistForm(true, true);
    }

    public void destroyArtist(ApplicationForm form) {
        if (form.getjTable_Artists().getSelectedRow() < 0) {
            Utility.msgWarning(form, "Δεν έχετε επιλέξει εγγραφή για τροποποίηση", "Επεξεργασία καλλιτέχνη");
            return;
        }
        try {
            int idx = form.getjTable_Artists().getSelectedRow();
            Artist artist1 = form.getArtistList().get(idx);
            int ans = Utility.msgPrompt(form, artist1.getArtisticname() + "\n\nΕίσαι σίγουρος για τη διαγραφή?", "Διαγραφή Καλλιτέχνη");
            if (ans == 0) {
                form.getArtistList().remove(artist1);
                this.destroy(artist1.getId());
                Utility.msgInfo(form, "Η διαγραφή ολοκληρώθηκε επιτυχώς!");   
                form.getjTable_Artists().setRowSelectionInterval(idx-1, idx-1);
            }
            
        } catch (NonexistentEntityException ex) {
            Logger.getLogger(ApplicationForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void editArtist(ApplicationForm form) {
        if (form.getjTable_Artists().getSelectedRow() < 0) {
            Utility.msgWarning(form, "Δεν έχετε επιλέξει εγγραφή για τροποποίηση", "Επεξεργασία καλλιτέχνη");
            return;
        }
        try {
            // prepare user selection for editing
            Artist artist1 = (Artist)form.getArtistList().get(form.getjTable_Artists().getSelectedRow());
            form.setArtist(artist1);
            // prepare user selection for editing
            form.setClonedObj(artist1.clone());
            // clear radio buttons if sex is undefined
            if (artist1.getSex() == null)
                form.getButtonGroup1().clearSelection();
            // enable edit form 
            form.setEditableArtistForm(true, false);
        
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(ApplicationForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void commitArtist(ApplicationForm form) {
        try {
            Artist artist1 = form.getArtist();
            if (artist1.getId() == null)
                this.create(artist1);
            else
                this.edit(artist1);
            form.setEditableArtistForm(false, false);
            Utility.msgInfo(form, "Τα στοιχεία του Καλλιτέχνη αποθηκεύτηκαν επιτυχώς!");

        } catch (Exception ex) {
            Logger.getLogger(ApplicationForm.class.getName()).log(Level.SEVERE, null, ex);
            Utility.msgError(form, "Εμφανίστηκε σφάλμα κατά την αποθήκευση!");
        }
    }

    public void revertArtist(ApplicationForm form) {
        int idx;
        // reset list
        Artist artist1 = form.getArtist();
        if (artist1.getId() == null) {
            // cancel from new entry
            form.getArtistList().remove(artist1);
            idx = form.getjTable_Artists().getRowCount()-1;
            if (idx > -1) {
                // has rows
                form.getjTable_Artists().clearSelection();
                form.getjTable_Artists().setRowSelectionInterval(idx, idx);
            } else {
                // no rows
                form.getjTable_Artists().clearSelection();
            }
        } else {
            // cancel from existing entry
            artist1.restore((Artist)form.getClonedObj());
            idx = form.getjTable_Artists().getSelectedRow();
            form.getArtistList().set(idx, artist1);
            // reset selection
            form.getjTable_Artists().clearSelection();
            form.getjTable_Artists().setRowSelectionInterval(idx, idx);
        }
        form.setEditableArtistForm(false, false);
    }

}
