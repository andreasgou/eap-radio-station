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
import radiostation.MusicProductionCompany;
import radiostation.Song;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import radiostation.Album;
import radiostation.MusicGroup;
import radiostation.gui.ApplicationForm;
import radiostation.gui.Utility;
import radiostation.jpa.exceptions.IllegalOrphanException;
import radiostation.jpa.exceptions.NonexistentEntityException;

/**
 *
 * @author user
 */
public class AlbumJpaController implements Serializable {

    public AlbumJpaController(EntityManager em) {
        this.em = em;
    }
    private EntityManager em = null;

    public EntityManager getEntityManager() {
        return em;
    }

    public void create(Album album) {
        if (album.getSongCollection() == null) {
            album.setSongCollection(new ArrayList<Song>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Artist artistId = album.getArtistId();
            if (artistId != null) {
                artistId = em.getReference(artistId.getClass(), artistId.getId());
                album.setArtistId(artistId);
            }
            MusicProductionCompany companyId = album.getCompanyId();
            if (companyId != null) {
                companyId = em.getReference(companyId.getClass(), companyId.getId());
                album.setCompanyId(companyId);
            }
            Collection<Song> attachedSongCollection = new ArrayList<Song>();
            for (Song songCollectionSongToAttach : album.getSongCollection()) {
                songCollectionSongToAttach = em.getReference(songCollectionSongToAttach.getClass(), songCollectionSongToAttach.getId());
                attachedSongCollection.add(songCollectionSongToAttach);
            }
            album.setSongCollection(attachedSongCollection);
            em.persist(album);
            if (artistId != null) {
                artistId.getAlbumCollection().add(album);
                artistId = em.merge(artistId);
            }
            if (companyId != null) {
                companyId.getAlbumCollection().add(album);
                companyId = em.merge(companyId);
            }
            for (Song songCollectionSong : album.getSongCollection()) {
                Album oldAlbumIdOfSongCollectionSong = songCollectionSong.getAlbumId();
                songCollectionSong.setAlbumId(album);
                songCollectionSong = em.merge(songCollectionSong);
                if (oldAlbumIdOfSongCollectionSong != null) {
                    oldAlbumIdOfSongCollectionSong.getSongCollection().remove(songCollectionSong);
                    oldAlbumIdOfSongCollectionSong = em.merge(oldAlbumIdOfSongCollectionSong);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Album album) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Album persistentAlbum = em.find(Album.class, album.getId());
            Artist artistIdOld = persistentAlbum.getArtistId();
            Artist artistIdNew = album.getArtistId();
            MusicProductionCompany companyIdOld = persistentAlbum.getCompanyId();
            MusicProductionCompany companyIdNew = album.getCompanyId();
            Collection<Song> songCollectionOld = persistentAlbum.getSongCollection();
            Collection<Song> songCollectionNew = album.getSongCollection();
            List<String> illegalOrphanMessages = null;
            for (Song songCollectionOldSong : songCollectionOld) {
                if (!songCollectionNew.contains(songCollectionOldSong)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Song " + songCollectionOldSong + " since its albumId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (artistIdNew != null) {
                artistIdNew = em.getReference(artistIdNew.getClass(), artistIdNew.getId());
                album.setArtistId(artistIdNew);
            }
            if (companyIdNew != null) {
                companyIdNew = em.getReference(companyIdNew.getClass(), companyIdNew.getId());
                album.setCompanyId(companyIdNew);
            }
            Collection<Song> attachedSongCollectionNew = new ArrayList<Song>();
            for (Song songCollectionNewSongToAttach : songCollectionNew) {
                songCollectionNewSongToAttach = em.getReference(songCollectionNewSongToAttach.getClass(), songCollectionNewSongToAttach.getId());
                attachedSongCollectionNew.add(songCollectionNewSongToAttach);
            }
            songCollectionNew = attachedSongCollectionNew;
            album.setSongCollection(songCollectionNew);
            album = em.merge(album);
            if (artistIdOld != null && !artistIdOld.equals(artistIdNew)) {
                artistIdOld.getAlbumCollection().remove(album);
                artistIdOld = em.merge(artistIdOld);
            }
            if (artistIdNew != null && !artistIdNew.equals(artistIdOld)) {
                artistIdNew.getAlbumCollection().add(album);
                artistIdNew = em.merge(artistIdNew);
            }
            if (companyIdOld != null && !companyIdOld.equals(companyIdNew)) {
                companyIdOld.getAlbumCollection().remove(album);
                companyIdOld = em.merge(companyIdOld);
            }
            if (companyIdNew != null && !companyIdNew.equals(companyIdOld)) {
                companyIdNew.getAlbumCollection().add(album);
                companyIdNew = em.merge(companyIdNew);
            }
            for (Song songCollectionNewSong : songCollectionNew) {
                if (!songCollectionOld.contains(songCollectionNewSong)) {
                    Album oldAlbumIdOfSongCollectionNewSong = songCollectionNewSong.getAlbumId();
                    songCollectionNewSong.setAlbumId(album);
                    songCollectionNewSong = em.merge(songCollectionNewSong);
                    if (oldAlbumIdOfSongCollectionNewSong != null && !oldAlbumIdOfSongCollectionNewSong.equals(album)) {
                        oldAlbumIdOfSongCollectionNewSong.getSongCollection().remove(songCollectionNewSong);
                        oldAlbumIdOfSongCollectionNewSong = em.merge(oldAlbumIdOfSongCollectionNewSong);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = album.getId();
                if (findAlbum(id) == null) {
                    throw new NonexistentEntityException("The album with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Album album;
            try {
                album = em.getReference(Album.class, id);
                album.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The album with id " + id + " no longer exists.", enfe);
            }
            
            Collection<Song> songCollection = album.getSongCollection();
            
            for (Song songCollectionSong : songCollection) {
                songCollectionSong.getPlaylistCollection().remove(album);
                songCollectionSong = em.merge(songCollectionSong);
            }
            em.remove(album);
            em.getTransaction().commit();
            
            
        }finally {
            if (em != null) {
                //em.close();
            }
        }
    }

    public List<Album> findAlbumEntities() {
        return findAlbumEntities(true, -1, -1);
    }

    public List<Album> findAlbumEntities(int maxResults, int firstResult) {
        return findAlbumEntities(false, maxResults, firstResult);
    }

    private List<Album> findAlbumEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Album.class));
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

    public Album findAlbum(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Album.class, id);
        } finally {
            em.close();
        }
    }

    public int getAlbumCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Album> rt = cq.from(Album.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    /* Methods triggered by form events */
    public void newAlbum(ApplicationForm form) {
        // create object
        Album album1 = new Album();
        // keep the object in the form
        form.setAlbum(album1);
        // init object
        album1.setTitle("<New Album>");
        album1.setSongCollection(new ArrayList<Song>());
        // add the new entry to the table
        form.getAlbumList().add(album1);
        int idx = form. getjTable_AlbumGroups().getRowCount()-1;
        form. getjTable_AlbumGroups().setRowSelectionInterval(idx, idx);
        // reset the list
        form.getjList_GroupAlbumSongs().setListData(album1.getSongCollection().toArray());
        form.setEditableGroupAlbumForm(true, true);
    }

   /* public void destroyAlbum(ApplicationForm form) {
        if (form. getjTable_AlbumGroups().getSelectedRow() < 0) {
            Utility.msgWarning(form, "Δεν έχετε επιλέξει εγγραφή για διαγραφή", "Επεξεργασία Αλμπουμ");
            return;
        }
        try {
            int idx = form. getjTable_AlbumGroups().getSelectedRow();
            Album album1 = form.getAlbumList().get(idx);
            int ans = Utility.msgPrompt(form, album1.getTitle()+ "\n\nΕίσαι σίγουρος για τη διαγραφή?", "Διαγραφή Αλμπουμ");
            if (ans == 0) {
                form.getAlbumList().remove(album1);
                this.destroy(album1.getId());
                Utility.msgInfo(form, "Η διαγραφή ολοκληρώθηκε επιτυχώς!");
                form. getjTable_AlbumGroups().setRowSelectionInterval(idx-1, idx-1);
            }
            
        } catch (NonexistentEntityException ex) {
            Logger.getLogger(ApplicationForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }*/

    public void editAlbum(ApplicationForm form) {
        if (form. getjTable_AlbumGroups().getSelectedRow() < 0) {
            Utility.msgWarning(form, "Δεν έχετε επιλέξει εγγραφή για τροποποίηση", "Επεξεργασία συγκροτήματος");
            return;
        }
        try {
            // prepare user selection for editing
            Album album1 = form.getAlbumList().get(form. getjTable_AlbumGroups().getSelectedRow());
            form.setAlbum(album1);
            // clone original object - used when cancel editing
            form.setClonedObj(album1.clone());
            // enable edit form 
            form.setEditableGroupAlbumForm(true, false);

        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(ApplicationForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void commitAlbum(ApplicationForm form) {
        try {
            
            Album album1 = form.getAlbum();
            if ((album1.getTitle().equalsIgnoreCase("<New Album>")||(album1.getTitle().equals("")))&& (album1.getSongCollection().size() ==0)){
            Utility.msgWarning(form, "Το άλμπουμ πρέπει να έχει τουλάχιστον 1 τραγούδι & να δώσετε τίτλο άλμπουμ", "Επεξεργασία αλμπουμ");
            form.highlightAlbumTitle();
            return;
            }
           
            if (album1.getTitle().equalsIgnoreCase("<New Album>")||(album1.getTitle().equals(""))){
                Utility.msgWarning(form, "Δεν έχετε δώσει τίτλο στο άλμπουμ", "Επεξεργασία άλμπουμ");
                form.highlightAlbumTitle();
                return;
            }
            if (album1.getSongCollection().size()==0) {
                Utility.msgWarning(form, "Το άλμπουμ πρέπει να έχει τουλάχιστον 1 τραγουδι", "Επεξεργασία αλμπουμ");
                return;
            }
                        
            if (album1.getId() == null) {
                this.create(album1);
            } else {
                this.edit(album1);
            }
            form.setEditableGroupAlbumForm(false, false);
            Utility.msgInfo(form, "Τα στοιχεία του άλμπουμ αποθηκεύτηκαν επιτυχώς!");

        } catch (Exception ex) {
            Logger.getLogger(ApplicationForm.class.getName()).log(Level.SEVERE, null, ex);
            Utility.msgError(form, "Εμφανίστηκε σφάλμα κατά την αποθήκευση!");
        }
    }

    public void revertAlbum(ApplicationForm form) {
        int idx;
        // reset list
        Album album1 = form.getAlbum();
        if (album1.getId() == null) {
            // cancel from new entry
            form.getAlbumList().remove(album1);
            idx = form.getjTable_AlbumGroups().getRowCount()-1;
            if (idx > -1) {
                // has rows
                form.getjTable_AlbumGroups().clearSelection();
                form.getjTable_AlbumGroups().setRowSelectionInterval(idx, idx);
                album1 = form.getAlbumList().get(idx);
                form.setAlbum(album1);
            } else {
                // no rows
                album1.setSongCollection(new ArrayList<Song>());
                form.getjTable_AlbumGroups().clearSelection();
            }
        } else {
            idx = form.getjTable_AlbumGroups().getSelectedRow();
            // cancel from existing entry
            album1.restore((Album)form.getClonedObj());
            idx = form.getjTable_AlbumGroups().getSelectedRow();
            form.getAlbumList().set(idx, album1);
            // reset selection
            form.getjTable_AlbumGroups().clearSelection();
            form.getjTable_AlbumGroups().setRowSelectionInterval(idx, idx);
        }
        form.getjList_GroupAlbumSongs().setListData(album1.getSongCollection().toArray());
        form.setEditableGroupAlbumForm(false, false);
    }

    public void addSongInAlbum(ApplicationForm form) {
        if (form.getjList_AvailableArtists().getSelectedIndex() < 0) {
            Utility.msgWarning(form, "Δεν έχετε επιλέξει καλλιτέχνη για προσθήκη στο συγκρότημα", "Επεξεργασία συγκροτήματος");
        } else {
            Album album1 = form.getAlbum();
            Song songToAlbum =  (Song)form.getjList_AvailableArtists().getSelectedValue();
            List songInAlbumList = (List)album1.getSongCollection();
            if (songInAlbumList.contains(songToAlbum)) {
                Utility.msgWarning(form, "To τραγούδι ανήκει ήδη στο άλμπουμ", "Επεξεργασία αλμπουμ");
            } else {
                songInAlbumList.add (songToAlbum);

                form.getjList_GroupAlbumSongs().setListData(songInAlbumList.toArray());
                form.getjList_GroupAlbumSongs().setSelectedIndex(songInAlbumList.size()-1);
            }
        }
    }

    public void removeSongFromAlbum(ApplicationForm form) {
        if (form.getjList_GroupAlbumSongs().getSelectedIndex() < 0) {
            Utility.msgWarning(form, "Δεν έχετε επιλέξει τραγούδι για αφαίρεση", "Επεξεργασία αλμπουμ");
        } else {
            List songInAlbumList = (List)form.getAlbum().getSongCollection();
            songInAlbumList.remove(form.getjList_GroupAlbumSongs().getSelectedIndex());
            form.getjList_GroupAlbumSongs().setListData(songInAlbumList.toArray());
            form.getjList_GroupAlbumSongs().setSelectedIndex(songInAlbumList.size()-1);
        }       
    }
    
}
