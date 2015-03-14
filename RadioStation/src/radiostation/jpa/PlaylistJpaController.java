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
import radiostation.Song;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import radiostation.Playlist;
import radiostation.gui.ApplicationForm;
import radiostation.gui.Utility;
import radiostation.jpa.exceptions.NonexistentEntityException;

/**
 *
 * @author user
 */
public class PlaylistJpaController implements Serializable {

    public PlaylistJpaController(EntityManager em) {
        this.em = em;
    }
    private EntityManager em = null;

    public EntityManager getEntityManager() {
        return em;
    }

    public void create(Playlist playlist) {
        if (playlist.getSongCollection() == null) {
            playlist.setSongCollection(new ArrayList<Song>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Song> attachedSongCollection = new ArrayList<Song>();
            Collection<Song> currentCollection = playlist.getSongCollection();

            // store playlist without collection to get a new ID from the Database
            playlist.setSongCollection(attachedSongCollection);
            em.persist(playlist);
            em.getTransaction().commit();

            // restore collections for the new entity and store again if not empty
            playlist.setSongCollection(currentCollection);
            if (currentCollection.size() > 0)
                edit(playlist);
            
        } catch (Exception ex) {
            Logger.getLogger(PlaylistJpaController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
        }
    
    }

    public void edit(Playlist playlist) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Playlist persistentPlaylist = em.find(Playlist.class, playlist.getId());
            Collection<Song> songCollectionOld = persistentPlaylist.getSongCollection();
            Collection<Song> songCollectionNew = playlist.getSongCollection();
            Collection<Song> attachedSongCollectionNew = new ArrayList<Song>();
            for (Song songCollectionNewSongToAttach : songCollectionNew) {
                songCollectionNewSongToAttach = em.getReference(songCollectionNewSongToAttach.getClass(), songCollectionNewSongToAttach.getId());
                attachedSongCollectionNew.add(songCollectionNewSongToAttach);
            }
            songCollectionNew = attachedSongCollectionNew;
            playlist.setSongCollection(songCollectionNew);
            playlist = em.merge(playlist);
            for (Song songCollectionOldSong : songCollectionOld) {
                if (!songCollectionNew.contains(songCollectionOldSong)) {
                    songCollectionOldSong.getPlaylistCollection().remove(playlist);
                    songCollectionOldSong = em.merge(songCollectionOldSong);
                }
            }
            for (Song songCollectionNewSong : songCollectionNew) {
                if (!songCollectionOld.contains(songCollectionNewSong)) {
                    songCollectionNewSong.getPlaylistCollection().add(playlist);
                    songCollectionNewSong = em.merge(songCollectionNewSong);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = playlist.getId();
                if (findPlaylist(id) == null) {
                    throw new NonexistentEntityException("The playlist with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
               // em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Playlist playlist;
            try {
                playlist = em.getReference(Playlist.class, id);
                playlist.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The playlist with id " + id + " no longer exists.", enfe);
            }
            Collection<Song> songCollection = playlist.getSongCollection();
            for (Song songCollectionSong : songCollection) {
                songCollectionSong.getPlaylistCollection().remove(playlist);
                songCollectionSong = em.merge(songCollectionSong);
            }
            em.remove(playlist);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                //em.close();
            }
        }
    }

    public List<Playlist> findPlaylistEntities() {
        return findPlaylistEntities(true, -1, -1);
    }

    public List<Playlist> findPlaylistEntities(int maxResults, int firstResult) {
        return findPlaylistEntities(false, maxResults, firstResult);
    }

    private List<Playlist> findPlaylistEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Playlist.class));
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

    public Playlist findPlaylist(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Playlist.class, id);
        } finally {
            em.close();
        }
    }

    public int getPlaylistCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Playlist> rt = cq.from(Playlist.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    /* Methods triggered by form events */
    public void newPlaylist(ApplicationForm form) {
        // create object
        Playlist playlist1 = new Playlist();
        // keep the object in the form
        form.setPlaylist(playlist1);
        // init object
        playlist1.setName("<New Play List>");
        playlist1.setSongCollection(new ArrayList<Song>());
        // add the new entry to the table
        form.getplaylistList().add(playlist1);
        int idx = form.getjTable_PlayLists().getRowCount()-1;
        form.getjTable_PlayLists().setRowSelectionInterval(idx, idx);
        // reset the list
        form.getjList_ListSongs().setListData(playlist1.getSongCollection().toArray());
        form.setEditablePlayListForm(true, true);
    }

    public void destroyPlaylist(ApplicationForm form) {
        if (form.getjTable_PlayLists().getSelectedRow() < 0) {
            Utility.msgWarning(form, "Δεν έχετε επιλέξει εγγραφή για διαγραφή", "Επεξεργασία λίστας");
            return;
        }
        try {
            int idx = form.getjTable_PlayLists().getSelectedRow();
            Playlist playlist1 = form.getplaylistList().get(idx);
            int ans = Utility.msgPrompt(form, playlist1.getName() + "\n\nΕίσαι σίγουρος για τη διαγραφή?", "Διαγραφή Λίστας");
            if (ans == 0) {
                form.getplaylistList().remove(playlist1);
                this.destroy(playlist1.getId());
                Utility.msgInfo(form, "Η διαγραφή ολοκληρώθηκε επιτυχώς!");
                form.getjTable_PlayLists().setRowSelectionInterval(idx-1, idx-1);
            }
            
        } catch (NonexistentEntityException ex) {
            Logger.getLogger(ApplicationForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void editPlaylist(ApplicationForm form) {
        if (form.getjTable_PlayLists().getSelectedRow() < 0) {
            Utility.msgWarning(form, "Δεν έχετε επιλέξει εγγραφή για τροποποίηση", "Επεξεργασία λίστας");
            return;
        }
        try {
            // prepare user selection for editing
            Playlist playlist1 = form.getplaylistList().get(form.getjTable_PlayLists().getSelectedRow());
            form.setPlaylist(playlist1);
            // clone original object - used when cancel editing
            form.setClonedObj(playlist1.clone());
            // enable edit form 
            form.setEditablePlayListForm(true, false);

        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(ApplicationForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void commitPlaylist(ApplicationForm form) {
        try {
            
            Playlist playlist1 = form.getPlaylist();
            /*if ((playlist1.getName().equalsIgnoreCase("<New Play List>")||(playlist1.getName().equals("")))&& (playlist1.getSongCollection().size() < 2)){
            Utility.msgWarning(form, "Το συγκρότημα πρέπει να έχει τουλάχιστον 2 μέλη & να δώσετε όνομα συγκροτήματος", "Επεξεργασία συγκροτήματος");
            form.highlightGroupName();
            return;
            }*/
           
            /*if (musicGroup1.getName().equalsIgnoreCase("<New Music Group>")||(musicGroup1.getName().equals(""))){
                Utility.msgWarning(form, "Δεν έχετε δώσει όνομα στο συγκρότημα", "Επεξεργασία συγκροτήματος");
                form.highlightGroupName();
                return;
            }
            if (musicGroup1.getArtistCollection().size() < 2) {
                Utility.msgWarning(form, "Το συγκρότημα πρέπει να έχει τουλάχιστον 2 μέλη", "Επεξεργασία συγκροτήματος");
                return;
            }*/
                        
            if (playlist1.getId() == null) {
                this.create(playlist1);
            } else {
                this.edit(playlist1);
            }
            form.setEditablePlayListForm(false, false);
            Utility.msgInfo(form, "Τα στοιχεία της λίστας αποθηκεύτηκαν επιτυχώς!");

        } catch (Exception ex) {
            Logger.getLogger(ApplicationForm.class.getName()).log(Level.SEVERE, null, ex);
            Utility.msgError(form, "Εμφανίστηκε σφάλμα κατά την αποθήκευση!");
        }
    }

    public void revertPalylist(ApplicationForm form) {
        int idx;
        // reset list
        Playlist playlist1 = form.getPlaylist();
        if (playlist1.getId() == null) {
            // cancel from new entry
            form.getplaylistList().remove(playlist1);
            idx = form.getjTable_PlayLists().getRowCount()-1;
            if (idx > -1) {
                // has rows
                form.getjTable_PlayLists().clearSelection();
                form.getjTable_PlayLists().setRowSelectionInterval(idx, idx);
                playlist1 = form.getplaylistList().get(idx);
                form.setPlaylist(playlist1);
            } else {
                // no rows
                playlist1.setSongCollection(new ArrayList<Song>());
                form.getjTable_Groups().clearSelection();
            }
        } else {
            idx = form.getjTable_PlayLists().getSelectedRow();
            // cancel from existing entry
            playlist1.restore((Playlist)form.getClonedObj());
            idx = form.getjTable_PlayLists().getSelectedRow();
            form.getplaylistList().set(idx, playlist1);
            // reset selection
            form.getjTable_PlayLists().clearSelection();
            form.getjTable_PlayLists().setRowSelectionInterval(idx, idx);
        }
        form.getjList_ListSongs().setListData(playlist1.getSongCollection().toArray());
        form.setEditablePlayListForm(false, false);
    }

    public void addSongInList(ApplicationForm form) {
        if (form.getjList_AvailableSongs().getSelectedIndex() < 0) {
            Utility.msgWarning(form, "Δεν έχετε επιλέξει τραγούδι για προσθήκη στη λίστα", "Επεξεργασία λίστας");
        } else {
            Playlist playlist1 = form.getPlaylist();
            Song songToPlaylist =  (Song)form.getjList_AvailableSongs().getSelectedValue();
            List songInPlayList = (List)playlist1.getSongCollection();
            if (songInPlayList.contains(songToPlaylist)) {
                Utility.msgWarning(form, "To τραγούδι ανήκει ήδη στη λίστα", "Επεξεργασία λίστας");
            } else {
                songInPlayList.add (songToPlaylist);

                form.getjList_ListSongs().setListData(songInPlayList.toArray());
                form.getjList_ListSongs().setSelectedIndex(songInPlayList.size()-1);
            }
        }
    }

    public void removeSongFromPlaylist(ApplicationForm form) {
        if (form.getjList_ListSongs().getSelectedIndex() < 0) {
            Utility.msgWarning(form, "Δεν έχετε επιλέξει τραγούδι για αφαίρεση", "Επεξεργασία λίστας");
        } else {
            List songInPlayList = (List)form.getPlaylist().getSongCollection();
            songInPlayList.remove(form.getjList_ListSongs().getSelectedIndex());
            form.getjList_ListSongs().setListData(songInPlayList.toArray());
            form.getjList_ListSongs().setSelectedIndex(songInPlayList.size()-1);
        }       
    }
}
