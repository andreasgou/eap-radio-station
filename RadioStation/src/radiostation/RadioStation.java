/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package radiostation;

import radiostation.gui.ApplicationForm;

/**
 *
 * @author g.giakoumis, a.gounaris, g.erenidis, t.gritsopoulos, n.doulaveras
 * 
 */
public class RadioStation {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ApplicationForm form=new ApplicationForm(DBManager.getEm("RadioStationPU"));
        form.setVisible(true);
    }
    
}
