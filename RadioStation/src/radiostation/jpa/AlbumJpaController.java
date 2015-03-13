/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package radiostation.jpa;

import icons.exceptions.IllegalOrphanException;
import icons.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import radiostation.Artist;
import radiostation.MusicProductionCompany;
import radiostation.Album;
import radiostation.Song;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import radiostation.Playlist;
import radiostation.gui.ApplicationForm;
import radiostation.gui.SongTableModel;
import radiostation.gui.Utility;

/**
 *
 * @author a.gounaris
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
        if (album.getAlbumCollection() == null) {
            album.setAlbumCollection(new ArrayList<Album>());
        }
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
            Album parentalbumId = album.getParentalbumId();
            if (parentalbumId != null) {
                parentalbumId = em.getReference(parentalbumId.getClass(), parentalbumId.getId());
                album.setParentalbumId(parentalbumId);
            }
            Collection<Song> attachedSongCollection = new ArrayList<Song>();
            for (Song songCollectionSongToAttach : album.getSongCollection()) {
                songCollectionSongToAttach = em.getReference(songCollectionSongToAttach.getClass(), songCollectionSongToAttach.getId());
                attachedSongCollection.add(songCollectionSongToAttach);
            }
            album.setSongCollection(attachedSongCollection);
            Collection<Album> attachedAlbumCollection = new ArrayList<Album>();
            for (Album albumCollectionAlbumToAttach : album.getAlbumCollection()) {
                albumCollectionAlbumToAttach = em.getReference(albumCollectionAlbumToAttach.getClass(), albumCollectionAlbumToAttach.getId());
                attachedAlbumCollection.add(albumCollectionAlbumToAttach);
            }
            album.setAlbumCollection(attachedAlbumCollection);
            em.persist(album);
            if (artistId != null) {
                artistId.getAlbumCollection().add(album);
                artistId = em.merge(artistId);
            }
            if (companyId != null) {
                companyId.getAlbumCollection().add(album);
                companyId = em.merge(companyId);
            }
            if (parentalbumId != null) {
                parentalbumId.getAlbumCollection().add(album);
                parentalbumId = em.merge(parentalbumId);
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
            for (Album albumCollectionAlbum : album.getAlbumCollection()) {
                Album oldParentalbumIdOfAlbumCollectionAlbum = albumCollectionAlbum.getParentalbumId();
                albumCollectionAlbum.setParentalbumId(album);
                albumCollectionAlbum = em.merge(albumCollectionAlbum);
                if (oldParentalbumIdOfAlbumCollectionAlbum != null) {
                    oldParentalbumIdOfAlbumCollectionAlbum.getAlbumCollection().remove(albumCollectionAlbum);
                    oldParentalbumIdOfAlbumCollectionAlbum = em.merge(oldParentalbumIdOfAlbumCollectionAlbum);
                }
            }
            em.getTransaction().commit();
        } finally {
//            if (em != null) {
//                em.close();
//            }
        }
    }

    public void edit(Album album, ApplicationForm form) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            //em.getTransaction().begin();
            
            // get a fresh copy of Album and related objects from db
            Album persistentAlbum = em.find(Album.class, album.getId());
            Artist artistIdOld = persistentAlbum.getArtistId();
            Artist artistIdNew = album.getArtistId();
            MusicProductionCompany companyIdOld = persistentAlbum.getCompanyId();
            MusicProductionCompany companyIdNew = album.getCompanyId();
            Album parentalbumIdOld = persistentAlbum.getParentalbumId();
            Album parentalbumIdNew = album.getParentalbumId();
            Collection<Song> songCollectionOld = persistentAlbum.getSongCollection();
            Collection<Song> songCollectionNew = album.getSongCollection();
            Collection<Album> albumCollectionOld = persistentAlbum.getAlbumCollection();
            Collection<Album> albumCollectionNew = album.getAlbumCollection();

            // Check for orphan songs after album update (they should be deleted with a warning)
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

            // refresh artist from db
            if (artistIdNew != null) {
                artistIdNew = em.getReference(artistIdNew.getClass(), artistIdNew.getId());
                album.setArtistId(artistIdNew);
            }
            // refresh production company from db
            if (companyIdNew != null) {
                companyIdNew = em.getReference(companyIdNew.getClass(), companyIdNew.getId());
                album.setCompanyId(companyIdNew);
            }
            // refresh parent album from db
            if (parentalbumIdNew != null) {
                parentalbumIdNew = em.getReference(parentalbumIdNew.getClass(), parentalbumIdNew.getId());
                album.setParentalbumId(parentalbumIdNew);
            }
            
            // refresh song collection 
            // Add new songs
            em.getTransaction().begin();
            Collection<Song> detachedSongCollectionNew = new ArrayList<Song>();
            for (Song songCollectionNewSongToAttach : songCollectionNew) {
                // new Song entry
                if (songCollectionNewSongToAttach.getId() == null) {
                    songCollectionNewSongToAttach.setAlbumId(album);
                    em.persist(songCollectionNewSongToAttach);

                } else if (songCollectionNewSongToAttach.getAlbumId() == null) {
                    detachedSongCollectionNew.add(songCollectionNewSongToAttach);
                }
            }
            // Destroy removed songs
            for (Song songCollectionSongToDetach : form.getSongsToRemoveList()) {
                // Remove song from album
                em.remove(songCollectionSongToDetach);
                // Remove song references from playlists
                Collection<Playlist> playlistCollection = songCollectionSongToDetach.getPlaylistCollection();
                for (Playlist playlistCollectionPlaylist : playlistCollection) {
                    playlistCollectionPlaylist.getSongCollection().remove(songCollectionSongToDetach);
                    playlistCollectionPlaylist = em.merge(playlistCollectionPlaylist);
                }
            }
            em.getTransaction().commit();

            em.getTransaction().begin();
            // refresh album collection 
            Collection<Album> attachedAlbumCollectionNew = new ArrayList<Album>();
            for (Album albumCollectionNewAlbumToAttach : albumCollectionNew) {
                albumCollectionNewAlbumToAttach = em.getReference(albumCollectionNewAlbumToAttach.getClass(), albumCollectionNewAlbumToAttach.getId());
                attachedAlbumCollectionNew.add(albumCollectionNewAlbumToAttach);
            }
            albumCollectionNew = attachedAlbumCollectionNew;
            album.setAlbumCollection(albumCollectionNew);

            // merge relations 
            album = em.merge(album);

            // if artist changed, remove album reference from the old artist's collection
            if (artistIdOld != null && !artistIdOld.equals(artistIdNew)) {
                artistIdOld.getAlbumCollection().remove(album);
                artistIdOld = em.merge(artistIdOld);
            }
            // if artist changed, add album reference to the new artist's collection
            if (artistIdNew != null && !artistIdNew.equals(artistIdOld)) {
                artistIdNew.getAlbumCollection().add(album);
                artistIdNew = em.merge(artistIdNew);
            }
            // if company changed, remove album reference from the old company's collection
            if (companyIdOld != null && !companyIdOld.equals(companyIdNew)) {
                companyIdOld.getAlbumCollection().remove(album);
                companyIdOld = em.merge(companyIdOld);
            }
            // if company changed, add album reference to the new company's collection
            if (companyIdNew != null && !companyIdNew.equals(companyIdOld)) {
                companyIdNew.getAlbumCollection().add(album);
                companyIdNew = em.merge(companyIdNew);
            }
            // if parent album changed, remove album reference from the old parent's collection
            if (parentalbumIdOld != null && !parentalbumIdOld.equals(parentalbumIdNew)) {
                parentalbumIdOld.getAlbumCollection().remove(album);
                parentalbumIdOld = em.merge(parentalbumIdOld);
            }
            // if parent album changed, add album reference to the new parent's collection
            if (parentalbumIdNew != null && !parentalbumIdNew.equals(parentalbumIdOld)) {
                parentalbumIdNew.getAlbumCollection().add(album);
                parentalbumIdNew = em.merge(parentalbumIdNew);
            }
            
            // Update album references in new songs
            for (Song songCollectionNewSong : songCollectionNew) {
                if (!songCollectionOld.contains(songCollectionNewSong)) {
                    Album oldAlbumIdOfSongCollectionNewSong = songCollectionNewSong.getAlbumId();
                    songCollectionNewSong.setAlbumId(album);
                    songCollectionNewSong = em.merge(songCollectionNewSong);
                    // remove songs from old album's song collection 
                    if (oldAlbumIdOfSongCollectionNewSong != null && !oldAlbumIdOfSongCollectionNewSong.equals(album)) {
                        oldAlbumIdOfSongCollectionNewSong.getSongCollection().remove(songCollectionNewSong);
                        oldAlbumIdOfSongCollectionNewSong = em.merge(oldAlbumIdOfSongCollectionNewSong);
                    }
                }
            }
            // Update parent album references in detached albums
            for (Album albumCollectionOldAlbum : albumCollectionOld) {
                if (!albumCollectionNew.contains(albumCollectionOldAlbum)) {
                    albumCollectionOldAlbum.setParentalbumId(null);
                    albumCollectionOldAlbum = em.merge(albumCollectionOldAlbum);
                }
            }
            // Update album references in new child albums (LP type)
            for (Album albumCollectionNewAlbum : albumCollectionNew) {
                if (!albumCollectionOld.contains(albumCollectionNewAlbum)) {
                    Album oldParentalbumIdOfAlbumCollectionNewAlbum = albumCollectionNewAlbum.getParentalbumId();
                    albumCollectionNewAlbum.setParentalbumId(album);
                    albumCollectionNewAlbum = em.merge(albumCollectionNewAlbum);
                    // remove albums from old parent album's collection
                    if (oldParentalbumIdOfAlbumCollectionNewAlbum != null && !oldParentalbumIdOfAlbumCollectionNewAlbum.equals(album)) {
                        oldParentalbumIdOfAlbumCollectionNewAlbum.getAlbumCollection().remove(albumCollectionNewAlbum);
                        oldParentalbumIdOfAlbumCollectionNewAlbum = em.merge(oldParentalbumIdOfAlbumCollectionNewAlbum);
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
            List<String> illegalOrphanMessages = null;
            Collection<Song> songCollectionOrphanCheck = album.getSongCollection();
            for (Song songCollectionOrphanCheckSong : songCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Album (" + album + ") cannot be destroyed since the Song " + songCollectionOrphanCheckSong + " in its songCollection field has a non-nullable albumId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Artist artistId = album.getArtistId();
            if (artistId != null) {
                artistId.getAlbumCollection().remove(album);
                artistId = em.merge(artistId);
            }
            MusicProductionCompany companyId = album.getCompanyId();
            if (companyId != null) {
                companyId.getAlbumCollection().remove(album);
                companyId = em.merge(companyId);
            }
            Album parentalbumId = album.getParentalbumId();
            if (parentalbumId != null) {
                parentalbumId.getAlbumCollection().remove(album);
                parentalbumId = em.merge(parentalbumId);
            }
            Collection<Album> albumCollection = album.getAlbumCollection();
            for (Album albumCollectionAlbum : albumCollection) {
                albumCollectionAlbum.setParentalbumId(null);
                albumCollectionAlbum = em.merge(albumCollectionAlbum);
            }
            em.remove(album);
            em.getTransaction().commit();
        } finally {
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
        }
    }

    public Album findAlbum(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Album.class, id);
        } finally {
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
        }
    }
    
    /* Methods triggered by form events */
    public void newAlbum(ApplicationForm form) {
        // create new object
        Album album1 = new Album(null, "<New Album>", "CS", (short)1);
        
        // keep the object in the form
        form.setAlbum(album1);
        
        // init object
        album1.setTotaldisks((short)1);
        album1.setSongCollection(new ArrayList<Song>());
        album1.setAlbumCollection(new ArrayList<Album>());
        
        // add the new entry to the table
        form.getAlbumList().add(album1);
        int idx = form. getjTable_AlbumGroups().getRowCount()-1;
        form. getjTable_AlbumGroups().setRowSelectionInterval(idx, idx);
        addSongInAlbum(form);
        
        // prepare form for editing
        form.setSongsToRemoveList(new ArrayList<Song>());
        form.setEditableGroupAlbumForm(true, true);
        
        // set focus on Album title
        form.getjTF_groupalbum_title().requestFocus();
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
            form.setSongsToRemoveList(new ArrayList<Song>());
            form.setEditableGroupAlbumForm(true, false);

        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(ApplicationForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void commitAlbum(ApplicationForm form) {
        try {
            
            Album album1 = form.getAlbum();
            if ((album1.getTitle().equalsIgnoreCase("<New Album>")||(album1.getTitle().equals("")))&& (album1.getSongCollection().size() ==0)){
                Utility.msgWarning(form, "Το άλμπουμ πρέπει να έχει τουλάχιστον 1 τραγούδι & να δώσετε τίτλο άλμπουμ.", "Επεξεργασία αλμπουμ");
                form.highlightAlbumTitle();
                return;
            }
           
            if (album1.getTitle().equalsIgnoreCase("<New Album>")||(album1.getTitle().equals(""))){
                Utility.msgWarning(form, "Δεν έχετε δώσει τίτλο στο άλμπουμ", "Επεξεργασία άλμπουμ");
                form.highlightAlbumTitle();
                return;
            }
            if (album1.getSongCollection().size()==0) {
                Utility.msgWarning(form, "Το άλμπουμ πρέπει να έχει τουλάχιστον 1 τραγούδι.", "Επεξεργασία αλμπουμ");
                return;
            }
                        
            if (album1.getId() == null) {
                this.create(album1);
            } else {
                this.edit(album1, form);
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
            // cancel from existing entry
            idx = form.getjTable_AlbumGroups().getSelectedRow();
            album1.restore((Album)form.getClonedObj());
            idx = form.getjTable_AlbumGroups().getSelectedRow();
            form.getAlbumList().set(idx, album1);
            // reset selection
            form.getjTable_AlbumGroups().clearSelection();
            form.getjTable_AlbumGroups().setRowSelectionInterval(idx, idx);
        }
        //form.getjList_GroupAlbumSongs().setListData(album1.getSongCollection().toArray());
        form.setEditableGroupAlbumForm(false, false);
    }

    public void addSongInAlbum(ApplicationForm form) {
        Album album1 = form.getAlbum();
        int row = album1.getSongCollection().size();
        int col = 1;    // song title column
        
        // create a Song and append to album
        Song songToAlbum =  new Song(null, 0, (short)(row+1));
        List songInAlbumList = (List)album1.getSongCollection();
        songInAlbumList.add (songToAlbum);
        // append to GUI control's bounded list
        form.getSongList().add(songToAlbum);
        
        // ensure we can select cells
        form.getjTable_GroupAlbumSongs().setColumnSelectionAllowed(true);
        // set focus on table
        form.getjTable_GroupAlbumSongs().requestFocusInWindow();
        // set edit mode on title cell 
        form.getjTable_GroupAlbumSongs().editCellAt(row, col);
        // put cursor in the cell
        form.getjTable_GroupAlbumSongs().getEditorComponent().requestFocus();
    }

    public void removeSongFromAlbum(ApplicationForm form) {
        if (form.getjTable_GroupAlbumSongs().getSelectedRow() < 0) {
            Utility.msgWarning(form, "Δεν έχετε επιλέξει τραγούδι για αφαίρεση", "Επεξεργασία άλμπουμ");
        } else {
            //List songInAlbumList = (List)form.getAlbum().getSongCollection();
            //songInAlbumList.remove(form.getjTable_GroupAlbumSongs().getSelectedRow());

            int idx = form.getjTable_GroupAlbumSongs().getSelectedRow();
            Song song = form.getSongList().get(idx);
            int ans = Utility.msgPrompt(form, song.getTitle()+ "\n\nΕίσαι σίγουρος για τη διαγραφή?", "Διαγραφή Τραγουδιού");
            if (ans == 0) {
                form.getSongList().remove(song);
                //List songInAlbumList = (List)form.getAlbum().getSongCollection();
                form.getAlbum().getSongCollection().remove(song);
                song.setAlbumId(null);
                form.getSongsToRemoveList().add(song);
                form.getjTable_GroupAlbumSongs().clearSelection();
            }            
        }       
    }
    
}
