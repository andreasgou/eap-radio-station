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
public class PlayList {
    private String name;
    private ArrayList<Song>songs;
    
    public PlayList(){
    this.name=name;
    songs=new ArrayList<Song>();
    }
    
     public void add(Song so){
        songs.add(so);
    }
    
    public Iterator<Song>getSong(){
        return songs.iterator();
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
