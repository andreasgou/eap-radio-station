/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package radiostation;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author user
 */
public class MusicGenre {
    private String name;
    private ArrayList <Artist>artists;
    public MusicGenre(){
    }
    public MusicGenre(String name){
        this.name=name;
        artists=new ArrayList<Artist>();
    }
    public void add(Artist ar){
        artists.add(ar);
    }
    
    public Iterator<Artist>getArtist(){
        return artists.iterator();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String toString(){
        return name;
    }
    
}
