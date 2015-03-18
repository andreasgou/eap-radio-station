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

    public void create(Album album, ApplicationForm form) {
        if (album.getSongCollection() == null) {
            album.setSongCollection(new ArrayList<Song>());
        }
        if (album.getAlbumCollection() == null) {
            album.setAlbumCollection(new ArrayList<Album>());
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

            // store Album without collections to get a new ID from the Database
            Collection<Song> attachedSongCollection = new ArrayList<Song>();
            Collection<Song> currentSongCollection = album.getSongCollection();
            Collection<Album> attachedAlbumCollection = new ArrayList<Album>();
            Collection<Album> currentAlbumCollection = album.getAlbumCollection();
            album.setSongCollection(attachedSongCollection);
            album.setAlbumCollection(attachedAlbumCollection);
            em.persist(album);
            em.getTransaction().commit();
            
            // restore collections for the new entity and store again if not empty
            album.setSongCollection(currentSongCollection);
            album.setAlbumCollection(currentAlbumCollection);
            if (currentSongCollection.size()+currentAlbumCollection.size() > 0)
                edit(album, form);
            
        } catch (NonexistentEntityException ex) {
            Logger.getLogger(AlbumJpaController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(AlbumJpaController.class.getName()).log(Level.SEVERE, null, ex);
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
            
             // Add new albums in a separate transaction
            for (Album albumCollectionNewAlbumToAttach : albumCollectionNew) {
                // new Song entry
                if (albumCollectionNewAlbumToAttach.getId() == null) {
                    this.create(albumCollectionNewAlbumToAttach, form);
                }
            }

            // refresh song collection 
            // Add new songs in a separate transaction
            em.getTransaction().begin();
            for (Song songCollectionNewSongToAttach : songCollectionNew) {
                // new Song entry
                if (songCollectionNewSongToAttach.getId() == null) {
                    songCollectionNewSongToAttach.setAlbumId(album);
                    em.persist(songCollectionNewSongToAttach);
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
            // merge relations 
            album = em.merge(album);
            em.getTransaction().commit();

            em.getTransaction().begin();

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
            // Destroy removed songs
            Collection<Song> songCollectionForDeletion = album.getSongCollection();
            for (Song songCollectionSongToDetach : songCollectionForDeletion) {
                // Remove song from album
                em.remove(songCollectionSongToDetach);
            }
//            List<String> illegalOrphanMessages = null;
//            Collection<Song> songCollectionOrphanCheck = album.getSongCollection();
//            for (Song songCollectionOrphanCheckSong : songCollectionOrphanCheck) {
//                if (illegalOrphanMessages == null) {
//                    illegalOrphanMessages = new ArrayList<String>();
//                }
//                illegalOrphanMessages.add("This Album (" + album + ") cannot be destroyed since the Song " + songCollectionOrphanCheckSong + " in its songCollection field has a non-nullable albumId field.");
//            }
//            if (illegalOrphanMessages != null) {
//                throw new IllegalOrphanException(illegalOrphanMessages);
//            }
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
    public void newAlbum(ApplicationForm form, javax.swing.JTable sourceList) {
        // create new object
        Album album1 = new Album(null, "<New Album>", "CS", (short)1);
        
        // keep the object in the form
        form.setAlbum(album1);
        
        // init object
        album1.setTotaldisks((short)1);
        album1.setSongCollection(new ArrayList<Song>());
        album1.setAlbumCollection(new ArrayList<Album>());
        
        // add the new entry to the table
        //form.getAlbumList().add(album1);
        if (sourceList.equals(form.getjTable_AlbumArtists())) {
            form.getArtistAlbumList().add(album1);
        } else {
            form.getGroupAlbumList().add(album1);
        }
        int idx = sourceList.getRowCount()-1;
        sourceList.setRowSelectionInterval(idx, idx);
        addSongInAlbum(form, sourceList);
        
        // prepare form for editing
        form.setSongsToRemoveList(new ArrayList<Song>());
        if (sourceList.equals(form.getjTable_AlbumArtists())) {
            form.setEditableArtistAlbumForm(true, false);
            form.getjTF_artistalbum_title().requestFocus();
        } else {
            form.setEditableGroupAlbumForm(true, false);
            form.getjTF_groupalbum_title().requestFocus();
        }
    }

    public void destroyAlbum(ApplicationForm form, javax.swing.JTable sourceList) {
        if (sourceList.getSelectedRow() < 0) {
            Utility.msgWarning(form, "Δεν έχετε επιλέξει εγγραφή για διαγραφή", "Επεξεργασία Αλμπουμ");
            return;
        }
        try {
            int idx = sourceList.getSelectedRow();
            Album album1 = form.getAlbumList().get(idx);
            int ans = Utility.msgPrompt(form, album1.getTitle()+ "\n\nΕίσαι σίγουρος για τη διαγραφή?", "Διαγραφή Αλμπουμ");
            if (ans == 0) {
                form.getAlbumList().remove(album1);
                this.destroy(album1.getId());
                Utility.msgInfo(form, "Η διαγραφή ολοκληρώθηκε επιτυχώς!");
                sourceList.setRowSelectionInterval(idx-1, idx-1);
            }
            
        } catch (NonexistentEntityException ex) {
            Logger.getLogger(ApplicationForm.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalOrphanException ex) {
            Logger.getLogger(AlbumJpaController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void editAlbum(ApplicationForm form, javax.swing.JTable sourceList) {
        if (sourceList.getSelectedRow() < 0) {
            Utility.msgWarning(form, "Δεν έχετε επιλέξει εγγραφή για τροποποίηση", "Επεξεργασία άλμπουμ");
            return;
        }
        try {
            // prepare user selection for editing
            Album album1 = form.getAlbumList().get(sourceList.getSelectedRow());
            form.setAlbum(album1);
            // clone original object - used when cancel editing
            form.setClonedObj(album1.clone());
            // enable edit form 
            form.setSongsToRemoveList(new ArrayList<Song>());
            if (sourceList.equals(form.getjTable_AlbumArtists()))
                form.setEditableArtistAlbumForm(true, false);
            else
                form.setEditableGroupAlbumForm(true, false);

        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(ApplicationForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void commitAlbum(ApplicationForm form) {
        try {
            
            Album album1 = form.getAlbum();
            if ((album1.getTitle().equalsIgnoreCase("<New Album>")||(album1.getTitle().equals("")))&& (album1.getSongCollection().size() ==0)){
                Utility.msgWarning(form, "Το άλμπουμ πρέπει να έχει τουλάχιστον 1 τραγούδι & να δώσετε τίτλο άλμπουμ.", "Επεξεργασία άλμπουμ");
                form.highlightAlbumTitle();
                return;
            }
           
            if (album1.getTitle().equalsIgnoreCase("<New Album>")||(album1.getTitle().equals(""))){
                Utility.msgWarning(form, "Δεν έχετε δώσει τίτλο στο άλμπουμ", "Επεξεργασία άλμπουμ");
                form.highlightAlbumTitle();
                return;
            }
            if (album1.getSongCollection().size()==0) {
                Utility.msgWarning(form, "Το άλμπουμ πρέπει να έχει τουλάχιστον 1 τραγούδι.", "Επεξεργασία άλμπουμ");
                return;
            }
            for (Song song : album1.getSongCollection()) {
                if (song.getTitle().trim().equals("")) {
                    Utility.msgWarning(form, "Υπάρχουν τραγούδια χωρίς τίτλο στο άλμουμ.", "Επεξεργασία άλμπουμ");
                    return;
                }
                for (Song songDuplicates : album1.getSongCollection()) {
                    if (!song.equals(songDuplicates) && song.getTitle().trim().equals(songDuplicates.getTitle().trim())) {
                        Utility.msgWarning(form, "Υπάρχουν τραγούδια με τον ίδιο τίτλο στο άλμουμ.", "Επεξεργασία άλμπουμ");
                        return;
                    }
                }
            }
            // Mark child albums with no songs            
            Collection<Album> albumsToRemove = new ArrayList<Album>();
            for (Album child : album1.getAlbumCollection()) {
                if (child.getSongCollection().size()==0) {
                    albumsToRemove.add(child);
                    album1.setTotaldisks((short)(album1.getTotaldisks().shortValue() - 1));
                } else {
                    for (Song song : child.getSongCollection()) {
                        if (song.getTitle().trim().equals("")) {
                            Utility.msgWarning(form, "Υπάρχουν τραγούδια χωρίς τίτλο τουλάχιστον σε ένα δίσκο του άλμπουμ.", "Επεξεργασία άλμπουμ");
                            return;
                        }
                    }
                }
            }
            // Delete silently marked child albums with no songs            
            for (Album child : albumsToRemove) {
                album1.getAlbumCollection().remove(child);
                child.setParentalbumId(null);
            }
                        
            if (album1.getId() == null) {
                this.create(album1, form);
            } else {
                this.edit(album1, form);
            }
            form.setEditableArtistAlbumForm(false, false);
            form.setEditableGroupAlbumForm(false, false);
            Utility.msgInfo(form, "Τα στοιχεία του άλμπουμ αποθηκεύτηκαν επιτυχώς!");

        } catch (Exception ex) {
            Logger.getLogger(ApplicationForm.class.getName()).log(Level.SEVERE, null, ex);
            Utility.msgError(form, "Εμφανίστηκε σφάλμα κατά την αποθήκευση!");
        }
    }

    public void revertAlbum(ApplicationForm form, javax.swing.JTable sourceList) {
        int idx;
        // reset list
        Album album1 = form.getAlbum();
        if (album1.getId() == null) {
            // cancel from new entry
            form.getAlbumList().remove(album1);
            idx = sourceList.getRowCount()-1;
            if (idx > -1) {
                // has rows
                sourceList.clearSelection();
                sourceList.setRowSelectionInterval(idx, idx);
                album1 = form.getAlbumList().get(idx);
                form.setAlbum(album1);
            } else {
                // no rows
                album1.setSongCollection(new ArrayList<Song>());
                sourceList.clearSelection();
            }
        } else {
            // cancel from existing entry
            album1.restore((Album)form.getClonedObj());
            idx = sourceList.getSelectedRow();
            form.getAlbumList().set(idx, album1);
            // reset selection
            sourceList.clearSelection();
            sourceList.setRowSelectionInterval(idx, idx);
            if (sourceList.equals(form.getjTable_AlbumArtists()))
                form.prepareArtistAlbumSongList();
            else
                form.prepareGroupAlbumSongList();
        }
        if (sourceList.equals(form.getjTable_AlbumArtists()))
            form.setEditableArtistAlbumForm(false, false);
        else
            form.setEditableGroupAlbumForm(false, false);
    }

    public void addSongInAlbum(ApplicationForm form, javax.swing.JTable sourceList) {
        Album album1 = form.getAlbum();
        javax.swing.JTable targetList;
        if (album1.isLongPlay()) {
            if (sourceList.equals(form.getjTable_AlbumArtists()))
                album1 = album1.getAlbum(((Integer)form.getjSP_artistalbum_diskNumber().getValue()).intValue());
            else
                album1 = album1.getAlbum(((Integer)form.getjSP_groupalbum_diskNumber().getValue()).intValue());
        }
        int row = album1.getSongCollection().size();
        int col = 1;    // song title column
        
        // create a Song and append to album
        Song songToAlbum =  new Song(null, 0, (short)(row+1));
        List songInAlbumList = (List)album1.getSongCollection();
        songInAlbumList.add (songToAlbum);
        
        if (sourceList.equals(form.getjTable_AlbumArtists())) {
            // append to GUI control's bounded list
            form.getSongInAlbumArtistList().add(songToAlbum);
            targetList = form.getjTable_ArtistAlbumSongs();
        } else {
            // append to GUI control's bounded list
            form.getSongInAlbumGroupList().add(songToAlbum);
            targetList = form.getjTable_GroupAlbumSongs();
        }
        // ensure we can select cells
        targetList.setColumnSelectionAllowed(true);
        // set focus on table
        targetList.requestFocusInWindow();
        // set edit mode on title cell 
        targetList.editCellAt(row, col);
        // put cursor in the cell
        targetList.getEditorComponent().requestFocus();
    }

    public void removeSongFromAlbum(ApplicationForm form, javax.swing.JTable sourceList) {
        javax.swing.JTable targetList;
        List<radiostation.Song> songList = null;
        if (sourceList.getSelectedRow() < 0) {
            Utility.msgWarning(form, "Δεν έχετε επιλέξει τραγούδι για αφαίρεση", "Επεξεργασία άλμπουμ");
        } else {
            if (sourceList.equals(form.getjTable_AlbumArtists())) {
                targetList = form.getjTable_ArtistAlbumSongs();
                songList = form.getSongInAlbumArtistList();
            } else {
                targetList = form.getjTable_GroupAlbumSongs();
                songList = form.getSongInAlbumGroupList();
            }
            int idx = targetList.getSelectedRow();
            Song song = songList.get(targetList.convertRowIndexToModel(idx));
            int ans = Utility.msgPrompt(form, song.getTitle()+ "\n\nΕίσαι σίγουρος για τη διαγραφή?", "Διαγραφή Τραγουδιού");
            if (ans == 0) {
                songList.remove(song);
                //List songInAlbumList = (List)form.getAlbum().getSongCollection();
                Album album = form.getAlbum();
                if (album.isLongPlay()) {
                    if (sourceList.equals(form.getjTable_AlbumArtists()))
                        album = album.getAlbum(((Integer)form.getjSP_artistalbum_diskNumber().getValue()).intValue());
                    else
                        album = album.getAlbum(((Integer)form.getjSP_groupalbum_diskNumber().getValue()).intValue());
                }
                album.getSongCollection().remove(song);
                song.setAlbumId(null);
                // don't add the song in the queue for deletion if it doesn't exist 
                if (song.getId() != null)
                    form.getSongsToRemoveList().add(song);
                targetList.clearSelection();
            }            
        }       
    }
    
}
