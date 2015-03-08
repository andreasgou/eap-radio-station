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
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import radiostation.MusicGenre;

/**
 *
 * @author a.gounaris
 */
public class Utility {

    private static final JPanel msgPanel = new JPanel();

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

    public static void msgWarning(JFrame frame, String text, String title) {
        JOptionPane.showMessageDialog(frame, text, title, JOptionPane.WARNING_MESSAGE);
    }

    public static void msgError(JFrame frame, String text) {
        JOptionPane.showMessageDialog(frame, text, "Radio Station", JOptionPane.ERROR_MESSAGE);
    }

    public static void msgInfo(JFrame frame, String text) {
        JOptionPane.showMessageDialog(frame, text, "Radio Station", JOptionPane.INFORMATION_MESSAGE);
    }

    public static int msgPrompt(JFrame frame, String text, String title) {
        return JOptionPane.showConfirmDialog(msgPanel, text, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
    }

}
