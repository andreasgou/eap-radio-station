/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package radiostation.gui;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import radiostation.MusicGenre;
import radiostation.gui.ApplicationForm;

/**
 *
 * @author a.gounaris
 */
public class Utility {

    public static Date parseToDate(String dateString) {
        DateFormat format = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
        try {
            return format.parse(dateString);
        } catch (ParseException ex) {
            Logger.getLogger(ApplicationForm.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

        public static MusicGenre getGenre(EntityManager em, String name) {
            MusicGenre genre = (MusicGenre) em.createNamedQuery("MusicGenre.findByGenrename").setParameter("genrename", name).getSingleResult();
            return genre;
        }

}
