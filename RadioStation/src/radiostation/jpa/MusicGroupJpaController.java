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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import radiostation.MusicGroup;
import radiostation.gui.ApplicationForm;
import radiostation.gui.Utility;
import radiostation.jpa.exceptions.NonexistentEntityException;

/**
 *
 * @author a.gounaris
 */
public class MusicGroupJpaController implements Serializable {

    public MusicGroupJpaController(EntityManager em) {
        this.em = em;
    }
    private EntityManager em = null;

    public EntityManager getEntityManager() {
        return em;
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
                //em.close();
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
                //em.close();
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
                //em.close();
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
            //em.close();
        }
    }

    public MusicGroup findMusicGroup(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(MusicGroup.class, id);
        } finally {
            //em.close();
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
            //em.close();
        }
    }


   /* Methods triggered by form events */
     public void newGroup(ApplicationForm form) {
        try {
            MusicGroup musicGroup1 = new MusicGroup();
            form.setMusicGroup(musicGroup1);
            form.setClonedObj(musicGroup1.clone());
            musicGroup1.setName("<New Nusic Group>");
            musicGroup1.setArtistCollection(new ArrayList<Artist>());
            form.getMusicGroupList().add(musicGroup1);
            int idx = form.getjTable_Groups().getRowCount()-1;
            form.getjTable_Groups().setRowSelectionInterval(idx, idx);
            form.getjList_GroupArtists().setListData(musicGroup1.getArtistCollection().toArray());
            form.setEditableGroupForm(true, true);
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(ApplicationForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void removeArtistFromMusicGroup(ApplicationForm form) {
        if (form.getjList_GroupArtists().getSelectedIndex() < 0) {
            Utility.msgWarning(form, "Δεν έχετε επιλέξει καλλιτέχνη για αφαίρεση", "Επεξεργασία συγκροτήματος");
        } else {
            List artistInGroupList = (List)form.getMusicGroup().getArtistCollection();
            artistInGroupList.remove(form.getjList_GroupArtists().getSelectedIndex());
            form.getjList_GroupArtists().setListData(artistInGroupList.toArray());
            form.getjList_GroupArtists().setSelectedIndex(artistInGroupList.size()-1);
        }
        
    }

    public void commitMusicGroup(ApplicationForm form) {
        try {
            MusicGroup musicGroup1 = form.getMusicGroup();
            if (musicGroup1.getId() == null) {
                this.create(musicGroup1);
            } else {
                this.edit(musicGroup1);
            }
            form.setEditableGroupForm(false, false);
            Utility.msgInfo(form, "Τα στοιχεία του συγκροτήματος αποθηκεύτηκαν επιτυχώς!");

        } catch (Exception ex) {
            Logger.getLogger(ApplicationForm.class.getName()).log(Level.SEVERE, null, ex);
            Utility.msgError(form, "Εμφανίστηκε σφάλμα κατά την αποθήκευση!");
        }
    }

    public void revertGroup(ApplicationForm form) {
        int idx;
        MusicGroup musicGroup1 = form.getMusicGroup();
        // reset list
        if (musicGroup1.getId() == null) {
            form.getMusicGroupList().remove(musicGroup1);
            musicGroup1.setArtistCollection(new ArrayList<Artist>());
            form.getjTable_Groups().clearSelection();
        } else {
            musicGroup1 = (MusicGroup)form.getClonedObj();
            idx = form.getjTable_Groups().getSelectedRow();
            form.getMusicGroupList().set(idx, musicGroup1);
            form.getjTable_Groups().clearSelection();
            form.getjTable_Groups().setRowSelectionInterval(idx, idx);
        }
        form.getjList_GroupArtists().setListData(musicGroup1.getArtistCollection().toArray());
        form.setEditableGroupForm(false, false);
    }

    public void editMusicGroup(ApplicationForm form) {
        if (form.getjTable_Groups().getSelectedRow() < 0) {
            Utility.msgWarning(form, "Δεν έχετε επιλέξει εγγραφή για τροποποίηση", "Επεξεργασία συγκροτήματος");
        } else {
            try {
                MusicGroup musicGroup1 = form.getMusicGroup(); 
                // get user selection
                musicGroup1 = form.getMusicGroupList().get(form.getjTable_Groups().getSelectedRow());
                // clone object - to be used if user cancels editing
                form.setClonedObj(musicGroup1.clone());
                //artistInGroupList = new Vector<Artist>();
                //artistInGroupList.addAll(musicGroup1.getArtistCollection());
                form.setEditableGroupForm(true, false);

            } catch (CloneNotSupportedException ex) {
                Logger.getLogger(ApplicationForm.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void addArtistInGroup(ApplicationForm form) {
        if (form.getjList_AvailableArtists().getSelectedIndex() < 0) {
            Utility.msgWarning(form, "Δεν έχετε επιλέξει καλλιτέχνη για προσθήκη στο συγκρότημα", "Επεξεργασία συγκροτήματος");
        } else {
            MusicGroup musicGroup1 = form.getMusicGroup();
            Artist artistToGroup =  (Artist)form.getjList_AvailableArtists().getSelectedValue();
            List artistInGroupList = (List)musicGroup1.getArtistCollection();
            artistInGroupList.add (artistToGroup);
            form.getjList_GroupArtists().setListData(artistInGroupList.toArray());
            form.getjList_GroupArtists().setSelectedIndex(artistInGroupList.size()-1);
        }
    }

    
}
