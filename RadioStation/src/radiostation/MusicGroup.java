/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package radiostation;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

/**
 *
 * @author user
 */
public class MusicGroup {
    private String name;
    private Date formationDate;
    private ArrayList<Artist>groupArtists;
    private ArrayList<Album>Albums;
    
    public MusicGroup(String name,Date formationDate){
        this.name=name;
        this.formationDate=formationDate;
        groupArtists=new ArrayList<Artist>(); 
    }    
    
     public void add(Artist ar){
        groupArtists.add(ar);
    }
    
    public Iterator<Artist>getArtist(){
        return groupArtists.iterator();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getFormationDate() {
        return formationDate;
    }

    public void setFormationDate(Date formationDate) {
        this.formationDate = formationDate;
    }

    public void setAlbums(ArrayList<Album> Albums) {
        this.Albums = Albums;
    }
    
    public String toString(){
        return name+formationDate;
    }
}
