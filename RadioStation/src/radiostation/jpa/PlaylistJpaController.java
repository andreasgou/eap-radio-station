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
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.swing.JTable;
import radiostation.Playlist;
import radiostation.gui.ApplicationForm;
import radiostation.gui.SongTableModel;
import radiostation.gui.Utility;
import radiostation.jpa.exceptions.NonexistentEntityException;

/**
 *
 * @author a.gounaris
 */
public class PlaylistJpaController implements Serializable {

    public PlaylistJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
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
            for (Song songCollectionSongToAttach : playlist.getSongCollection()) {
                songCollectionSongToAttach = em.getReference(songCollectionSongToAttach.getClass(), songCollectionSongToAttach.getId());
                attachedSongCollection.add(songCollectionSongToAttach);
            }
            playlist.setSongCollection(attachedSongCollection);
            em.persist(playlist);
            for (Song songCollectionSong : playlist.getSongCollection()) {
                songCollectionSong.getPlaylistCollection().add(playlist);
                songCollectionSong = em.merge(songCollectionSong);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
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
                em.close();
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
                em.close();
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

    public void editPlaylist(ApplicationForm form, javax.swing.JTable sourceList) {
        if (sourceList.getSelectedRow() < 0) {
            Utility.msgWarning(form, "Δεν έχετε επιλέξει εγγραφή για τροποποίηση", "Επεξεργασία λίστας τραγουδιών");
            return;
        }
        try {
            // prepare user selection for editing
            Playlist playlist = form.getPlaylistList().get(sourceList.getSelectedRow());
            form.setPlaylist(playlist);
            // clone original object - used when cancel editing
            form.setClonedObj(playlist.clone());
            // enable edit form 
            form.setSongsToRemoveList(new ArrayList<Song>());
            form.setSongInAlbumGroupList((List)playlist.getSongCollection());

            // Initialize search list for songs
            List<Song>songs = readSongsForPlaylist(form.getjTF_song_search().getText().toString().trim(), form);
            SongTableModel songsFiltered = new SongTableModel(songs);
            form.getjTable_Available_Songs().setModel(songsFiltered);
            form.prepareSongsForPlaylist();
            form.setEditablePlaylistForm(true, false);

        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(ApplicationForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<Song> readSongsForPlaylist(String filter, ApplicationForm form) {
        List<Song> results = null;
        EntityManager em = getEntityManager();
        String criteria = "%" + filter + "%";
        Query query = em.createNativeQuery(
            "SELECT s.* " 
                + "FROM app.SONG s "
                + "INNER JOIN app.ALBUM al ON al.ID=s.ALBUM_ID "
                + "LEFT JOIN app.ARTIST ar ON ar.ID=al.ARTIST_ID "
                + "LEFT JOIN app.MUSICGROUP mg ON mg.ID=al.MUSICGROUP_ID "
                + "WHERE s.TITLE LIKE ? OR ar.ARTISTICNAME LIKE ? OR mg.NAME LIKE ? OR al.TITLE LIKE ?",
            Song.class
        ).setParameter(1, criteria)
         .setParameter(2, criteria)
         .setParameter(3, criteria)
         .setParameter(4, criteria);
        
        results = query.getResultList();
        if (em != null) {
            em.close();
        }
        return results;
    }

    public void commitPlaylist(ApplicationForm form) {
        try {
            
            Playlist playlist = form.getPlaylist();
            int totalDuration = 0;
            for (Song song : playlist.getSongCollection()){
                totalDuration += song.getDuration();
            }
            if ((playlist.getName().equalsIgnoreCase("<New Playlist>")||(playlist.getName().equals("")))&& (totalDuration < 1800)){
                Utility.msgWarning(form, "Η λίστα πρέπει να έχει τραγούδια διάρκειας τουλάχιστον μισής ώρας & να δώσετε όνομα λίστας", "Επεξεργασία λίστας τραγουδιών");
            //form.highlightGroupName();
            return;
            }
           
            if (playlist.getName().equalsIgnoreCase("<New Playlist>")||(playlist.getName().equals(""))){
                Utility.msgWarning(form, "Δεν έχετε δώσει όνομα στη λίστα", "Επεξεργασία συγκροτήματος");
                //form.highlightGroupName();
                return;
            }
            if (totalDuration < 1800) {
                Utility.msgWarning(form, "Η λίστα πρέπει να έχει τραγούδια διάρκειας τουλάχιστον μισής ώρας", "Επεξεργασία λίστας τραγουδιών");
                return;
            }
                        
            if (playlist.getId() == null) {
                this.create(playlist);
            } else {
                this.edit(playlist);
            }
            form.setEditablePlaylistForm(false, false);
            Utility.msgInfo(form, "Τα στοιχεία της λίστας αποθηκεύτηκαν επιτυχώς!");

        } catch (Exception ex) {
            Logger.getLogger(ApplicationForm.class.getName()).log(Level.SEVERE, null, ex);
            Utility.msgError(form, "Εμφανίστηκε σφάλμα κατά την αποθήκευση!");
        }
    }

    public void revertPlaylist(ApplicationForm form, JTable sourceList) {
        int idx;
        // reset list
        Playlist playlist = form.getPlaylist();
        if (playlist.getId() == null) {
            // cancel from new entry
            form.getPlaylistList().remove(playlist);
            idx = sourceList.getRowCount()-1;
            if (idx > -1) {
                // has rows
                sourceList.clearSelection();
                sourceList.setRowSelectionInterval(idx, idx);
                playlist = form.getPlaylistList().get(idx);
                form.setPlaylist(playlist);
            } else {
                // no rows
                playlist.setSongCollection(new ArrayList<Song>());
                sourceList.clearSelection();
            }
        } else {
            // cancel from existing entry
            playlist.restore((Playlist)form.getClonedObj());
            idx = sourceList.getSelectedRow();
            form.getPlaylistList().set(idx, playlist);
            // reset selection
            sourceList.clearSelection();
            sourceList.setRowSelectionInterval(idx, idx);
        }
        form.setEditablePlaylistForm(false, false);
    }

    public void destroyGroup(ApplicationForm form) {
        if (form.getjTable_Playlist().getSelectedRow() < 0) {
            Utility.msgWarning(form, "Δεν έχετε επιλέξει εγγραφή για διαγραφή", "Επεξεργασία συγκροτήματος");
            return;
        }
        try {
            int idx = form.getjTable_Playlist().getSelectedRow();
            Playlist playlist = form.getPlaylistList().get(idx);
            int ans = Utility.msgPrompt(form, playlist.getName() + "\n\nΕίσαι σίγουρος για τη διαγραφή?", "Διαγραφή Λίστας Τραγουδιών");
            if (ans == 0) {
                form.getPlaylistList().remove(playlist);
                this.destroy(playlist.getId());
                Utility.msgInfo(form, "Η διαγραφή ολοκληρώθηκε επιτυχώς!");
                form.getjTable_Playlist().setRowSelectionInterval(idx-1, idx-1);
            }
            
        } catch (NonexistentEntityException ex) {
            Logger.getLogger(ApplicationForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void addSongInPlaylist(ApplicationForm form) {
        // Pick the selected SongTableModel from the list
        SongTableModel stm = (SongTableModel)form.getjTable_Available_Songs().getModel();
        // retrieve the song
        int idx = form.getjTable_Available_Songs().getSelectedRow();
        Song song = stm.getSongsModel().get(form.getjTable_Available_Songs().convertRowIndexToModel(idx));
        // and add it to the playlist
        Playlist playlist = form.getPlaylist();
        playlist.getSongCollection().add(song);
        // append to GUI control's bounded list
        ((SongTableModel)form.getjTable_PlaylistSongs().getModel()).addSong(song);
    }

    public void removeSongFromPlaylist(ApplicationForm form, javax.swing.JTable sourceList) {
        if (sourceList.getSelectedRow() < 0) {
            Utility.msgWarning(form, "Δεν έχετε επιλέξει τραγούδι για αφαίρεση", "Επεξεργασία λίστας τραγουδιών");
        } else {
            javax.swing.JTable targetList = form.getjTable_PlaylistSongs();
            int idx = targetList.getSelectedRow();
            Song song = form.getSongInAlbumGroupList().get(targetList.convertRowIndexToModel(idx));
            form.getPlaylist().getSongCollection().remove(song);
            ((SongTableModel)form.getjTable_PlaylistSongs().getModel()).deleteRow(idx);
        }
    }

    public void newPlaylist(ApplicationForm form, JTable sourceList) {
        // create object
        Playlist playlist = new Playlist();
        // keep the object in the form
        form.setPlaylist(playlist);
        // init object
        playlist.setName("<New Playlist>");
        playlist.setCreationdate(new Date());
        playlist.setSongCollection(new ArrayList<Song>());
        // add the new entry to the table
        form.getPlaylistList().add(playlist);
        int idx = sourceList.getRowCount()-1;
        sourceList.setRowSelectionInterval(idx, idx);
        // reset the list
        form.getSongInAlbumGroupList().clear();
        SongTableModel songsInPlaylist = new SongTableModel(form.getSongInAlbumGroupList());
        form.getjTable_PlaylistSongs().setModel(songsInPlaylist);

        // Initialize search list for songs
        List<Song>songs = readSongsForPlaylist(form.getjTF_song_search().getText().toString().trim(), form);
        SongTableModel songsFiltered = new SongTableModel(songs);
        form.getjTable_Available_Songs().setModel(songsFiltered);
        form.prepareSongsForPlaylist();
        form.setEditablePlaylistForm(true, false);
    }
    
}
